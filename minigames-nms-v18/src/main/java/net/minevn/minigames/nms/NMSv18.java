package net.minevn.minigames.nms;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class NMSv18 implements INMS {
	@Override
	public void sendFakeItem(Player player, int slot, ItemStack item) {
		var nmsi = CraftItemStack.asNMSCopy(item);
		var handle = ((CraftPlayer) player).getHandle();
		var packet = new ClientboundContainerSetSlotPacket(
				handle.inventoryMenu.containerId,
				handle.inventoryMenu.incrementStateId(),
				slot,
				nmsi);
		handle.connection.send(packet);
	}

	@Override
	public void sendArmor(Player player, Player target) {
		var nmsP = ((CraftPlayer) player).getHandle();
		List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> eq = new ArrayList<>();
		for (var slot : EquipmentSlot.values()) {
			var item = nmsP.getItemBySlot(slot);
			eq.add(new Pair<>(slot, item));
		}
		((CraftPlayer) target).getHandle().connection.send(new ClientboundSetEquipmentPacket(nmsP.getId(), eq));
	}

	@Override
	public ArmorStand createArmorStand(Location l, String name, ItemStack i, boolean gravity, Plugin main) {
		var eas = new net.minecraft.world.entity.decoration.ArmorStand(((CraftWorld) l.getWorld()).getHandle(), l.getX(), l.getY(),
				l.getZ());
		eas.setRot(l.getYaw(), l.getPitch());
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
