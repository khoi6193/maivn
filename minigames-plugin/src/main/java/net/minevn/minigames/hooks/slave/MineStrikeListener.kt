package net.minevn.minigames.hooks.slave

import net.minefs.MineStrike.Modes.Competitive
import net.minevn.minigames.Minigames
import net.minevn.minigames.PlayerData
import net.minevn.minigames.Stat
import net.minevn.minigames.hooks.SlaveListener
import net.minevn.mmclient.api.SlaveStateChangeEvent
import net.minevn.mmclient.slaves.SlaveState
import org.bukkit.event.EventHandler

class MineStrikeListener : SlaveListener() {
	@EventHandler
	fun onSlaveStateChange(event: SlaveStateChangeEvent) {
		if (event.newState == SlaveState.ENDING) {
			val main = Minigames.getInstance()!!
			val ms = main.msHook
			val game = ms?.game!!.takeIf { it.javaClass == Competitive::class.java } ?: return
			val stats = game.stats!!
			stats.forEach {
				val pd = PlayerData.getData(it.key) ?: return@forEach
				val msd = it.value
				pd.updateStat(Stat.MS_CP_PLAY, 1.0)
				pd.updateStat(Stat.MS_CP_TIMEPLAY, msd.timePlayed.toDouble())
				pd.updateStat(Stat.MS_CP_KILL, msd.kills.toDouble())
				pd.updateStat(Stat.MS_CP_DEATH, msd.deaths.toDouble())
				pd.updateStat(Stat.MS_CP_WIN, msd.wins.toDouble())
				pd.updateStat(Stat.MS_CP_LOSE, msd.loses.toDouble())
				pd.updateStat(Stat.MS_CP_RWIN, msd.gWin.toDouble())
				pd.updateStat(Stat.MS_CP_RLOSE, msd.gLose.toDouble())
				pd.updateStat(Stat.MS_CP_BOMBDEFUSE, msd.bombDefused.toDouble())
				pd.updateStat(Stat.MS_CP_BOMBPLANT, msd.bombPlanted.toDouble())
				pd.updateStat(Stat.MS_CP_KNIFE_KILL, msd.knifeKill.toDouble())
				pd.updateStat(Stat.MS_CP_HEADSHOTS_KILL, msd.headshotKill.toDouble())
				pd.updateStat(Stat.MS_CP_MVP, msd.mvPs.toDouble())

				if (msd.loses <= 0) pd.updateStat(Stat.MS_CP_WIN_STREAK, 1.0)
				else pd.resetStat(Stat.MS_CP_WIN_STREAK)
			}
		}
	}
}