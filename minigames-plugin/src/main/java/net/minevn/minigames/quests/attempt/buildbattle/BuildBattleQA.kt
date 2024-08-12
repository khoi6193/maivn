package net.minevn.minigames.quests.attempt.buildbattle

import net.minevn.minigames.PlayerData
import net.minevn.minigames.quests.QuestAttempt
import net.minevn.minigames.quests.QuestObjective
import plugily.projects.buildbattle.arena.impl.BaseArena

open class BuildBattleQA(player: PlayerData, val arena: BaseArena, objective: QuestObjective, count: Int) :
	QuestAttempt(player, objective, count) {
	constructor(player: PlayerData, arena: BaseArena, objective: QuestObjective) : this(player, arena, objective, 1)

	init {
		if (this.javaClass == BuildBattleQA::class.java) attempt()
	}
}