package net.minevn.minigames.commands;

import dev.jorel.commandapi.CommandAPICommand;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.gui.InventoryGui;
import net.minevn.minigames.shop.Shop;
import net.minevn.minigames.shop.ShopCategory;

public class MainCmd {
	public static void init() {
		// open shop
		new CommandAPICommand("shop")
			.withAliases("cuahang")
			.executesPlayer((p, a) -> {
				new Shop(ShopCategory.get("MAIN"), p);
			})
			.register();

		// open inventory
		new CommandAPICommand("khodo")
			.withAliases("inv", "balo")
			.executesPlayer((p, a) -> {
				var data = PlayerData.getData(p);
				if (data != null) new InventoryGui(data);
			})
			.register();
	}
}
