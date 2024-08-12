package net.minevn.minigames.gadgets;

import net.minevn.minigames.Configs;
import net.minevn.minigames.KUtils;
import net.minevn.minigames.Minigames;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class Hat {
	private String id;
	private String name;
	private Material type;
	private List<String> description;
	private short data;

	public Hat(String id, String name, Material type, List<String> description, short data) {
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

	public void sendPacket(Player player) {
		var helmet = player.getInventory().getHelmet();
		if (helmet == null || helmet.getType() == Material.AIR) {
			helmet = getItem();
		}
		Minigames.nms.sendFakeItem(player, 5, helmet);
	}

	//region static
	private static Map<String, Hat> list = new HashMap<>();

	public static Hat get(String id) {
		return list.get(id);
	}

	public static void load() {
		Minigames main = Minigames.getInstance();
		main.getLogger().info("Hats...");
		YamlConfiguration config = YamlConfiguration
				.loadConfiguration(new File(Configs.getMasterPath() + "hats.yml"));
		config.getKeys(false).forEach(key -> {
			try {
				String name = KUtils.colorCodes(config.getString(key + ".name", ""));
//                Material m = XMaterial.matchXMaterial(config.getString(key + ".type")).get().parseMaterial();
				short data = (short) config.getInt(key + ".data");
				List<String> lore = KUtils.colorCodes(config.getStringList(key + ".description"));
				list.put(key, new Hat(key, name, Material.IRON_AXE, lore, data));
			} catch (Exception e) {
				main.getLogger().log(Level.WARNING, "Can't load " + key + " hat", e);
			}
		});
	}

	public static List<String> getSuggestions() {
		return list.values().stream().map(x -> "HatPI$" + x.id + "$0").toList();
	}
	//endregion
}
