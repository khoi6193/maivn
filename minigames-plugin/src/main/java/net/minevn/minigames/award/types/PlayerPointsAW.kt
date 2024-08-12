package net.minevn.minigames.award.types

import net.minevn.minigames.Minigames
import net.minevn.minigames.award.PlayerAward
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class PlayerPointsAW(amount: Int) : PlayerAward(null, amount) {
	override fun apply(p: Player, note: String) {
		Minigames.getInstance().playerPoints.api.give(p.uniqueId, amount)
	}

	override fun getGuiItem(): ItemStack = ItemStack(Material.DIAMOND)

	override fun getName(): String = "§b$amount §2Points"

	override fun typeName(): String = ""

	companion object {
		/**
		 * valid data: number
		 */
		@JvmStatic
		fun fromData(input: String): PlayerPointsAW = PlayerPointsAW(input.toInt())
	}
}