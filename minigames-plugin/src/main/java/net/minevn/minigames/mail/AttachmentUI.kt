package net.minevn.minigames.mail

import net.minevn.guiapi.GuiInventory
import net.minevn.guiapi.GuiItemStack
import net.minevn.minigames.*
import net.minevn.minigames.Utils.runAsync
import net.minevn.minigames.Utils.runSync
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import java.util.*
import kotlin.math.abs

class AttachmentUI(
	private val player: PlayerData,
	private val mail: Mail,
	private val main: MailUI,
) : GuiInventory(54, Messages.GUI_TITLE_MAIL_VIEW) {
	private val plugin = Minigames.getInstance()
	private val viewer: Player = player.player
	private var closeable = false
	private val self = mail.sender == viewer.uniqueId.toString()
	private var recipient: MutableList<MailRecipient> = mutableListOf()

	init {
		buildAsync()
	}

	private fun buildAsync() {
		lock()
		Utils.runNotSync { build() }
	}

	private fun setRevokeButton() {
		val btnRevoke = GuiItemStack(
			Material.CHEST,
			Messages.GUI_BTN_MAIL_ATTACHMENT_REVOKE,
			Messages.GUI_BTN_MAIL_ATTACHMENT_REVOKE_DESC
		).onClick {
			lock()
			runAsync {
				recipient = MySQL.getInstance().getRecipient(mail.id)
				MySQL.getInstance().updateMail(viewer.uniqueId.toString(), mail, true)
				if (mail.revoked > 0L || mail.isAttachmentReceived || recipient.any { it.attachment > 0 }) {
					setRevokeButton()
					unlock()
					return@runAsync
				}
				runSync {
					if (mail.attachment > 0L) {
						player.sendMessage(Messages.MSG_MAIL_ATTACHMENT_REVOKE_RECEIVED)
						return@runSync
					}
					val money = mail.money
					val points = mail.points
					if (points > 0) {
						plugin.playerPoints.api.give(viewer.uniqueId, points)
					}
					if (money > 0) {
						plugin.economy.depositPlayer(viewer, mail.money.toDouble())
						player.sendMessage(Messages.ECO_GIVE.replace("%amount%", "$money"))
					}
					runAsync {
						MySQL.getInstance().revokeAttachment(mail)
						mail.attachments.forEach {
							player.addItem(it, "Revoked from mail ${mail.id}")
							player.sendMessage(
								Messages.MSG_ITEM_AWARD.replace("%item%", it.item.itemMeta?.displayName!!)
							)
						}

						runSync {
							closeable = true
							viewer.closeInventory()
						}
					}
				}
			}
		}

		val btnRevoked = GuiItemStack(
			Material.BARRIER,
			Messages.GUI_BTN_MAIL_ATTACHMENT_CANT_REVOKE,
			Messages.GUI_BTN_MAIL_ATTACHMENT_ALREADY_REVOKED_DESC
		)

		val btnMulti = GuiItemStack(
			Material.BARRIER,
			Messages.GUI_BTN_MAIL_ATTACHMENT_CANT_REVOKE,
			Messages.GUI_BTN_MAIL_ATTACHMENT_REVOKE_MULTI_DESC
		)

		val btnReceived = GuiItemStack(
			Material.BARRIER,
			Messages.GUI_BTN_MAIL_ATTACHMENT_CANT_REVOKE,
			Messages.GUI_BTN_MAIL_ATTACHMENT_REVOKE_RECEIVED_DESC
		)

		val toSet = if (mail.revoked > 0) btnRevoked
		else if (recipient.size != 1) btnMulti
		else if (recipient.any { it.attachment > 0 }) btnReceived
		else btnRevoke

		setItem(1, toSet)
	}

	private fun setReceiveButton() {
		val receiveLore = Messages.GUI_BTN_MAIL_ATTACHMENT_RECEIVE_DESC.toMutableList()
		val fees = mutableListOf<String>()
		if (mail.money < 0) fees.add("§e${abs(mail.money)} MG")
		if (mail.points < 0) fees.add("§e${abs(mail.points)} Points")
		if (fees.isNotEmpty()) {
			receiveLore.add("§f")
			receiveLore.add(Messages.GUI_BTN_MAIL_ATTACHMENT_RECEIVE_FEE + fees.joinToString(" §7và "))
		}

		val btnReceive = GuiItemStack(
			Material.CHEST,
			Messages.GUI_BTN_MAIL_ATTACHMENT_RECEIVE,
			receiveLore
		).onClick {
			lock()
			runAsync {
				MySQL.getInstance().updateMail(viewer.uniqueId.toString(), mail, false)
				if (mail.attachment > 0L || mail.revoked > 0L) {
					setReceiveButton()
					unlock()
					return@runAsync
				}
				runSync {
					if (mail.attachment > 0L) {
						player.sendMessage(Messages.MSG_MAIL_ATTACHMENT_RECEIVED)
						return@runSync
					}
					val money = mail.money
					val points = mail.points
					var costMoney = 0
					var costPoints = 0
					if (money < 0) {
						costMoney = abs(money)
						if (!plugin.economy.has(viewer, costMoney.toDouble())) {
							player.sendMessage(Messages.ERR_MAIL_ATTACHMENT_NO_MONEY)
							return@runSync
						}
						plugin.economy.withdrawPlayer(viewer, costMoney.toDouble())
						player.sendMessage(Messages.ECO_TAKE.replace("%amount%", "$costMoney"))
					}
					if (points < 0) {
						costPoints = abs(points)
						if (plugin.playerPoints.api.look(viewer.uniqueId) < costPoints) {
							player.sendMessage(Messages.ERR_MAIL_ATTACHMENT_NO_POINTS)
							return@runSync
						}
						plugin.playerPoints.api.take(viewer.uniqueId, costPoints)
					}
					if (points > 0) {
						plugin.playerPoints.api.give(viewer.uniqueId, points)
					}
					if (money > 0) {
						plugin.economy.depositPlayer(viewer, mail.money.toDouble())
						player.sendMessage(Messages.ECO_GIVE.replace("%amount%", "$money"))
					}
					runAsync {
						MySQL.getInstance().readAttachment(mail)
						player.noticeMail()
						mail.attachments.forEach {
							player.addItem(it, "Received from mail ${mail.id}")
							player.sendMessage(
								Messages.MSG_ITEM_AWARD.replace("%item%", it.item.itemMeta?.displayName!!)
							)
						}

						if (costMoney > 0 || costPoints > 0) {
							val returnCost = Mail(
								sender = "00000000-0000-0000-0000-000000000000",
								senderName = "Hệ thống",
								date = System.currentTimeMillis(),
								title = Messages.MAIL_MONEY_RETURN_TITLE,
								message = Messages.MAIL_MONEY_RETURN_MESSAGE.replace("%mailid%", "${mail.id}"),
								points = costPoints,
								money = costMoney,
								attachmentCount = 0
							)
							MySQL.getInstance().sendMail(returnCost, mutableListOf(UUID.fromString(mail.sender)))
						}

						runSync {
							closeable = true
							viewer.closeInventory()
						}
					}
				}
			}
		}

		val btnCantAffford = GuiItemStack(
			Material.BARRIER,
			Messages.GUI_BTN_MAIL_ATTACHMENT_CANT_RECEIVE,
			Messages.GUI_BTN_MAIL_ATTACHMENT_NOT_AFFORD_DESC,
		)

		val btnAlreadyReceived = GuiItemStack(
			Material.BARRIER,
			Messages.GUI_BTN_MAIL_ATTACHMENT_CANT_RECEIVE,
			Messages.GUI_BTN_MAIL_ATTACHMENT_RECEIVED_DESC,
		)

		val btnAlreadyRevoked = GuiItemStack(
			Material.BARRIER,
			Messages.GUI_BTN_MAIL_ATTACHMENT_CANT_RECEIVE,
			Messages.GUI_BTN_MAIL_ATTACHMENT_REVOKED_DESC,
		)

		val canAfford = (mail.money >= 0 || plugin.economy.has(viewer, abs(mail.money).toDouble())) &&
				(mail.points >= 0 || plugin.playerPoints.api.look(viewer.uniqueId) > abs(mail.points))

		val toSet: GuiItemStack = if (mail.revoked > 0) btnAlreadyRevoked
		else if (mail.attachment > 0L) btnAlreadyReceived
		else if (!canAfford) btnCantAffford
		else btnReceive

		setItem(1, toSet)
	}

	private fun build() {
		if (Bukkit.isPrimaryThread()) throw IllegalAccessException("Attachment UI can't be build in main thread")
		lock()
		mail.attachments = MySQL.getInstance().getAttachments(mail)

		if (self) {
			recipient = MySQL.getInstance().getRecipient(mail.id)
		}

		// deco
		for (slot in 0 until  9) {
			setItem(slot, GuiItemStack(Material.BLACK_STAINED_GLASS_PANE, "§f"))
		}
		for (slot in 45 until  54) {
			setItem(slot, GuiItemStack(Material.BLACK_STAINED_GLASS_PANE, "§f"))
		}

		// mail items
		for (index in 0 until 36) {
			val slot = index + 9
			if (index >= mail.attachments.size) {
				setItem(slot, null)
				continue
			}
			val playerItem = mail.attachments[index]
			val item = playerItem.item
			val im = item.itemMeta
			val lore = ArrayList(playerItem.description)
			im.lore = lore
			item.itemMeta = im
			setItem(slot, GuiItemStack(item))
		}

		// receive button
		if (!self) {
			setReceiveButton()
		} else {
			setRevokeButton()
		}

		// money
		val moneyLore = if (mail.money >= 0) {
			Messages.GUI_BTN_MAIL_PREVIEW_MONEY_DESC
		} else {
			Messages.GUI_BTN_MAIL_PREVIEW_MONEY_DESC_FEE
		}
		setItem(4, GuiItemStack(
			Material.GOLD_INGOT,
			Messages.GUI_BTN_MAIL_PREVIEW_MONEY.replace("%money%", "${mail.money}"),
			moneyLore.map { it.replace("%money%", "${abs(mail.money)}") }
		))

		// point
		val pointLore = if (mail.points >= 0) {
			Messages.GUI_BTN_MAIL_PREVIEW_POINTS_DESC
		} else {
			Messages.GUI_BTN_MAIL_PREVIEW_POINTS_DESC_FEE
		}
		setItem(7, GuiItemStack(
			Material.EMERALD,
			Messages.GUI_BTN_MAIL_PREVIEW_POINTS.replace("%points%", "${mail.points}"),
			pointLore.map { it.replace("%points%", "${abs(mail.points)}") }
		))

		setItem(49, btnClose {
			closeable = true
			viewer.closeInventory()
		})

		unlock()
		// show gui
		view(viewer)
	}

	override fun onClose(e: InventoryCloseEvent) {
		if (!closeable) return
		main.buildAsync()
	}
}