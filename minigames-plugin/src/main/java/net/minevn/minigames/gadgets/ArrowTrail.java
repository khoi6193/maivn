package net.minevn.minigames.gadgets;

import net.minevn.guiapi.XMaterial;
import net.minevn.minigames.Configs;
import net.minevn.minigames.KUtils;
import net.minevn.minigames.Minigames;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

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
		Minigames main = Minigames.getInstance();
		main.getLogger().info("Arrow trail...");
		YamlConfiguration config = YamlConfiguration
				.loadConfiguration(new File(Configs.getMasterPath() + "arrowtrails.yml"));
		config.getKeys(false).forEach(key -> {
			try {
				String name = KUtils.colorCodes(config.getString(key + ".name", ""));
				String particle = config.getString(key + ".particle");
				Material m = XMaterial.quickMatch(config.getString(key + ".material", ""));
				List<String> lore = KUtils.colorCodes(config.getStringList(key + ".description"));
				short data = (short) config.getInt(key + ".data");
				list.put(key, new ArrowTrail(key, name, particle, m, lore, data));
			} catch (Exception e) {
				main.getLogger().log(Level.WARNING, "Can't load " + key + " arrowtrail", e);
			}
		});
	}

	public static ArrowTrail get(String id) {
		return list.get(id);
	}

	public static List<String> getSuggestions() {
		return list.values().stream().map(x -> "ArrowTrailPI$" + x.id + "$0").toList();
	}
	//endregion
}
