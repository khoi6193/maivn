package net.minevn.minigames.quests.attempt.murdermystery

import net.minevn.minigames.PlayerData
import net.minevn.minigames.quests.QuestObjective
import plugily.projects.murdermystery.arena.Arena

class MMWinQA(player: PlayerData, arena: Arena) : MurderMysteryQA(player, arena, QuestObjective.MM_WIN) {
	init {
		if (this.javaClass == MMWinQA::class.java) attempt()
	}
}