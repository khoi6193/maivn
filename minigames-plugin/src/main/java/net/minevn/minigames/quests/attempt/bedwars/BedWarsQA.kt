package net.minevn.minigames.quests.attempt.bedwars

import com.andrei1058.bedwars.api.arena.IArena
import com.andrei1058.bedwars.api.arena.team.ITeam
import net.minevn.minigames.PlayerData
import net.minevn.minigames.quests.QuestAttempt
import net.minevn.minigames.quests.QuestObjective

open class BedWarsQA(player: PlayerData, val team: ITeam, val arena: IArena, objective: QuestObjective, count: Int) :
	QuestAttempt(player, objective, count) {
	constructor(
		player: PlayerData, team: ITeam, arena: IArena, objective: QuestObjective
	) : this(player, team, arena, objective, 1)

	val teamSize = arena.maxInTeam

	init {
		if (this.javaClass == BedWarsQA::class.java) attempt()
	}
}