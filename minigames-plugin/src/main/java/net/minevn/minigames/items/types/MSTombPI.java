package net.minevn.minigames.items.types;

import net.minefs.MineStrike.Main;
import net.minefs.MineStrike.Skins.Tomb;
import net.minevn.minigames.Messages;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.items.PlayerItem;
import net.minevn.minigames.items.UsablePI;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MSTombPI extends UsablePI {
	private Tomb tomb;

	public MSTombPI(PlayerData owner, int id, Tomb tomb, long expire, long obtained, boolean isUsing) {
		super(Messages.PI_CATEGORY_MSTOMB, owner, id, tomb.getItem().getType(), tomb.getItem().getDurability(),
				expire, obtained, isUsing);
		this.tomb = tomb;
	}

	@Override
	public String getData() {
		return tomb.getId() + "";
	}

	@Override
	public List<String> getDescription() {
		return new ArrayList<>();
	}

	@Override
	public PlayerItem clone() {
		return new MSTombPI(null, -1, tomb, expire, System.currentTimeMillis(), false);
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = super.getItem();
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(tomb.getName());
		List<String> lores = new ArrayList<String>();
		addDescription(lores);
		addExpireDate(lores);
		addUsingStatus(lores);
		im.setLore(lores);
		item.setItemMeta(im);
		return item;
	}

	public Tomb getTomb() {
		return tomb;
	}

	@Override
	public int getShippingCost() {
		return 1;
	}

	// static
	public static MSTombPI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		var tomb = Main.getInstance().getTomb(Integer.parseInt(data));
		if (tomb == null) throw new IllegalArgumentException("tomb does not exist: " + data);
		return new MSTombPI(owner, id, tomb, expire, obtained, isUsing);
	}
}
