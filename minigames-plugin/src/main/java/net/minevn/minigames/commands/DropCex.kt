package net.minevn.minigames.commands

import net.minevn.minigames.Configs
import net.minevn.minigames.Minigames
import net.minevn.minigames.getSerializedLocation
import net.minevn.mmclient.MatchMakerClient
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.logging.Level

class DropCex : CommandExecutor {
	override fun onCommand(sender: CommandSender, command: Command, allies: String, args: Array<String>): Boolean {
		val mm = MatchMakerClient.getInstance()
		if (sender !is Player) {
			sender.sendMessage("player only!")
			return true
		}
		if (!sender.hasPermission("minevn.admin.drops")) return true
		if (args.isEmpty()) return true
		val folder = File("${Configs.getMasterPath()}drops")
		if (!folder.exists()) folder.mkdirs()
		val file = File(folder, "${mm.info.mapID}.yml")
		val cf = YamlConfiguration.loadConfiguration(file)
		val sec = cf.getConfigurationSection("locations")
		val locations = sec?.getKeys(false)?: mutableSetOf<String>()
		when (args[0].lowercase()) {
			"add" -> {
				if (args.size < 2) return true
				try {
					val radius = args[1].toDoubleOrNull() ?: return true
					val loc = sender.location.getSerializedLocation()
					cf.set("locations.${locations.size}.location", loc)
					cf.set("locations.${locations.size}.radius", radius)
					cf.save(file)
					sender.sendMessage("added $loc with radius $radius")
				} catch (e: Exception) {
					Minigames.getInstance().logger.log(Level.WARNING, "can't save drop location into ${mm.info.mapID}", e)
				}
			}
			"remove" -> {
				if (args.size < 2) return true
				val id = args[1]
				if (!locations.contains(id)) {
					sender.sendMessage("id ${args[1]} doesn't exist")
					return true
				}
				try {
					cf.set("locations.$id", null)
					cf.save(file)
					sender.sendMessage("removed id ${args[1]}")
				} catch (e: Exception) {
					Minigames.getInstance().logger.log(Level.WARNING, "can't remove drop location into ${mm.info.mapID}", e)
				}
			}
			"list" -> {
				sender.sendMessage("Locations:\n" + locations.joinToString("\n") {
					"[$it] ${cf.getString("locations.$it.location")}|${cf.getDouble("locations.$it.radius")}"
				}.ifBlank { "Nothing" })
			}
		}
		return true
	}
}