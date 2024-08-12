package net.minevn.minigames.quests.attempt.villagedefense

import net.minevn.minigames.PlayerData
import net.minevn.minigames.quests.QuestAttempt
import net.minevn.minigames.quests.QuestObjective
import plugily.projects.villagedefense.arena.Arena

open class VillageDefenseQA(
	player: PlayerData, val arena: Arena, objective: QuestObjective, count: Int
) : QuestAttempt(player, objective, count) {
	constructor(player: PlayerData, arena: Arena, objective: QuestObjective) : this(player, arena, objective, 1)

	init {
		if (this.javaClass == VillageDefenseQA::class.java) attempt()
	}
}