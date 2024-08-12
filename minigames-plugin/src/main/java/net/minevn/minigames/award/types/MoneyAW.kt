package net.minevn.minigames.award.types

import net.minevn.minigames.Minigames
import net.minevn.minigames.award.PlayerAward
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class MoneyAW(amount: Int) : PlayerAward(null, amount) {
	override fun apply(p: Player, note: String) {
		Minigames.getInstance().economy.depositPlayer(p, amount.toDouble())
	}

	override fun getGuiItem(): ItemStack = ItemStack(Material.GOLD_INGOT)

	override fun getName(): String = "§e$amount §2MG"

	override fun typeName(): String = ""

	companion object {
		/**
		 * valid data: number
		 */
		@JvmStatic
		fun fromData(input: String): MoneyAW = MoneyAW(input.toInt())
	}
}