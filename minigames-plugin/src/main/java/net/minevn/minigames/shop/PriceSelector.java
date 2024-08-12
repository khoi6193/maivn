package net.minevn.minigames.shop;

import net.minevn.guiapi.GuiInventory;
import net.minevn.guiapi.GuiItemStack;
import net.minevn.minigames.Minigames;
import net.minevn.minigames.Utils;
import net.minevn.minigames.award.PlayerAward;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PriceSelector extends GuiInventory {
	public PriceSelector(ShopItem item, Player buyer, Shop previous) {
		super(9, "§2§lMua vật phẩm: " + item.getItem().getName());
		int slot = 0;
		for (ShopItemPrice price : item.getPrices()) {
			if (slot >= 8)
				break;
			ItemStack is = new ItemStack(price.getUnit().getIcon());
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("§b§l" + item.getItem().getUnit(price.getAmount()));
			//            lores.add("§f﹃ Giá: " + price.getPriceLore(item.getSale()) + " §f﹄");
			List<String> lores = new ArrayList<>(price.getPriceLore(item.getSale()));
			int take = item.getItem().getUnitType() == PlayerAward.ItemUnit.PCS ? price.getAmount() : 1;
			if (item.getStock() >= 0 && item.getStock() < take) {
				lores.add("§cKhông đáp ứng đủ số lượng");
			}
			im.setLore(lores);
			is.setItemMeta(im);
			setItem(slot++, new GuiItemStack(is) {
				@Override
				public void onClick(InventoryClickEvent e) {
					if (item.getStock() >= 0 && item.getStock() < take) {
						return;
					}
					lock();
					Bukkit.getScheduler().runTaskAsynchronously(Minigames.getInstance(), () -> {
						item.buy(buyer, price);
						Utils.runSync(buyer::closeInventory);
					});
				}
			});
		}
		if (previous != null) {
			setItem(8, new GuiItemStack(Material.BARRIER, "§c§lTrở lại") {
				@Override
				public void onClick(InventoryClickEvent e) {
					lock();
					buyer.openInventory(previous.getInventory());
					previous.unlock();
				}
			});
		} else {
			setItem(8, new GuiItemStack(Material.BARRIER, "§c§lThoát") {
				@Override
				public void onClick(InventoryClickEvent e) {
					lock();
					buyer.closeInventory();
				}
			});
		}
		buyer.openInventory(getInventory());
	}
}
