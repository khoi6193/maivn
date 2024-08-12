package net.minevn.minigames.quests.gui

import net.minevn.guiapi.GuiInventory
import net.minevn.guiapi.GuiItemStack
import net.minevn.guiapi.XMaterial
import net.minevn.minigames.Messages
import net.minevn.minigames.PlayerData
import net.minevn.minigames.quests.Quest
import net.minevn.minigames.quests.QuestCategory

@Suppress("DEPRECATION")
class QuestCategoryUI(viewer: PlayerData) : GuiInventory(9, Messages.GUI_TITLE_QUEST_CATEGORIES) {
	val list = QuestCategory.getAll().filter { it.showInGui && !Quest.getByCategory(it).isNullOrEmpty() }

	init {
		val player = viewer.player
		if (list.isNotEmpty()) {
			list.forEachIndexed { index, cat ->
				if (index < 8) {
					setItem(
						index,
						GuiItemStack(cat.iconType, cat.iconData, 1, cat.name, cat.description).onClick {
							it.isCancelled = true
							QuestListUI(viewer, cat)
						}
					)
				}
			}
			setItem(8, GuiItemStack(XMaterial.BARRIER.parseMaterial(), 0, Messages.GUI_BTN_CLOSE).onClick {
				it.isCancelled = true
				player.closeInventory()
			})
			openIventory(player)
		} else {
			player.sendTitle("§f", Messages.MSG_QUEST_NO_QUEST)
		}
	}
}