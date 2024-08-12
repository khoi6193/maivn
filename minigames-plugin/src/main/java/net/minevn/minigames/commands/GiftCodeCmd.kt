package net.minevn.minigames.commands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import net.minevn.minigames.*
import net.minevn.minigames.Utils.runNotSync
import net.minevn.minigames.gadgets.ItemBundle
import net.minevn.minigames.items.types.ItemBundlePI
import java.lang.System.currentTimeMillis

class GiftCodeCmd {
	init {
		CommandAPICommand("giftcode")
			.withAliases("giftcodes", "code")
//			.withPermission("minigames.giftcode")
			.withArguments(StringArgument("code"))
			.executesPlayer(PlayerCommandExecutor{ p, a->
				val pd = PlayerData.getData(p)
				runNotSync {
					try {
						val sql = MySQL.getInstance()
						val code = a[0] as String
						val setid = sql.getGiftCode(code)
						val set = if (setid != null) ItemBundle[setid] else null
						if (set == null) {
							pd.sendMessage(Messages.ERR_GIFTCODE_NOT_VALID)
							return@runNotSync
						}
						if (set.onlyOnce && sql.bundleUsed(setid, p.uniqueId.toString())) {
							pd.sendMessage(Messages.ERR_GIFTCODE_ONLY_ONCE)
							return@runNotSync
						}
						sql.useGiftCode(code, p.uniqueId.toString())
						val item = ItemBundlePI(pd, -1, set, 0, currentTimeMillis())
						pd.addItem(item, "From giftcode $code")
						pd.sendMessage(Messages.MSG_ITEM_AWARD.replace("%item%", set.name))
					} catch (e: Exception) {
						pd.sendMessage(Messages.ERR_GENERAL)
						e.severe(p, "Error using giftcode")
					}
				}
			})
			.register()

		// admin cmd
		val generate = CommandAPICommand("generate")
			.withAliases("taocode", "gen")
			.withPermission("minigames.giftcode.generate")
			.withArguments(
				StringArgument("setid")
					.replaceSuggestions(ArgumentSuggestions.strings { ItemBundle.getIDSuggestions() }),
				StringArgument("prefix"),
				IntegerArgument("length", 5, 32),
				IntegerArgument("amount", 1, 100)
			)
			.executes(CommandExecutor {s, a ->
				runNotSync {
					try {
						val setid = a[0] as String
						val prefix = a[1] as String
						val length = a[2] as Int
						val amount = a[3] as Int
						ItemBundle[setid] ?: run {
							s.sendMessage("setid khong ton tai")
							return@runNotSync
						}
						val codes = (1 .. amount).map { randomString(prefix, length) }.toTypedArray()
						val sql = MySQL.getInstance()
						if (!sql.validate(codes)) {
							s.sendMessage("code bi trung, hay gen lai, thu doi prefix")
							return@runNotSync
						}
						sql.createGiftCode(setid, codes)
						s.sendMessage("da tao ${codes.size} code, check sql")
					} catch (e: Exception) {
						e.severe(s, "Error generating giftcode, check console")
					}
				}
			})

		CommandAPICommand("giftcodeadmin")
			.withAliases("giftcodesadmin", "codeadmin", "ca", "gca")
			.withPermission("minigames.giftcode.admin")
			.withSubcommand(generate)
			.register()
	}
}