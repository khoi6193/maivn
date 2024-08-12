package net.minevn.minigames.award.types

import net.minevn.minigames.PlayerData
import net.minevn.minigames.award.PlayerAward
import net.minevn.minigames.items.PlayerItem
import net.minevn.minigames.items.StackablePI
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class StackableAW(private val item: StackablePI) : PlayerAward(ItemUnit.PCS, item.amount) {
	override fun apply(player: Player, note: String) {
		val data = PlayerData.getData(player) ?: return
		item.amount = amount
		data.addItem(item, "$note (StackableAW)")
	}

	override fun getGuiItem(): ItemStack = item.item

	override fun getName(): String = item.item.itemMeta.displayName

	override fun typeName(): String = item.categoryName

	companion object {
		// statics
		/**
		 * valid data: StackablePIType$amount
		 * example: SpeakerWorldPI$7
		 */
		@Throws(Exception::class)
		@JvmStatic
		fun fromData(data: String): StackableAW {
			val arr = data.split("$")
			val type = arr[0]
			val amount = arr[1].toInt()
			val item = PlayerItem.fromData(
				type, null, "", -1, 0, System.currentTimeMillis(),
				false, amount
			)
			return StackableAW(item as StackablePI)
		}
	}
}