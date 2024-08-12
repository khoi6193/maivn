package net.minevn.minigames.commands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import net.minevn.minigames.PlayerData
import net.minevn.minigames.mail.MailUI
import net.minevn.minigames.mail.compose.MailComposeUI

class MailCmd {
	init {
		CommandAPICommand("mail")
			.withAliases("thu", "hopthu")
			.withSubcommand(sendMail("send"))
			.executesPlayer(PlayerCommandExecutor { p, _ ->
				MailUI(PlayerData.getData(p))
			})
			.register()

		sendMail("guithu").register()
	}

	private fun sendMail(command: String) : CommandAPICommand {
		return CommandAPICommand(command)
			.withPermission("minigames.mail.send")
			.executesPlayer(PlayerCommandExecutor { p, _ ->
				MailComposeUI(PlayerData.getData(p))
			})
	}
}