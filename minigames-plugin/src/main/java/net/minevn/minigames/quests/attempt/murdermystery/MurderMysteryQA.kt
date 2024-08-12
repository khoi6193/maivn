package net.minevn.minigames.quests.attempt.murdermystery

import net.minevn.minigames.PlayerData
import net.minevn.minigames.quests.QuestAttempt
import net.minevn.minigames.quests.QuestObjective
import plugily.projects.murdermystery.arena.Arena
import plugily.projects.murdermystery.arena.role.Role

open class MurderMysteryQA(player: PlayerData, val arena: Arena, objective: QuestObjective, count: Int) :
	QuestAttempt(player, objective, count) {
	constructor(player: PlayerData, arena: Arena, objective: QuestObjective) : this(player, arena, objective, 1)
	
	val playerIsMurderer = Role.isRole(Role.MURDERER, player.player, arena)
	val playerIsDetective = Role.isRole(Role.DETECTIVE, player.player, arena)
	val playerIsHero = arena.getCharacter(Arena.CharacterType.HERO)?.equals(player) == true

	init {
		if (this.javaClass == MurderMysteryQA::class.java) attempt()
	}
}