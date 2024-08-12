package net.minevn.minigames.quests.attempt.murdermystery.killing

import net.minevn.minigames.PlayerData
import org.bukkit.entity.Player
import plugily.projects.murdermystery.arena.Arena

class MMKnifeKillQA(player : PlayerData, victim: Player, arena : Arena) : MMKillQA(player, victim, arena) {
	init {
		if (this.javaClass == MMKnifeKillQA::class.java) attempt()
	}
}