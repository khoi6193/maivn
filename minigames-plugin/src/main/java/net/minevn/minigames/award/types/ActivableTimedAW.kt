package net.minevn.minigames.award.types

import net.minevn.minigames.PlayerData
import net.minevn.minigames.award.PlayerAward
import net.minevn.minigames.items.PlayerItem
import net.minevn.minigames.items.types.ActivableTimedPI
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ActivableTimedAW(private val item: ActivableTimedPI) : PlayerAward(ItemUnit.DAYS, item.duration) {
	override fun apply(p: Player, note: String) {
		item.duration = amount
		if (amount == 0) {
			// nếu item vĩnh viễn thì khỏi cần kích hoạt
			val aw = TimedAW(item.playerItem, 0)
			aw.apply(p, note)
			return
		}
		val data = PlayerData.getData(p)
		data?.addItem(item, "$note (ActivableItemAW)")
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
		 * valid data: TimedTypePI$data$days
		 * example: HitSoundPI$hẻo$7
		 */
		@Throws(Exception::class)
		@JvmStatic
		fun fromData(data: String): ActivableTimedAW {
			val arr = data.split("$")
			val type = arr[0]
			val pidata = arr[1]
			val amount = arr[2].toInt()
			val item = PlayerItem.fromData(
				type, null, pidata, -1, 0, System.currentTimeMillis(),
				false, amount
			)
			val atp = ActivableTimedPI(null, -1, item, amount, System.currentTimeMillis())
			return ActivableTimedAW(atp)
		}
	}
}