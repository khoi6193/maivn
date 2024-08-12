package net.minevn.minigames.quests.attempt.minestrike

import net.minefs.MineStrike.Modes.Game
import net.minevn.minigames.PlayerData
import net.minevn.minigames.quests.QuestObjective

class MSWin(player: PlayerData, game: Game) : MineStrikeQA(player, game, QuestObjective.MS_WIN) {
	init {
		if (this.javaClass == MSWin::class.java) attempt()
	}
}