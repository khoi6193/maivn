package net.minevn.minigames.quests

import net.minevn.minigames.PlayerData

open class QuestAttempt(val player: PlayerData, val objective: QuestObjective, val count: Int) {
	constructor(player: PlayerData, objective: QuestObjective) : this(player, objective, 1)

	init {
		this.attempt()
	}

	fun attempt() {
	}
}