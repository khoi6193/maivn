package net.minevn.minigames.hooks.gadgetsmenu

import com.yapzhenyie.GadgetsMenu.GadgetsMenu
import com.yapzhenyie.GadgetsMenu.cosmetics.miniatures.MiniatureType
import com.yapzhenyie.GadgetsMenu.cosmetics.pets.PetType
import net.minevn.minigames.PlayerData
import net.minevn.minigames.hooks.gadgetsmenu.wrappers.MiniatureCW
import net.minevn.minigames.hooks.gadgetsmenu.wrappers.PetCW
import net.minevn.minigames.items.types.MiniaturePI
import net.minevn.minigames.items.types.PetPI
import org.bukkit.entity.Player

class GadgetsMenuHook {
	private val pets = PetType.values().associateBy { it.name.replace(" ", "_").lowercase() }
	private val miniatures = MiniatureType.values().associateBy { it.name.replace(" ", "_").lowercase() }

	private fun manager(player: Player) = GadgetsMenu.getPlayerManager(player)

	fun changePetName(player: Player, name: String?) {
		manager(player).currentPet?.entityPet?.entityNMS?.customNameNMS = name ?: "Pet của ${player.name}"
	}

	fun equip(player: Player, cosmeticType: WrappedCosmeticType<GMCosmeticWrapper>?, name: String? = null) {
		if (cosmeticType == null) return
		val manager = manager(player)
		when (val cosmetic = cosmeticType.content.getContent()) {
			is PetType ->  {
				manager.equipPet(cosmetic)
				changePetName(player, name)
			}
			is MiniatureType -> manager.equipMiniature(cosmetic)
		}
	}

	fun updateGadgets(playerData: PlayerData) {
		val player = playerData.player?.takeIf { it.isOnline } ?: return
		val gm = GadgetsMenu.getPlayerManager(player)

		playerData.getUsingItem(PetPI::class.java)?.equip()
			?: run { gm.unequipPet() }
		playerData.getUsingItem(MiniaturePI::class.java)?.equip()
			?: run { gm.unequipMiniature() }
	}

	fun getPet(name: String) = pets[name.lowercase().replace(" ", "_")]
		?.let { WrappedCosmeticType(PetCW(it)) }
		?: throw IllegalArgumentException("pet $name khong ton tai")
	fun unequipPet(player: Player) = manager(player).unequipPet()

	fun getMiniature(name: String) = miniatures[name.lowercase().replace(" ", "_")]
		?.let { WrappedCosmeticType(MiniatureCW(it)) }
		?: throw IllegalArgumentException("miniature $name khong ton tai")
	fun unequipMiniature(player: Player) = manager(player).unequipMiniature()

	fun getSuggestions(input: MutableList<String>) {
		input.addAll(PetType.values().map { "PetPI$${it.name.replace(" ", "_")}$0" })
		input.addAll(MiniatureType.values().map { "MiniaturePI$${it.name.replace(" ", "_")}$0" })
	}
}