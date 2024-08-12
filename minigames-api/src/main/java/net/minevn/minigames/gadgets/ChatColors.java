package net.minevn.minigames.gadgets;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatColors {
	private String id, name, color;
	private Material m;
	private List<String> description;
	private short data;

	public ChatColors(String id, String name, String color, Material m, List<String> description, short data) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.data = data;
		this.m = m;
		this.description = description;

	}

	public String getColor() {
		return color;
	}

	public String getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Material getMaterial() {
		return m;
	}

	public List<String> getDescription() {
		return description;
	}

	public short getData() {
		return data;
	}

	public ItemStack getItem() {
		ItemStack i = new ItemStack(m, 1, data);
		ItemMeta im = i.getItemMeta();
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
		i.setItemMeta(im);
		return i;
	}

	// region static
	private static Map<String, ChatColors> list = new HashMap<>();

	public static Map<String, ChatColors> list() {
		return list;
	}

	public static void load() {
	}

	public static ChatColors get(String id) {
		return list.get(id);
	}
	// endregion
}
