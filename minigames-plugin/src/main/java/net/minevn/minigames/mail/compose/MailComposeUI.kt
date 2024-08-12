package net.minevn.minigames.mail.compose

import net.minevn.guiapi.GuiInventory
import net.minevn.guiapi.GuiItemStack
import net.minevn.minigames.*
import net.minevn.minigames.chatactions.prompt.BookPrompt
import net.minevn.minigames.chatactions.prompt.ChatPrompt
import net.minevn.minigames.items.PlayerItem
import net.minevn.minigames.items.StackablePI
import net.minevn.minigames.mail.Mail
import org.bukkit.Bukkit
import org.bukkit.Material

class MailComposeUI(
	private val player: PlayerData
) : GuiInventory(54, Messages.GUI_TITLE_MAIL_COMPOSE) {
	private val viewer = player.player?.takeIf { it.isOnline }!!
	private var receivers = listOf<String>()
	private var title = ""
	private var content = mutableListOf<String>()
	private var attachments = mutableMapOf<PlayerItem, Int>()
	private var money = 0
	private var point = 0

	init {
		buildAsync()
	}

	private fun buildAsync() {
		lock()
		Utils.runNotSync { build() }
	}

	private fun build() {
		if (Bukkit.isPrimaryThread()) throw IllegalAccessException("MailComposeUI can't be build in main thread")
		lock()
		//deco
		for (i in 9..17) setItem(i, GuiItemStack(Material.BLACK_STAINED_GLASS_PANE, "§f"))
		for (i in 45..53) setItem(i, GuiItemStack(Material.BLACK_STAINED_GLASS_PANE, "§f"))

		//receiver
		val receiversLore = Messages.GUI_BTN_MAIL_RECEIVERS_DESC.toMutableList()
		receiversLore.addAll(receivers.map { "§7- §f$it" })
		setItem(0, GuiItemStack(
			Material.PLAYER_HEAD,
			Messages.GUI_BTN_MAIL_RECEIVERS,
			receiversLore
		).onClick {
			player.sendMessage(Messages.MSG_MAIL_TYPE_RECEIVERS)
			viewer.closeInventory()
			ChatPrompt(player) {
				val names = it.split(" ")
				if (names.size > 1 && !viewer.hasPermission("minigames.mail.receivers")) {
					player.sendMessage(Messages.ERR_MAIL_SEND_MULTI_RECEIVERS)
				}
				else if (it.contains(player.name, true)) {
					player.sendMessage(Messages.ERR_MAIL_SEND_TO_SELF)
				}
				else {
					receivers = names
					player.sendMessage(Messages.MSG_MAIL_RECEIVERS_SET)
				}
				buildAsync()
			}
		})

		//content
		setItem(2, GuiItemStack(
			Material.WRITABLE_BOOK,
			Messages.GUI_BTN_MAIL_CONTENT,
			Messages.GUI_BTN_MAIL_CONTENT_DESC.map {
				it.replace("%title%", title.ifEmpty { Messages.MAIL_DEFAULT_TITLE })
			})
			.onClick {
				viewer.closeInventory()
				player.sendMessage(Messages.MSG_MAIL_TYPE_CONTENT)
				BookPrompt(player, content, Messages.GUI_TITLE_MAIL_COMPOSE) {
					content = it.pages
					if (it.hasTitle()) title = it.title!!
					player.sendMessage(Messages.MSG_MAIL_CONTENT_SET)
					buildAsync()
				}
			})

		// attachment
		val addItem = GuiItemStack(
			Material.CHEST,
			Messages.GUI_BTN_MAIL_ATTACHMENT,
			Messages.GUI_BTN_MAIL_ATTACHMENT_DESC
		).onClick {
			AddAttachmentUI(player, attachments) {
				if (attachments.contains(it)) attachments.remove(it)
				else {
					if (attachments.size == 18) {
						player.sendMessage(Messages.ERR_MAIL_ATTACHMENT_FULL)
						return@AddAttachmentUI
					}
					attachments[it] = if (it is StackablePI) it.amount else 1
				}
				buildAsync()
			}
		}
		setItem(4, addItem)

		//money attachment
		setItem(6, GuiItemStack(
			Material.GOLD_INGOT,
			Messages.GUI_BTN_MAIL_MONEY.replace("%money%", "$money"),
			Messages.GUI_BTN_MAIL_MONEY_DESC)
			.onClick {
				player.sendMessage(Messages.MSG_MAIL_TYPE_MONEY)
				viewer.closeInventory()
				ChatPrompt(player) {
					try {
						if (it.toInt() <= 0 && attachments.isEmpty()) {
							player.sendMessage(Messages.ERR_NUMBER_CURRENCY_SHIPPING_EMPTY)
							return@ChatPrompt
						}
						money = it.toInt()
						player.sendMessage(Messages.MSG_MAIL_MONEY_SET.replace("%money%", "$money"))
					} catch (e: NumberFormatException) {
						player.sendMessage(Messages.ERR_NUMBER_INTEGER_FORMAT)
					} finally {
						buildAsync()
					}
				}
			})

		//point attachment
		setItem(8, GuiItemStack(
			Material.EMERALD,
			Messages.GUI_BTN_MAIL_POINTS.replace("%points%", "$point"),
			Messages.GUI_BTN_MAIL_POINTS_DESC)
			.onClick {
				player.sendMessage(Messages.MSG_MAIL_TYPE_POINTS)
				viewer.closeInventory()
				ChatPrompt(player) {
					try {
						val amount = it.toInt()
						if (amount <= 0 && attachments.isEmpty()) {
							player.sendMessage(Messages.ERR_NUMBER_CURRENCY_SHIPPING_EMPTY)
							return@ChatPrompt
						}
						point = amount
						player.sendMessage(Messages.MSG_MAIL_POINTS_SET.replace("%point%", "$point"))
					} catch (e: NumberFormatException) {
						player.sendMessage(Messages.ERR_NUMBER_INTEGER_FORMAT)
					} finally {
						buildAsync()
					}
				}
			})

		//attachment
		var i = 0
		for (slot in 18..35) {
			if (i >= attachments.size) {
				setItem(slot, null)
				continue
			}
			val playerItem = attachments.keys.toList()[i]
			val item = playerItem.item
			val im = item.itemMeta
			val lore = ArrayList(playerItem.description)
			lore.add(playerItem.shippingCostLine)
			lore.add(Messages.PI_CLICK_TO_DETACH_MAIL)
			if (playerItem is StackablePI) lore.add(Messages.PI_CLICK_TO_CHANGE_AMOUNT)
			im.lore = lore
			item.itemMeta = im
			if (playerItem is StackablePI) {
				item.amount = attachments[playerItem]!!
			}
			setItem(slot, GuiItemStack(item).onClick {
				if (playerItem is StackablePI && it.isShiftClick) {
					viewer.closeInventory()
					player.sendMessage(Messages.MSG_MAIL_TYPE_ITEM_AMOUNT.replace(
						"%max_amount%", "${playerItem.amount}"))
					ChatPrompt(player) { input ->
						try {
							val amount = input.toInt()
							if (amount <= 0) throw NumberFormatException()
							if (amount > playerItem.amount) {
								player.sendMessage(Messages.ERR_NUMBER_GTE_ITEM_AMOUNT)
							} else {
								attachments[playerItem] = amount
								player.sendMessage(Messages.MSG_SET_ITEM_AMOUNT
									.replace("%amount%", "$amount"))
							}
						} catch (e: NumberFormatException) {
							player.sendMessage(Messages.ERR_NUMBER_INTEGER_GTE0_FORMAT)
						} finally {
							buildAsync()
						}
					}
				}
				else {
					attachments.remove(playerItem)
					if (attachments.isEmpty()) {
						if (money < 0) money = 0
						if (point < 0) point = 0
					}
					buildAsync()
				}
			})
			i++
		}

		var cantShipName = ""
		val price = attachments.keys.sumOf {
			if (it.shippingCost == -1) {
				cantShipName = it.item.itemMeta.displayName
				return@sumOf 0
			}
			it.shippingCost
		}

		val priceDisplay = if (cantShipName.isEmpty()) "$price Points" else "§cKhông thể gửi"
		//send
		setItem(45, GuiItemStack(
			Material.PAPER,
			Messages.GUI_BTN_MAIL_SEND,
			Messages.GUI_BTN_MAIL_SEND_DESC.map {
				it.replace("%points%", priceDisplay)
			}
		).onClick {
			if (receivers.isEmpty()) {
				player.sendMessage(Messages.ERR_MAIL_NO_RECEIVER)
				return@onClick
			}

			if (content.isEmpty() && attachments.isEmpty()) {
				player.sendMessage(Messages.ERR_MAIL_SEND_NO_CONTENT)
				return@onClick
			}

			if (cantShipName.isNotEmpty()) {
				player.sendMessage(Messages.ERR_MAIL_SEND_CANNOT_SHIP.replace("%item%", cantShipName))
				return@onClick
			}

			if (money < 0 && attachments.isEmpty()) {
				player.sendMessage(Messages.ERR_MAIL_SEND_NO_PAID_ATTACHMENT)
				return@onClick
			}

			if (price > 0) {
				val point = Minigames.getInstance().playerPoints.api.look(player.uuid)
				if (point < price) {
					player.sendMessage(Messages.ERR_MAIL_SEND_NO_POINTS.replace("%points%", "$price"))
					return@onClick
				}
				Minigames.getInstance().playerPoints.api.take(player.uuid, price)
			}

			val mail = Mail(
				sender = player.uuid.toString(),
				senderName = player.name,
				date = System.currentTimeMillis(),
				title = title,
				message = content.joinToString(separator = "~*~"),
				points = point,
				money = money,
				attachmentCount = attachments.size
			)
			attachments.toMap().forEach {
				var playerItem = it.key
				if (playerItem is StackablePI) {
					attachments.remove(playerItem)
					playerItem = playerItem.clone()
					playerItem.amount = it.value
					attachments[playerItem] = it.value
				}
				mail.attachments = attachments.keys.toMutableList()
			}

			sendMail(mail)
			viewer.closeInventory()
		})

		//close
		setItem(51, GuiItemStack(
			Material.BARRIER,
			Messages.GUI_BTN_CLOSE
		).onClick {
			viewer.closeInventory()
		})

		unlock()
		// show gui
		view(viewer)
	}

	private fun sendMail(mail: Mail) {
		val sql = MySQL.getInstance()
		val rep = receivers.map {
			val uuid = sql.getPlayerUUID(it)
			if (uuid == null) {
				player.sendMessage(Messages.ERR_MAIL_SEND_NO_RECEIVER.replace("%player%", it))
				viewer.closeInventory()
				return
			}
			uuid
		}

		if (money > 0) Minigames.getInstance().economy.withdrawPlayer(viewer, money.toDouble())
		if (point > 0) Minigames.getInstance().playerPoints.api.take(viewer.uniqueId, point)
		Utils.runAsync {
			attachments.forEach {
				val playerItem = it.key
				if (playerItem is StackablePI) player.useStackableItem(playerItem.javaClass, it.value)
				else player.removeItem(playerItem, "attach to mail")
			}
			sql.sendMail(mail, rep)
			player.sendMessage(Messages.MSG_MAIL_SENT)
		}
	}
}