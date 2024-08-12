package net.minevn.minigames.chatactions.prompt

import net.minevn.minigames.PlayerData
import net.minevn.minigames.chatactions.ChatListener

class ChatPrompt(
	playerData: PlayerData, private val fallback: (String) -> Unit
) : ChatListener(playerData) {

	override fun onChat(message: String) {
		try {
			fallback(message)
		} finally {
			destroy()
		}
	}
}