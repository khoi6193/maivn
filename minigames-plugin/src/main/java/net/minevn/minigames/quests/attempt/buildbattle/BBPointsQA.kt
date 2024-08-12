package net.minevn.minigames.quests.attempt.buildbattle

import net.minevn.minigames.PlayerData
import net.minevn.minigames.quests.QuestObjective
import plugily.projects.buildbattle.arena.impl.BaseArena

class BBPointsQA(player: PlayerData, arena: BaseArena, points: Int) :
	BuildBattleQA(player, arena, QuestObjective.BB_POINTS, points) {
	init {
		if (this.javaClass == BBPointsQA::class.java) attempt()
	}
}