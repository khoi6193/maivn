package net.minevn.minigames.hooks.slave

import net.minevn.minigames.PlayerData
import net.minevn.minigames.Stat
import net.minevn.minigames.hooks.SlaveListener
import net.minevn.minigames.quests.QuestObjective
import net.minevn.minigames.quests.attempt.woolwars.WoolWarsQA
import net.minevn.woolwars.WoolWars
import net.minevn.woolwars.arena.Arena
import net.minevn.woolwars.arena.GameState
import net.minevn.woolwars.arena.player.PlayerInfo
import net.minevn.woolwars.events.ArenaGameEndEvent
import net.minevn.woolwars.events.ArenaPlayerDeathEvent
import net.minevn.woolwars.events.ArenaPlayerLeaveEvent
import net.minevn.woolwars.events.ArenaStateChangeEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

class WoolWarsListener : SlaveListener() {

	@EventHandler
	fun onQuit(e: ArenaPlayerLeaveEvent) {
		val player = e.player
		updateTop(e.arena, player, null)
		removeTomb(player)
	}

	@EventHandler
	fun onEnd(e: ArenaGameEndEvent) {
		val arena = e.arena
		val players = e.winTeam.members
		if (players.size == 0) return
		arena.players.forEach { (p: Player, info: PlayerInfo?) ->
			val win = info.team == e.winTeam
			if (win) {
				WoolWarsQA(PlayerData.getData(p), arena, QuestObjective.WW_WIN)
			}
			updateTop(e.arena, p, win)
		}
		playMVPM(players.maxBy {
			val stats = arena.getInfo(it).stats
			(stats.kills + stats.damage - stats.deaths)
		})

		WoolWarsQA(PlayerData.getData(players.maxBy { arena.getInfo(it).stats.blockPlaced }), arena, QuestObjective.WW_TOP_PLACE)
		WoolWarsQA(PlayerData.getData(players.maxBy { arena.getInfo(it).stats.blockBroken }), arena, QuestObjective.WW_TOP_BREAK)
		WoolWarsQA(PlayerData.getData(players.maxBy { arena.getInfo(it).stats.kills }), arena, QuestObjective.WW_TOP_KILL)
	}

	@EventHandler
	fun onDeath(e: ArenaPlayerDeathEvent) {
		val player = e.player
		val arena = WoolWars.getInstance().getArenaByPlayer(player)
		if (arena.centerCuboid.isIn(player)) {
			val relocation = player.eyeLocation.clone().add(3.0, 1.0, 3.0)
			addTomb(player, relocation)
		} else addTomb(player)
	}

	@EventHandler
	fun onStateChange(e: ArenaStateChangeEvent) {
		if (e.state == GameState.ACTIVE_ROUND) {
			removeAllTombs()
		}
	}

	private fun updateTop(arena: Arena, player: Player, win: Boolean?) {
		val pd = PlayerData.getData(player)
		val info = arena.getInfo(player).stats
		pd.updateStat(Stat.WW_KILL, info.kills.toDouble())
		pd.updateStat(Stat.WW_DEATH, info.deaths.toDouble())
		if (win == null) return
		pd.updateStat(Stat.WW_WIN, if (win) 1.0 else -1.0)
		if (win) pd.updateStat(Stat.WW_WIN_STREAK, 1.0)
		else pd.resetStat(Stat.WW_WIN_STREAK)
	}
}