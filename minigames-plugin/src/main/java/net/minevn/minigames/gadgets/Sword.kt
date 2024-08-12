package net.minevn.minigames.gadgets

import net.minevn.minigames.Configs
import net.minevn.minigames.Minigames
import net.minevn.minigames.colorCodes
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.logging.Level

class Sword(val iD: String, val name: String, val type: Material, val data: Short,
            val hitSound: String?, val description: List<String>) {

	fun getItem(): ItemStack {
		val item = ItemStack(type, 1, data)
		val im = item.itemMeta
		im.setDisplayName(name)
		im.isUnbreakable = true
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES)
		item.itemMeta = im
		return item
	}

	fun replaceItem(item: ItemStack): ItemStack {
		val sname = item.i18NDisplayName
		item.type = type
		item.durability = data
		val im = item.itemMeta
		im.setDisplayName("$name §b($sname)")
		im.isUnbreakable = true
		im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES)
		item.itemMeta = im
		return item
	}

	//region static
	companion object {
		private val list: MutableMap<String, Sword> = HashMap()

		@JvmStatic
		operator fun get(id: String) = list[id]

		@JvmStatic
		fun load() {
			val main = Minigames.getInstance()
			main.logger.info("Swords...")
			val config = YamlConfiguration
				.loadConfiguration(File(Configs.getMasterPath() + "swords.yml"))
			config.getKeys(false).forEach { key: String ->
				try {
					val name = config.getString("$key.name", "")!!.colorCodes()
					var hitSound = config.getString("$key.hitsound")
					if (hitSound != null) hitSound = hitSound.colorCodes()
					val lore = config.getStringList("$key.description").colorCodes()
					val data = config.getInt("$key.data").toShort()
					list[key] = Sword(key, name, Material.IRON_SWORD, data, hitSound, lore)
				} catch (e: Exception) {
					main.logger.log(Level.WARNING, "Can't load $key sword", e)
				}
			}
		}

		@JvmStatic
		fun getSuggestions() = list.values.map { x: Sword -> "SwordPI$" + x.iD + "$0" }
	}
	//endregion
}