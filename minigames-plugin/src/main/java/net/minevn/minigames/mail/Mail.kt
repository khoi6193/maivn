package net.minevn.minigames.mail

import net.minevn.minigames.Messages
import net.minevn.minigames.PlayerData
import net.minevn.minigames.items.PlayerItem
import net.minevn.minigames.relativeTime
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta

class Mail(
	val id: Int = -1, //default, should be handled by sql
	val sender: String, // as uuid
	val senderName: String,
	val date: Long,
	val title: String,
	val message: String,
	/**
	 * point đính kèm, nếu < 0 thì sẽ là chi phí nhận đính kèm
	 */
	val points: Int = 0,
	/**
	 * money đính kèm, nếu < 0 thì sẽ là chi phí nhận đính kèm
	 */
	val money: Int = 0,
	val attachmentCount: Int = 0,
	/**
	 * Thời gian đọc thư, = 0 là chưa đọc
	 */
	val read: Long = 0,
	/**
	 * Thời gian nhận đính kèm, = 0 là chưa nhận
	 */
	var attachment: Long = 0
) {
	var attachments: MutableList<PlayerItem> = mutableListOf()
	var isDeleted = false
	var receivers: String? = null
	var revoked = 0L

	/**
	 * Người nhận đã nhận đính kèm hay chưa
	 *
	 * Dành cho UI thư đã gửi
	 */
	var isAttachmentReceived = false

	fun isAttachmentAvailable() = attachmentCount > 0 || money > 0 || points > 0

	fun isUnread() = read == 0L || (attachment == 0L && isAttachmentAvailable() && revoked == 0L)

	fun getGuiItem() : ItemStack {
		val type = if (isUnread()) Material.ENCHANTED_BOOK else Material.BOOK
		val readStatus = if (read == 0L) {
			" (${Messages.MAIL_UNREAD.lowercase()})"
		} else ""

		val stack = ItemStack(type)
		val im = stack.itemMeta
		val title = title.ifEmpty { Messages.MAIL_DEFAULT_TITLE }
		im.setDisplayName("§f#$id §e$title§f$readStatus")
		val lores = Messages.MAIL_DESC.map {
			if ((it.contains("%attachment%")  || it.contains("%containing%")) && !isAttachmentAvailable()) return@map null
			if (it.contains("%money%") && money == 0) return@map null
			if (it.contains("%points%") && points == 0) return@map null
			if (it.contains("%attachmentStatus%") && !isAttachmentAvailable()) return@map null
			it.replace("%sender%", senderName)
				.replace("%containing%", Messages.MAIL_DESC_CONTAINING)
				.replace("%date%", relativeTime(date))
				.replace("%money%", "$money")
				.replace("%points%", "$points")
				.replace("%attachment%", "$attachmentCount")
				.replace(
					"%attachmentStatus%",
					if (revoked > 0L) Messages.MAIL_ATTACHMENT_REVOKED
					else if (attachment == 0L) Messages.MAIL_ATTACHMENT_NOT_RECEIVED
					else Messages.MAIL_ATTACHMENT_RECEIVED
				)
		}.filterNotNull().toMutableList()

		im.lore = lores
		stack.itemMeta = im
		return stack
	}

	/**
	 * Gui item for list sent UI
	 */
	fun getSelfGuiItem() : ItemStack {
		val icon = if (!isAttachmentReceived && isAttachmentAvailable() && revoked == 0L) {
			Material.ENCHANTED_BOOK
		} else {
			Material.BOOK
		}
		val stack = ItemStack(icon)
		val im = stack.itemMeta
		val title = title.ifEmpty { Messages.MAIL_DEFAULT_TITLE }
		im.setDisplayName("§f#$id §e$title")

		val lores = mutableListOf<String>()

		lores.add(Messages.MAIL_SENT_DESC_TIME.replace("%date%", relativeTime(date)))
		lores.add(Messages.MAIL_SENT_DESC_RECEIVERS)
		receivers?.split(" ")?.forEach {
			lores.add("§7- §f$it")
		}

		lores.add("§f")
		lores.add(Messages.MAIL_SENT_DESC_OPEN_READ)
		if (isAttachmentAvailable()) {
			lores.add(Messages.MAIL_SENT_DESC_OPEN_ATTACHMENT)
		}

		im.lore = lores
		stack.itemMeta = im
		return stack
	}

	fun openContent(player: PlayerData) {
		val item = ItemStack(Material.WRITTEN_BOOK)
		val im = item.itemMeta as BookMeta
		im.title = title
		im.author = senderName
		im.pages = message.split("~*~")
		item.itemMeta = im
		player.player.openBook(item)
	}
}