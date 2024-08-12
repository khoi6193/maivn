package net.minevn.minigames.items.types;

import net.minevn.minigames.PlayerData;
import net.minevn.minigames.items.UsablePI;
import net.minevn.mmclient.rooms.RoomMap;

import java.util.List;

public class RoomPI extends UsablePI {
	private RoomMap room;

	public RoomPI(PlayerData owner, int id, RoomMap room, long expire, long obtained, boolean isUsing) {
		super("thanh hoang ngu lam", owner, id, room.getIcon(), room.getItemData(), expire, obtained, isUsing);
		this.room = room;
	}

	@Override
	public String getData() {
		return room.getId();
	}

	@Override
	public List<String> getDescription() {
		return room.getDescription();
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
