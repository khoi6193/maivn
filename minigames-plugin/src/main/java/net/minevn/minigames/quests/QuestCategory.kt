package net.minevn.minigames.quests

import net.minevn.guiapi.XMaterial
import net.minevn.minigames.Configs
import net.minevn.minigames.Minigames
import net.minevn.minigames.colorCodes
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.logging.Level

class QuestCategory(val id: String, val name: String, val description: List<String>,
					val showInGui: Boolean, val iconType: Material, val iconData: Short) {
	companion object {
		private val map = mutableMapOf<String, QuestCategory>()

		@JvmStatic
		fun load() {
			map.clear()
			val main = Minigames.getInstance()
			main.logger.info("Quest categories...")
			val root = File(Configs.getMasterPath(), "quest")
			if (!root.exists() || !root.isDirectory) {
				main.logger.warning("Quests folder not found")
				return
			}
			val file = File(root, "categories.yml")
			if (!file.exists() || file.isDirectory) {
				main.logger.warning("Quests category config not found")
				return
			}
			val config = YamlConfiguration.loadConfiguration(file)
			for (id in config.getKeys(false)) {
				try {
					val section = config.getConfigurationSection(id)!!
					val name = section.getString("name")!!.colorCodes()
					val description = section.getStringList("description").colorCodes()
					val showInGui = section.getBoolean("show-in-gui")
					val iconType = XMaterial.quickMatch(section.getString("icon.type", "BOOK"))
					val iconData = section.getInt("icon.data").toShort()
					map[id] = QuestCategory(id, name, description, showInGui, iconType, iconData)
				} catch (e: Exception) {
					main.logger.log(Level.WARNING, "Can not load quest category $id", e)
				}
			}
			main.logger.info("Loaded ${map.size} quest categories.")
		}

		@JvmStatic
		fun get(id: String): QuestCategory? {
			return map[id]
		}

		fun getAll() = map.values.toList()
	}
}