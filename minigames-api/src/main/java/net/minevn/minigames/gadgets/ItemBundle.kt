package net.minevn.minigames.gadgets

import net.minevn.minigames.award.PlayerAward
import org.bukkit.Material
import java.util.*

class ItemBundle(val id: String, val name: String, val type: Material, val data: Short,
                 val description: List<String>, val items: List<PlayerAward>, val random: Boolean) {

	companion object {
		@JvmStatic
		private val list: MutableMap<String, ItemBundle> = HashMap()

		@JvmStatic
		operator fun get(id: String): ItemBundle? = list[id]

		@JvmStatic
		fun load() {}
	}
}