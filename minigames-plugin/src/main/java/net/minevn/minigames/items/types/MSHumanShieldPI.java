package net.minevn.minigames.items.types;

import net.minevn.minigames.Messages;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.items.PlayerItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MSHumanShieldPI extends PlayerItem {
	public MSHumanShieldPI(PlayerData owner, int id, long expire, long obtained) {
		super(Messages.PI_CATEGORY_TOOLS, owner, id, Material.TOTEM_OF_UNDYING, (short) 0, expire, obtained);
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = super.getItem();
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(Messages.PI_MS_HUMANSHIELD);
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
		return Messages.PI_MS_HUMANSHIELD_DESC;
	}

	@Override
	public PlayerItem clone() {
		return new MSHumanShieldPI(null, -1, expire, System.currentTimeMillis());
	}

	// static
	public static MSHumanShieldPI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		return new MSHumanShieldPI(owner, id, expire, obtained);
	}

	private static ItemStack i = null;

	public static ItemStack getHotbarItem() {
		if (i == null) {
			i = new ItemStack(Material.TOTEM_OF_UNDYING, 1);
			ItemMeta im = i.getItemMeta();
			im.setUnbreakable(true);
			im.setDisplayName(Messages.PI_MS_HUMANSHIELD);
			List<String> lores = new ArrayList<>(Messages.PI_MS_HUMANSHIELD_DESC);
			im.setLore(lores);
			im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
			i.setItemMeta(im);
		}
		return i.clone();
	}
}
