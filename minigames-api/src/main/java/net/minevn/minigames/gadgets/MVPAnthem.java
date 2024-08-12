package net.minevn.minigames.gadgets;

import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		p.stopSound("cs.musics.lobby", SoundCategory.RECORDS);
		p.stopSound("musics.lobby", SoundCategory.RECORDS);
		p.playSound(p.getLocation(), sound, 100, 1);
	}

	public List<String> getDescription() {
		return description;
	}

	//region static
	private static Map<String, MVPAnthem> list = new HashMap<>();

	public static void load() {

	}

	public static MVPAnthem get(String id) {
		return list.get(id);
	}
	//endregion
}
