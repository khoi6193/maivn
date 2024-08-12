package net.minevn.minigames.chatactions.types

import net.minevn.minigames.*
import net.minevn.minigames.chatactions.ChatListener
import net.minevn.minigames.items.types.PetPI

class PetRenameCL(playerData: PlayerData, private val item: PetPI) : ChatListener(playerData) {
	init {
		playerData.sendMessage(Messages.MSG_PET_RENAME_INPUT)
		playerData.player.sendMessages(Messages.MSG_PET_RENAME_RULES)
	}

	override fun onChat(message: String) {
		try {
			if (!playerData.items.contains(item)) {
				playerData.sendMessage(Messages.ERR_GENERAL)
				return
			}
			if (message.length > 32) {
				playerData.sendMessage(Messages.ERR_LONG_PET_NAME)
				return
			}
			item.getItemData().name = message.stripColors()
			MySQL.getInstance().saveItem(item)
			if (item.isUsing) {
				Utils.runSync { Minigames.getInstance().gmHook.changePetName(playerData.player, message) }
			}
			playerData.sendMessage(Messages.MSG_PET_RENAME_SUCCESS)
		} finally {
			destroy()
		}
	}
}