package net.minevn.minigames.quests

import net.minevn.minigames.Messages
import net.minevn.minigames.PlayerData
import net.minevn.minigames.Utils
import net.minevn.minigames.award.PlayerAward
import org.bukkit.Sound

open class QuestAttempt(val player: PlayerData, val objective: QuestObjective, val count: Int) {
	constructor(player: PlayerData, objective: QuestObjective) : this(player, objective, 1)

	init {
		if (this.javaClass == QuestAttempt::class.java) attempt()
	}

	fun attempt() {
		player.debug("§a[QUEST] triggered Objective §b§l${objective.name} (${javaClass.simpleName}) " +
				"§aamount §b§l$count")
		val list = Quest.getByObjective(objective) ?: return
		for (quest in list) {
			if (!quest.checkCondition(this)) {
				player.debug("§c[QUEST] §c§l${quest.id} §ccondition check failed")
				continue
			}
			val pq: PlayerQuest = quest.getPlayerQuest(player)
			synchronized(pq) {
				if (pq.finished) {
					player.debug("§c[QUEST] §c§l${quest.id} §cfinished")
					return@synchronized
				}
				if (quest.mustObtain && !pq.obtained) {
					player.debug("§c[QUEST] §c§l${quest.id} §cnot obtained")
					return@synchronized
				}
				pq.count(count)
				quest.announce(player.player, pq.done)
				player.debug("§a[QUEST] §a§l${quest.id} §acount")
				if (pq.done < quest.done) return@synchronized
				pq.finish()
				player.player.playSound(player.player.location, Sound.ENTITY_PLAYER_LEVELUP, 100f, 2f)
				quest.doneMessage.forEach {
					player.sendMessage(it.replace("%name%", quest.name))
				}
				Utils.runNotSync {
					if (quest.randomAward) {
						applyAward(quest.awards.random(), quest.name)
					} else {
						quest.awards.forEach { applyAward(it, quest.name) }
					}
				}
				if (quest.cooldown > 0 && pq.nextReset == 0L) pq.nextReset(System.currentTimeMillis() + quest.cooldown)
			}
		}
	}

	private fun applyAward(award: PlayerAward, questID: String) {
		award.apply(player.player, "from quest $questID")
		player.sendMessage(Messages.MSG_QUEST_AWARD.replace("%item%", award.name))
	}
}