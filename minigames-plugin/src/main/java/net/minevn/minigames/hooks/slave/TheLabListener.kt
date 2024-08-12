package net.minevn.minigames.hooks.slave

import net.minevn.minigames.PlayerData
import net.minevn.minigames.Stat
import net.minevn.minigames.hooks.SlaveListener
import net.minevn.minigames.quests.QuestObjective
import net.minevn.minigames.quests.attempt.thelab.TheLabQA
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import ro.Fr33styler.TheLab.Api.GameEndEvent
import ro.Fr33styler.TheLab.Api.GameLeaveEvent
import ro.Fr33styler.TheLab.Main

class TheLabListener : SlaveListener() {
	@EventHandler
	fun onQuit(e: GameLeaveEvent) {
		val player = e.player
		removeTomb(player)
	}

	@EventHandler
	fun onEnd(e: GameEndEvent) {
		val players = e.players
		val top = e.top.takeIf { it.isNotEmpty() }?.toList()
		players.forEach { player: Player -> updateTop(player, top) }
		if (top == null) return
		top.forEach { TheLabQA(manager.getGame(it), PlayerData.getData(it), top, QuestObjective.TL_WIN) }
		playMVPM(top[0])
	}

	private fun updateTop(player: Player, top: List<Player>?) {
		val pd = PlayerData.getData(player)
		pd.updateStat(Stat.TL_POINTS, manager.getGame(player).atoms[player]!!.toDouble())
		if (top == null) return
		if (top.contains(player)) pd.updateStat(Stat.TL_TOP_STREAK, 1.0)
		else pd.resetStat(Stat.TL_TOP_STREAK)
	}

	companion object {
		private val main = Bukkit.getServer().pluginManager.getPlugin("TheLab")!! as Main
		private val manager = main.manager
	}
}