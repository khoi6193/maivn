package net.minevn.minigames.quests.attempt.murdermystery.killing

import net.minevn.minigames.PlayerData
import net.minevn.minigames.quests.QuestObjective
import net.minevn.minigames.quests.attempt.IGameKillQA
import net.minevn.minigames.quests.attempt.murdermystery.MurderMysteryQA
import org.bukkit.entity.Player
import plugily.projects.murdermystery.arena.Arena
import plugily.projects.murdermystery.arena.role.Role

open class MMKillQA(player: PlayerData, private val victim: Player, arena: Arena) :
	MurderMysteryQA(player, arena, QuestObjective.MM_KILL), IGameKillQA {
	val victimIsMurderer = Role.isRole(Role.MURDERER, victim, arena)
	val victimIsDetective = Role.isRole(Role.DETECTIVE, victim, arena)

	init {
		if (this.javaClass == MMKillQA::class.java) attempt()
	}

	override fun getVictim(): Player = victim
}