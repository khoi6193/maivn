package net.minevn.minigames

enum class TopType(val game: String, val type: String) {
	BW_KILL("bedwar", "kill"),
	BW_DEATH("bedwar", "death"),
	BW_WIN("bedwar", "win"),
	BW_LOSE("bedwar", "lose"),
	BW_BED("bedwar", "bed"),
	VD_KILL("villagerdefense", "kill"),
	VD_DEATH("villagerdefense", "death"),
	VD_WIN("villagerdefense", "win"),
	VD_LOSE("villagerdefense", "lose"),
	MM_KILL("murdermystery", "kill"),
	MM_DEATH("murdermystery", "death"),
	MM_WIN("murdermystery", "win"),
	MM_LOSE("murdermystery", "lose"),
	BB_POINTS("buildbattle", "points"),
	BB_WIN("buildbattle", "win"),
	BB_LOSE("buildbattle", "lose"),
	WW_KILL("woolwar", "kill"),
	WW_DEATH("woolwar", "death"),
	WW_WIN("woolwar", "win"),
	WW_LOSE("woolwar", "lose"),
	TL_POINTS("thelab", "points");

	override fun toString(): String {
		return "${game}_${type}"
	}

	companion object {
		private val TYPES: MutableMap<String, TopType> = HashMap()
		@JvmStatic
		fun value(key: String): TopType {
			return TYPES[key]!!
		}

		init {
			for (type in values()) {
				TYPES[type.toString()] = type
			}
		}
	}
}