package net.minevn.minigames.quests.attempt.minestrike.killing

import net.minefs.MineStrike.Modes.Game
import net.minevn.minigames.PlayerData
import net.minevn.minigames.quests.QuestObjective
import net.minevn.minigames.quests.attempt.IGameKillQA
import net.minevn.minigames.quests.attempt.minestrike.MineStrikeQA
import org.bukkit.entity.Player

/**
 * Type chung cho objective MS_KILL
 *
 * Các type con như MSGunKillQA, MSKnifeKillQA dùng để check condition
 */
open class MSKillQA(player: PlayerData, private val victim: Player, arena: Game) :
	MineStrikeQA(player, arena, QuestObjective.MS_KILL), IGameKillQA {
	override fun getVictim(): Player = victim

	init {
		if (this.javaClass == MSKillQA::class.java) attempt()
	}
}