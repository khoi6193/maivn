package net.minevn.minigames.quests

class PlayerQuest(val quest: Quest, done: Int, var nextReset: Long, finished: Boolean, obtained: Boolean) {
	var finished = finished
		private set
	var obtained = obtained
		private set
	var done = done
		private set

	fun finish() {
		finished = true
	}

	fun obtain() {
		obtained = true
	}

	fun unObtain() {
		obtained = false
	}

	fun count(step: Int) {
		done += step
	}

	fun reset() {
		done = 0
		finished = false
		obtained = false
		nextReset = 0
	}

	fun nextReset(nextReset : Long) {
		this.nextReset = nextReset;
	}

	fun isNewlyQuest() = done == 0 && !finished && !obtained
}