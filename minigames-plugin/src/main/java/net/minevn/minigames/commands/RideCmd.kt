@file:JvmName("RideCmd")
package net.minevn.minigames.commands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandExecutor
import net.minevn.minigames.Messages
import net.minevn.minigames.PlayerData
import net.minevn.minigames.Utils
import net.minevn.minigames.items.types.RideTicketPI
import org.bukkit.entity.Player

class RideCmd {
	init {
		CommandAPICommand("ride")
			.withAliases("xichdu")
			.withPermission("minigames.ride")
			.withArguments(
				PlayerArgument("player"),
				StringArgument("rideid")
			)
			.executes(CommandExecutor { _, a ->
				Utils.runNotSync ride@{
					val p = a[0] as? Player ?: return@ride
					val pd = PlayerData.getData(p) ?: return@ride
					val ticket = pd.getItem(RideTicketPI::class.java, a[0] as String) ?: run {
						pd.sendMessage(Messages.ERR_RIDE_NO_TICKET)
						return@ride
					}
					val ride = ticket.getRide()?.takeIf { it.isEnabled } ?: run {
						pd.sendMessage(Messages.ERR_RIDE_UNAVAILABLE)
						return@ride
					}
					if (ride.isFull || ride.isRunning) {
						pd.sendMessage(Messages.ERR_RIDE_PLAYING)
						return@ride
					}
					pd.removeItem(ticket, "ride ticket")
					Utils.runSync { ride.addPlayer(p) }
				}
			})
			.register()
	}
}
