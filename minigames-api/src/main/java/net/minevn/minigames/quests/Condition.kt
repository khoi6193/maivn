package net.minevn.minigames.quests

class Condition(val type: ConditionType, val value: String) {

	fun check(a: QuestAttempt): Boolean = type.check(a, this)
}