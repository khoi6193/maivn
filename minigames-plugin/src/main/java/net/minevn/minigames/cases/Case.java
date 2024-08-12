package net.minevn.minigames.cases;

import net.minevn.guiapi.XMaterial;
import net.minevn.minigames.KUtils;
import net.minevn.minigames.Minigames;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.Utils;
import net.minevn.minigames.award.PlayerAward;
import net.minevn.minigames.award.types.ItemAW;
import net.minevn.minigames.items.types.CaseKeyPI;
import net.minevn.minigames.items.types.CasePI;
import net.minevn.minigames.shop.PriceSelector;
import net.minevn.minigames.shop.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Case {
	private String id;
	private String name;
	private ItemStack icon;
	private ItemStack keyIcon;
	private CaseItemList itemList;
	private ShopItem keyShop = null;
	private Minigames main;

	public Case(String id, String name, CaseItemList itemList, String prices, ItemStack icon, ItemStack keyIcon) {
		this.id = id;
		this.itemList = itemList;
		this.icon = icon;
		this.keyIcon = keyIcon;
		this.name = name;
		main = Minigames.getInstance();
		if (prices != null) {
			try {
				var keyItem = new CaseKeyPI(null, -1, this, 0, System.currentTimeMillis());
				keyShop = new ShopItem(-1, new ItemAW(keyItem, 1), prices);
			} catch (Exception e) {
				Minigames.getInstance().getLogger().log(Level.SEVERE, "Case " + name + " have invalid price", e);
			}
		}
	}

	public ItemStack getCaseIcon() {
		return icon.clone();
	}

	public ItemStack getKeyIcon() {
		return keyIcon.clone();
	}

	public String getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<SingleItem> getPreviewableItems() {
		return itemList.getItems().stream().filter(SingleItem::isPreview).collect(Collectors.toList());
	}

	public SingleItem getRandomItem() {
		return itemList.getRandomItem();
	}

	public CaseKeyPI getAvailableKey(Player player) {
		var data = PlayerData.getData(player);
		return data == null ? null : data.getItem(CaseKeyPI.class, id);
	}

	public List<CaseKeyPI> getAvailableKeys(Player player) {
		var data = PlayerData.getData(player);
		return data == null ? null : data.getItems(CaseKeyPI.class, id);
	}

	public CasePI getAvailableCase(Player player) {
		var data = PlayerData.getData(player);
		return data == null ? null : data.getItem(CasePI.class, id);
	}

	public void openKeyShop(Player viewer) {
		new PriceSelector(keyShop, viewer, null);
	}

	@SuppressWarnings("ConstantConditions")
	public void reLoad(CommandSender sender, String root) {
		File f = new File(root + "/cases/" + id + ".yml");
		if (!f.exists()) {
			sender.sendMessage("§cFile " + f.getAbsolutePath() + " khong con ton tai");
			return;
		}

		List<String> opening = new ArrayList<>();

		for (Player p : Bukkit.getOnlinePlayers()) {
			InventoryHolder h;
			if ((h = p.getOpenInventory().getTopInventory().getHolder()) instanceof CaseGUI
					&& ((CaseGUI) h).getCurrentCase() == this) {
				opening.add(p.getName());
			}
		}

		YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
		String name = KUtils.colorCodes(config.getString("name"));

		// load items
		List<SingleItem> items = new ArrayList<>();
		for (String itemID : config.getConfigurationSection("items").getKeys(false)) {
			try {
				String itemKey = "items." + itemID + ".";
				var item = PlayerAward.fromData(config.getString(itemKey + "type"),
						config.getString(itemKey + "data"));
				if (item == null) {
					Minigames.getInstance().getLogger().warning("Case " + f.getName() + ", item " + itemID
							+ " không hợp lệ");
					continue;
				}
				int rate = config.getInt(itemKey + "rate");
				boolean preview = config.getBoolean(itemKey + "preview");
				boolean announce = config.getBoolean(itemKey + "announce");
				items.add(new SingleItem(item, rate, preview, announce));
			} catch (Exception e) {
				Minigames.getInstance().getLogger().log(Level.WARNING, "Có lỗi xảy ra khi load item "
						+ itemID + " trong case " + f.getName(), e);
			}
		}

		// case icon
		List<String> caseLores = config.getStringList("icon.case.lores");
		short caseData = (short) config.getInt("icon.case.data");
		caseLores = caseLores.stream().map(x -> KUtils.colorCodes(x.replace("%name%", name)))
				.collect(Collectors.toList());
		if (config.getBoolean("icon.case.showlistitems")) {
			caseLores.addAll(0, items.stream().filter(SingleItem::isPreview)
					.map(x -> x.getItem().getName()).toList());
		}
		ItemStack caseItem = Utils.createItemStack(XMaterial.quickMatch(config.getString("icon.case.material")),
				caseData, name, caseLores);
		Utils.makeUnbreakable(caseItem);

		// key icon
		List<String> keyLores = config.getStringList("icon.key.lores");
		short keyData = (short) config.getInt("icon.key.data");
		keyLores = keyLores.stream().map(x -> KUtils.colorCodes(x.replace("%name%", name)))
				.collect(Collectors.toList());
		if (config.getBoolean("icon.key.showlistitems")) {
			keyLores.addAll(0, items.stream().filter(SingleItem::isPreview).map(x -> x.getItem().getName()).toList());
		}
		ItemStack keyItem = Utils.createItemStack(XMaterial.quickMatch(config.getString("icon.key.material")),
				keyData, "§eChìa khoá: " + name, keyLores);
		Utils.makeUnbreakable(keyItem);

		// prices
		String prices = config.getString("keyprices");

		this.itemList = new CaseItemList(items);
		this.icon = caseItem;
		this.keyIcon = keyItem;
		this.name = name;
		sender.sendMessage("§a" + id + ": Reload thanh cong.");
		if (!opening.isEmpty()) {
			String openingStr = String.join(", ", opening);
			sender.sendMessage("§6Nhung nguoi dang mo case " + id + ": " + openingStr);
		}
	}

	// statics
	private static Map<String, Case> _cases;

	public static void loadCases(String root) {
		_cases = new HashMap<>();
		File folder = new File(root + "/cases");
		Minigames.getInstance().getLogger().info("Loading cases from folder " + folder.getAbsolutePath());
		loadCasesFromFolder(folder);
		Minigames.getInstance().getLogger().info("§dLoaded " + _cases.keySet().size() + " cases");
	}

	@SuppressWarnings("ConstantConditions")
	private static void loadCasesFromFolder(File folder) {
		if (!folder.isDirectory()) {
			return;
		}
		//noinspection ConstantConditions
		for (File f : folder.listFiles()) {
			try {
				if (f.isDirectory()) {
					loadCasesFromFolder(f);
					continue;
				}
				YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
				String id = f.getName().replace(".yml", "");
				String name = KUtils.colorCodes(config.getString("name"));
				List<SingleItem> items = new ArrayList<>();

				// load items
				for (String itemID : config.getConfigurationSection("items").getKeys(false)) {
					try {
						String itemKey = "items." + itemID + ".";
						PlayerAward item = PlayerAward.fromData(config.getString(itemKey + "type"),
								config.getString(itemKey + "data"));
						if (item == null) {
							Minigames.getInstance().getLogger().warning("Case " + f.getName() + ", item " + itemID
									+ " không hợp lệ");
							continue;
						}
						int rate = config.getInt(itemKey + "rate");
						boolean preview = config.getBoolean(itemKey + "preview");
						boolean announce = config.getBoolean(itemKey + "announce");
						items.add(new SingleItem(item, rate, preview, announce));
					} catch (Exception e) {
						Minigames.getInstance().getLogger().log(Level.WARNING, "Có lỗi xảy ra khi load item "
								+ itemID + " trong case " + f.getName(), e);
					}
				}

				// case icon
				List<String> caseLores = config.getStringList("icon.case.lores");
				short caseData = (short) config.getInt("icon.case.data");
				if (!caseLores.isEmpty()) {
					caseLores = caseLores.stream().map(x -> KUtils.colorCodes(x).replace("%name%", name))
							.collect(Collectors.toList());
				}
				if (config.getBoolean("icon.case.showlistitems")) {
					caseLores.addAll(0, items.stream().filter(SingleItem::isPreview)
							.map(x -> x.getItem().getName()).toList());
				}
				var caseM = XMaterial.quickMatch(config.getString("icon.case.material"));
				ItemStack caseItem = Utils.createItemStack(caseM, caseData, name, caseLores);
				Utils.makeUnbreakable(caseItem);

				// key icon
				List<String> keyLores = config.getStringList("icon.key.lores");
				short keyData = (short) config.getInt("icon.key.data");
				keyLores = keyLores.stream().map(x -> KUtils.colorCodes(x.replace("%name%", name)))
						.collect(Collectors.toList());
				if (config.getBoolean("icon.key.showlistitems")) {
					keyLores.addAll(0, items.stream().filter(SingleItem::isPreview)
							.map(x -> x.getItem().getName()).toList());
				}
				var keyM = XMaterial.quickMatch(config.getString("icon.key.material"));
				ItemStack keyItem = Utils.createItemStack(keyM, keyData, "§eChìa khoá: " + name, keyLores);
				Utils.makeUnbreakable(keyItem);

				// prices
				String prices = config.getString("keyprices");

				// init...
				_cases.put(id, new Case(id, name, new CaseItemList(items), prices, caseItem, keyItem));
			} catch (Exception e) {
				Minigames.getInstance().getLogger().log(Level.WARNING, "Có lỗi xảy ra khi load case "
						+ f.getName(), e);
			}
		}
	}

	public static void reloadAll(CommandSender sender, String root) {
		for (Case _case : _cases.values()) {
			_case.reLoad(sender, root);
		}
	}

	public static Case get(String id) {
		return _cases.get(id);
	}

	public static List<String> getCaseSuggestions() {
		return _cases.values().stream().map(x -> "CasePI$" + x.id + "$1").toList();
	}

	public static List<String> getKeySuggestions() {
		return _cases.values().stream().map(x -> "CaseKeyPI$" + x.id + "$1").toList();
	}

	public static String[] getBundleSuggestions() {
		return _cases.values().stream().map(x -> x.id + "$1").toArray(String[]::new);
	}
}
