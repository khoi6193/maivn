package net.minevn.minigames.items;

import net.minevn.minigames.PlayerData;
import org.bukkit.Material;

public abstract class StackablePI extends PlayerItem {

	public static final int STACKABLE_MAXAMOUNT = 64;
	protected int amount;

	public StackablePI(String catename, PlayerData owner, int id, Material type, short data, long expire, long obtained, int amount) {
		super(catename, owner, id, type, data, expire, obtained);
		this.amount = amount;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public void addAmount(int amount) {
		this.amount += amount;
	}

	public void subtractAmount(int amount) {
		this.amount -= amount;
	}

	public abstract StackablePI clone();

}
