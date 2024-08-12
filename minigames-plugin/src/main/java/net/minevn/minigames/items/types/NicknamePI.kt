package net.minevn.minigames.items.types

import net.minevn.minigames.Messages
import net.minevn.minigames.PlayerData
import net.minevn.minigames.Utils
import net.minevn.minigames.chatactions.types.NickRenameCL
import net.minevn.minigames.createHeadItem
import net.minevn.minigames.gadgets.NamePreset
import net.minevn.minigames.items.CustomizablePI
import net.minevn.minigames.items.ItemData
import net.minevn.minigames.items.UsablePI
import net.minevn.minigames.items.datatypes.NamedData
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class NicknamePI(
	owner: PlayerData?, id: Int, private val preset: NamePreset, expire: Long, obtained: Long, using: Boolean,
) : UsablePI(
	Messages.PI_CATEGORY_NICKNAME, owner, id, preset.iconType, preset.iconData, expire, obtained, using
), CustomizablePI<NamedData> {
	private var nicknameData = NamedData()

	override fun getData(): String = preset.id

	override fun getDescription(): MutableList<String> = preset.description
		.takeIf { it.isNotEmpty() } ?: Messages.PI_NICKNAME_DESC

	override fun clone() = NicknamePI(null, -1, preset, expire, System.currentTimeMillis(), false)

	override fun getItem(): ItemStack {
		val item = preset.gameProfile?.createHeadItem() ?: super.getItem()
		val im = item.itemMeta
		im.setDisplayName(Messages.PI_NICKNAME.replace("%preset%", preset.name))
		val lores = mutableListOf<String>()
		lores.add("§f")
		addDescription(lores)
		addExpireDate(lores)
		lores.add(Messages.PI_CLICKR_TO_RENAME)
		addUsingStatus(lores)
		im.lore = lores
		item.itemMeta = im
		return item
	}

	override fun getItemData() = nicknameData

	override fun getDataClass() = NamedData::class.java

	override fun initData(json: String) {
		nicknameData = ItemData.parseItem(json, getDataClass())
	}

	fun updateNickname() {
		owner.displayName = preset.parse(nicknameData.name ?: owner.name)
	}

	override fun onUsingToggle() {
		if (isUsing) {
			Utils.runSync { updateNickname() }
		} else {
			Utils.runSync {
				owner.displayName = owner.name
			}
		}
	}

	override fun onClick(e: InventoryClickEvent) {
		if (e.isRightClick) {
			val player = e.whoClicked as Player
			player.closeInventory()
			NickRenameCL(owner, this)
		} else super.onClick(e)
	}

	companion object {
		@JvmStatic
		fun fromData(
			owner: PlayerData?, itemData: String?, id: Int, expire: Long, obtained: Long, using: Boolean,
			amount: Int,
		) = NamePreset[itemData!!]?.let { NicknamePI(owner, id, it, expire, obtained, using) }
			?: throw IllegalArgumentException("namepreset $itemData khong ton tai")
	}
}