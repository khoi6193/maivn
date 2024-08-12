package net.minevn.minigames.mail.compose

import net.minevn.guiapi.GuiInventory
import net.minevn.guiapi.GuiItemStack
import net.minevn.minigames.Messages
import net.minevn.minigames.PlayerData
import net.minevn.minigames.Utils
import net.minevn.minigames.gui.InventoryCategory
import net.minevn.minigames.gui.InventoryCategory.Companion.getRootCategories
import net.minevn.minigames.items.PlayerItem
import net.minevn.minigames.items.types.ActivableTimedPI
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import java.util.function.Consumer

class AddAttachmentUI(
	private val player: PlayerData,
	private val attachments: Map<PlayerItem, Int>,
	private val fallback: Consumer<PlayerItem>
) : GuiInventory(54, Messages.GUI_TITLE_INVENTORY) {
	private val viewer: Player = player.player
	private var page = 0
	private var items: ArrayList<PlayerItem> = ArrayList()
	private var currentCategory: InventoryCategory? = null

	init {
		if (viewer.isOnline) buildAsync()
	}

	private fun build() {
		lock()
		getItems()
		var cate = 0
		var categories: List<InventoryCategory?>
		if (currentCategory != null) {
			categories = currentCategory!!.getChildrens()
			if (categories.isEmpty()) {
				categories = currentCategory!!.getSiblings()
			}
		} else {
			categories = getRootCategories()
		}
		for (slot in 45..52) {
			if (slot == 45 && currentCategory != null) {
				setItem(
					slot,
					GuiItemStack(
						Material.BARRIER,
						Messages.GUI_BTN_BACK
					).onClick { changeCategory(null) })
				continue
			}
			if (cate >= categories.size) {
				setItem(slot, GuiItemStack(Material.BLACK_STAINED_GLASS_PANE))
				continue
			}
			val icon: InventoryCategory = categories[cate++]
			setItem(
				slot, GuiItemStack(
					icon.material, icon.data, 1, icon.name, icon.description
				).onClick { changeCategory(icon) }
			)
		}

		// pagination
		if (page > 0) {
			setItem(
				52,
				GuiItemStack(
					Material.LIME_STAINED_GLASS_PANE,
					Messages.GUI_BTN_PREV_PAGE
				).onClick {
					page--
					buildAsync()
				}
			)
		} else setItem(52, GuiItemStack(Material.BLACK_STAINED_GLASS_PANE))
		if (pagesize * (page + 1) < items.size) {
			setItem(
				53,
				GuiItemStack(
					Material.LIME_STAINED_GLASS_PANE,
					Messages.GUI_BTN_NEXT_PAGE
				).onClick {
					page++
					buildAsync()
				}
			)
		} else setItem(53, GuiItemStack(Material.BLACK_STAINED_GLASS_PANE))

		// items
		for (slot in 0 until pagesize) {
			val index = pagesize * page + slot
			if (index >= items.size) {
				setItem(slot, null)
				continue
			}
			val playerItem = items[index]
			val item = playerItem.item
			val im = item.itemMeta
			val lore = ArrayList(playerItem.description)
			lore.add(playerItem.shippingCostLine)
			lore.add(Messages.PI_CLICK_TO_ATTACH_MAIL)
			im.lore = lore
			item.itemMeta = im
			setItem(slot, GuiItemStack(item).onClick {
				fallback.accept(playerItem)
			})
		}
		unlock()

		// open to player
		if (!isViewing(viewer)) {
			Utils.runSync {
				viewer.openInventory(inventory)
				viewer.playSound(viewer.eyeLocation, Sound.BLOCK_CHEST_OPEN, 1f, 1f)
			}
		}
	}

	private fun buildAsync() {
		lock()
		Utils.runNotSync { this.build() }
	}

	private fun changeCategory(category: InventoryCategory?) {
		currentCategory = category
		page = 0
		buildAsync()
	}

	override fun onClose(e: InventoryCloseEvent) {
		viewer.setItemOnCursor(null)
		viewer.playSound(viewer.eyeLocation, Sound.BLOCK_CHEST_CLOSE, 1f, 1f)
	}

	private fun getItems() {
		items = ArrayList()
		player.items.forEach { item ->
			if (
				attachments.containsKey(item) ||
				item.shippingCost == -1 ||
				(item is ActivableTimedPI && item.duration != 0) ||
				item.expire > 0 ||
				currentCategory?.let { item.categoryRegex.matches(it.regex.toRegex()) } == false
			) {
				return@forEach
			}
			items.add(item)
			// TODO configurable
		}
	}

	companion object {
		private const val pagesize = 45
	}
}
