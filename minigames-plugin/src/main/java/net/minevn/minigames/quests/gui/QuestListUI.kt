package net.minevn.minigames.quests.gui

import net.minevn.guiapi.GuiInventory
import net.minevn.guiapi.GuiItemStack
import net.minevn.guiapi.XMaterial
import net.minevn.minigames.Messages
import net.minevn.minigames.PlayerData
import net.minevn.minigames.Utils
import net.minevn.minigames.quests.Quest
import net.minevn.minigames.quests.QuestCategory
import java.lang.System.currentTimeMillis
import kotlin.math.max

@Suppress("DEPRECATION")
class QuestListUI(private val viewer: PlayerData, private val cat: QuestCategory?) :
	GuiInventory(27, cat?.name ?: "") {
	val list = cat?.let { Quest.getByCategory(cat)?.map { it.getPlayerQuest(viewer) }}

	init {
		if (!list.isNullOrEmpty() && (cat?.id != "hidden" || viewer.player.hasPermission("minigames.hiddenquests"))) {
			list.forEachIndexed { slot, it ->
				if (slot > 27) return@forEachIndexed
				val quest = it.quest
				var iconData: Short = 0
				lateinit var status: String

				val iconType = when(!quest.mustObtain || it.obtained) {
					true -> when(it.finished) {
						true -> { // quest done
							status = Messages.GUI_BTN_QUEST_STATUS_FINISHED
							iconData = 30
							XMaterial.IRON_HOE
						}
						false -> { // quest doing
							status = Messages.GUI_BTN_QUEST_STATUS_DOING
							XMaterial.ENCHANTED_BOOK
						}
					}
					false ->  { // quest waiting to obtain
						status = Messages.GUI_BTN_QUEST_STATUS_OBTAINABLE
						XMaterial.BOOK
					}
				}.parseMaterial()!!

				val info = mutableListOf("§f", Messages.GUI_BTN_QUEST_INFO_STATUS.replace("%status%", status))
				if (!it.finished && (!quest.mustObtain || it.obtained)) {
					info.add(Messages.GUI_BTN_QUEST_INFO_PROGRESS
						.replace("%done%", it.done.toString())
						.replace("%required%", quest.done.toString())
					)
				} else if (it.nextReset > currentTimeMillis()) {
					var left: Long = (it.nextReset - currentTimeMillis()) / 1000L
					val hours = (left / 3600).toInt()
					left %= 3600
					val minutes = (left / 60).toInt()
					val time = if (hours > 0) "$hours ${Messages.MSG_HOUR}" else "$minutes ${Messages.MSG_MINUTE}"
					info.add(Messages.GUI_BTN_QUEST_INFO_RESET_WAIT.replace("%timer%", time))
				}
				info.addAll(quest.desc)
				info.add(Messages.GUI_BTN_QUEST_INFO_REWARD)
				info.addAll(quest.awards.map { "§f ⌬ ${it.displayName}" })
				if (quest.mustObtain) {
					info.add("§f")
					if (!it.obtained) {
						if (quest.isObtainable(viewer)) {
							info.add(Messages.GUI_BTN_QUEST_CLICK_TO_OBTAIN)
						} else {
							info.add(Messages.GUI_BTN_QUEST_UNOBTAINABLE)
						}
					} else if (!it.finished) {
						info.add(Messages.GUI_BTN_QUEST_CLICK_TO_CANCEL)
					}
				}

				setItem(slot, GuiItemStack(iconType, iconData, 1, quest.name, info).onClick { e ->
					e.isCancelled = true
					e.cursor = null
					if (it.finished) return@onClick
					if (quest.mustObtain && !it.obtained && quest.isObtainable(viewer) && !e.isShiftClick) {
						it.obtain()
						viewer.sendMessage("Bạn đã nhận nhiệm vụ ${it.quest.name}")
						openGUI()
						return@onClick
					}
					if (it.obtained && e.isShiftClick) {
						if (!list.contains(it)) return@onClick
						viewer.questPoints = max(viewer.questPoints - 10, 0)
						viewer.sendMessage("Bạn đã hủy nhiệm vụ ${it.quest.name}")
						it.reset()
						it.count(-it.done)
						openGUI()
						return@onClick
					}
					return@onClick
				})
			}
			openIventory(viewer.player)
		} else {
			viewer.player.sendTitle("§f", Messages.MSG_QUEST_NO_QUEST)
		}
	}

	private fun openGUI() {
		Utils.runAsync { Utils.runSync { QuestListUI(viewer, cat) } }
	}
}