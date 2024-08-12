package net.minevn.minigames.chatactions.types

import net.minevn.minigames.Messages
import net.minevn.minigames.MySQL
import net.minevn.minigames.PlayerData
import net.minevn.minigames.chatactions.ChatListener
import net.minevn.minigames.items.types.MSGunPI
import net.minevn.minigames.items.types.MSGunTagPI
import net.minevn.minigames.sendMessages

class GunRenameCL(
	playerData: PlayerData, private val gun: MSGunPI, private val tag: MSGunTagPI
) : ChatListener(playerData) {
	init {
		playerData.sendMessage(Messages.MSG_GUN_RENAME_INPUT)
		playerData.player.sendMessages(Messages.MSG_GUN_RENAME_RULES)
	}

	override fun onChat(message: String) {
		try {
			if (!playerData.items.contains(gun) || !playerData.items.contains(tag)) {
				playerData.sendMessage(Messages.ERR_GENERAL)
				return
			}
			if (message.length > 32) {
				playerData.sendMessage(Messages.ERR_LONG_NICKNAME)
				return
			}
			playerData.removeItem(tag, "renaming gun")
			gun.getItemData().name = tag.parse(message)
			MySQL.getInstance().saveItem(gun)
			playerData.sendMessage(Messages.MSG_GUN_RENAME_SUCCESS)
		} finally {
			destroy()
		}
	}
}