package net.minevn.minigames.items.types

import net.minevn.guiapi.XMaterial
import net.minevn.minigames.Messages
import net.minevn.minigames.PlayerData
import net.minevn.minigames.items.PlayerItem
import org.bukkit.inventory.ItemStack

class RideTicketPI(
	owner: PlayerData?, id: Int, private val rideID: String, expire: Long, obtained: Long, using: Boolean
) : PlayerItem(
	Messages.PI_CATEGORY_RIDE_TICKET, owner, id, XMaterial.PAPER.parseMaterial(), 0, expire, obtained, using
) {
	override fun getData() = rideID

	override fun getDescription(): MutableList<String> = Messages.PI_RIDE_TICKET_DESC
	override fun clone() = RideTicketPI(null, -1, rideID, expire, System.currentTimeMillis(), false)

	override fun getItem(): ItemStack {
		val item = super.getItem()!!
		val im = item.itemMeta!!
		im.setDisplayName("$categoryName: §7$rideID")
		val lores = mutableListOf<String>()
		addDescription(lores)
		addExpireDate(lores)
		im.lore = lores
		item.itemMeta = im
		return item
	}

	fun getRide() = net.clownercraft.ccRides.ccRidesPlugin.getInstance().configHandler.rides[rideID]

	companion object {
		@JvmStatic
		fun fromData(
			owner: PlayerData?, itemData: String?, id: Int, expire: Long, obtained: Long, using: Boolean,
			amount: Int
		) = RideTicketPI(owner, id, itemData!!, expire, obtained, using)

		@JvmStatic
		val suggestions = net.clownercraft.ccRides.ccRidesPlugin.getInstance().configHandler.rides.keys
			.map { "RideTicketPI$$it$1" }
	}
}