package net.minevn.minigames.items.types;

import net.minevn.minigames.PlayerData;
import net.minevn.minigames.items.StackablePI;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class MSPVERetryPI extends StackablePI {
	public MSPVERetryPI(PlayerData owner, int id, long expire, long obtained, int amount) {
		super("", owner, id, Material.WOODEN_SHOVEL, (short) 34, expire, obtained, amount);
	}

	@Override
	public String getData() {
		return "";
	}

	@Override
	public List<String> getDescription() {
		return new ArrayList<>();
	}

	@Override
	public MSPVERetryPI clone() {
		return new MSPVERetryPI(owner, id, expire, obtained, amount);
	}

	// region static
	public static MSPVERetryPI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		return new MSPVERetryPI(owner, id, expire, obtained, amount);
	}
	// endregion
}
