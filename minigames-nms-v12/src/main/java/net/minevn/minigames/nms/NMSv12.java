package net.minevn.minigames.nms;

import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.EnumItemSlot;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_12_R1.PacketPlayOutSetSlot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class NMSv12 implements INMS {
	@Override
	public void sendFakeItem(Player player, int slot, ItemStack item) {
		var nmsi = CraftItemStack.asNMSCopy(item);
		var handle = ((CraftPlayer) player).getHandle();
		var packet = new PacketPlayOutSetSlot(
				handle.defaultContainer.windowId,
				slot,
				nmsi);
		handle.playerConnection.sendPacket(packet);
	}

	@Override
	public void sendArmor(Player player, Player target) {
		var item = player.getInventory().getHelmet();
		if (item == null) item = new ItemStack(Material.AIR);
		((CraftPlayer) target).getHandle()
				.playerConnection
				.sendPacket(new PacketPlayOutEntityEquipment(player.getEntityId(),
						EnumItemSlot.HEAD,
						CraftItemStack.asNMSCopy(player.getInventory().getHelmet())));
	}

	@Override
	public ArmorStand createArmorStand(Location l, String name, ItemStack i, boolean gravity, Plugin main) {
		EntityArmorStand eas = new EntityArmorStand(((CraftWorld) l.getWorld()).getHandle(), l.getX(), l.getY(),
				l.getZ());
		eas.yaw = l.getYaw();
		eas.pitch = l.getPitch();
		ArmorStand as = (ArmorStand) eas.getBukkitEntity();
		if (name != null) {
			as.setCustomName(name);
			as.setCustomNameVisible(true);
		}
		as.setSmall(true);
		as.setInvulnerable(true);
		as.setVisible(false);
		as.setBasePlate(false);
		as.setHelmet(i);
		as.setCollidable(false);
		as.setGravity(gravity);
		as.setMetadata("noRotation", new FixedMetadataValue(main, "true"));
		as.setMetadata("toRemove", new FixedMetadataValue(main, "true"));
		as = ((CraftWorld) l.getWorld()).addEntity(((CraftArmorStand) as).getHandle(), CreatureSpawnEvent.SpawnReason.CUSTOM);
		// as.teleport(l);
		return as;
	}
}
