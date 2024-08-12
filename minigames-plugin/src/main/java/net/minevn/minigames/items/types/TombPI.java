package net.minevn.minigames.items.types;

import net.minevn.minigames.Messages;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.gadgets.Tomb;
import net.minevn.minigames.items.PlayerItem;
import net.minevn.minigames.items.UsablePI;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TombPI extends UsablePI {
	private Tomb tomb;

	public TombPI(PlayerData owner, int id, Tomb tomb, long expire, long obtained, boolean isUsing) {
		super(Messages.PI_CATEGORY_TOMB, owner, id, tomb.getType(), tomb.getData(), expire, obtained, isUsing);
		this.tomb = tomb;
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = super.getItem();
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(tomb.getName());
		List<String> lores = new ArrayList<>(getDescription());
		lores.add("§f");
		if (getExpire() > 0) {
			lores.add(Messages.PI_EXPIRE_DATE.replace("%date%", getExpireDate()));
			lores.add("§f");
		}
		lores.add(isUsing() ? Messages.PI_CLICK_TO_UNUSE : Messages.PI_CLICK_TO_USE);
		im.setLore(lores);
		item.setItemMeta(im);
		return item;
	}

	@Override
	public String getData() {
		return tomb.getID();
	}

	@Override
	public List<String> getDescription() {
		return tomb.getDescription();
	}

	@Override
	public PlayerItem clone() {
		return new TombPI(null, -1, tomb, expire, System.currentTimeMillis(), false);
	}

	public Tomb getTomb() {
		return tomb;
	}

	// static
	public static TombPI fromData(
		PlayerData owner,
		String data,
		int id, long expire, long obtained, boolean isUsing, int amount
	) {
		var tomb = Tomb.get(data);
		if (tomb == null) throw new IllegalArgumentException("tomb " + data + " khong ton tai");
		return new TombPI(owner, id, tomb, expire, obtained, isUsing);
	}
}
