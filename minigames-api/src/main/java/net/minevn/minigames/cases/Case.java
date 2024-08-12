package net.minevn.minigames.cases;

import net.minevn.minigames.PlayerData;
import net.minevn.minigames.items.types.CaseKeyPI;
import net.minevn.minigames.items.types.CasePI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Case {
	private String id;
	private String name;
	private ItemStack icon;
	private ItemStack keyIcon;
	private CaseItemList itemList;

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
		return data == null ? null : new CaseKeyPI(null, 0, null, 0, 0);
	}

	public List<CaseKeyPI> getAvailableKeys(Player player) {
		var data = PlayerData.getData(player);
		return data == null ? null : new ArrayList<>();
	}

	public CasePI getAvailableCase(Player player) {
		var data = PlayerData.getData(player);
		return data == null ? null : new CasePI(null, 0, null, 0, 0);
	}

	public void openKeyShop(Player viewer) {

	}

	@SuppressWarnings("ConstantConditions")
	public void reLoad(CommandSender sender, String root) {
	}

	// statics
	private static Map<String, Case> _cases;

	public static void loadCases(String root) {
	}

	@SuppressWarnings("ConstantConditions")
	private static void loadCasesFromFolder(File folder) {
	}

	public static void reloadAll(CommandSender sender, String root) {
		for (Case _case : _cases.values()) {
			_case.reLoad(sender, root);
		}
	}

	public static Case get(String id) {
		return _cases.get(id);
	}
}
