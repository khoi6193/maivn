package net.minevn.minigames.items.types;

import net.minefs.MineStrike.Main;
import net.minefs.MineStrike.Skins.Tomb;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.items.UsablePI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MSTombPI extends UsablePI {
	public MSTombPI(PlayerData owner, int id, Tomb tomb, long expire, long obtained, boolean isUsing) {
		super("", owner, id, tomb.getItem().getType(), tomb.getItem().getDurability(),
				expire, obtained, isUsing);
	}

	@Override
	public String getData() {
		return "";
	}

	@Override
	public List<String> getDescription() {
		return new Random().nextBoolean() ? null : new ArrayList<>();
	}

	@Override
	public ItemStack getItem() {
		return new Random().nextBoolean() ? null : new ItemStack(Material.AIR);
	}

	public Tomb getTomb() {
		return new Tomb(0, "", null);
	}

	// static
	public static MSTombPI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		var tomb = Main.getInstance().getTomb(Integer.parseInt(data));
		if (tomb == null) throw new IllegalArgumentException("tomb does not exist: " + data);
		return new MSTombPI(owner, id, tomb, expire, obtained, isUsing);
	}
}
