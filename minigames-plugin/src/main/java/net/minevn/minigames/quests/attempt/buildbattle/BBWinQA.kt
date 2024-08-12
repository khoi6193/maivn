package net.minevn.minigames.quests.attempt.buildbattle

import net.minevn.minigames.PlayerData
import net.minevn.minigames.quests.QuestObjective
import plugily.projects.buildbattle.arena.impl.BaseArena

class BBWinQA(player: PlayerData, arena: BaseArena) : BuildBattleQA(player, arena, QuestObjective.BB_WIN) {
	init {
		if (this.javaClass == BBWinQA::class.java) attempt()
	}
}