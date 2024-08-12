package net.minevn.minigames.quests.attempt.minestrike.killing

import net.minefs.MineStrike.Modes.Game
import net.minefs.MineStrike.Skins.Knife
import net.minevn.minigames.PlayerData
import org.bukkit.entity.Player

class MSKnifeKillQA(
	player: PlayerData, victim: Player, arena: Game, val knife: Knife
) : MSKillQA(player, victim, arena) {

	init {
		if (this.javaClass == MSKnifeKillQA::class.java) attempt()
	}
}