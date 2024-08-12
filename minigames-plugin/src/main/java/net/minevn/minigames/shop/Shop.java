package net.minevn.minigames.shop;

import net.minevn.guiapi.GuiInventory;
import net.minevn.guiapi.GuiItemStack;
import net.minevn.minigames.Messages;
import net.minevn.minigames.Minigames;
import net.minevn.minigames.MySQL;
import net.minevn.minigames.award.PlayerAward;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Shop extends GuiInventory {
	private ShopCategory category;
	private LinkedHashMap<Integer, ShopItem> items;
	private int currentPage = 0;
	private Player viewer;
//	private static List<ShopItem> _items;

	public Shop(ShopCategory category, Player viewer) {
		super(54, category == null ? "§2§lCửa hàng" : category.getName());
		var sql = MySQL.getInstance();
		Minigames main = Minigames.getInstance();
		BukkitScheduler scheduler = Bukkit.getScheduler();
		this.viewer = viewer;
		this.category = category;
		scheduler.runTaskAsynchronously(main, () -> {
			items = sql.shopGetItems();
			if (category != null) {
				items = items.values().stream().filter(item -> item.isActivated() && item.getCategories().contains(category))
					.collect(Collectors.toMap(
						ShopItem::getID,
						x -> x,
						(u, v) -> {
							throw new IllegalStateException(String.format("Duplicate key %s", u));
						},
						LinkedHashMap::new
					));
			} else {
				items = items.values().stream().filter(ShopItem::isActivated)
					.collect(Collectors.toMap(
						ShopItem::getID,
						x -> x,
						(u, v) -> {
							throw new IllegalStateException(String.format("Duplicate key %s", u));
						},
						LinkedHashMap::new
					));
			}
			renderGUI(false);
			scheduler.runTask(main, () -> viewer.openInventory(getInventory()));
		});
	}

	public void renderGUI(boolean update) {
		clear();
		int slot = 0;

		// Items
		List<ShopItem> items = new ArrayList<>(this.items.values());
		for (int i = 45 * currentPage; i < i + 45 && i < items.size(); i++) {
			ShopItem shopItem = items.get(i);
			PlayerAward msitem = shopItem.getItem();
			ItemStack item = shopItem.getGuiIcon();
			ItemMeta im = item.getItemMeta();
			List<String> lore = new ArrayList<>();
			lore.add("§3" + msitem.typeName());
			ShopItemPrice sip = shopItem.firstPrice();
			if (sip == null) throw new IllegalArgumentException("Không có giá: item " + shopItem.getID());
			if (im.hasLore()) {
				lore.add("§r");
				lore.addAll(im.getLore());
			}
			lore.addAll(sip.getPriceLore(shopItem.getSale()));
			if (shopItem.getStock() >= 0) {
				lore.add("§f");
				int min = shopItem.getItem().getUnitType() == PlayerAward.ItemUnit.PCS ? sip.getAmount() : 1;
				if (shopItem.getStock() > min) {
					lore.add(Messages.SHOP_ITEM_LIMIT);
				} else if (shopItem.getStock() == min) {
					lore.add(Messages.SHOP_ITEM_LAST);
				} else {
					lore.add(Messages.SHOP_OUT_OF_STOCK);
				}
			}
			lore.add("§f");
			if (msitem.isPreviewable()) lore.add(Messages.SHOP_CLICK_TO_PREVIEW);
			lore.add(Messages.SHOP_CLICK_TO_BUY);
			im.setLore(lore);
			im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
			item.setItemMeta(im);
			setItem(slot++, new GuiItemStack(item) {
				@Override
				public void onClick(InventoryClickEvent e) {
					if (e.isLeftClick()) {
						if (shopItem.getStock() == 0) {
							viewer.sendTitle("§f", "§c§lVật phẩm này đã hết hàng", 5, 60, 5);
							viewer.closeInventory();
						} else {
							buy(shopItem);
						}
					}
					if (msitem.isPreviewable() && e.isRightClick()) msitem.preview(viewer);
				}
			});
		}

		// Nút tới & lùi trang
		ItemMeta im;
		var type = (currentPage + 1) * 45 < items.size()
			? Material.LIME_STAINED_GLASS_PANE
			: Material.GRAY_STAINED_GLASS_PANE;
		ItemStack next = new ItemStack(type, 1);
		im = next.getItemMeta();
		im.setDisplayName("§a§lTrang kế >>");
		next.setItemMeta(im);
		setItem(53, new GuiItemStack(next) {
			@Override
			public void onClick(InventoryClickEvent e) {
				nextPage();
			}
		});

		ItemStack prev = new ItemStack(currentPage > 0 ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE, 1);
		im = prev.getItemMeta();
		im.setDisplayName("§a§l<< Trang trước");
		prev.setItemMeta(im);
		setItem(45, new GuiItemStack(prev) {
			@Override
			public void onClick(InventoryClickEvent e) {
				previousPage();
			}
		});

		// Danh mục shop
		var cates = ShopCategory.values();
		for (int i = 0; i < cates.size() && i < 8; i++) {
			ShopCategory cate = cates.get(i);
			setItem(46 + i, new GuiItemStack(cate.getGuiIcon()) {
				@Override
				public void onClick(InventoryClickEvent e) {
					lock();
					new Shop(cate, viewer);
				}
			});
		}

		// Send packets
		if (update)
			viewer.updateInventory();
	}

	public void nextPage() {
		if ((currentPage + 1) * 45 < items.size()) {
			currentPage++;
			renderGUI(true);
		}
	}

	public void previousPage() {
		if (currentPage > 0) {
			currentPage--;
			renderGUI(true);
		}
	}

	public void buy(ShopItem item) {
		if (!items.containsValue(item))
			return;
		lock();
		new PriceSelector(item, viewer, this);
	}

	public void stockUpdate(int shopItemID, int stock) {
		ShopItem si = items.get(shopItemID);
		if (si == null)
			return;
		si.setStock(stock);
		renderGUI(true);
	}
}
