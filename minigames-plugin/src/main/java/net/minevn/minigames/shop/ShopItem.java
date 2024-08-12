package net.minevn.minigames.shop;

import net.minevn.minigames.Minigames;
import net.minevn.minigames.MySQL;
import net.minevn.minigames.award.PlayerAward;
import net.minevn.mmclient.MatchMakerClient;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public class ShopItem {
	private int id;
	private PlayerAward item;
	private List<ShopItemPrice> prices;
	private int requiredLevel = 0;
	private long createdTime = 0;
	private boolean isActivated;
	private List<ShopCategory> categories;
	int sale = 0;
	int stock = -1;
	private static final ReentrantLock locker = new ReentrantLock();

	/**
	 * MSItem, prices from string
	 *
	 * @param prices amount|price|currentcyunit cách nhau bởi dấu phẩy, vd: 1|10000|MONEY,1|69|POINTS
	 */
	public ShopItem(int id, PlayerAward item, String prices) {
		this.id = id;
		this.item = item;
		this.prices = new ArrayList<>();
		String[] pricesString = prices.split(",");
		for (String s : pricesString) {
			try {
				this.prices.add(new ShopItemPrice(s, this));
			} catch (Exception e) {
				Minigames.getInstance().getLogger().log(Level.SEVERE, "Không thể tạo giá cho item #" + id, e);
			}
		}
//        locker = new ReentrantLock();
	}

	public int getID() {
		return id;
	}

	public int getRequiredLevel() {
		return requiredLevel;
	}

	public void setRequiredLevel(int level) {
		requiredLevel = level;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}

//    public String getPricesString() {
//        String prices = "";
//        for (ShopItemPrice price : this.prices)
//            prices += price + ",";
//        prices.replaceAll(",$", "");
//        return prices;
//    }

	public ItemStack getGuiIcon() {
		return this.item.getGuiItem();
	}

	public PlayerAward getItem() {
		return item;
	}

	public ShopItemPrice firstPrice() {
		if (!prices.isEmpty())
			return prices.get(0);
		return null;
	}

	public List<ShopItemPrice> getPrices() {
		return prices;
	}

	public List<ShopCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<ShopCategory> categories) {
		this.categories = categories;
	}

	public int getSale() {
		return sale;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public void setSale(int sale) {
		this.sale = sale;
	}

	public boolean buy(Player p, ShopItemPrice price) {
		Minigames main = Minigames.getInstance();
		var sql = MySQL.getInstance();
		try {
			locker.lock();
			if (!prices.contains(price)) {
				p.sendTitle("§cCó lỗi xảy ra", "§eVui lòng thử lại sau", 5, 60, 5);
				return false;
			}
			if (!price.canAfford(p, sale)) {
				p.sendTitle("§f", "§c§lBạn không có đủ tiền", 5, 60, 5);
				var err = price.getUnit().getUnAffordMessage();
				if (err != null) MatchMakerClient.getInstance().sendMessage(p, err);
				return false;
			}

			// stock check
			int stock = id == -1 ? -1 : sql.getShopStock(id);
			if (stock >= 0) {
				int stockAmount = 1;
				if (item.getUnitType() == PlayerAward.ItemUnit.PCS) {
					stockAmount = price.getAmount();
				}
				if (stock < stockAmount) {
					p.sendTitle("§f", "§c§lVật phẩm này đã hết hàng", 5, 60, 5);
					return false;
				}

				// stock update
				sql.removeShopStock(id, stockAmount);

				// stock refresh
				int newStock = sql.getShopStock(id);
				Bukkit.getScheduler().runTask(Minigames.getInstance(), () -> {
					for (Player other : Bukkit.getOnlinePlayers()) {
						InventoryHolder h = other.getOpenInventory().getTopInventory().getHolder();
						if (h == null || !(h instanceof Shop)) continue;
						((Shop) h).stockUpdate(id, newStock);
					}
				});
			}

			price.pay(p, sale);
			item.setAmount(price.getAmount());
			item.apply(p, "Bought from shop");
			p.sendTitle("§a§lMua thành công", "§b§lThành tiền: " + price.getCost(sale), 5, 60, 5);
			return true;
		} catch (Exception e) {
			main.getLogger().log(Level.SEVERE, "Có lỗi khi " + p.getName() + " mua item #" + id, e);
			p.sendTitle("§cCó lỗi xảy ra", "§eVui lòng thử lại sau", 5, 60, 5);
			p.sendMessage(
					"§c§lCó lỗi xảy ra khi thực hiện giao dịch. Vui lòng chụp màn hình lại và gửi cho Admin,"
							+ " kèm thời gian chính xác xuất hiện lỗi này.");
			return false;
		} finally {
			locker.unlock();
		}
	}
}
