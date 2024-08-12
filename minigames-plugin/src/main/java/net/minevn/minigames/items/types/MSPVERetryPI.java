package net.minevn.minigames.items.types;

import net.minevn.minigames.Messages;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.items.StackablePI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MSPVERetryPI extends StackablePI {
	public MSPVERetryPI(PlayerData owner, int id, long expire, long obtained, int amount) {
		super(Messages.PI_CATEGORY_TOOLS, owner, id, Material.WOODEN_SHOVEL, (short) 34, expire, obtained, amount);
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = super.getItem();
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(Messages.PI_MS_PVE_RETRY);
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
		return Messages.PI_MS_PVE_RETRY_DESC;
	}

	@Override
	public MSPVERetryPI clone() {
		return new MSPVERetryPI(owner, id, expire, obtained, amount);
	}

	// region static
	public static MSPVERetryPI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		return new MSPVERetryPI(owner, id, expire, obtained, amount);
	}
	// endregion
}
