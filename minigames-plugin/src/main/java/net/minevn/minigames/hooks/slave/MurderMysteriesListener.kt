package net.minevn.minigames.hooks.slave

import net.minevn.minigames.PlayerData
import net.minevn.minigames.Stat
import net.minevn.minigames.hooks.SlaveListener
import net.minevn.minigames.quests.QuestObjective
import net.minevn.minigames.quests.attempt.murdermystery.MMWinQA
import net.minevn.minigames.quests.attempt.murdermystery.MurderMysteryQA
import net.minevn.minigames.quests.attempt.murdermystery.killing.MMBowKillQA
import net.minevn.minigames.quests.attempt.murdermystery.killing.MMKnifeKillQA
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.plugin.java.JavaPlugin
import plugily.projects.murdermystery.Main
import plugily.projects.murdermystery.api.StatsStorage
import plugily.projects.murdermystery.api.events.game.MMGameLeaveAttemptEvent
import plugily.projects.murdermystery.api.events.game.MMGameStopEvent
import plugily.projects.murdermystery.api.events.player.MMPlayerStatisticChangeEvent
import plugily.projects.murdermystery.arena.ArenaRegistry
import plugily.projects.murdermystery.arena.role.Role

class MurderMysteriesListener : SlaveListener() {
	val plugin = JavaPlugin.getPlugin(Main::class.java)

	@EventHandler
	fun onQuit(e: MMGameLeaveAttemptEvent) {
		val player = e.player
	}

	@EventHandler
	fun onEnd(e: MMGameStopEvent) {
		val arena = e.arena
		val players = ArrayList(arena.players.toList())
		val members = players.filter { !Role.isRole(Role.MURDERER, it, arena) }
		val murderWin = arena.playersLeft.size == arena.aliveMurderer()
		players.forEach {
			val win = if (arena.isMurderAlive(it)) murderWin else members.isNotEmpty()
			if (win) MMWinQA(PlayerData.getData(it), arena)
			updateTop(it, win)
		}
		playMVPM(players.maxBy {
			val user = plugin.userManager.getUser(it)
			user.getStat(StatsStorage.StatisticType.KILLS) - user.getStat(StatsStorage.StatisticType.DEATHS)
		})
	}

	/**
	 * Not sure if this works
	 */
	@EventHandler(ignoreCancelled = true)
	fun onDeath(e: EntityDamageByEntityEvent) {
		if (e.entity is Player) {
			val victim = e.entity as Player
			if (e.finalDamage < victim.health) return
			val damager = e.damager
			var killer: Player? = null
			var projectileKill = false
			if (damager is Projectile && damager.shooter is Player) {
				killer = damager.shooter as Player
				projectileKill = true
			} else if (damager is Player) {
				killer = damager
			}
			if (killer != null) {
				val arena = ArenaRegistry.getArena(killer)
				val data = PlayerData.getData(killer)
				if (projectileKill) {
					MMBowKillQA(data, victim, arena)
				} else {
					MMKnifeKillQA(data, victim, arena)
				}
			}
		}
	}

	@EventHandler
	fun onPickGold(e: MMPlayerStatisticChangeEvent) {
		val arena = e.arena
		val amount = e.number
		val data = PlayerData.getData(e.player)
		when (e.statisticType) {
			StatsStorage.StatisticType.LOCAL_GOLD -> MurderMysteryQA(data, arena, QuestObjective.MM_PICK_GOLD, amount)
			StatsStorage.StatisticType.LOCAL_SCORE -> MurderMysteryQA(data, arena, QuestObjective.MM_SCORE, amount)
			else -> return
		}
	}

	private fun updateTop(player: Player, win: Boolean?) {
		val pd = PlayerData.getData(player)
		val user = plugin.userManager.getUser(player)
		pd.updateStat(Stat.MM_KILL, user.getStat(StatsStorage.StatisticType.KILLS).toDouble())
		pd.updateStat(Stat.MM_DEATH, user.getStat(StatsStorage.StatisticType.DEATHS).toDouble())
		if (win == null) return
		pd.updateStat(if (win) Stat.MM_WIN else Stat.MM_LOSE, 1.0)
		if (win) pd.updateStat(Stat.MM_WIN_STREAK, 1.0)
		else pd.resetStat(Stat.MM_WIN_STREAK)
	}
}