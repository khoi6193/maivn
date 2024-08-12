package net.minevn.minigames.gadgets

import net.minevn.guiapi.XMaterial
import net.minevn.minigames.*
import net.minevn.minigames.Utils.runLater
import net.minevn.minigames.award.PlayerAward
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class ItemBundle(
	val id: String,
	val name: String,
	val iconType: Material,
	val iconData: Short,
	val description: List<String>,
	val random: Boolean,
	val onlyOnce: Boolean,
) {
	lateinit var items: List<PlayerAward>

	companion object {
		@JvmStatic
		private val list: MutableMap<String, ItemBundle> = HashMap()

		@JvmStatic
		operator fun get(id: String): ItemBundle? = list[id]

		fun load(file: File) {
			if (file.extension != "yml") return
			try {
				val id = file.nameWithoutExtension
				val config = YamlConfiguration.loadConfiguration(file)
				val name = config.getString("name")?.colorCodes() ?: id
				val lore = config.getStringList("description").colorCodes()
				val iconType = XMaterial.quickMatch(config.getString("icon-type")!!)
				val iconData = config.getInt("icon-data").toShort()
				val random = config.getBoolean("random")
				val onlyOnce = config.getBoolean("only-once")
				val bundle = ItemBundle(id, name, iconType, iconData, lore, random, onlyOnce)
				list[id] = bundle
				runLater {
					// load items in the next tick to allow bundles containing other bundles
					try {
						bundle.items = config.getList("items")
							?.map {
								val map = it as Map<*, *>
								val itemType = map["item-type"] as String
								val itemData = map["item-data"] as String
								PlayerAward.fromData(itemType, itemData)
							}
							?: listOf()
						log("Loaded bundle $id with ${bundle.items.size} items")
					} catch (e: Exception) {
						e.warning("Khong the load item tu bundle $id")
					}
				}
			} catch (e: Exception) {
				e.warning("Failed to load bundle ${file.name}")
			}
		}

		@JvmStatic
		fun load() {
			list.clear()
			// load yml file from bundles folder
			val bundlesFolder = File(Configs.getMasterPath(), "bundles")
			if (!bundlesFolder.exists()) bundlesFolder.mkdirs()
			bundlesFolder.listFiles()?.forEach { load(it) }
			Minigames.getInstance().logger.info("Loaded ${list.size} bundles")
		}

		@JvmStatic
		fun getIDSuggestions() = list.keys.toTypedArray()

		@JvmStatic
		fun getSuggestions(): List<String> = list.values.map { x: ItemBundle -> "ItemBundlePI$${x.id}$1" }
	}
}