package net.minevn.minigames.items.types;

import net.minevn.minigames.Messages;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.items.StackablePI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MSPVEMedicLargePI extends StackablePI {
	public MSPVEMedicLargePI(PlayerData owner, int id, long expire, long obtained, int amount) {
		super(Messages.PI_CATEGORY_TOOLS, owner, id, Material.IRON_BOOTS, (short) 0, expire, obtained, amount);
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = super.getItem();
		ItemMeta im = item.getItemMeta();
		im.setUnbreakable(false);
		im.setDisplayName(Messages.PI_MS_PVE_MEDIC_LARGE);
		List<String> lores = new ArrayList<>(getDescription());
		im.setLore(lores);
		item.setItemMeta(im);
		item.setAmount(amount);
		return item;
	}

	@Override
	public String getData() {
		return "";
	}

	@Override
	public List<String> getDescription() {
		return Messages.PI_MS_PVE_MEDIC_LARGE_DESC;
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
