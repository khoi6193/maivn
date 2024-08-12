package net.minevn.minigames.hooks.gadgetsmenu.wrappers

import com.yapzhenyie.GadgetsMenu.cosmetics.pets.PetType
import net.minevn.minigames.hooks.gadgetsmenu.GMCosmeticWrapper

class PetCW(private val pet: PetType) : GMCosmeticWrapper() {
	override fun getContent() = pet
}