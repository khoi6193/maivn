package net.minevn.minigames.nms;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public interface INMS {
	void sendFakeItem(Player player, int slot, ItemStack item);

	void sendArmor(Player player, Player target);

	ArmorStand createArmorStand(Location l, String name, ItemStack i, boolean gravity, Plugin main);
}
