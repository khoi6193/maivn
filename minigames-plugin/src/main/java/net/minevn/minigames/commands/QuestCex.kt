package net.minevn.minigames.commands

import net.minevn.minigames.PlayerData
import net.minevn.minigames.quests.QuestCategory
import net.minevn.minigames.quests.gui.QuestCategoryUI
import net.minevn.minigames.quests.gui.QuestListUI
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class QuestCex : CommandExecutor {
	override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>?): Boolean {
		if (sender !is Player) return true
		val playerData = PlayerData.getData(sender)

		// co truyen args → lay theo cat
		if (!args.isNullOrEmpty()){
			QuestListUI(playerData, QuestCategory.get(args[0]))
			return true
		}

		// khong truyen args → mo danh sach cat
		QuestCategoryUI(playerData)
		return true
	}
}