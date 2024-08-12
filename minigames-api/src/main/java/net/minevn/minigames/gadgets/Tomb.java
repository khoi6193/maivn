package net.minevn.minigames.gadgets;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tomb {
	private String id;
	private String name;
	private Material type;
	private short data;
	private List<String> description;

	@Deprecated
	public Tomb(String id, String name, ItemStack item, List<String> description) {
		this.id = id;
		this.name = name;
		this.type = item.getType();
		this.data = item.getDurability();
		this.description = description;
	}
	
	public Tomb(String id, String name, Material type, short data, List<String> description) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.data = data;
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
	
	public List<String> getDescription() {
		return description;
	}

	public ArmorStand spawn(Player player) {
		return null;
	}

	public ArmorStand spawn(Player player, Location location) {
		return null;
	}

	//region static
	private static Map<String, Tomb> list = new HashMap<>();

	public static Tomb get(String id) {
		return list.get(id);
	}

	public static void load() {
	}
	//endregion
}
