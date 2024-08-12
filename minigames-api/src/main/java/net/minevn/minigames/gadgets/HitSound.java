package net.minevn.minigames.gadgets;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HitSound {
	private String id, name, sound;
	private Material m;
	private List<String> description;
	private short data;

	public HitSound(String id, String name, String sound, Material m, List<String> description, short data) {
		this.id = id;
		this.name = name;
		this.sound = sound;
		this.data = data;
		this.m = m;
		this.description = description;
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

	public short getData() {
		return data;
	}

	public List<String> getDescription() {
		return description;
	}

	public ItemStack getItem() {
		ItemStack i = new ItemStack(m, 1, data);
		ItemMeta im = i.getItemMeta();
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
		i.setItemMeta(im);
		return i;
	}

	public void play(Location l) {
		l.getWorld().playSound(l, sound, 1, 1);
	}

	public void preview(Player p) {
		p.playSound(p.getLocation(), sound, 1, 1);
	}

	//region static
	private static Map<String, HitSound> list = new HashMap<>();

	public static Map<String, HitSound> list() {
		return list;
	}

	public static void load() {
	}

	public static HitSound get(String id) {
		return list.get(id);
	}
	//endregion
}
