package net.minevn.minigames.gadgets;

import net.minevn.guiapi.XMaterial;
import net.minevn.minigames.Configs;
import net.minevn.minigames.KUtils;
import net.minevn.minigames.Minigames;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

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

	@SuppressWarnings("ConstantConditions")
	public static void load() {
		list.clear();
		Minigames main = Minigames.getInstance();
		main.getLogger().info("Hit sounds...");
		YamlConfiguration config = YamlConfiguration
				.loadConfiguration(new File(Configs.getMasterPath() + "hitsounds.yml"));
		config.getKeys(false).forEach(key -> {
			try {
				var section = config.getConfigurationSection(key);
				String name = KUtils.colorCodes(section.getString("name", ""));
				String sound = section.getString("sound");
				Material m = XMaterial.quickMatch(section.getString("material"));
				List<String> lore = KUtils.colorCodes(config.getStringList(key + ".description"));
				short data = (short) section.getInt("data");
				list.put(key, new HitSound(key, name, sound, m, lore, data));
			} catch (Exception e) {
				main.getLogger().log(Level.WARNING, "Can't load " + key + " hitsound", e);
			}
		});
		main.getLogger().info("Loaded " + list.size() + " hitsound(s)");
	}

	public static HitSound get(String id) {
		return list.get(id);
	}

	public static List<String> getSuggestions() {
		return list.values().stream().map(x -> "HitSoundPI$" + x.id + "$0").toList();
	}
	//endregion
}
