package net.minevn.minigames

import java.util.concurrent.ConcurrentHashMap

enum class Stat(val game: String, val type: String, val isReplace: Boolean = false) {
	GN_PARKOUR("generic", "parkour_%mapid%", true),

	MS_CP_PLAY("minestrike", "cpplay"),
	MS_CP_TIMEPLAY("minestrike", "cptimeplay"),
	MS_CP_KILL("minestrike", "cpkill"),
	MS_CP_DEATH("minestrike", "cpdeath"),
	MS_CP_WIN("minestrike", "cpwin"),
	MS_CP_LOSE("minestrike", "cplose"),
	MS_CP_RWIN("minestrike", "cprwin"),
	MS_CP_RLOSE("minestrike", "cprlose"),
	MS_CP_BOMBDEFUSE("minestrike", "cpdefuse"),
	MS_CP_BOMBPLANT("minestrike", "cpplant"),
	MS_CP_HEADSHOTS_KILL("minestrike", "cpheadshotskill"),
	MS_CP_GRENADE_KILL("minestrike", "cpgrenadekill"),
	MS_CP_KNIFE_KILL("minestrike", "cpknifekill"),
	MS_CP_MVP("minestrike", "cpmvp"),
	MS_CP_WIN_STREAK("minestrike", "cpkillstreak"),

	BW_KILL("bedwar", "kill"),
	BW_FINALKILL("bedwar", "finalkill"),
	BW_DEATH("bedwar", "death"),
	BW_FINALDEATH("bedwar", "finaldeath"),
	BW_WIN("bedwar", "win"),
	BW_LOSE("bedwar", "lose"),
	BW_BED("bedwar", "bed"),
	BW_WIN_STREAK("bedwar", "winstreak"),

	VD_KILL("villagerdefense", "kill"),
	VD_DEATH("villagerdefense", "death"),
	VD_WIN("villagerdefense", "win"),
	VD_LOSE("villagerdefense", "lose"),
	VD_WIN_STREAK("villagerdefense", "winstreak"),

	MM_KILL("murdermystery", "kill"),
	MM_DEATH("murdermystery", "death"),
	MM_WIN("murdermystery", "win"),
	MM_LOSE("murdermystery", "lose"),
	MM_WIN_STREAK("murdermystery", "winstreak"),

	BB_POINTS("buildbattle", "points"),
	BB_WIN("buildbattle", "win"),
	BB_LOSE("buildbattle", "lose"),
	BB_WIN_STREAK("buildbattle", "winstreak"),

	WW_KILL("woolwar", "kill"),
	WW_DEATH("woolwar", "death"),
	WW_WIN("woolwar", "win"),
	WW_LOSE("woolwar", "lose"),
	WW_WIN_STREAK("woolwar", "winstreak"),

	TL_POINTS("thelab", "points"),
	TL_WIN("thelab", "win"), //count when player are in top 1
	TL_TOP("thelab", "top"), //top 3 players
	TL_TOP_STREAK("thelab", "topstreak"), //count when player are in top 3
	;

	override fun toString(): String {
		return toString(null)
	}

	/**
	 * @param params = mapOf("%mapid%" to "test_parkour_map")
	 * @return top key with params applied
	 */
	fun toString(params: Map<String, String>?): String {
		val str = "${game}_${type}"
		params?.entries?.forEach { str.replace(it.key, it.value) }
		return str
	}

	/**
	 * match generic types
	 */
	fun match(key: String): Boolean {
		val myKey = toString()
		if (myKey.contains("%")) {
			val mine = myKey.split("_")
			val their = key.split("_")
			mine.forEachIndexed { i, it ->
				if (it != their[i] && !it.contains("%")) return false
			}
			return true
		}
		return myKey == key
	}

	companion object {
		private val TYPES = mutableMapOf<String, Stat>().also {
			for (type in values()) {
				it[type.toString()] = type
			}
		}

		private val cached = ConcurrentHashMap<String, CachedTop>()

		@Deprecated(message = "ko xai nua")
		@JvmStatic
		fun value(key: String): Stat? {
			return TYPES[key] ?: values().firstOrNull { it.match(key) }?.also { TYPES[key] = it }
		}

		/**
		 * Get top 10 of the desired key (cached) <br />
		 * Updates every 1 minute
		 */
		@JvmStatic
		fun get(type: String): LinkedHashMap<String, Double> {
			return cached[type]?.takeIf { !it.isExpired() }?.list ?: run {
				// at this point, the map is empty or expired
				val temp = cached[type]?.list ?:  linkedMapOf() // get current expired map or create new one (dummy)
				cached[type] = CachedTop(temp) // place new one to prevent continuous call
				Utils.runNotSync { cached[type] = CachedTop(MySQL.getInstance().getStats(type)) } // update map async
				temp
			}
		}
	}
}

private class CachedTop(val list: LinkedHashMap<String, Double>) {
	val loadedTime = System.currentTimeMillis()

	fun isExpired() = System.currentTimeMillis() - loadedTime > 60000L // 1 mintute
}