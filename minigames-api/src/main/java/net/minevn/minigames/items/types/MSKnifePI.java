package net.minevn.minigames.items.types;

import net.minefs.MineStrike.Main;
import net.minefs.MineStrike.Skins.Knife;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.items.UsablePI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MSKnifePI extends UsablePI {
	private Knife knife;

	public MSKnifePI(PlayerData owner, int id, Knife knife, long expire, long obtained, boolean isUsing) {
		super("haha", owner, id, knife.getItem().getType(), knife.getItem().getDurability(),
				expire, obtained, isUsing);
		this.knife = knife;
	}

	@Override
	public String getData() {
		return knife.getID() + "";
	}

	@Override
	public List<String> getDescription() {
		return new ArrayList<>();
	}

	@Override
	public ItemStack getItem() {
		return new Random().nextBoolean() ? null : new ItemStack(Material.AIR);
	}

	public Knife getKnife() {
		return knife;
	}

	// static
	public static MSKnifePI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		var knife = Main.getInstance().getKnife(Integer.parseInt(data));
		if (knife == null) throw new IllegalArgumentException("knife does not exist: " + data);
		return new MSKnifePI(owner, id, knife, expire, obtained, isUsing);
	}
}
