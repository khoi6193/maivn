package net.minevn.minigames.quests.attempt.woolwars

import net.minevn.minigames.PlayerData
import net.minevn.minigames.quests.QuestAttempt
import net.minevn.minigames.quests.QuestObjective
import net.minevn.woolwars.arena.Arena

class WoolWarsQA(
	player: PlayerData, val arena: Arena, objective: QuestObjective, count: Int
) : QuestAttempt(player, objective, count) {
	constructor(player: PlayerData, arena: Arena, objective: QuestObjective) : this(player, arena, objective, 1)

	init {
		if (this.javaClass == WoolWarsQA::class.java) attempt()
	}
}