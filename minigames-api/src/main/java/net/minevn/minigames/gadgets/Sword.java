package net.minevn.minigames.gadgets;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sword {
	private String id;
	private Material type;
	private short data;
	private String name;
	private String hitSound;
	private List<String> description;

	public Sword(String id, String name, Material type, short data, String hitSound, List<String> description) {
		this.id = id;
		this.type = type;
		this.data = data;
		this.name = name;
		this.hitSound = hitSound;
		this.description = description;
	}

	public Material getType() {
		return type;
	}

	public short getData() {
		return data;
	}

	public String getName() {
		return name;
	}

	public String getID() {
		return id;
	}

	public String getHitSound() {
		return hitSound;
	}

	public List<String> getDescription() {
		return description;
	}

	public ItemStack getItem() {
		var item = new ItemStack(type, 1, data);
		var im = item.getItemMeta();
		im.setDisplayName(name);
		im.setUnbreakable(true);
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(im);
		return item;
	}

	public ItemStack replaceItem(ItemStack item) {
		var sname = item.getI18NDisplayName();
		item.setType(type);
		item.setDurability(data);
		var im = item.getItemMeta();
		im.setDisplayName(name + " §b(" + sname + ")");
		im.setUnbreakable(true);
		im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(im);
		return item;
	}

	//region static
	private static Map<String, Sword> list = new HashMap<>();

	public static Sword get(String id) {
		return list.get(id);
	}

	public static void load() {
	}
	//endregion
}
