package net.minevn.minigames.items.types;

import net.minevn.minigames.Messages;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.items.PlayerItem;
import net.minevn.minigames.items.UsablePI;
import net.minevn.mmclient.rooms.RoomMap;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RoomPI extends UsablePI {
	private RoomMap room;

	public RoomPI(PlayerData owner, int id, RoomMap room, long expire, long obtained, boolean isUsing) {
		super(Messages.PI_CATEGORY_ROOM, owner, id, room.getIcon(), room.getItemData(), expire, obtained, isUsing);
		this.room = room;
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = super.getItem();
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(room.getName());
		List<String> lores = new ArrayList<String>();
		var desc = getDescription();
		if (desc != null) {
			lores.addAll(desc);
			lores.add("§f");
		}
		if (getExpire() > 0) {
			lores.add(Messages.PI_EXPIRE_DATE.replace("%date%", getExpireDate()));
			lores.add("§f");
		}
//        lores.add(Messages.PI_CLICK_TO_LISTEN);
		lores.add(isUsing() ? Messages.PI_CLICK_TO_UNUSE : Messages.PI_CLICK_TO_USE);
		im.setLore(lores);
		item.setItemMeta(im);
		return item;
	}

	@Override
	public String getData() {
		return room.getId();
	}

	@Override
	public List<String> getDescription() {
		return room.getDescription();
	}

	@Override
	public PlayerItem clone() {
		return new RoomPI(null, -1, room, expire, System.currentTimeMillis(), false);
	}

	public RoomMap getRoomMap() {
		return room;
	}

	// static
	public static RoomPI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		var room = RoomMap.getRoomMap(data);
		if (room == null) throw new IllegalArgumentException("room does not exist: " + data);
		return new RoomPI(owner, id, room, expire, obtained, isUsing);
	}
}
