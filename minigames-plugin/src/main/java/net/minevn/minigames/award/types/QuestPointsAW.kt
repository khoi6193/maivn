package net.minevn.minigames.award.types

import net.minevn.minigames.PlayerData
import net.minevn.minigames.award.PlayerAward
import net.minevn.minigames.quests.QuestAttempt
import net.minevn.minigames.quests.QuestObjective
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class QuestPointsAW(amount: Int) : PlayerAward(null, amount) {
	override fun apply(p: Player, note: String) {
		val playerData = PlayerData.getData(p) ?: return
		playerData.addQuestPoints(amount)
		QuestAttempt(playerData, QuestObjective.QUEST_POINTS_EARN, amount)
	}

	override fun getGuiItem(): ItemStack = ItemStack(Material.EMERALD)

	override fun getName(): String = "§e$amount §2QP"

	override fun typeName(): String = ""

	companion object {
		/**
		 * valid data: number
		 */
		@JvmStatic
		fun fromData(input: String): QuestPointsAW = QuestPointsAW(input.toInt())
	}

}