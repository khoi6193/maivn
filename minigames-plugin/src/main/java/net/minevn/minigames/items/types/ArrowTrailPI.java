package net.minevn.minigames.items.types;

import net.minevn.minigames.Messages;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.gadgets.ArrowTrail;
import net.minevn.minigames.items.PlayerItem;
import net.minevn.minigames.items.UsablePI;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ArrowTrailPI extends UsablePI {

	private final ArrowTrail at;

	public ArrowTrailPI(PlayerData owner, int id, ArrowTrail at, long expire, long obtained, boolean isUsing) {
		super(Messages.PI_CATEGORY_TRAIL, owner, id, at.getMaterial(), at.getData(), expire, obtained, isUsing);
		this.at = at;
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = super.getItem();
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(at.getName());
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
		return at.getID();
	}

	@Override
	public List<String> getDescription() {
		return at.getDescription();
	}

	@Override
	public PlayerItem clone() {
		return new ArrowTrailPI(null, -1, at, expire, System.currentTimeMillis(), false);
	}

	@Override
	public boolean isStackable() {
		return true;
	}

	public ArrowTrail getArrowTrail() {
		return at;
	}

	// static
	public static ArrowTrailPI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		var at = ArrowTrail.get(data);
		if (at == null) throw new IllegalArgumentException("arrow trail does not exist: " + data);
		return new ArrowTrailPI(owner, id, at, expire, obtained, isUsing);
	}
}
