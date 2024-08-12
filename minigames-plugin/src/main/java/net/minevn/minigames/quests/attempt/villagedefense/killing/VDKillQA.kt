package net.minevn.minigames.quests.attempt.villagedefense.killing

import net.minevn.minigames.PlayerData
import net.minevn.minigames.quests.QuestObjective
import net.minevn.minigames.quests.attempt.villagedefense.VillageDefenseQA
import plugily.projects.villagedefense.arena.Arena

open class VDKillQA(player: PlayerData, arena: Arena) : VillageDefenseQA(player, arena, QuestObjective.VD_KILL) {
	init {
		if (this.javaClass == VDKillQA::class.java) attempt()
	}
}