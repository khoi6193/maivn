package net.minevn.minigames.hooks.slave

import com.andrei1058.bedwars.api.arena.IArena
import com.andrei1058.bedwars.api.arena.team.ITeam
import com.andrei1058.bedwars.api.events.gameplay.GameEndEvent
import com.andrei1058.bedwars.api.events.player.PlayerBedBreakEvent
import com.andrei1058.bedwars.api.events.player.PlayerKillEvent
import com.andrei1058.bedwars.api.events.player.PlayerLeaveArenaEvent
import com.andrei1058.bedwars.api.events.player.PlayerReSpawnEvent
import com.andrei1058.bedwars.api.events.team.TeamEliminatedEvent
import net.minevn.minigames.*
import net.minevn.minigames.hooks.SlaveListener
import net.minevn.minigames.quests.attempt.bedwars.BWBedDestroyQA
import net.minevn.minigames.quests.attempt.bedwars.BWKillQA
import net.minevn.minigames.quests.attempt.bedwars.BWWinQA
import net.minevn.mmclient.MatchMakerClient
import net.minevn.mmclient.api.SlaveStateChangeEvent
import net.minevn.mmclient.slaves.SlaveHandler
import net.minevn.mmclient.slaves.SlaveState
import net.minevn.mmclient.slaves.types.BW1058
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import java.util.*

class BedWarsListener : SlaveListener() {
	private var uuids: List<UUID>? = null
	private var uuidByTeam: Map<ITeam, List<UUID>>? = null
	private var teamByUuid: Map<UUID, ITeam>? = null
	var size = 0
		private set
	var uniqueIPs = 0
		private set

	private fun getMMC(): SlaveHandler = MatchMakerClient.getInstance().slaveHandler

	fun getOnlineSize() = uuids?.mapNotNull { Bukkit.getPlayer(it) }?.filter { it.isOnline }?.size ?: 0


	fun getOnlineUniqueIPs() =  uuids
		?.mapNotNull { Bukkit.getPlayer(it) }
		?.filter { it.isOnline }
		?.distinctBy { it.address.address.hostAddress }
		?.size ?: 0

	@EventHandler
	fun onGameStart(e: SlaveStateChangeEvent) {
		if (e.newState == SlaveState.PLAYING) {
			val arena = (getMMC() as BW1058).getArena()
			val players = getMMC().players!!
			uuids = players.map { it.uniqueId }
			uuidByTeam = players.groupBy { arena.getTeam(it) }.mapValues { it.value.map { p -> p.uniqueId } }
			teamByUuid = players.associate { it.uniqueId to arena.getTeam(it) }
			size = players.size
			uniqueIPs = players.distinctBy { it.address.address.hostAddress }.size
		}
	}

	@EventHandler
	fun onQuit(e: PlayerLeaveArenaEvent) {
		val player = e.player
		removeTomb(player)
	}

	@EventHandler
	fun onEndGame(e: GameEndEvent) {
		val arena = e.arena
		val winners = uuidByTeam!![e.teamWinner]!!
			.mapNotNull { Bukkit.getPlayer(it) }
			.filter { it.isOnline }
			.toMutableList()
		if (winners.size == 0) return
		winners.forEach { onEndGame(it, arena, e.teamWinner, winners) }
		playMVPM(winners.maxBy {
			val kills2 = arena.getPlayerKills(it, true) + arena.getPlayerKills(it, false)
			val beds2 = arena.getPlayerBedsDestroyed(it)
			val deaths2 = arena.getPlayerDeaths(it, true) + arena.getPlayerDeaths(it, false)
			kills2 - deaths2 + beds2 * 2
		})
	}

	@EventHandler
	fun onDeath(e: PlayerKillEvent) {
		val victim = e.victim
		addTomb(victim)

		// quest attempt
		val arena = e.arena
		val player = e.killer ?: return
		val cause = e.cause
		val playerData = PlayerData.getData(player)
		val team = teamByUuid!![player.uniqueId]!!
		val victimTeam = teamByUuid!![victim.uniqueId]!!
		BWKillQA(playerData, team, victim, victimTeam, cause, arena)
	}

	@EventHandler
	fun onTeamEliminated(e: TeamEliminatedEvent) {
		uuidByTeam?.get(e.team)
			?.mapNotNull { Bukkit.getPlayer(it) }
			?.filter { it.isOnline }
			?.forEach { onEndGame(it, e.arena, null, null) }
	}

	@EventHandler
	fun onBedDestroy(e: PlayerBedBreakEvent) {
		// quest attempt
		val arena = e.arena
		val player = e.player
		val playerData = PlayerData.getData(player) ?: return
		val team = teamByUuid!![player.uniqueId]!!
		val destroyedTeam = e.victimTeam
		BWBedDestroyQA(playerData, team, destroyedTeam, arena)
	}

	@EventHandler
	fun onRespawn(e: PlayerReSpawnEvent) {
		removeTomb(e.player)
	}

	private fun onEndGame(player: Player, arena: IArena, winnerTeam: ITeam?, winners: List<Player>?) {
		val playerData = PlayerData.getData(player)
		if (player.isMonke()) {
			playerData.sendMessage(Messages.MONKE_EXP)
			return
		}
		val team = teamByUuid!![player.uniqueId]!!
		val win = team == winnerTeam
		if (win) {
			BWWinQA(playerData, team, arena)
		}
		updateTop(arena, player, win)

		// tổng kết exp
		if (uniqueIPs >= Configs.minUniqueIPs) {
			var exp = 0
			if (win) exp += 2000
			val kill = arena.getPlayerKills(player, false)
			val finalKill = arena.getPlayerKills(player, true)
			val bedDestroy = arena.getPlayerBedsDestroyed(player)
			exp += 50 * kill
			exp += 100 * finalKill
			exp += 200 * bedDestroy
			exp += 10 * playerData.playTime
			exp += when (winners?.indexOf(player)) {
				0 -> 300
				1 -> 200
				2 -> 100
				else -> 0
			}

			// số người chơi
			val max = arena.maxPlayers
			val current = size
			val rate = current.toDouble() / max.toDouble()
			exp += (exp * rate).toInt()

			playerData.addExp(exp)
		} else {
			playerData.sendMessage(Messages.MSG_EXP_RECEIVE_NOT_ENOUGH_PLAYERS)
		}
	}

	private fun updateTop(arena: IArena, player: Player, win: Boolean?) {
		val pd = PlayerData.getData(player)

		val kill = arena.getPlayerKills(player, false).toDouble()
		val finalKill = arena.getPlayerKills(player, true).toDouble()
		val death = arena.getPlayerDeaths(player, false).toDouble()
		val finalDeath = arena.getPlayerDeaths(player, true).toDouble()
		val bedDestroy = arena.getPlayerBedsDestroyed(player).toDouble()
		pd.updateStat(Stat.BW_KILL, kill + finalKill)
		pd.updateStat(Stat.BW_FINALKILL, finalKill)
		pd.updateStat(Stat.BW_DEATH, death + finalDeath)
		pd.updateStat(Stat.BW_FINALDEATH, finalDeath)
		pd.updateStat(Stat.BW_BED, bedDestroy)
		if (win == null) return
		pd.updateStat(if (win) Stat.BW_WIN else Stat.BW_LOSE, 1.0)
		if (win) pd.updateStat(Stat.BW_WIN_STREAK, 1.0)
		else pd.resetStat(Stat.BW_WIN_STREAK)
	}
}