package net.minevn.minigames;

import net.minevn.minigames.items.types.RoomPI;
import net.minevn.mmclient.DefaultPlayerPerks;
import net.minevn.mmclient.MatchMakerClient;
import net.minevn.mmclient.api.IPlayerPerks;
import net.minevn.mmclient.rooms.RoomMap;
import org.bukkit.entity.Player;

public class PlayerPerks implements IPlayerPerks {

	public IPlayerPerks previous;

	public PlayerPerks() {
		Minigames.getInstance().getLogger().info("PlayerPerks init");
		var mm = MatchMakerClient.getInstance();
		previous = mm.getPerks();
		if (!(previous instanceof DefaultPlayerPerks || previous instanceof PlayerPerks)) {
			throw new IllegalArgumentException("Current perks handler is not default: "
					+ previous.getClass().getName());
		}
		mm.setPerks(this);
	}

	@Override
	public boolean lockRoomPerk(Player player) {
		return previous.lockRoomPerk(player);
	}

	@Override
	public RoomMap getRoomMap(Player player) {
		var data = PlayerData.getData(player);
		var room = data.getUsingItem(RoomPI.class);
		if (room != null) return room.getRoomMap();
		return previous.getRoomMap(player);
	}

	@Override
	public int getLevel(Player player) {
		return PlayerData.getData(player).getLevel();
	}
}
