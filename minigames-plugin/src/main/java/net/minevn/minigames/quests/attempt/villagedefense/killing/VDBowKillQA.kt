package net.minevn.minigames.quests.attempt.villagedefense.killing

import net.minevn.minigames.PlayerData
import plugily.projects.villagedefense.arena.Arena

class VDBowKillQA(player : PlayerData, arena : Arena) : VDKillQA(player, arena) {

	init {
		if (this.javaClass == VDBowKillQA::class.java) attempt()
	}
}