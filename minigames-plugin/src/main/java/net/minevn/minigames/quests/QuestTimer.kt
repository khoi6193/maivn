package net.minevn.minigames.quests

enum class QuestTimer {
	/**
	 * Reset mỗi ngày
	 */
	DAILY {
		override fun getResetTime(): Long {
			var curr = System.currentTimeMillis()
			curr -= (curr - 61200000L) % 86400000L
			return curr + 86400000L
		}
	},

	WEEKLY {
		override fun getResetTime(): Long = DAILY.getResetTime() * 7
	};

	abstract fun getResetTime(): Long
}