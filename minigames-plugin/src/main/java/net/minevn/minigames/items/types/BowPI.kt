package net.minevn.minigames.items.types

import net.minevn.minigames.Messages
import net.minevn.minigames.PlayerData
import net.minevn.minigames.gadgets.Bow
import net.minevn.minigames.items.UsablePI
import org.bukkit.inventory.ItemStack

class BowPI(owner: PlayerData?, id: Int, val bow: Bow, expire: Long, obtained: Long, isUsing: Boolean
) : UsablePI(Messages.PI_CATEGORY_BOW, owner, id, bow.type, bow.data, expire, obtained, isUsing) {

	override fun getItem(): ItemStack {
		val item: ItemStack = super.getItem()
		val im = item.itemMeta
		im.setDisplayName(bow.name)
		val lores: MutableList<String?> = description.toMutableList()
		lores.add("§f")
		if (getExpire() > 0) {
			lores.add(Messages.PI_EXPIRE_DATE.replace("%date%", expireDate))
			lores.add("§f")
		}
		lores.add(if (isUsing) Messages.PI_CLICK_TO_UNUSE else Messages.PI_CLICK_TO_USE)
		im.lore = lores
		item.itemMeta = im
		return item
	}

	override fun getData(): String = bow.id

	override fun getDescription() = bow.description
	override fun clone() = BowPI(null, -1, bow, expire, System.currentTimeMillis(), false)

	companion object {
		// static
		@JvmStatic
		fun fromData(
			owner: PlayerData?, data: String?, id: Int, expire: Long, obtained: Long, isUsing: Boolean, amount: Int,
		): BowPI {
			val bow = Bow[data!!] ?: throw IllegalArgumentException("bow $data khong ton tai")
			return BowPI(owner, id, bow, expire, obtained, isUsing)
		}
	}
}