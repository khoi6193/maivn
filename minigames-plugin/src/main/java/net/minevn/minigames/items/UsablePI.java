package net.minevn.minigames.items;

import net.minevn.minigames.Messages;
import net.minevn.minigames.Minigames;
import net.minevn.minigames.MySQL;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.gui.InventoryGui;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public abstract class UsablePI extends PlayerItem {

	private boolean isusing;

	public UsablePI(String catename, PlayerData owner, int id, Material type, short data, long expire, long obtained, boolean isUsing) {
		this(catename, owner, id, type, data, expire, obtained, isUsing, isUsing);
	}

	public UsablePI(String catename, PlayerData owner, int id, Material type, short data, long expire, long obtained, boolean isUsing,
					boolean glow) {
		super(catename, owner, id, type, data, expire, obtained, glow);
		isusing = isUsing;
	}

	public final boolean isUsing() {
		return isusing;
	}

	public final void setUsing(boolean isusing) {
		this.isusing = isusing;
	}

	public boolean isStackable() {
		return false;
	}

	/**
	 * This is called asynchronously
	 */
	public void onUsingToggle() {
	}

	@Override
	public void onClick(InventoryClickEvent e) {
		Minigames main = Minigames.getInstance();
		Player p = (Player) e.getWhoClicked();
		var data = PlayerData.getData(p);
		if (!(e.getInventory().getHolder() instanceof InventoryGui ig)) return;
		if (e.isShiftClick()) {
			ig.lock();
			Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
				if (!isUsing()) data.useItem(this);
				else {
					setUsing(false);
					setGlowing(false);
					MySQL.getInstance().saveItem(this);
				}
				ig.build();
				onUsingToggle();
			});
		} else if (isPreviewable()) preview(p);
	}

	protected void addUsingStatus(List<String> lores) {
		lores.add(isUsing() ? Messages.PI_CLICK_TO_UNUSE : Messages.PI_CLICK_TO_USE);
	}
}

