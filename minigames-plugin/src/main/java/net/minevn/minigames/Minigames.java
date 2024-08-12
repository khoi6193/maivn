package net.minevn.minigames;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.google.common.collect.ImmutableList;
import net.milkbowl.vault.economy.Economy;
import net.minevn.minigames.cases.Case;
import net.minevn.minigames.commands.*;
import net.minevn.minigames.drops.DropEntity;
import net.minevn.minigames.drops.DropTasks;
import net.minevn.minigames.gadgets.*;
import net.minevn.minigames.gui.InventoryCategory;
import net.minevn.minigames.hooks.MineStrikeHook;
import net.minevn.minigames.hooks.SlaveListener;
import net.minevn.minigames.hooks.gadgetsmenu.GadgetsMenuHook;
import net.minevn.minigames.hooks.slave.*;
import net.minevn.minigames.items.types.BowPI;
import net.minevn.minigames.items.types.HatPI;
import net.minevn.minigames.items.types.SwordPI;
import net.minevn.minigames.nms.*;
import net.minevn.minigames.quests.Quest;
import net.minevn.minigames.quests.QuestCategory;
import net.minevn.minigames.shop.ShopCategory;
import net.minevn.mmclient.MatchMakerClient;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public class Minigames extends JavaPlugin {
	private PlayerPoints pp;
	private Economy econ;
	private MineStrikeHook mshook;
	private GadgetsMenuHook gmhook;
	private SlaveListener slaveListener;
	private DropTasks droptasks;

	@Override
	public void onEnable() {
		_instance = this;
		var server = getServer();
		var pm = server.getPluginManager();

		if (nms == null) {
			getLogger().severe("Unsupported version: " + version);
			pm.disablePlugin(this);
			return;
		}

		Configs.loadConfig();

		try {
			var file = MessagesConfig.getConfigFile();
			MessagesConfig.save(file);
			MessagesConfig.load(file);
		} catch (Exception ex) {
			getLogger().log(Level.WARNING, "Can't load message config!", ex);
		}

		new MySQL(this,
			Configs.getDbHost(),
			Configs.getDbName(),
			Configs.getDbUsername(),
			Configs.getDbPassword(),
			Configs.getDbPort()
		);
		server.getServicesManager().register(Economy.class, new VaultHandler(), this, ServicePriority.Highest);
		new MGListener(this);
		new PlayerPerks();
		server.getMessenger().registerOutgoingPluginChannel(this, "fs:minestrike");
		Objects.requireNonNull(getCommand("loa")).setExecutor(new LoaCex());
		Objects.requireNonNull(getCommand("economy")).setExecutor(new EcoCex());
		Objects.requireNonNull(getCommand("quest")).setExecutor(new QuestCex());
		new DebugCex(this);
		if (!setupEconomy()) {
			getLogger().severe("Khong the setup vault");
		}
		if (pm.getPlugin("MineStrike") != null) {
			mshook = new MineStrikeHook();
			slaveListener = new MineStrikeListener();
		}
		if (pm.getPlugin("GadgetsMenu") != null) {
			gmhook = new GadgetsMenuHook();
		}
		if (pm.getPlugin("VillageDefense") != null) {
			slaveListener = new VillageDefenseListener();
		}
		if (pm.getPlugin("BedWars1058") != null) {
			slaveListener = new BedWarsListener();
		}
		if (pm.getPlugin("WoolWars") != null) {
			slaveListener = new WoolWarsListener();
		}
		if (pm.getPlugin("BuildBattle") != null) {
			slaveListener = new BuildBattleListener();
		}
		if (pm.getPlugin("MurderMystery") != null) {
			slaveListener = new MurderMysteriesListener();
		}
		if (pm.getPlugin("TheLab") != null) {
			slaveListener = new TheLabListener();
		}
		setupPlaceholderExpansions();
		HitSound.load();
		Sword.load();
		ArrowTrail.load();
		Hat.load();
		ChatColors.load();
		MVPAnthem.load();
		Tomb.load();
		NamePreset.load();
		Case.loadCases(Configs.getMasterPath());
		ShopCategory.load();
		QuestCategory.load();
		InventoryCategory.load();
		Quest.load();
		ItemBundle.load();

		// protocollib
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
			this,
			ListenerPriority.HIGH,
			PacketType.Play.Server.SET_SLOT,
			PacketType.Play.Server.ENTITY_EQUIPMENT,
			PacketType.Play.Server.ENTITY_EFFECT,
			PacketType.Play.Server.REMOVE_ENTITY_EFFECT,
			PacketType.Play.Server.WINDOW_ITEMS,
			PacketType.Play.Server.NAMED_ENTITY_SPAWN
		) {
			@Override
			public void onPacketSending(PacketEvent event) {
				var packet = event.getPacket().deepClone();
				Player player = event.getPlayer();
				boolean refreshArmor = false;
				if (event.getPacketType() == PacketType.Play.Server.ENTITY_EFFECT) {
					if (nms instanceof NMSv12) {
						var effect = packet.getBytes().read(0);
						if (effect == 14) refreshArmor = true;
					} else {
						var effect = packet.getEffectTypes().read(0);
						if (effect != null && effect.getName().equals("INVISIBILITY")) refreshArmor = true;
					}
				}
				if (event.getPacketType() == PacketType.Play.Server.REMOVE_ENTITY_EFFECT) {
					var effect = packet.getEffectTypes().read(0);
					if (effect != null && effect.getName().equals("INVISIBILITY")) refreshArmor = true;
				}
				var id = packet.getIntegers().read(0);
				if (refreshArmor) {
					// force final
					final var fPlayer = player;
					for (var other : ImmutableList.copyOf(Bukkit.getOnlinePlayers())) {
						if (other != player && other.getWorld() == player.getWorld()) {
							Utils.runAsync(() -> Minigames.nms.sendArmor(fPlayer, other));
						}
					}
				}
				if (event.getPacketType() == PacketType.Play.Server.NAMED_ENTITY_SPAWN) {
					var other = Bukkit.getPlayer(packet.getUUIDs().read(0));
					if (other != null && other.isOnline()) {
						var fp = player;
						Utils.runAsync(() -> Minigames.nms.sendArmor(other, fp));
					}
					return;
				}
				if (event.getPacketType() == PacketType.Play.Server.SET_SLOT
						|| event.getPacketType() == PacketType.Play.Server.ENTITY_EQUIPMENT) {
					if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
						// thanks to some idiot at Mojang
						if (id != 0) return;
					} else if (event.getPacketType() == PacketType.Play.Server.ENTITY_EQUIPMENT) {
						player = Utils.getPlayer(id);
						if (player == null) return;
						try {
							var pairs = packet.getSlotStackPairLists().read(0);
							if (pairs != null) {
								var data = PlayerData.getData(player);
								if (data == null) return;
								var psw = data.getUsingItem(SwordPI.class);
								var hat = Utils.isInvisible(player) ? null : data.getUsingItem(HatPI.class);
								boolean update = false;
								boolean hatUpdated = false;
								for (var pair : pairs) {
									if (psw != null) {
										var item = pair.getSecond();
										if (item.getType().name().contains("SWORD")) {
											pair.setSecond(psw.getSword().replaceItem(item));
											update = true;
										}
									}
									if (pair.getFirst() == EnumWrappers.ItemSlot.HEAD && hat != null) {
										pair.setSecond(hat.getHat().replaceItem(pair.getSecond()));
										update = true;
										hatUpdated = true;
									}
								}
								if (hat != null && !hatUpdated) {
									pairs.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, hat.getHat().getItem()));
									update = true;
								}
								if (update) {
									packet.getSlotStackPairLists().write(0, pairs);
									event.setPacket(packet);
								}
								return;
							}
						} catch (FieldAccessException ignored) {
						}
					}
					ItemStack item = packet.getItemModifier().read(0);
					var data = PlayerData.getData(player);
					if (data != null) {
						if (item.getType().name().contains("SWORD")) {
							var psw = data.getUsingItem(SwordPI.class);
							if (psw != null) {
								var sword = psw.getSword();
								sword.replaceItem(item);
								event.setPacket(packet);
							}
						}
						if (item.getType().name().contains("BOW")) {
							var pb = data.getUsingItem(BowPI.class);
							if (pb != null) {
								var bow = pb.getBow();
								bow.replaceItem(item);
								event.setPacket(packet);
							}
						}
						if (!Utils.isInvisible(player)) {
							var hat = data.getUsingItem(HatPI.class);
							if (hat != null) {
								try {
									int slot = packet.getIntegers().read(nms.getClass() == NMSv12.class ? 1 : 2);
									if (slot == 5) {
										item = hat.getHat().replaceItem(item);
										packet.getItemModifier().write(0, item);
										event.setPacket(packet);
									}
								} catch (FieldAccessException ex) {
									// for monkey slaves (entity equipment)
									var slot = packet.getItemSlots().read(0);
									if (slot == EnumWrappers.ItemSlot.HEAD) {
										packet.getItemModifier().write(0, hat.getHat().getItem());
										event.setPacket(packet);
									}
								}
							}
						}
					}
				} else if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
