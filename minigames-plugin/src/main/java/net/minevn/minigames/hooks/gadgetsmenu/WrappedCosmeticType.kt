package net.minevn.minigames.hooks.gadgetsmenu

import com.yapzhenyie.GadgetsMenu.utils.GMaterial
import org.bukkit.Material

/**
 * Wrapper cho các cosmetic của GadgetsMenu, nhằm đảm bảo không bị lỗi khi trong slave
 */
class WrappedCosmeticType<out T: GMCosmeticWrapper>(val content: T) {
	private fun getGMaterial(): GMaterial = content.getContent().material
	fun getMaterial(): Material = getGMaterial().enumMaterial.type
	fun getData() = getGMaterial().enumMaterial.data.toShort()
	fun getGameProfile() = net.minevn.minigames.getGameProfile(getGMaterial().texture)
}