package net.minevn.minigames.hooks

import net.minevn.minigames.Minigames
import net.minevn.minigames.PlayerData
import net.minevn.minigames.gadgets.MVPAnthem
import net.minevn.minigames.gadgets.Tomb
import net.minevn.minigames.items.types.MVPAnthemPI
import net.minevn.minigames.items.types.TombPI
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import java.util.*

abstract class SlaveListener : Listener {
	private val armorStandMap: MutableMap<UUID, ArmorStand> = HashMap()
	protected val main: Minigames = Minigames.getInstance()

	init {
		main.server.pluginManager.registerEvents(this, main)
	}

	//	@EventHandler(ignoreCancelled = true)
	fun onPlayerDeath(e: PlayerDeathEvent) {
		addTomb(e.entity)
	}

	//	@EventHandler
	fun onQuit(e: PlayerQuitEvent) {
		removeTomb(e.player)
	}

	//	@EventHandler
	fun onRespawn(e: PlayerRespawnEvent) {
		removeTomb(e.player)
	}

	protected fun addTomb(player: Player, loc: Location? = player.eyeLocation) {
		removeTomb(player)
		val playerData = PlayerData.getData(player) ?: return
		val item = playerData.getUsingItem(TombPI::class.java)
		val tomb = (if (item != null) item.tomb else Tomb.get("default")) ?: return
		armorStandMap[player.uniqueId] = tomb.spawn(player, loc)
	}

	protected fun removeTomb(player: Player) {
		val ast = armorStandMap.remove(player.uniqueId)
		ast?.remove()
	}

	protected fun removeAllTombs() {
		armorStandMap.values.forEach { it.remove() }
		armorStandMap.clear()
	}

	protected fun playMVPM(player: Player?) {
		val data = PlayerData.getData(player) ?: return
		val item = data.getUsingItem(MVPAnthemPI::class.java)
		var music: MVPAnthem? = null
		if (item != null) music = item.music
		if (music == null) return
		music.play(player)
	}
}