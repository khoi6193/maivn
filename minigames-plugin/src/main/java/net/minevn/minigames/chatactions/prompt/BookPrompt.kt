package net.minevn.minigames.chatactions.prompt

import net.minevn.minigames.PlayerData
import net.minevn.minigames.chatactions.BookListener
import org.bukkit.inventory.meta.BookMeta

class BookPrompt(
	playerData: PlayerData, pages: List<String>, display: String, private val fallback: (BookMeta) -> Unit
) : BookListener(playerData, pages, display) {

	override fun onBook(meta: BookMeta) {
		try {
			fallback(meta)
		} finally {
			destroy()
		}
	}
}