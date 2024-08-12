package net.minevn.minigames.items.types;

import net.minevn.minigames.PlayerData;
import net.minevn.minigames.items.StackablePI;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class MSPVEMedicPI extends StackablePI {
	public MSPVEMedicPI(PlayerData owner, int id, long expire, long obtained, int amount) {
		super("", owner, id, Material.IRON_LEGGINGS, (short) 0, expire, obtained, amount);
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
	public MSPVEMedicPI clone() {
		return new MSPVEMedicPI(owner, id, expire, obtained, amount);
	}

	// region static
	public static MSPVEMedicPI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		return new MSPVEMedicPI(owner, id, expire, obtained, amount);
	}
	// endregion
}
