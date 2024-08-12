package net.minevn.minigames.shop;

import com.google.common.collect.ImmutableList;
import net.minevn.guiapi.XMaterial;
import net.minevn.minigames.Configs;
import net.minevn.minigames.Minigames;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;

public class ShopCategory {
//	MAIN("§e§lShop chính", Material.CHEST, Messages.PI_MAIN_DESC, (short) 0),
//	WEAPON("§e§lSkin vũ khí", Material.IRON_SWORD, Messages.PI_WEAPON_DESC, (short) 14),
//	COSTUME("§e§lTrang phục", Material.DIAMOND_CHESTPLATE, Messages.PI_COSTUME_DESC, (short) 0),
//	EFFECT("§e§lHiệu ứng", Material.BLUE_ORCHID, Messages.PI_EFFECT_DESC, (short) 0),
//	ITEM("§e§lVật phẩm", Material.ENDER_PEARL, Messages.PI_ITEM_DESC, (short) 0),
//	TOMB("§e§lBia đá", Material.WOODEN_AXE, Messages.PI_TOMB_DESC, (short) 3),
//	CRATES("§e§lRương báu vật", Material.ENDER_CHEST, Messages.PI_CRATES_DESC, (short) 0);

	private String name;
	private ItemStack icon;

	private ShopCategory(String name, Material icon, short data, List<String> description) {
		this.name = name;
		this.icon = new ItemStack(icon, 1, data);
		ItemMeta im = this.icon.getItemMeta();
		im.setUnbreakable(true);
		im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
		im.setDisplayName(name);
		im.setLore(description);
		this.icon.setItemMeta(im);
	}

	public String getName() {
		return name;
	}

	public ItemStack getGuiIcon() {
		return icon;
	}

	// region static
	private static LinkedHashMap<String, ShopCategory> list = new LinkedHashMap<>();

	@SuppressWarnings("ConstantConditions")
	public static void load() {
		list.clear();
		var main = Minigames.getInstance();
		main.getLogger().info("Shop category...");
		YamlConfiguration config = YamlConfiguration
				.loadConfiguration(new File(Configs.getMasterPath() + "shop-category.yml"));
		config.getKeys(false).forEach(key -> {
			try {
				var section = config.getConfigurationSection(key);
				var name = ChatColor.translateAlternateColorCodes('&', section.getString("name"));
				var iconType = XMaterial.quickMatch(section.getString("icon-type"));
				var iconData = (short) section.getInt("icon-data");
				var description = new ArrayList<String>();
				for (var string : section.getStringList("description")) {
					description.add(ChatColor.translateAlternateColorCodes('&', string));
				}
				list.put(key, new ShopCategory(name, iconType, iconData, description));
			} catch (Exception e) {
				main.getLogger().log(Level.WARNING, "Can't load " + key + " shop category", e);
			}
		});
		main.getLogger().info("Loaded " + list.size() + " shop categories...");
	}

	public static List<ShopCategory> values() {
		return ImmutableList.copyOf(list.values());
	}

	public static ShopCategory get(String id) {
		return list.get(id);
	}
	// endregion
}
