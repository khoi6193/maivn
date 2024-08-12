package net.minevn.minigames.gadgets;

import net.minevn.guiapi.XMaterial;
import net.minevn.minigames.Configs;
import net.minevn.minigames.KUtils;
import net.minevn.minigames.Minigames;
import net.minevn.minigames.Utils;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class MVPAnthem {
	private String id, name, sound;
	private List<String> description;
	private Material m;
	private short data;

	public MVPAnthem(String id, String name, List<String> description, String sound, Material m, short data) {
		this.id = id;
		this.name = name;
		this.sound = sound;
		this.description = description;
		this.m = m;
		this.data = data;
	}

	public String getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSound() {
		return sound;
	}

	public Material getMaterial() {
		return m;
	}

	public short getData() {
		return data;
	}

	public void preview(Player p) {
		p.stopSound("minevn.musics.lobby", SoundCategory.RECORDS);
		p.playSound(p.getLocation(), sound, 100, 1);
	}

	public void play(Player p) {
		var world = p.getWorld();
		world.playSound(p.getLocation(), sound, 1000000000, 1);
		Utils.runLater(() -> world.getPlayers().forEach(pl ->
				pl.sendTitle("§aMVP: §e" + p.getName(), getName(), 5, 150, 5)), 10);
	}

	public List<String> getDescription() {
		return description;
	}

	//region static
	private static Map<String, MVPAnthem> list = new HashMap<>();

	@SuppressWarnings("ConstantConditions")
	public static void load() {
		list.clear();
		Minigames main = Minigames.getInstance();
		main.getLogger().info("MVP anthems...");
		YamlConfiguration config = YamlConfiguration
				.loadConfiguration(new File(Configs.getMasterPath() + "mvpanthems.yml"));
		config.getKeys(false).forEach(key -> {
			try {
				var section = config.getConfigurationSection(key);
				String name = KUtils.colorCodes(section.getString("name", ""));
				String sound = section.getString("music");
				Material m = XMaterial.quickMatch(section.getString("material"));
				List<String> lore = KUtils.colorCodes(config.getStringList(key + ".description"));
				short data = (short) section.getInt("data");
				list.put(key, new MVPAnthem(key, name, lore, sound, m, data));
			} catch (Exception e) {
				main.getLogger().log(Level.WARNING, "Can't load " + key + " MVP anthem", e);
			}
		});
		main.getLogger().info("Loaded " + list.size() + " MVP anthem(s)");
	}

	public static MVPAnthem get(String id) {
		return list.get(id);
	}

	public static List<String> getSuggestions() {
		return list.values().stream().map(x -> "MVPAnthemPI$" + x.id + "$0").toList();
	}
	//endregion
}
