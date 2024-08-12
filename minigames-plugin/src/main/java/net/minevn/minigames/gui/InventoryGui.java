package net.minevn.minigames.gui;

import net.minevn.guiapi.GuiInventory;
import net.minevn.guiapi.GuiItemStack;
import net.minevn.minigames.Messages;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.Utils;
import net.minevn.minigames.items.DraggablePI;
import net.minevn.minigames.items.PlayerItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;

public class InventoryGui extends GuiInventory {
	private static final int pagesize = 45;
	private final PlayerData data;
	private final Player viewer;
	private int page = 0;
	private List<PlayerItem> items;
	private DraggablePI cursor;
	private InventoryCategory currentCategory = null;

	public InventoryGui(PlayerData data) {
		super(54, Messages.GUI_TITLE_INVENTORY);
		this.data = data;
		viewer = data.getPlayer();
		if (!viewer.isOnline()) return;
		buildAsync();
	}

	public void build() {
		lock();
		getItems();
		int cate = 0;
		List<InventoryCategory> categories;
		if (currentCategory != null) {
			categories = currentCategory.getChildrens();
			if (categories.isEmpty()) {
				// lay cate dong cap neu ko co cate con
				categories = currentCategory.getSiblings();
			}
		} else {
			categories = InventoryCategory.getRootCategories();
		}
		for (int slot = 45; slot <= 52; slot++) {
			if (slot == 45 && currentCategory != null) {
				setItem(slot, new GuiItemStack(Material.BARRIER, Messages.GUI_BTN_BACK).onClick(e -> {
					changeCategory(null);
				}));
				continue;
			}
			if (cate >= categories.size()) {
				setItem(slot, new GuiItemStack(Material.BLACK_STAINED_GLASS_PANE));
				continue;
			}
			var icon = categories.get(cate++);
			setItem(
				slot, new GuiItemStack(
					icon.getMaterial(), icon.getData(), 1, icon.getName(), icon.getDescription()
				).onClick(e -> changeCategory(icon))
			);
		}

		// pagination
		if (page > 0) {
			setItem(
				52, new GuiItemStack(Material.LIME_STAINED_GLASS_PANE, Messages.GUI_BTN_PREV_PAGE).onClick(e -> {
					page--;
					buildAsync();
				})
			);
		} else setItem(52, new GuiItemStack(Material.BLACK_STAINED_GLASS_PANE));
		if (pagesize * (page + 1) < items.size()) {
			setItem(
				53, new GuiItemStack(Material.LIME_STAINED_GLASS_PANE, Messages.GUI_BTN_NEXT_PAGE).onClick(e -> {
					page++;
					buildAsync();
				})
			);
		} else setItem(53, new GuiItemStack(Material.BLACK_STAINED_GLASS_PANE));

		// items
		int i = 0;

		for (int slot = 0; slot < pagesize; slot++) {
			var index = pagesize * page + slot;
			if (index >= items.size()) {
				setItem(slot, null);
				continue;
			}
			var item = items.get(index);
			var is = item.getItem();
			setItem(slot, new GuiItemStack(is).onClick(e -> {
				if (cursor != null) {
					cursor.drop(item, e);
					cursor = null;
					viewer.setItemOnCursor(null);
					return;
				}
				if (item instanceof DraggablePI draggable) {
					cursor = draggable;
					viewer.setItemOnCursor(is);
					draggable.onCursorPick(this);
					return;
				}
				item.onClick(e);
			}));
		}

		unlock();

		// open to player
		if (!isViewing(viewer)) {
			Utils.runSync(() -> {
				viewer.openInventory(getInventory());
				viewer.playSound(viewer.getEyeLocation(), Sound.BLOCK_CHEST_OPEN, 1f, 1f);
			});
		}
	}

	private void buildAsync() {
		lock();
		Utils.runNotSync(this::build);
	}

	public void changeCategory(InventoryCategory category) {
		currentCategory = category;
		page = 0;
		buildAsync();
	}

	@Override
	public void onClose(InventoryCloseEvent e) {
		viewer.setItemOnCursor(null);
		viewer.playSound(viewer.getEyeLocation(), Sound.BLOCK_CHEST_CLOSE, 1f, 1f);
	}

	private void getItems() {
		if (currentCategory == null) {
			items = new ArrayList<>(data.getItems());
			return;
		}
		String regex = currentCategory.getRegex();
		items = new ArrayList<>();
		for (PlayerItem item : data.getItems()) {
			if (item.getCategoryRegex().matches(regex)) {
				items.add(item);
			}
		}
	}
}
