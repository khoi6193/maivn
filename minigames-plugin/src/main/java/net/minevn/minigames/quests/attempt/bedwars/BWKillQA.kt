package net.minevn.minigames.quests.attempt.bedwars

import com.andrei1058.bedwars.api.arena.IArena
import com.andrei1058.bedwars.api.arena.team.ITeam
import com.andrei1058.bedwars.api.events.player.PlayerKillEvent.PlayerKillCause
import net.minevn.minigames.PlayerData
import net.minevn.minigames.quests.QuestObjective
import net.minevn.minigames.quests.attempt.IGameKillQA
import org.bukkit.entity.Player

class BWKillQA(
	player: PlayerData, team: ITeam, private val victim: Player, val victimTeam: ITeam,
	cause: PlayerKillCause, arena: IArena
) : BedWarsQA(player, team, arena, QuestObjective.BW_KILL), IGameKillQA {
	val isFinalKill = cause.name.contains("FINAL")
	val isBowKill = cause == PlayerKillCause.PLAYER_SHOOT || cause == PlayerKillCause.PLAYER_SHOOT_FINAL_KILL

	override fun getVictim(): Player = victim

	init {
		if (this.javaClass == BWKillQA::class.java) attempt()
	}
}