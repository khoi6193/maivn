package net.minevn.minigames.hooks.gadgetsmenu.wrappers

import com.yapzhenyie.GadgetsMenu.cosmetics.miniatures.MiniatureType
import net.minevn.minigames.hooks.gadgetsmenu.GMCosmeticWrapper

class MiniatureCW(private val miniature: MiniatureType) : GMCosmeticWrapper() {
	override fun getContent() = miniature
}