package net.minevn.minigames.chatactions.types

import net.minevn.minigames.*
import net.minevn.minigames.chatactions.ChatListener
import net.minevn.minigames.items.types.NicknamePI

class NickRenameCL(playerData: PlayerData, private val item: NicknamePI) : ChatListener(playerData) {
	init {
		playerData.sendMessage(Messages.MSG_NICK_RENAME_INPUT)
		playerData.player.sendMessages(Messages.MSG_NICK_RENAME_RULES)
	}

	override fun onChat(message: String) {
		try {
			if (!playerData.items.contains(item)) {
				playerData.sendMessage(Messages.ERR_GENERAL)
				return
			}
			if (message.length > 32) {
				playerData.sendMessage(Messages.ERR_LONG_NICKNAME)
				return
			}
			item.getItemData().name = message.stripColors()
			MySQL.getInstance().saveItem(item)
			item.onUsingToggle()
			playerData.sendMessage(Messages.MSG_NICK_RENAME_SUCCESS)
		} finally {
			destroy()
		}
	}
}