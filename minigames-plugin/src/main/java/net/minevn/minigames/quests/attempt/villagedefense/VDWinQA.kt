package net.minevn.minigames.quests.attempt.villagedefense

import net.minevn.minigames.PlayerData
import net.minevn.minigames.quests.QuestObjective
import plugily.projects.villagedefense.arena.Arena

class VDWinQA(player: PlayerData, arena: Arena) : VillageDefenseQA(player, arena, QuestObjective.VD_WIN) {
	init {
		if (this.javaClass == VDWinQA::class.java) attempt()
	}
}