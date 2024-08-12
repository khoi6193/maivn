package net.minevn.minigames.items.types;

import net.minefs.MineStrike.Main;
import net.minefs.MineStrike.Skins.Knife;
import net.minevn.minigames.Messages;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.Utils;
import net.minevn.minigames.items.PlayerItem;
import net.minevn.minigames.items.UsablePI;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MSKnifePI extends UsablePI {
	private Knife knife;

	public MSKnifePI(PlayerData owner, int id, Knife knife, long expire, long obtained, boolean isUsing) {
		super(Messages.PI_CATEGORY_MSKNIFE, owner, id, knife.getItem().getType(), knife.getItem().getDurability(),
				expire, obtained, isUsing);
		this.knife = knife;
	}

	@Override
	public String getData() {
		return knife.getID() + "";
	}

	@Override
	public List<String> getDescription() {
		return new ArrayList<>();
	}

	@Override
	public PlayerItem clone() {
		return new MSKnifePI(null, -1, knife, expire, System.currentTimeMillis(), false);
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = super.getItem();
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(knife.getName() + " §f" + knife.getSymbol());
		List<String> lores = new ArrayList<>();
		addDescription(lores);
		addExpireDate(lores);
		addUsingStatus(lores);
		im.setLore(lores);
		item.setItemMeta(im);
		return item;
	}

	public Knife getKnife() {
		return knife;
	}

	@Override
	public int getShippingCost() {
		return Utils.getMineStrikeItemPrice(knife.getName());
	}

	// static
	public static MSKnifePI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		var knife = Main.getInstance().getKnife(Integer.parseInt(data));
		if (knife == null) throw new IllegalArgumentException("knife does not exist: " + data);
		return new MSKnifePI(owner, id, knife, expire, obtained, isUsing);
	}
}
