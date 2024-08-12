package net.minevn.minigames.items.types;

import net.minevn.minigames.PlayerData;
import net.minevn.minigames.items.PlayerItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MSCreeperGrenadePI extends PlayerItem {
	public MSCreeperGrenadePI(PlayerData owner, int id, long expire, long obtained) {
		super("yay", owner, id, Material.TOTEM_OF_UNDYING, (short) 0, expire, obtained);
	}

	@Override
	public ItemStack getItem() {
		return new ItemStack(Material.AIR);
	}

	@Override
	public String getData() {
		return "";
	}

	@Override
	public List<String> getDescription() {
		return new ArrayList<>();
	}

	// static
	public static MSCreeperGrenadePI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		return new MSCreeperGrenadePI(owner, id, expire, obtained);
	}
}
