package net.minevn.minigames.items.types;

import net.minevn.minigames.PlayerData;
import net.minevn.minigames.items.PlayerItem;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

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
	public String getData() {
		// TypePI$data$amount
		return String.join("$", item.getClass().getSimpleName(), item.getData(), duration + "");
	}

	@Override
	public List<String> getDescription() {
		return item.getDescription();
	}

	@Override
	public void onClick(InventoryClickEvent e) {
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

	// static

	/**
	 * valid data: TimedTypePI$data$days
	 * example: HitSoundPI$hẻo$7
	 */
	public static ActivableTimedPI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) throws Exception {
		return null;
	}
}
