package net.minevn.minigames.award;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class PlayerAward {

	private ItemUnit unit;
	protected int amount;
	private String displayName;

	public PlayerAward(ItemUnit unit, int amount) {
		if (unit == ItemUnit.PCS && amount <= 0)
			throw new IllegalArgumentException("Với loại item này thì số lượng phải lớn hơn 0");
		if (amount < 0)
			throw new IllegalArgumentException("Số lượng không được âm");
		this.amount = amount;
		this.unit = unit;
	}

	public String getUnit() {
		return getUnit(amount);
	}

	public ItemUnit getUnitType() {
		return unit;
	}

	public String getUnit(int amount) {
		if (unit == null)
			return "";
		if (unit == ItemUnit.DAYS && amount == 0)
			return "Vĩnh viễn";
		return amount + " " + unit.getName();
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getAmount() {
		return amount;
	}

	public abstract void apply(Player p, String note) throws Exception;

	public abstract ItemStack getGuiItem();

	public abstract String getName();

	public String getDisplayName() {
		return displayName != null ? displayName : getName();
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public abstract String typeName();

	public boolean isPreviewable() {
		return false;
	}

	public void preview(Player player) {
	}

	public enum ItemUnit {
		PCS("cái"), DAYS("ngày");

		private String name;

		ItemUnit(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	// statics
	public static final String[] types = new String[]{"StackableAW", "TimedAW", "ActivableTimedAW", "ItemAW"};

	public static PlayerAward fromData(String type, String data) throws Exception {
		var clazz = Class.forName("net.minevn.minigames.award.types." + type);
		var method = clazz.getMethod("fromData", String.class);
		return (PlayerAward) method.invoke(null, data);
	}
}