//                    if (packet.getIntegers().read(0) != 0) return;
					var data = PlayerData.getData(event.getPlayer());
					if (data == null) return;
					var psw = data.getUsingItem(SwordPI.class);
					var pb = data.getUsingItem(BowPI.class);
					var hat = data.getUsingItem(HatPI.class);
//                    if (psw == null) return;
					var list = packet.getItemListModifier().read(0);
					int size = list.size();
					int type = packet.getIntegers().read(0);
					boolean update = false;
					for (int i = 0; i < size; i++) {
						var item = list.get(i);
						if (type == 0) {
							if (psw != null && item.getType().name().contains("SWORD")) {
								list.set(i, psw.getSword().replaceItem(item));
								update = true;
							}
							if (pb != null &&item.getType().name().contains("BOW")) {
								list.set(i, pb.getBow().replaceItem(item));
								update = true;
							}
						}
						if (hat != null && type == 0 && i == 5) {
							list.set(i, hat.getHat().replaceItem(item));
							update = true;
						}
					}
					if (update) {
						packet.getItemListModifier().write(0, list);
						event.setPacket(packet);
					}
				}
			}
		});

		var mm = MatchMakerClient.getInstance();
		if (!mm.isSlave()) { // for lobby
			pp = (PlayerPoints) pm.getPlugin("PlayerPoints");
			MainCmd.init();
			AdminCmd.init();
			new RideCmd();
			new GiftCodeCmd();
			new MailCmd();
		} else {
			DropEntity.load();
			Objects.requireNonNull(getCommand("drops")).setExecutor(new DropCex());
			droptasks = new DropTasks();
		}

		// scheduler
		new Scheduler();
	}

	public void setupPlaceholderExpansions() {
		if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new Expansions().register();
		} else {
			getLogger().severe("Khong the setup placeholder expansion");
		}
	}

	private boolean setupEconomy() {
		if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return true;
	}

	public PlayerPoints getPlayerPoints() {
		return pp;
	}

	public Economy getEconomy() {
		return econ;
	}

	public MineStrikeHook getMSHook() {
		return mshook;
	}

	public GadgetsMenuHook getGMHook() {
		return gmhook;
	}

	public SlaveListener getSlaveListener() {
		return slaveListener;
	}

	//region singleton
	private static Minigames _instance;

	public static Minigames getInstance() {
		return _instance;
	}

	private static final String version = Bukkit.getServer().getClass().getPackage().getName()
			.replace(".", ",").split(",")[3];

	public static final INMS nms = switch (version) {
		case "v1_12_R1" -> new NMSv12();
		case "v1_18_R2" -> new NMSv18();
		case "v1_19_R1" -> new NMSv19();
		case "v1_19_R3" -> new NMSv194();
		default -> null;
	};

	public DropTasks getDropTasks() {
		return droptasks;
	}
	//endregion
}
