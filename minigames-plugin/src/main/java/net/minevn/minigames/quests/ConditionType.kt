package net.minevn.minigames.quests

import me.clip.placeholderapi.PlaceholderAPI
import net.minefs.MineStrike.Grenades.GrenadeType
import net.minefs.MineStrike.Guns.GunCategory
import net.minefs.MineStrike.Main
import net.minevn.minigames.Minigames
import net.minevn.minigames.Stat
import net.minevn.minigames.hooks.slave.BedWarsListener
import net.minevn.minigames.hooks.slave.MurderMysteriesListener
import net.minevn.minigames.hooks.slave.VillageDefenseListener
import net.minevn.minigames.items.types.MSGunPI
import net.minevn.minigames.quests.attempt.bedwars.BWKillQA
import net.minevn.minigames.quests.attempt.bedwars.BedWarsQA
import net.minevn.minigames.quests.attempt.buildbattle.BBPointsQA
import net.minevn.minigames.quests.attempt.minestrike.killing.MSGrenadeKillQA
import net.minevn.minigames.quests.attempt.minestrike.killing.MSGunKillQA
import net.minevn.minigames.quests.attempt.minestrike.killing.MSKillQA
import net.minevn.minigames.quests.attempt.minestrike.killing.MSKnifeKillQA
import net.minevn.minigames.quests.attempt.murdermystery.MurderMysteryQA
import net.minevn.minigames.quests.attempt.murdermystery.killing.MMBowKillQA
import net.minevn.minigames.quests.attempt.murdermystery.killing.MMKnifeKillQA
import net.minevn.minigames.quests.attempt.thelab.TheLabQA
import net.minevn.minigames.quests.attempt.villagedefense.killing.VDBowKillQA
import net.minevn.minigames.quests.attempt.villagedefense.killing.VDKnifeKillQA
import net.minevn.mmclient.MatchMakerClient
import plugily.projects.villagedefense.api.StatsStorage

