package net.minevn.minigames.award.types

import net.minevn.minigames.PlayerData
import net.minevn.minigames.award.PlayerAward
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ExpAW(amount: Int) : PlayerAward(null, amount) {
	override fun apply(p: Player, note: String) {
		PlayerData.getData(p)?.addExp(amount)
	}

	override fun getGuiItem(): ItemStack = ItemStack(Material.GOLD_INGOT)

	override fun getName(): String = "§e$amount §2EXP"

	override fun typeName(): String = ""

	companion object {
		/**
		 * valid data: number
		 */
		@JvmStatic
		fun fromData(input: String): ExpAW = ExpAW(input.toInt())
	}
}