package net.minevn.minigames.mail

import net.minevn.guiapi.GuiInventory
import net.minevn.guiapi.GuiItemStack
import net.minevn.minigames.*
import net.minevn.minigames.Utils.*
import net.minevn.minigames.mail.compose.MailComposeUI
import org.bukkit.Bukkit.isPrimaryThread
import org.bukkit.Material

class MailUI(
	private val player: PlayerData,
	private val listSent: Boolean
) : GuiInventory(54, Messages.GUI_TITLE_MAIL) {
	private val viewer = player.player?.takeIf { it.isOnline }!!
	private var page = 0

	constructor(player: PlayerData) : this(player, false)

	init {
		buildAsync()
	}

	fun buildAsync() {
		lock()
		runNotSync { build() }
	}

	private fun build() {
		if (isPrimaryThread()) throw IllegalAccessException("Mail UI can't be build in main thread")

		// load mails
		val mails = if (!listSent) {
			MySQL.getInstance().getMails(player.uuid.toString())
		} else {
			MySQL.getInstance().getSentMails(player.uuid.toString())
		}

		// deco
		for (slot in 45 .. 53) {
			setItem(slot, GuiItemStack(Material.BLACK_STAINED_GLASS_PANE))
		}

		// mail items
		val pageSize = 45
		for (slot in 0 .. 44) {
			val index = page * pageSize + slot
			if (index >= mails.size) {
				setItem(slot, null)
				continue
			}
			val mail = mails[index]
			val mailIcon = if (listSent) {
				mail.getSelfGuiItem()
			} else {
				mail.getGuiItem()
			}
			setItem(slot, GuiItemStack(mailIcon).onClick {
				if (!listSent) {
					if (it.isLeftClick) {
						if (it.isShiftClick && mail.isAttachmentAvailable()) {
							AttachmentUI(player, mail, this)
						} else {
							runAsync {
								if (mail.read == 0L) {
									MySQL.getInstance().readContent(mail)
									player.noticeMail()
								}
								runSync {
									mail.openContent(player)
								}
							}
						}
						return@onClick
					}
					if (it.isRightClick) {
						if (it.isShiftClick) {
							MySQL.getInstance().deleteMail(mail, player.uuid.toString())
							buildAsync()
							return@onClick
						}
						// TODO reply
					}
				} else if (it.isLeftClick) {
					if (it.isShiftClick) {
						if (mail.attachmentCount > 0 || mail.money > 0 || mail.points > 0) {
							AttachmentUI(player, mail, this)
						}
					} else {
						runSync {
							mail.openContent(player)
						}
					}
				}
			})
		}

		// sent mail
		val btnSentMails = GuiItemStack(
			Material.CHEST_MINECART,
			Messages.GUI_BTN_MAIL_SENT,
			Messages.GUI_BTN_MAIL_SENT_DESC
		).onClick {
			MailUI(player, true)
		}

		val btnMain = GuiItemStack(
			Material.CHEST,
			Messages.GUI_BTN_MAIL,
			Messages.GUI_BTN_MAIL_DESC
		).onClick {
			MailUI(player)
		}

		setItem(47, if (listSent) btnMain else btnSentMails)

		// compose mail
		setItem(
			49,
			GuiItemStack(
				Material.FEATHER,
				Messages.GUI_BTN_MAIL_COMPOSE,
				Messages.GUI_BTN_MAIL_COMPOSE_DESC
			).onClick {
				MailComposeUI(player)
			}
		)

		// controls
		if (page > 0) setItem(45, btnPagePrev {
			page--
			buildAsync()
		})
		if (pageSize * (page + 1) < mails.size) setItem(53, btnPageNext {
			page++
			buildAsync()
		})
		setItem(51, btnClose { viewer.closeInventory() })

		unlock()
		// show gui
		view(viewer)
	}
}