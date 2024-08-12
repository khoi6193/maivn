package net.minevn.minigames.chatactions

import net.minevn.minigames.PlayerData

abstract class ChatListener(protected val playerData: PlayerData) {
	init {
		playerData.chatListener = this
	}

	abstract fun onChat(message: String)

	fun destroy() {
		if (playerData.chatListener == this) {
			playerData.chatListener = null
		}
	}
}