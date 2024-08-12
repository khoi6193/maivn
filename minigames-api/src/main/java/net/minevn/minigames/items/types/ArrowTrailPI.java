package net.minevn.minigames.items.types;

import net.minevn.minigames.PlayerData;
import net.minevn.minigames.gadgets.ArrowTrail;
import net.minevn.minigames.items.UsablePI;

import java.util.List;

public class ArrowTrailPI extends UsablePI {

	private final ArrowTrail at;

	public ArrowTrailPI(PlayerData owner, int id, ArrowTrail at, long expire, long obtained, boolean isUsing) {
		super("suc vat", owner, id, at.getMaterial(), at.getData(), expire, obtained, isUsing);
		this.at = at;
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
