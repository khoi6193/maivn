package net.minevn.minigames.chatactions

import de.tr7zw.nbtapi.NBT
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT
import net.minevn.minigames.PlayerData
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta

abstract class BookListener(
	protected val playerData: PlayerData,
	pages: List<String> = listOf(),
	display: String = "§fEdit"
) {
	init {
		playerData.bookListener = this
		val book = ItemStack(Material.WRITABLE_BOOK)
		val meta = book.itemMeta as BookMeta
		meta.setDisplayName(display)
		meta.pages = pages
		book.itemMeta = meta
		NBT.modify(book) { t: ReadWriteItemNBT ->
			t.setBoolean("bookListener", true)
		}
		playerData.player.inventory.addItem(book)
		//set player to hold the book on hand
		playerData.player.inventory.heldItemSlot = playerData.player.inventory.contents.indexOf(book)
	}

	abstract fun onBook(meta: BookMeta)

	fun destroy() {
		if (playerData.bookListener == this) {
			playerData.bookListener = null
		}
	}
}