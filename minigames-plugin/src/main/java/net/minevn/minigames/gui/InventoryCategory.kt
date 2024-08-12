package net.minevn.minigames.gui

import net.minevn.guiapi.XMaterial
import net.minevn.minigames.Configs
import net.minevn.minigames.Minigames
import net.minevn.minigames.colorCodes
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.logging.Level

class InventoryCategory(
	val id: String, val name: String, val material: Material, val data: Short,
	val description: List<String>, val regex: String, val parent: String?
) {

	fun getChildrens() = categories.values.filter { it.parent == id }

	fun getSiblings() = when(parent != null) {
		true -> categories.values.filter { it.parent == parent }
		false -> listOf()
	}

	companion object {
		private val categories = LinkedHashMap<String, InventoryCategory>()

		@JvmStatic
		fun getRootCategories() = categories.values.filter { it.parent == null }

		@JvmStatic
		fun load() {
			val main = Minigames.getInstance()
			categories.clear()
			main.logger.info("Inventory Categories...")
			val configFile = File("${Configs.getMasterPath()}inventory-categories.yml")
			if (!configFile.exists()) configFile.createNewFile()
			val config = YamlConfiguration.loadConfiguration(configFile)
			try {
				for (key in config.getKeys(false)) {
					val category = config.getConfigurationSection(key) ?: continue
					val name = category.getString("name", "")!!.colorCodes()
					val material = XMaterial.quickMatch(category.getString("icon-type", "STONE"))
					val description = category.getStringList("description").colorCodes()
					val data = category.getInt("icon-data").toShort()
					val regex = category.getString("regex")!!
					val parent = category.getString("parent")
					categories[key] = InventoryCategory(key, name, material, data, description, regex, parent)
					main.logger.info("Loaded inventory category $name")
				}
			} catch (e: Exception) {
				main.logger.log(Level.WARNING, "Can't load inventory categories", e)
			}
		}

		@JvmStatic
		operator fun get(id: String) = categories[id]
	}
}