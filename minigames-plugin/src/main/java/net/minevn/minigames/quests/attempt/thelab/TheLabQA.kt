package net.minevn.minigames.quests.attempt.thelab

import net.minevn.minigames.PlayerData
import net.minevn.minigames.quests.QuestAttempt
import net.minevn.minigames.quests.QuestObjective
import org.bukkit.entity.Player
import ro.Fr33styler.TheLab.Handler.Game

class TheLabQA(
	val arena: Game, player: PlayerData, val top: List<Player>, objective: QuestObjective
) : QuestAttempt(player, objective) {
	init {
		if (this.javaClass == TheLabQA::class.java) attempt()
	}
}