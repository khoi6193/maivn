package net.minevn.minigames.items

import net.minevn.minigames.gui.InventoryGui
import org.bukkit.event.inventory.InventoryClickEvent

interface DraggablePI {
	fun drop(other: PlayerItem, event: InventoryClickEvent)

	fun onCursorPick(gui: InventoryGui) {
	}
}