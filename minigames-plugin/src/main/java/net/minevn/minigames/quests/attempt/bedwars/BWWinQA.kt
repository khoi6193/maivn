package net.minevn.minigames.quests.attempt.bedwars

import com.andrei1058.bedwars.api.arena.IArena
import com.andrei1058.bedwars.api.arena.team.ITeam
import net.minevn.minigames.PlayerData
import net.minevn.minigames.quests.QuestObjective

class BWWinQA(player: PlayerData, team: ITeam, arena: IArena) : BedWarsQA(player, team, arena, QuestObjective.BW_WIN) {
	init {
		if (this.javaClass == BWWinQA::class.java) attempt()
	}
}