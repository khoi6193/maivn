package net.minevn.minigames.items.types;

import com.mojang.authlib.GameProfile;
import net.minevn.minigames.Messages;
import net.minevn.minigames.Minigames;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.award.types.TimedAW;
import net.minevn.minigames.gui.InventoryGui;
import net.minevn.minigames.items.PlayerItem;
import net.minevn.minigames.items.UsablePI;
import net.minevn.mmclient.MatchMakerClient;
import net.minevn.mmclient.utils.MMUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Kích hoạt item có thời hạn sử dụng
 */
public class ActivableTimedPI extends PlayerItem {

	private PlayerItem item;
	private int duration; // in days

	public ActivableTimedPI(PlayerData owner, int id, PlayerItem item, int duration, long obtained) {
		super(item.getCategoryName(), owner, id, item.getItem().getType(), item.getItem().getDurability(), 0, obtained);
		this.item = item;
		this.duration = duration;
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = this.item.getItem();
		ItemMeta im = item.getItemMeta();
		List<String> lores = new ArrayList<String>(getDescription());
		lores.add("§f");
		lores.add(Messages.PI_DURATION.replace("%days%", duration + ""));
		lores.add("§f");
		if (isPreviewable()) lores.add(Messages.SHOP_CLICK_TO_PREVIEW);
		if (this.item instanceof UsablePI) {
			lores.add(Messages.PI_CLICK_TO_USE);
		} else {
			lores.add(Messages.PI_CLICK_TO_ACTIVATE);
		}
		im.setLore(lores);
		item.setItemMeta(im);
		return item;
	}

	@Override
	public String getData() {
		// TypePI$data$amount
		return String.join("$", item.getClass().getSimpleName(), item.getData(), duration + "");
	}

	@Override
	public List<String> getDescription() {
		return item.getDescription();
	}

	@Override
	public PlayerItem clone() {
		return new ActivableTimedPI(null, -1, item.clone(), duration, System.currentTimeMillis());
	}

	@Override
	public void onClick(InventoryClickEvent e) {
		if (!(e.getInventory().getHolder() instanceof InventoryGui ig)) return;
		if (e.isLeftClick() && e.isShiftClick()) {
			ig.lock();
			MMUtils.runNotSync(() -> {
				try {
					owner.removeItem(this, "active item");
					var award = new TimedAW(item, duration);
					award.apply(owner.getPlayer(), "from ActivableTimedPI");
					if (item instanceof UsablePI) {
						var usable = owner.getTimedItem(item.getClass(), item.getData());
						if (usable != null) {
							owner.useItem((UsablePI) usable);
							((UsablePI) usable).onUsingToggle();
						}
					}
					ig.build();
				} catch (Exception ex) {
					MatchMakerClient.getInstance().sendMessage(e.getWhoClicked(), "§cCó lỗi xảy ra khi kích hoạt " +
							"vật phẩm, vui lòng báo cho admin càng sớm càng tốt để được hỗ trợ!");
					Minigames.getInstance().getLogger().log(Level.SEVERE, "Lỗi khi kích hoạt item #" + id, ex);
				}
			});
		} else if (isPreviewable()) preview((Player) e.getWhoClicked());
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public PlayerItem getPlayerItem() {
		return item;
	}

	@Override
	public GameProfile getGameProfile() {
		return item.getGameProfile();
	}

	@Override
	public String getCategoryRegex() {
		return item.getCategoryRegex();
	}

	@Override
	public boolean isPreviewable() {
		return item.isPreviewable();
	}

	@Override
	public void preview(Player p) {
		item.preview(p);
	}

	// static

	/**
	 * valid data: TimedTypePI$data$days
	 * example: HitSoundPI$hẻo$7
	 */
	public static ActivableTimedPI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) throws Exception {
		var aw = TimedAW.fromData(data);
		return new ActivableTimedPI(owner, id, aw.getItem(), aw.getAmount(), obtained);
	}
}
