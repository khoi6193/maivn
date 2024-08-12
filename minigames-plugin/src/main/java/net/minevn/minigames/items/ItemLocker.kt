package net.minevn.minigames.items

import net.minevn.minigames.Minigames
import net.minevn.minigames.PlayerData
import java.lang.System.currentTimeMillis
import java.util.concurrent.locks.ReentrantLock

class ItemLocker(val player: PlayerData) : ReentrantLock() {
	val main = Minigames.getInstance()!!
	private var locktime = 0L
	private var lastmessage = "unknown"

	fun lock(message: String) {
		lastmessage = message
		main.logger.info("ItemAction $player -> $message")
		lock()
	}

	override fun lock() {
		locktime = currentTimeMillis()
		super.lock()
	}

	override fun unlock() {
		super.unlock()
		// warn if locktime is too long
		if (locktime != 0L && currentTimeMillis() - locktime > 1000) {
			main.logger.warning("ItemAction $player -> $lastmessage took ${currentTimeMillis() - locktime}ms")
		}
		locktime = 0L
		lastmessage = "unknown"
	}
}