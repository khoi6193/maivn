package net.minevn.minigames.gadgets;

import net.minevn.guiapi.XMaterial;
import net.minevn.minigames.Configs;
import net.minevn.minigames.KUtils;
import net.minevn.minigames.Minigames;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

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

	public ItemStack getItem() {
		var item = new ItemStack(type, 1, data);
		var im = item.getItemMeta();
		im.setDisplayName(name);
		im.setUnbreakable(true);
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(im);
		return item;
	}

	public ArmorStand spawn(Player player) {
		return spawn(player, player.getEyeLocation());
	}

	public ArmorStand spawn(Player player, Location location) {
		return Minigames.nms.createArmorStand(location, player.getName(), getItem(), true,
				Minigames.getInstance());
	}

	//region static
	private static Map<String, Tomb> list = new HashMap<>();

	public static Tomb get(String id) {
		return list.get(id);
	}

	public static void load() {
		Minigames main = Minigames.getInstance();
		main.getLogger().info("Tombs...");
		YamlConfiguration config = YamlConfiguration
				.loadConfiguration(new File(Configs.getMasterPath() + "tombs.yml"));
		config.getKeys(false).forEach(key -> {
			try {
				String name = KUtils.colorCodes(config.getString(key + ".name", ""));
                Material m = XMaterial.quickMatch(config.getString(key + ".material", ""));
				List<String> lore = KUtils.colorCodes(config.getStringList(key + ".description"));
				short data = (short) config.getInt(key + ".data");
				list.put(key, new Tomb(key, name, m, data, lore));
			} catch (Exception e) {
				main.getLogger().log(Level.WARNING, "Can't load " + key + " tomb", e);
			}
		});
		if (!list.containsKey("default")) {
			main.getLogger().warning("No default tomb found");
		}
		main.getLogger().info("Loaded " + list.size() + " tombs");
	}

	public static List<String> getSuggestions() {
		return list.values().stream().map(x -> "TombPI$" + x.id + "$0").toList();
	}
	//endregion
}
