package net.minevn.minigames.award.types

import net.minevn.minigames.Messages
import net.minevn.minigames.PlayerData
import net.minevn.minigames.award.PlayerAward
import net.minevn.minigames.cases.Case
import net.minevn.minigames.items.types.CaseKeyPI
import net.minevn.minigames.items.types.CasePI
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class CaseBundleAW(val case: Case, amount: Int) : PlayerAward(ItemUnit.PCS, amount) {

	override fun apply(p: Player?, note: String?) {
		val data = PlayerData.getData(p) ?: return
		for (i in 1 .. amount) {
			data.addItem(CasePI(data, -1, case, 0, System.currentTimeMillis()), "$note (CaseBundbleAW)")
			data.addItem(CaseKeyPI(data, -1, case, 0, System.currentTimeMillis()), "$note (CaseBundbleAW)")
		}
	}

	override fun getGuiItem(): ItemStack = case.caseIcon.apply {
		// rename to "Case Bundle"
		itemMeta = itemMeta.apply {
			setDisplayName(Messages.PI_ITEM_CASEBUNDLE.replace("%case%", case.name))
		}
	}

	override fun getName(): String = case.name

	override fun typeName(): String = ""

	// static
	/**
	 * valid data: case$amount
	 * example: hlw_01$1
	 */
	companion object {
		@Throws(Exception::class)
		@JvmStatic
		fun fromData(data: String): CaseBundleAW {
			val arr = data.split("$")
			return CaseBundleAW(Case.get(arr[0]), arr[1].toInt())
		}
	}
}