package net.minevn.minigames.hooks.slave

import net.minevn.minigames.PlayerData
import net.minevn.minigames.Stat
import net.minevn.minigames.hooks.SlaveListener
import net.minevn.minigames.quests.attempt.villagedefense.VDWinQA
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.plugin.java.JavaPlugin
import plugily.projects.villagedefense.Main
import plugily.projects.villagedefense.api.StatsStorage
import plugily.projects.villagedefense.api.event.game.VillageGameLeaveAttemptEvent
import plugily.projects.villagedefense.api.event.game.VillageGameStopEvent
import plugily.projects.villagedefense.api.event.player.VillagePlayerRespawnEvent
import plugily.projects.villagedefense.arena.Arena

class VillageDefenseListener : SlaveListener() {
	val plugin = JavaPlugin.getPlugin(Main::class.java)

	@EventHandler
	fun onRespawn(e: VillagePlayerRespawnEvent) {
		removeTomb(e.player)
	}

	@EventHandler
	fun onEndGame(e: VillageGameStopEvent) {
		val arena = e.arena
		val players = arena.playersLeft
		val win = players.size > 0 && arena.villagers.size > 0
		arena.players.forEach {
			if (win) {
				VDWinQA(PlayerData.getData(it), arena)
			}
			updateTop(arena, it, win)
		}
		if (players.size == 0) return
		playMVPM(players.maxBy {
			val user = arena.plugin.userManager.getUser(it)
			user.getStat(StatsStorage.StatisticType.HIGHEST_WAVE) +
					user.getStat(StatsStorage.StatisticType.KILLS) -
					user.getStat(StatsStorage.StatisticType.DEATHS) +
					user.getStat(StatsStorage.StatisticType.ORBS) / 2
		})
	}

	private fun updateTop(arena: Arena, player: Player, win: Boolean?) {
		val pd = PlayerData.getData(player)
		val user = arena.plugin.userManager.getUser(player)
		pd.updateStat(Stat.VD_KILL, user.getStat(StatsStorage.StatisticType.KILLS).toDouble())
		pd.updateStat(Stat.VD_DEATH, user.getStat(StatsStorage.StatisticType.DEATHS).toDouble())
		if (win == null) return
		pd.updateStat(if (win) Stat.VD_WIN else Stat.VD_LOSE, 1.0)
		if (win) pd.updateStat(Stat.VD_WIN_STREAK, 1.0)
		else pd.resetStat(Stat.VD_WIN_STREAK)
	}

	@EventHandler
	fun onQuit(e: VillageGameLeaveAttemptEvent) {
//		updateTop(e.arena, e.player, null) // Chỉ update cuối trận
		removeTomb(e.player)
	}
}