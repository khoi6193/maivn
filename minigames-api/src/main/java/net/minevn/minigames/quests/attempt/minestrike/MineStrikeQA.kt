package net.minevn.minigames.quests.attempt.minestrike

import net.minefs.MineStrike.Modes.Game
import net.minevn.minigames.PlayerData
import net.minevn.minigames.quests.QuestAttempt
import net.minevn.minigames.quests.QuestObjective

open class MineStrikeQA(player: PlayerData, val arena: Game, objective: QuestObjective, count: Int) :
	QuestAttempt(player, objective, count) {
	constructor(player: PlayerData, arena: Game, objective: QuestObjective) : this(player, arena, objective, 1)
}