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

class Bow(var id: String, var name: String, var type: Material, var data: Short = 0,
          var hitSound: String?, var description: List<String>) {

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
		private val list: MutableMap<String, Bow> = HashMap()

		@JvmStatic
		operator fun get(id: String): Bow? {
			return list[id]
		}

		@JvmStatic
		fun load() {
			val main = Minigames.getInstance()
			main.logger.info("Bows...")
			val config = YamlConfiguration
				.loadConfiguration(File(Configs.getMasterPath() + "bows.yml"))
			config.getKeys(false).forEach { key: String ->
				try {
					val name = config.getString("$key.name", "")!!.colorCodes()
					var hitSound = config.getString("$key.hitsound")
					if (hitSound != null) hitSound = hitSound.colorCodes()
					val lore = config.getStringList("$key.description").colorCodes()
					val data = config.getInt("$key.data").toShort()
					list[key] = Bow(key, name, Material.BOW, data, hitSound, lore)
				} catch (e: Exception) {
					main.logger.log(Level.WARNING, "Can't load $key bow", e)
				}
			}
		}

		@JvmStatic
		fun getSuggestions() = list.values.map { x: Bow -> "BowPI$" + x.id + "$0" }
	}
	//endregion
}