package net.minevn.minigames.items;

import net.minevn.minigames.PlayerData;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

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
	}
}

