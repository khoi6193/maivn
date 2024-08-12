package net.minevn.minigames.items.types

import net.minevn.minigames.Messages
import net.minevn.minigames.PlayerData
import net.minevn.minigames.Utils.runNotSync
import net.minevn.minigames.award.PlayerAward
import net.minevn.minigames.createHeadItem
import net.minevn.minigames.gadgets.ItemBundle
import net.minevn.minigames.gui.InventoryGui
import net.minevn.minigames.items.PlayerItem
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class ItemBundlePI(
	owner: PlayerData?, id: Int, private val bundle: ItemBundle, expire: Long, obtained: Long
) : PlayerItem("", owner, id, bundle.iconType, bundle.iconData, expire, obtained, false) {

	override fun getItem(): ItemStack {
		val item = gameProfile?.createHeadItem() ?: super.getItem()
		val im = item.itemMeta
		im.setDisplayName(bundle.name)
		val lores = description.toMutableList()
		lores.add("§f")
		lores.add(Messages.PI_CLICK_TO_OPEN)
		im.lore = lores
		item.itemMeta = im
		return item
	}

	override fun getData(): String = bundle.id

	override fun getDescription(): List<String> = bundle.description
	override fun clone() = ItemBundlePI(null, -1, bundle, expire, System.currentTimeMillis())

	override fun onClick(e: InventoryClickEvent) {
		(e.clickedInventory?.holder as? InventoryGui ?: return).lock()
		owner.player.closeInventory()
		runNotSync {
			owner.removeItem(this, "use bundle")
			if (bundle.random) apply(bundle.items.random(), "from bundle ${bundle.id}")
			else bundle.items.forEach { apply(it, "from bundle ${bundle.id}") }
		}
	}

	private fun apply(award: PlayerAward, questID: String) {
		award.apply(owner.player, "from quest $questID")
		owner.player.sendMessage(Messages.MSG_ITEM_AWARD.replace("%item%", award.name))
	}

	companion object {
		@JvmStatic
		fun fromData(
			owner: PlayerData?, itemData: String?, id: Int, expire: Long, obtained: Long, using: Boolean,
			amount: Int,
		) = ItemBundle[itemData!!]?.let { ItemBundlePI(owner, id, it, expire, obtained) }?:
		throw IllegalArgumentException("bundle does not exist: $itemData")
	}
}