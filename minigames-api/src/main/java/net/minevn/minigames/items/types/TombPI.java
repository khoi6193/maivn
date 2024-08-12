package net.minevn.minigames.items.types;

import net.minevn.minigames.PlayerData;
import net.minevn.minigames.gadgets.Tomb;
import net.minevn.minigames.items.UsablePI;
import java.util.List;

public class TombPI extends UsablePI {
	private Tomb tomb;

	public TombPI(PlayerData owner, int id, Tomb tomb, long expire, long obtained, boolean isUsing) {
		super("thanh hoang khoc thue", owner, id, tomb.getType(), tomb.getData(), expire, obtained, isUsing);
		this.tomb = tomb;
	}

	@Override
	public String getData() {
		return tomb.getID();
	}

	@Override
	public List<String> getDescription() {
		return tomb.getDescription();
	}

	public Tomb getTomb() {
		return tomb;
	}

	// static
	public static TombPI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		var tomb = Tomb.get(data);
		if (tomb == null) throw new IllegalArgumentException("tomb " + data + " khong ton tai");
		return new TombPI(owner, id, tomb, expire, obtained, isUsing);
	}
}
