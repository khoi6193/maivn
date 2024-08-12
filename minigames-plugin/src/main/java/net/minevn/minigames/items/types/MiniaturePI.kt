package net.minevn.minigames.items.types

import net.minevn.guiapi.XMaterial
import net.minevn.minigames.*
import net.minevn.minigames.hooks.gadgetsmenu.WrappedCosmeticType
import net.minevn.minigames.hooks.gadgetsmenu.wrappers.MiniatureCW
import net.minevn.minigames.items.CustomizablePI
import net.minevn.minigames.items.ItemData
import net.minevn.minigames.items.UsablePI
import net.minevn.minigames.items.datatypes.NamedData
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class MiniaturePI(
	owner: PlayerData?, id: Int, private val miniature: WrappedCosmeticType<MiniatureCW>?,
	private val backedData: String, expire: Long, obtained: Long, using: Boolean
) : UsablePI(
	Messages.PI_CATEGORY_MINIATURE, owner, id, miniature?.getMaterial() ?: XMaterial.PAPER.parseMaterial(),
	miniature?.getData() ?: 0, expire, obtained, using
), CustomizablePI<NamedData> {
	private var miniatureData = NamedData()

	override fun getData(): String = miniature?.content?.getContent()?.name ?: backedData

	override fun getDescription(): MutableList<String> = Messages.PI_MINIATURE_DESC
	override fun clone() = MiniaturePI(null, -1, miniature, backedData, expire, System.currentTimeMillis(), false)

	override fun getGameProfile() = miniature?.getGameProfile()

	override fun getItem(): ItemStack {
		val item = super.getItem()
		val im = item.itemMeta
		if (im is SkullMeta) im.applyGameProfile(gameProfile)
		miniature?.content?.getContent()?.also { pet ->
			im.setDisplayName(pet.displayName)
			val lores = mutableListOf<String>()
			addDescription(lores)
			addExpireDate(lores)
			addUsingStatus(lores)
			im.lore = lores
		} ?: run { im.setDisplayName("unsupported pet: $backedData") }
		item.itemMeta = im
		return item
	}

	override fun getItemData() = miniatureData

	override fun getDataClass() = NamedData::class.java

	override fun initData(json: String) {
		miniatureData = ItemData.parseItem(json, getDataClass())
	}

	override fun onUsingToggle() {
		if (isUsing) {
			equip()
		} else {
			Utils.runSync {
				val player = owner?.player?.takeIf { it.isOnline } ?: return@runSync
				Minigames.getInstance().gmHook?.unequipMiniature(player)
			}
		}
	}

	fun equip() {
		if (miniature == null) return
		Utils.runSync {
			val player = owner?.player?.takeIf { it.isOnline } ?: return@runSync
			Minigames.getInstance().gmHook?.equip(player, miniature, miniatureData.name)
		}
	}

	companion object {
		@JvmStatic
		fun fromData(
			owner: PlayerData?, itemData: String?, id: Int, expire: Long, obtained: Long, using: Boolean,
			amount: Int
		) = Minigames.getInstance().gmHook?.getMiniature(itemData!!)
			.let { MiniaturePI(owner, id, it, itemData!!, expire, obtained, using) }
	}
}