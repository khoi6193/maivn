package net.minevn.minigames.commands

import net.minevn.minigames.Minigames
import net.minevn.minigames.PlayerData
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DebugCex(val main: Minigames) : CommandExecutor {
	init {
		main.getCommand("debug")?.setExecutor(this)
	}

	override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>?): Boolean {
		val pd = when {
			(args?.size ?: 0) > 0 -> Bukkit.getPlayer(args!![0])?.let { PlayerData.getData(it) }
			else -> when (sender) {
				is Player -> PlayerData.getData(sender)
				else -> null
			}
		} ?: run {
			sender.sendMessage("§cKhông tìm thấy người chơi")
			return true
		}
		pd.isDebug = !pd.isDebug
		sender.sendMessage("§aĐã ${if (pd.isDebug) "bật" else "tắt"} chế độ debug cho ${pd.player.name}")
		return true
	}
}