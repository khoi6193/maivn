package net.minevn.minigames.award.types

import net.minevn.minigames.PlayerData
import net.minevn.minigames.award.PlayerAward
import net.minevn.minigames.items.PlayerItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ItemAW(val item: PlayerItem, amount: Int) : PlayerAward(ItemUnit.PCS, amount) {

	override fun apply(p: Player, note: String) {
		val data = PlayerData.getData(p) ?: return
		for (i in 1 .. amount) {
			data.addItem(item.clone(), "$note (ItemAW)")
		}
	}

	override fun getGuiItem(): ItemStack {
		val item = item.item
		val im = item.itemMeta
		val desc = this.item.description
		if (desc != null) im.lore = desc
		item.itemMeta = im
		return item
	}

	override fun getName(): String = item.item.itemMeta.displayName

	override fun typeName(): String = item.categoryName

	override fun isPreviewable(): Boolean = item.isPreviewable

	override fun preview(player: Player) {
		if (item.isPreviewable) item.preview(player)
	}

	companion object {
		// static
		/**
		 * valid data: TimedTypePI$data$amount<br></br>
		 * example: CasePI$test$1
		 */
		@Throws(Exception::class)
		@JvmStatic
		fun fromData(data: String): ItemAW {
			val arr = data.split("$")
			val type = arr[0]
			val pidata = arr[1]
			val amount = arr[2].toInt()
			val item = PlayerItem.fromData(
				type, null, pidata, -1, 0, System.currentTimeMillis(),
				false, amount
			)
			return ItemAW(item, amount)
		}
	}
}