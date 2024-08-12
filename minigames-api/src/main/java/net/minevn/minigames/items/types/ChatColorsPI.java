package net.minevn.minigames.items.types;

import net.minevn.minigames.PlayerData;
import net.minevn.minigames.gadgets.ChatColors;
import net.minevn.minigames.items.UsablePI;

import java.util.List;

public class ChatColorsPI extends UsablePI {

	private ChatColors cc;

	public ChatColorsPI(PlayerData owner, int id, ChatColors cc, long expire, long obtained, boolean isUsing) {
		super("thanh hoang re rach", owner, id, cc.getMaterial(), cc.getData(), expire, obtained, isUsing);
		this.cc = cc;
	}


	@Override
	public String getData() {
		return cc.getID();
	}

	@Override
	public List<String> getDescription() {
		return cc.getDescription();
	}

	@Override
	public boolean isStackable() {
		return false;
	}

	public ChatColors getChatColors() {
		return cc;
	}

	// static
	public static ChatColorsPI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		var cc = ChatColors.get(data);
		if (cc == null) throw new IllegalArgumentException("chat color does not exist: " + data);
		return new ChatColorsPI(owner, id, cc, expire, obtained, isUsing);
	}
}
