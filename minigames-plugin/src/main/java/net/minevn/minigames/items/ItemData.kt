package net.minevn.minigames.items

import com.google.gson.Gson

open class ItemData {
	fun toJson(): String = gson.toJson(this)

	companion object {
		private val gson = Gson()

		@JvmStatic
		fun <T : ItemData> parseItem(json: String, type: Class<out T>): T {
			return gson.fromJson(json, type)
		}
	}
}