package net.minevn.minigames.quests.attempt.bedwars

import com.andrei1058.bedwars.api.arena.IArena
import com.andrei1058.bedwars.api.arena.team.ITeam
import net.minevn.minigames.PlayerData
import net.minevn.minigames.quests.QuestObjective

class BWBedDestroyQA(player: PlayerData, team: ITeam, val destroyedTeam: ITeam, arena: IArena) :
	BedWarsQA(player, team, arena, QuestObjective.BW_BEDDESTROY) {

	init {
		if (this.javaClass == BWBedDestroyQA::class.java) attempt()
	}
}
