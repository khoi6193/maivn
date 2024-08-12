package net.minevn.minigames.items.types

import net.minevn.minigames.Messages
import net.minevn.minigames.PlayerData
import net.minevn.minigames.gadgets.Sword
import net.minevn.minigames.items.UsablePI
import org.bukkit.inventory.ItemStack

class SwordPI(owner: PlayerData?, id: Int, val sword: Sword, expire: Long, obtained: Long, isUsing: Boolean
) : UsablePI(Messages.PI_CATEGORY_SWORD, owner, id, sword.type, sword.data, expire, obtained, isUsing) {

	override fun getItem(): ItemStack {
		val item = super.getItem()
		val im = item.itemMeta
		im.setDisplayName(sword.name)
		val lores: MutableList<String> = ArrayList(description)
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

	override fun getData() = sword.iD

	override fun getDescription() = sword.description
	override fun clone() = SwordPI(null, -1, sword, expire, System.currentTimeMillis(), false)

	companion object {
		// static
		@JvmStatic
		fun fromData(
			owner: PlayerData?, data: String?, id: Int, expire: Long, obtained: Long, isUsing: Boolean, amount: Int
		): SwordPI {
			val sword = Sword[data!!] ?: throw IllegalArgumentException("sword $data khong ton tai")
			return SwordPI(owner, id, sword, expire, obtained, isUsing)
		}
	}
}