package net.minevn.minigames.quests.attempt.minestrike.killing

import net.minefs.MineStrike.Grenades.Grenade
import net.minefs.MineStrike.Modes.Game
import net.minevn.minigames.PlayerData
import org.bukkit.entity.Player

class MSGrenadeKillQA(
	player: PlayerData, victim: Player, arena: Game, val grenade: Grenade
) : MSKillQA(player, victim, arena) {

	init {
		if (this.javaClass == MSGrenadeKillQA::class.java) attempt()
	}
}