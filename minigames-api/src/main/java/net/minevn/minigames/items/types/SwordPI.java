package net.minevn.minigames.items.types;

import net.minevn.minigames.PlayerData;
import net.minevn.minigames.gadgets.Sword;
import net.minevn.minigames.items.UsablePI;

import java.util.List;

public class SwordPI extends UsablePI {
	private Sword sword;

	public SwordPI(PlayerData owner, int id, Sword sword, long expire, long obtained, boolean isUsing) {
		super("thanh hoang ngu nhu bo", owner, id, sword.getType(), sword.getData(), expire, obtained, isUsing);
		this.sword = sword;
	}

	@Override
	public String getData() {
		return sword.getID();
	}

	@Override
	public List<String> getDescription() {
		return sword.getDescription();
	}

	public Sword getSword() {
		return sword;
	}

	// static
	public static SwordPI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		var sword = Sword.get(data);
		if (sword == null) throw new IllegalArgumentException("sword " + data + " khong ton tai");
		return new SwordPI(owner, id, sword, expire, obtained, isUsing);
	}
}
