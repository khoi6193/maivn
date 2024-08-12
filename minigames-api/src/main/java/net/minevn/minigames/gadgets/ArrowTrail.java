package net.minevn.minigames.gadgets;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArrowTrail {
	private String id, name, particle;
	private Material m;
	private List<String> description;
	private short data;

	public ArrowTrail(String id, String name, String particle, Material m, List<String> description, short data) {
		this.id = id;
		this.name = name;
		this.particle = particle;
		this.data = data;
		this.m = m;
		this.description = description;

	}

	public String getParticle() {
		return particle;
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

	//region static
	private static Map<String, ArrowTrail> list = new HashMap<>();

	public static Map<String, ArrowTrail> list() {
		return list;
	}

	public static void load() {
	}

	public static ArrowTrail get(String id) {
		return list.get(id);
	}
	//endregion
}