enum class ConditionType {
	/**
	 * Roll từ 0 đến 10000, nhỏ hơn giá trị truyền vào thì pass condition
	 *
	 * Thường dùng cho drop
	 */
	ROLL {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return (0..10000).random() < condition.value.toInt()
		}
	},

	IPS_GTE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			val handler = slaveHandler()
			val value = condition.value.toInt()
			if (handler is BedWarsListener) {
				return handler.uniqueIPs >= value
			}
			return mmc().slaveHandler.onlineUniqueIPs >= value
		}
	},

	PLAYERS_GTE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			val handler = slaveHandler()
			val value = condition.value.toInt()
			if (handler is BedWarsListener) {
				return handler.size >= value
			}
			return mmc().slaveHandler.players.size >= value
		}
	},

	IN_MODES {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			val modes = condition.value.split(",").map { it.trim() }
			return mmc().info?.modeID?.let { modes.contains(it) } == true
		}
	},

	IN_MAPS {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			val maps = condition.value.split(",").map { it.trim() }
			return mmc().info?.mapID?.let { maps.contains(it) } == true
		}
	},

	PLAYER_HEALTH_LT {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt.player.player.health < condition.value.toDouble()
		}
	},

	PLAYER_IS_JUMPING {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return !attempt.player.player.isOnGround
		}
	},

	IS_ALIVE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return PlaceholderAPI.setPlaceholders(attempt.player.player, "%minevn_dead%") == ""
		}
	},

	//todo need test
	STREAK {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			//check if player has enough streak
			val pd = attempt.player
			val streak = pd.getStat(when (mmc().info.mode) {
				"bedwars" -> Stat.BW_WIN_STREAK.toString()
				"murdermystery" -> Stat.MM_WIN_STREAK.toString()
				"villagedefense" -> Stat.VD_WIN_STREAK.toString()
				"buildbattle" -> Stat.BB_WIN_STREAK.toString()
				"thelab" -> Stat.TL_TOP_STREAK.toString()
				"minestrike" -> Stat.MS_CP_WIN_STREAK.toString()
				else -> return false
			}).toInt()
			return streak >= attempt.count
		}
	},

	/**
	 * Số người tối thiểu trong các mode solo, duo, trio, squad cách nhau dấu |
	 *
	 * Ví dụ nếu khai báo 3|5|8|10 thì lần lượt solo, duo, trio, squad cần tối thiểu 3 người, 5 người, 8 và 10 người.
	 *
	 * Có thể dùng condition này để chỉnh quest chỉ áp dụng cho một số mode, vd chỉ áp dụng cho solo/duo thì tăng
	 * số người yêu cầu của trio & squad lên 99
	 */
	BW_PLAYERS_GTE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			if (attempt !is BedWarsQA) return false
			val min = condition.value.split("|").map { it.toInt() }
			val index = attempt.teamSize - 1
			return mmc().slaveHandler.onlinePlayers >= min[index]
		}
	},

	BW_FINAL_KILL {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is BWKillQA && attempt.isFinalKill
		}
	},

	BW_KILL_WITH_BOW {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is BWKillQA && attempt.isBowKill
		}
	},

	BW_SOLO_OR_DUO {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is BedWarsQA && (attempt.teamSize == 1 || attempt.teamSize == 2)
		}
	},

	BW_TRIO_OR_SQUAD {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is BedWarsQA && (attempt.teamSize == 3 || attempt.teamSize == 4)
		}
	},

	BW_BED_DESTROYED {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is BedWarsQA && attempt.team.isBedDestroyed == condition.value.toBoolean()
		}
	},

	BW_TEAM_MEMBER_REMAIN {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is BedWarsQA && attempt.team.size <= condition.value.toInt()
		}
	},

	MS_KILL_WITH_GUN {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is MSGunKillQA && condition.value.split(",").contains(attempt.gun.name)
		}
	},

	MS_KILL_WITH_BASE_GUN {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is MSGunKillQA && condition.value.split(",").contains(attempt.gun.base.name)
		}
	},

	MS_KILL_HEADSHOT {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is MSGunKillQA && attempt.headShot
		}
	},

	MS_KILL_BLOCKBANG {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is MSGunKillQA && attempt.blockBang
		}
	},

	MS_KILL_WITH_KNIFE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is MSKnifeKillQA
		}
	},

	MS_KILL_WITH_GRENADE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is MSGrenadeKillQA
		}
	},

	MS_KILL_WITH_KNIFE_OR_GRENADE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is MSGrenadeKillQA || attempt is MSKnifeKillQA
		}
	},

	MS_KILL_WITH_EXPLOSIVE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is MSGrenadeKillQA && attempt.grenade.grenadeType == GrenadeType.FRAG
		}
	},

	MS_KILL_WITH_FIRE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return if (attempt is MSGrenadeKillQA) {
				val type = attempt.grenade.grenadeType
				type == GrenadeType.FIRE || type == GrenadeType.INCENDIARY
			} else false
		}
	},

	MS_KILL_WITH_SNIPER {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is MSGunKillQA && attempt.gun.hasSnipe()
		}
	},

	MS_KILL_LAST_AMMO {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is MSGunKillQA && attempt.ammo.ammo == 0
		}
	},

	MS_KILL_WITH_RIFLE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is MSGunKillQA && attempt.gun.category == GunCategory.RIFLE
		}
	},

	MS_KILL_WITH_SHOTGUN {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is MSGunKillQA && attempt.gun.category == GunCategory.SHOTGUN
		}
	},

	MS_KILL_WITH_SMG {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is MSGunKillQA && attempt.gun.category == GunCategory.SMG
		}
	},

	MS_KILL_WITH_MG {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is MSGunKillQA && attempt.gun.category == GunCategory.MG
		}
	},

	MS_KILL_WITH_PISTOL {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is MSGunKillQA && attempt.gun.category == GunCategory.PISTOL
		}
	},

	MS_KILL_STREAK {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			if (attempt !is MSKillQA) return false
			val ms = Minigames.getInstance().msHook
			return ms.getStatus(attempt.player.player)!!.roundKills % condition.value.toInt() == 0
		}
	},

	MS_KILL_VICTIM_HOLDING_GUN {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			if (attempt !is MSKillQA) return false
			val item = attempt.getVictim().inventory.itemInMainHand
			val gun = Main.getInstance().getGun(item) ?: return false
			return condition.value.split(",").contains(gun.name)
		}
	},

	MS_PLAYER_HAVE_GUN {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt.player.getItem(MSGunPI::class.java, condition.value) != null
		}
	},

	MM_KILL_WITH_KNIFE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is MMKnifeKillQA
		}
	},

	MM_KILL_WITH_BOW {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is MMBowKillQA
		}
	},

	MM_ROLE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			val qa = attempt as? MurderMysteryQA?: return false
			return condition.value.lowercase().split(",").map { it.trim() }.let {
				(it.contains("murderer") && qa.playerIsMurderer) || (it.contains("detective") && qa.playerIsDetective)
						|| (it.contains("hero") && qa.playerIsHero) || it.contains("innocent")
			}
		}
	},

	MM_INNOCENT_REMAIN {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			val handler = slaveHandler() as? MurderMysteriesListener ?: return false
			val arena = handler.plugin.userManager.getUser(attempt.player.player).arena
			return arena.playersLeft.size <= condition.value.toInt()
		}
	},

	MM_TIME_REMAIN {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			val handler = slaveHandler() as? MurderMysteriesListener ?: return false
			val arena = handler.plugin.userManager.getUser(attempt.player.player).arena
			return arena.timer <= condition.value.toInt()
		}
	},

	VD_KILL_WITH_KNIFE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is VDKnifeKillQA
		}
	},

	VD_KILL_WITH_BOW {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return attempt is VDBowKillQA
		}
	},

	VD_VILLAGER_ALIVE_PERCENT_GTE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			val handler = slaveHandler() as? VillageDefenseListener ?: return false
			val arena = handler.plugin.userManager.getUser(attempt.player.player).arena
			val dead = arena.villagers.count { it.isDead }
			return (arena.villagers.size - dead) / (arena.villagers.size * 100) >= condition.value.toDouble()
		}
	},

	VD_SPENT_ORB_TOP {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			val handler = slaveHandler() as? VillageDefenseListener ?: return false
			val player = attempt.player
			val user = handler.plugin.userManager.getUser(player.player)
			val users = handler.plugin.userManager.getUsers(user.arena).toList()
			users.sortedByDescending { it.getStat(StatsStorage.StatisticType.ORBS) }.let {
				return it.indexOf(user) <= condition.value.toInt()
			}
		}
	},

	VD_KILL_TOP {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			val handler = slaveHandler() as? VillageDefenseListener ?: return false
			val player = attempt.player
			val user = handler.plugin.userManager.getUser(player.player)
			val users = handler.plugin.userManager.getUsers(user.arena).toList()
			users.sortedByDescending { it.getStat(StatsStorage.StatisticType.KILLS) }.let {
				return it.indexOf(user) <= condition.value.toInt()
			}
		}
	},

	VD_XP_TOP {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			val handler = slaveHandler() as? VillageDefenseListener ?: return false
			val player = attempt.player
			val user = handler.plugin.userManager.getUser(player.player)
			val users = handler.plugin.userManager.getUsers(user.arena).toList()
			users.sortedByDescending { it.getStat(StatsStorage.StatisticType.XP) }.let {
				return it.indexOf(user) <= condition.value.toInt()
			}
		}
	},

	VD_LEVEL_TOP {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			val handler = slaveHandler() as? VillageDefenseListener ?: return false
			val player = attempt.player
			val user = handler.plugin.userManager.getUser(player.player)
			val users = handler.plugin.userManager.getUsers(user.arena).toList()
			users.sortedByDescending { it.getStat(StatsStorage.StatisticType.LEVEL) }.let {
				return it.indexOf(user) <= condition.value.toInt()
			}
		}
	},

	TL_TOP {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			if (attempt !is TheLabQA) return false
			val size = condition.value.toInt()
			if (attempt.top.size < size) return true
			val top = attempt.top.toMutableList()
			return top.indexOf(attempt.player.player) <= size
		}
	},

	BB_POINTS {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean {
			return if (attempt is BBPointsQA) {
				val points = condition.value.toIntOrNull()
				points != null && attempt.count >= points
			} else return false
		}
	};

	abstract fun check(attempt: QuestAttempt, condition: Condition): Boolean

	companion object {
		private fun mmc() = MatchMakerClient.getInstance()
		private fun slaveHandler() = Minigames.getInstance().slaveListener
	}
}