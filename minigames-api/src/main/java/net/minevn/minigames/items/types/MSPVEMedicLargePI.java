package net.minevn.minigames.items.types;

import net.minevn.minigames.PlayerData;
import net.minevn.minigames.items.StackablePI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MSPVEMedicLargePI extends StackablePI {
	public MSPVEMedicLargePI(PlayerData owner, int id, long expire, long obtained, int amount) {
		super("", owner, id, Material.IRON_BOOTS, (short) 0, expire, obtained, amount);
	}

	@Override
	public ItemStack getItem() {
		return super.getItem();
	}

	@Override
	public String getData() {
		return "";
	}

	@Override
	public List<String> getDescription() {
		return new ArrayList<>();
	}

	@Override
	public MSPVEMedicLargePI clone() {
		return new MSPVEMedicLargePI(owner, id, expire, obtained, amount);
	}

	// region static
	public static MSPVEMedicLargePI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		return new MSPVEMedicLargePI(owner, id, expire, obtained, amount);
	}
	// endregion
}
