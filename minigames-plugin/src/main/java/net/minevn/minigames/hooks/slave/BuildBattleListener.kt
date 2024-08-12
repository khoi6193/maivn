package net.minevn.minigames.hooks.slave

import net.minevn.minigames.PlayerData
import net.minevn.minigames.Stat
import net.minevn.minigames.hooks.SlaveListener
import net.minevn.minigames.quests.attempt.buildbattle.BBPointsQA
import net.minevn.minigames.quests.attempt.buildbattle.BBWinQA
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import plugily.projects.buildbattle.api.StatsStorage
import plugily.projects.buildbattle.api.event.game.BBGameEndEvent
import plugily.projects.buildbattle.api.event.game.BBGameLeaveEvent
import plugily.projects.buildbattle.arena.impl.BaseArena

class BuildBattleListener : SlaveListener() {

	@EventHandler
	fun onEndGame(e: BBGameEndEvent) {
		val arena = e.arena
		val players = arena.players
		players.forEach { p: Player ->
			val playerData = PlayerData.getData(p)
			val points = arena.plugin.userManager.getUser(p).getStat(StatsStorage.StatisticType.TOTAL_POINTS_EARNED)
			BBPointsQA(playerData, arena, points)
			updateTop(arena, p, players.size != 0)
		}
		if (players.size == 0) return
		val winner = players.maxBy {
			val user = arena.plugin.userManager.getUser(it)
			user.getStat(StatsStorage.StatisticType.TOTAL_POINTS_EARNED) +
					user.getStat(StatsStorage.StatisticType.SUPER_VOTES)
		}
		BBWinQA(PlayerData.getData(winner), arena)
		playMVPM(winner)
	}

	private fun updateTop(arena: BaseArena, player: Player, win: Boolean?) {
		val pd = PlayerData.getData(player)
		val user = arena.plugin.userManager.getUser(player)
		pd.updateStat(Stat.BB_POINTS, user.getStat(StatsStorage.StatisticType.TOTAL_POINTS_EARNED).toDouble())
		if (win == null) return
		pd.updateStat(if (win) Stat.BB_WIN else Stat.BB_LOSE, 1.0)
		if (win) pd.updateStat(Stat.BB_WIN_STREAK, 1.0)
		else pd.resetStat(Stat.BB_WIN_STREAK)
	}

	@EventHandler
	fun onQuit(e: BBGameLeaveEvent) {
		updateTop(e.arena, e.player, null)
	}
}