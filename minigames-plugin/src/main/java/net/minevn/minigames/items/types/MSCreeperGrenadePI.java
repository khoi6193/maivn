package net.minevn.minigames.items.types;

import net.minevn.minigames.Messages;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.items.PlayerItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MSCreeperGrenadePI extends PlayerItem {
	public MSCreeperGrenadePI(PlayerData owner, int id, long expire, long obtained) {
		super(Messages.PI_CATEGORY_TOOLS, owner, id, Material.IRON_SHOVEL, (short) 105, expire, obtained);
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = super.getItem();
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(Messages.PI_MS_CREEPERGRENADE);
		List<String> lores = new ArrayList<>(getDescription());
		lores.add("§f");
		addExpireDate(lores);
		im.setLore(lores);
		item.setItemMeta(im);
		return item;
	}

	@Override
	public String getData() {
		return "";
	}

	@Override
	public List<String> getDescription() {
		return Messages.PI_MS_CREEPERGRENADE_DESC;
	}

	@Override
	public PlayerItem clone() {
		return new MSCreeperGrenadePI(null, -1, expire, System.currentTimeMillis());
	}

	// static
	public static MSCreeperGrenadePI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		return new MSCreeperGrenadePI(owner, id, expire, obtained);
	}
}
