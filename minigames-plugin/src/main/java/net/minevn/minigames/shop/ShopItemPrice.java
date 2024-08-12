package net.minevn.minigames.shop;

import net.minevn.minigames.Messages;
import net.minevn.minigames.Minigames;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ShopItemPrice {
	private int amount, price;
	private CurrencyUnit unit;
	private ShopItem item;

	public ShopItemPrice(String data, ShopItem item) {
		try {
//			Minigames.getInstance().getLogger().info("Price: " + data);
			String[] priceCurrent = data.split("\\|");
			int amount = Integer.parseInt(priceCurrent[0]);
			int price = Integer.parseInt(priceCurrent[1]);
			CurrencyUnit unit = CurrencyUnit.valueOf(priceCurrent[2]);
			if (price <= 0)
				throw new IllegalArgumentException("Giá phải lớn hơn 0 và CurrencyUnit không được null!");
			this.amount = amount;
			this.price = price;
			this.unit = unit;
			this.item = item;
		} catch (Exception e) {
			throw new IllegalArgumentException("Prices không hợp lệ? " + data, e);
		}
	}

	public int getPrice() {
		return price;
	}

	public CurrencyUnit getUnit() {
		return unit;
	}

	public int getAmount() {
		return amount;
	}

	public boolean canAfford(Player p, int sale) {
		int price = this.price;
		if (sale > 0 && sale < 100)
			price = (int) Math.ceil(price * (1 - (sale / 100f)));
		return unit.canAfford(p, price);
	}

	public void pay(Player p, int sale) throws Exception {
//		Main main = Main.getInstance();
		int price = this.price;
		if (sale > 0 && sale < 100)
			price = (int) Math.ceil(price * (1 - (sale / 100f)));
		unit.pay(p, price);
	}

	public String singleString() {
		return amount + "|" + price + "|" + unit.name();
	}

	public List<String> getPriceLore(int sale) {
		List<String> lore = new ArrayList<>();
		if (sale > 0) {
			for (String x : Messages.SHOP_PRICE_SALE_LORE) {
				String replace = x
						.replace("%price%", price + "")
						.replace("%price_type%", unit.getLoweredCaseName() + "")
						.replace("%unit%", item.getItem().getUnit(amount) + "")
						.replace("%color%", unit.colorCode())
						.replace("%price_discount%", (int) Math.ceil(price * (1 - (sale / 100f))) + "")
						.replace("%sale%", sale + "");
				lore.add(replace);
			}
            /*return Arrays.asList("§f﹃ Giá gốc: §7" + price + " " + unit.getLoweredCaseName()
                            + " §b§o(" + item.getItem().getUnit(amount) + ")",
                    "§f §f §f Giảm sốc: " + unit.colorCode() + "§l" + (int) Math.ceil(price * (1 - (sale / 100f)))
                            + " " + unit.getLoweredCaseName() + " §c(-"
                            + sale + "%) §f﹄");*/
		} else {
			for (String x : Messages.SHOP_PRICE_LORE) {
				String replace = x
						.replace("%price%", price + "")
						.replace("%price_type%", unit.getLoweredCaseName() + "")
						.replace("%unit%", item.getItem().getUnit(amount) + "")
						.replace("%color%", unit.colorCode());
				lore.add(replace);
			}
            /*return List.of(unit.colorCode() + "§f﹃ Giá: " + unit.colorcode + "§l" + price + " "
                    + unit.getLoweredCaseName() + " §b§o(" + item.getItem().getUnit(amount) + ")" + " §f﹄");*/
		}
		return lore;
//        return new ArrayList<>();
//        if (sale > 0)
//            return "§f§o§m" + price + " " + unit.getLoweredCaseName() + "§r "
//                    + unit.colorCode() + "§l" + (int) Math.ceil(price * (1 - (sale / 100f)))
//                    + " " + unit.getLoweredCaseName() + " §c(-" + sale + "%)";
//        else
//            return unit.colorCode() + "§l" + price + " "
//                    + unit.getLoweredCaseName();
	}

	public String getCost(int sale) {
		return unit.colorCode() + "§l" + (int) Math.ceil(price * (1 - (sale / 100f)))
				+ " " + unit.getLoweredCaseName();
	}

	enum CurrencyUnit {
		MONEY("MG", Material.GOLD_INGOT, "§a") {
			@Override
			public boolean canAfford(Player p, int amount) {
				return Minigames.getInstance().getEconomy().getBalance(p) >= amount;
			}

			@Override
			public void pay(Player p, int amount) {
				Minigames.getInstance().getEconomy().withdrawPlayer(p, amount);
			}

			@Override
			public boolean isForceLowerCase() {
				return false;
			}

			@Override
			public String getUnAffordMessage() {
				return Messages.ERR_SHOP_NO_MONEY;
			}
		},
		POINTS("xu", Material.DIAMOND, "§e") {
			@Override
			public boolean canAfford(Player p, int amount) {
				return Minigames.getInstance().getPlayerPoints().getAPI().look(p.getUniqueId()) >= amount;
			}

			@Override
			public void pay(Player p, int amount) {
				Minigames.getInstance().getPlayerPoints().getAPI().take(p.getUniqueId(), amount);
			}

			@Override
			public String getUnAffordMessage() {
				return Messages.ERR_SHOP_NO_POINTS;
			}
		};

		private String name, colorcode;
		private Material icon;

		CurrencyUnit(String name, Material icon, String colorcode) {
			this.name = name;
			this.icon = icon;
			this.colorcode = colorcode;
		}

		public String getName() {
			return name;
		}

		public String colorCode() {
			return colorcode;
		}

		public Material getIcon() {
			return icon;
		}

		public boolean isForceLowerCase() {
			return true;
		}

		public String getLoweredCaseName() {
			return isForceLowerCase() ? name.toLowerCase() : name;
		}

		public String getUnAffordMessage() {
			return null;
		}

		public abstract boolean canAfford(Player p, int sale);

		public abstract void pay(Player p, int amount) throws Exception;
	}
}
