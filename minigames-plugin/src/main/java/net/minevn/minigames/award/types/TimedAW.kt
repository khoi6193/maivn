package net.minevn.minigames.award.types

import net.minevn.minigames.PlayerData
import net.minevn.minigames.award.PlayerAward
import net.minevn.minigames.items.PlayerItem
import net.minevn.mmclient.utils.MMUtils
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class TimedAW(val item: PlayerItem, days: Int) : PlayerAward(ItemUnit.DAYS, days) {

	override fun apply(p: Player, note: String) {
		val expire = if (amount == 0) 0 else System.currentTimeMillis() + 86400000L * amount
		item.expire = expire
		val data = PlayerData.getData(p)
		if (data != null) MMUtils.runNotSync { data.addItem(item, "$note (TimedAW)") }
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
		fun fromData(data: String): TimedAW {
			val arr = data.split("$")
			val type = arr[0]
			val pidata = arr[1]
			val amount = arr[2].toInt()
			val item = PlayerItem.fromData(
				type, null, pidata, -1, 0, System.currentTimeMillis(),
				false, amount
			)
			return TimedAW(item, amount)
		}
	}
}