package net.minevn.minigames.items.types

import net.minevn.guiapi.XMaterial
import net.minevn.minigames.*
import net.minevn.minigames.chatactions.types.PetRenameCL
import net.minevn.minigames.hooks.gadgetsmenu.WrappedCosmeticType
import net.minevn.minigames.hooks.gadgetsmenu.wrappers.PetCW
import net.minevn.minigames.items.CustomizablePI
import net.minevn.minigames.items.ItemData
import net.minevn.minigames.items.UsablePI
import net.minevn.minigames.items.datatypes.NamedData
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class PetPI(
	owner: PlayerData?, id: Int, private val pet: WrappedCosmeticType<PetCW>?,
	private val backedData: String, expire: Long, obtained: Long, using: Boolean
) : UsablePI(
	Messages.PI_CATEGORY_PET, owner, id, pet?.getMaterial() ?: XMaterial.PAPER.parseMaterial(),
	pet?.getData() ?: 0, expire, obtained, using
), CustomizablePI<NamedData> {
	private var petData = NamedData()

	override fun getData(): String = pet?.content?.getContent()?.name ?: backedData

	override fun getDescription(): MutableList<String> = Messages.PI_PET_DESC
	override fun clone() = PetPI(null, -1, pet, backedData, expire, System.currentTimeMillis(), false)

	override fun getGameProfile() = pet?.getGameProfile()

	override fun getItem(): ItemStack {
		val item = gameProfile?.createHeadItem() ?: super.getItem()
		val im = item.itemMeta
		pet?.content?.getContent()?.also { pet ->
			im.setDisplayName(pet.displayName)
			val lores = mutableListOf<String>()
			addDescription(lores)
			addExpireDate(lores)
			lores.add(Messages.PI_CLICKR_TO_RENAME)
			addUsingStatus(lores)
			im.lore = lores
		} ?: run { im.setDisplayName("unsupported pet: $backedData") }
		item.itemMeta = im
		return item
	}

	override fun getItemData() = petData

	override fun getDataClass() = NamedData::class.java

	override fun initData(json: String) {
		petData = ItemData.parseItem(json, getDataClass())
	}

	override fun onUsingToggle() {
		if (isUsing) {
			equip()
		} else {
			Utils.runSync {
				val player = owner?.player?.takeIf { it.isOnline } ?: return@runSync
				Minigames.getInstance().gmHook?.unequipPet(player)
			}
		}
	}

	fun equip() {
		if (pet == null) return
		Utils.runSync {
			val player = owner?.player?.takeIf { it.isOnline } ?: return@runSync
			Minigames.getInstance().gmHook?.equip(player, pet, petData.name)
		}
	}

	override fun onClick(e: InventoryClickEvent) {
		if (e.isRightClick) {
			e.whoClicked.closeInventory()
			PetRenameCL(owner, this)
		} else super.onClick(e)
	}

	companion object {
		@JvmStatic
		fun fromData(
			owner: PlayerData?, itemData: String?, id: Int, expire: Long, obtained: Long, using: Boolean,
			amount: Int
		) = Minigames.getInstance().gmHook?.getPet(itemData!!)
			.let { PetPI(owner, id, it, itemData!!, expire, obtained, using) }
	}
}