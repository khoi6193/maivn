package net.minevn.minigames;

import de.tr7zw.nbtapi.NBT;
import net.minevn.minigames.items.types.*;
import net.minevn.minigames.quests.QuestAttempt;
import net.minevn.minigames.quests.QuestObjective;
import net.minevn.mmclient.MatchMakerClient;
import net.minevn.mmclient.api.PlayerMoveServerEvent;
import net.minevn.mmclient.api.SlaveStateChangeEvent;
import net.minevn.mmclient.events.RoomLeaveEvent;
import net.minevn.mmclient.slaves.SlaveState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.utils.ReflectionUtils;

import java.util.Random;

public class MGListener implements Listener {
	private Minigames main;

	public MGListener(Minigames main) {
		this.main = main;
		main.getLogger().info("Registering events...");
		main.getServer().getPluginManager().registerEvents(this, main);
		ReflectionUtils.setPlugin(main);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		new PlayerData(e.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		PlayerData data = PlayerData.getData(e.getPlayer());
		if (data != null) data.destroy();
	}

	@EventHandler
	public void onServerMove(PlayerMoveServerEvent e) {
		var data = PlayerData.getData(e.getPlayer());
		if (data != null) MySQL.getInstance().saveData(data);
	}

	@EventHandler
	public void onStateChange(SlaveStateChangeEvent e) {
		if (e.getNewState() == SlaveState.ENDING) {
			for (Player player : MatchMakerClient.getInstance().getSlaveHandler().getPlayers()) {
				var data = PlayerData.getData(player);
				new QuestAttempt(data, QuestObjective.PLAY);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDamaged(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player p) {
			var data = PlayerData.getData(p);
			if (data == null) return;
			var sound = data.getUsingItem(HitSoundPI.class);
			if (sound == null) return;
			sound.getHitSound().play(p.getEyeLocation());
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player p && e.getEntity() instanceof LivingEntity target) {
			if (!p.getInventory().getItemInMainHand().getType().name().contains("SWORD")) return;
			var data = PlayerData.getData(p);
			if (data == null) return;
			var sword = data.getUsingItem(SwordPI.class);
			if (sword == null) return;
			String sound = sword.getSword().getHitSound();
			if (sound != null) {
				target.getWorld().playSound(target.getEyeLocation(), sound, 1f, 1f);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onShoot(EntityShootBowEvent e) {
		if (e.getEntity() instanceof Player p) {
			if (!(e.getProjectile() instanceof Arrow)) return;
			var arrow = e.getProjectile();
			var data = PlayerData.getData(p);
			if (data == null) return;
			for (var at : data.getStackedUsingItem(ArrowTrailPI.class)) {
				var particle = new ParticleBuilder(ParticleEffect.valueOf(at.getArrowTrail().getParticle()));
				var random = new Random();
				new BukkitRunnable() {
					@Override
					public void run() {
						var v = arrow.getVelocity();
						particle.setLocation(arrow.getLocation())
								.setOffset(random.nextFloat() - 0.5f, random.nextFloat() - 0.5f,
										random.nextFloat() - 0.5f)
								.setSpeed(1f)
								.display();
						if (arrow.isDead() || arrow.isOnGround()) {
							cancel();
						}
					}
				}.runTaskTimerAsynchronously(main, 0, 1);
			}
		}
	}

	@EventHandler
	public void onHitArrow(ProjectileHitEvent e) {
		var hitEntity = e.getHitEntity();
		if (!(hitEntity instanceof LivingEntity target)) return;
		if (e.getEntity() instanceof Arrow arrow && arrow.getShooter() instanceof Player p) {
			if (!(p.getInventory().getItemInMainHand().getType().name().contains("BOW"))) return;
			var data = PlayerData.getData(p);
			if (data == null) return;
			var bow = data.getUsingItem(BowPI.class);
			if (bow == null) return;
			String sound = bow.getBow().getHitSound();
			if (sound != null) {
				target.getWorld().playSound(target.getEyeLocation(), sound, 1f, 1f);
			}
		}
	}

	@EventHandler
	public void onHeld(PlayerItemHeldEvent e) {
		var player = e.getPlayer();
		int slot = e.getNewSlot();
		Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
			var item = player.getInventory().getItem(slot);
			if (item != null && item.getType().name().contains("SWORD")) {
				Minigames.nms.sendFakeItem(player, slot + 36, item);
			}
		});
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		var player = (Player) e.getPlayer();
		var data = PlayerData.getData(player);
		if (data == null) return;
		// update hat
		var hat = data.getUsingItem(HatPI.class);
		if (hat != null) {
			hat.getHat().sendPacket(player);
		}
	}

	@EventHandler
	public void onWorldMove(PlayerChangedWorldEvent e) {
		Utils.runLater(() -> {
			var data = PlayerData.getData(e.getPlayer());
			if (data == null) return;
			data.updateGadgets();
		}, 10);
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		var data = PlayerData.getData(e.getPlayer());
		if (data != null && data.getChatListener() != null) {
			e.setCancelled(true);
			data.getChatListener().onChat(e.getMessage());
		}
	}

	@EventHandler
	public void onEditBook(PlayerEditBookEvent e) {
		var player = e.getPlayer();
		var data = PlayerData.getData(player);
		if (data == null || data.getBookListener() == null) return;
		data.getBookListener().onBook(e.getNewBookMeta());
		var item = e.getPlayer().getInventory().getItemInMainHand();
		if (NBT.get(item, t -> t.hasTag("bookListener"))) {
			item.setAmount(0);
			player.getInventory().setItemInMainHand(null);
		}
	}

	@EventHandler
	public void onLeaveRoom(RoomLeaveEvent e) {
		var data = PlayerData.getData(e.getPlayer());
		if (data != null) {
			Utils.runNotSync(data::noticeMail);
		}
	}
}
