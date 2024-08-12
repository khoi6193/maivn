package net.minevn.minigames.gadgets

import net.minevn.guiapi.XMaterial
import net.minevn.minigames.*
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import java.awt.Color
import java.io.File
import java.util.function.Consumer
import java.util.logging.Level

class NamePreset(
	val id: String,
	val name: String,
	texture: String?,
	val iconType: Material,
	val iconData: Short,
	val description: MutableList<String>,
	private val colors: List<Color>,
	private val bold: Boolean,
	private val italic: Boolean,
	private val underLine: Boolean,
	private val strikeThrough: Boolean
) {
	val gameProfile = getGameProfile(texture)

	fun parse(text: String) = text.gradient(colors, bold, italic, underLine, strikeThrough)

	//region static
	companion object {
		@JvmStatic
		private val list: MutableMap<String, NamePreset> = HashMap()

		@JvmStatic
		operator fun get(id: String): NamePreset? = list[id]

		@JvmStatic
		fun load() {
			val main = Minigames.getInstance()
			main.logger.info("Name presets...")
			val config = YamlConfiguration
				.loadConfiguration(File(Configs.getMasterPath() + "name-presets.yml"))
			config.getKeys(false).forEach(Consumer { key: String ->
				try {
					val name 			= config.getString("$key.name", "")!!.colorCodes()
					val texture 		= config.getString("$key.texture")
					val m 				= XMaterial.quickMatch(config.getString("$key.material"))
					val lore 			= config.getStringList("$key.description").colorCodes().toMutableList()
					val colors 			= config.getStringList("$key.colors").map { Color.decode(it) }
					val itemData 		= config.getInt("$key.data").toShort()
					val bold 			= config.getBoolean("$key.bold")
					val italic 			= config.getBoolean("$key.italic")
					val underLine 		= config.getBoolean("$key.under-line")
					val strikeThrough 	= config.getBoolean("$key.strike-through")
					list[key] = NamePreset(
						key, name, texture, m, itemData, lore, colors, bold, italic, underLine, strikeThrough
					)
				} catch (e: Exception) {
					main.logger.log(Level.WARNING, "Can't load $key name preset", e)
				}
			})
			main.logger.info("Loaded " + list.size + " name presets")
		}

		@JvmStatic
		fun getSuggestions(): List<String> = list.values.map { x: NamePreset -> "NicknamePI$" + x.id + "$0" }

		@JvmStatic
		fun getGunTagSuggestions(): List<String> = list.values.map { x: NamePreset -> "MSGunTagPI$" + x.id + "$1" }
	}
	//endregion
}