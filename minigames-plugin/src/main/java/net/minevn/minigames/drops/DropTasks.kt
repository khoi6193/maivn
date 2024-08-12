package net.minevn.minigames.drops

import net.minevn.minigames.Minigames
import org.bukkit.scheduler.BukkitRunnable

class DropTasks : BukkitRunnable() {
	val dropped: MutableList<DropEntity> = mutableListOf()
	val enabled = DropEntity.material != null

	init {
		if (enabled) runTaskTimer(Minigames.getInstance(), 20 * 60, 20 * 3 * 60)
	}

	override fun run() {
		val drop = DropEntity()
		if (drop.spawn() == null) return
		dropped.add(drop)
		//Minigames.getInstance().logger.info("spawned 1 drop at ${drop.location}")
	}

	override fun cancel() {
		super.cancel()
		dropped.forEach{ it.despawn() }
	}
}