package net.minevn.minigames.items

interface CustomizablePI<T> where T : ItemData {
	fun getItemData(): T

	fun getDataClass(): Class<T>

	fun initData(json: String)
}