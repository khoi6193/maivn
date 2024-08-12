package net.minevn.minigames.gadgets

import org.bukkit.Material

class NamePreset(
	val id: String, val name: String, val type: Material, val data: Short,
	val description: List<String>, val prefix: String, val suffix: String,
) {
	//region static
	companion object {
		@JvmStatic
		private val list: MutableMap<String, NamePreset> = HashMap()

		@JvmStatic
		operator fun get(id: String): NamePreset? {
			return list[id]
		}

		@JvmStatic
		fun load() {
		}
	}
	//endregion
}