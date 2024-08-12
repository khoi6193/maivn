package net.minevn.minigames.items.types

import net.minevn.minigames.Messages
import net.minevn.minigames.PlayerData
import net.minevn.minigames.chatactions.types.GunRenameCL
import net.minevn.minigames.createHeadItem
import net.minevn.minigames.gadgets.NamePreset
import net.minevn.minigames.gui.InventoryCategory
import net.minevn.minigames.gui.InventoryGui
import net.minevn.minigames.items.DraggablePI
import net.minevn.minigames.items.PlayerItem
import net.minevn.minigames.stripColors
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class MSGunTagPI(
	owner: PlayerData?, id: Int, private val preset: NamePreset, expire: Long, obtained: Long
) : PlayerItem(
	Messages.PI_CATEGORY_GUNNAME, owner, id, Material.NAME_TAG, 0, expire, obtained
), DraggablePI {
	override fun getData(): String = preset.id

	override fun getDescription(): MutableList<String> = Messages.PI_GUNNAME_DESC

	override fun clone() = MSGunTagPI(null, -1, preset, expire, System.currentTimeMillis())

	override fun getItem(): ItemStack {
		val item = preset.gameProfile?.createHeadItem() ?: super.getItem()
		val im = item.itemMeta
		im.setDisplayName(Messages.PI_GUNNAME.replace("%preset%", preset.name))
		val lores = mutableListOf<String>()
		lores.add("§f")
		addDescription(lores)
		addExpireDate(lores)
		lores.add(Messages.PI_CLICK_TO_RENAME)
		im.lore = lores
		item.itemMeta = im
		return item
	}

	override fun drop(other: PlayerItem, event: InventoryClickEvent) {
		val viewer = owner.player
		if (other !is MSGunPI) {
			viewer.playSound(viewer.location, "cs.shop.shopcantbuy", 1.0f, 1.0f)
			val msg = if (other is ActivableTimedPI && other.playerItem is MSGunPI)
				Messages.ERR_RENAME_GUN_ACTIVATED_ONLY
				else Messages.ERR_RENAME_GUN_ONLY
			owner.sendMessage(msg)
			return
		}
		viewer.closeInventory()
		GunRenameCL(owner, other, this)
	}

	override fun onCursorPick(gui: InventoryGui) {
		gui.changeCategory(InventoryCategory["gun"])
	}

	fun parse(text: String) = preset.parse(text.stripColors())

	companion object {
		@JvmStatic
		fun fromData(
			owner: PlayerData?, itemData: String?, id: Int, expire: Long, obtained: Long, using: Boolean,
			amount: Int,
		) = NamePreset[itemData!!]?.let { MSGunTagPI(owner, id, it, expire, obtained) }
			?: throw IllegalArgumentException("namepreset $itemData khong ton tai")
	}
}