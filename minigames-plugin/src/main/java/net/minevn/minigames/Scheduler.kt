package net.minevn.minigames

import net.minevn.minigames.quests.QuestAttempt
import net.minevn.minigames.quests.QuestObjective
import net.minevn.mmclient.MatchMakerClient
import net.minevn.mmclient.slaves.SlaveHandler
import net.minevn.mmclient.slaves.SlaveState
import org.bukkit.scheduler.BukkitRunnable

class Scheduler : BukkitRunnable() {
	private val main = Minigames.getInstance()!!
	private val mmslave: SlaveHandler? = MatchMakerClient.getInstance().slaveHandler
	private var tick = 0

	init {
		runTaskTimer(main, 1, 1)
	}

	override fun run() {
		try {
			if (tick % 1200 == 0) {
				mmslave
					?.takeIf { it.state == SlaveState.PLAYING }
					?.players
					?.mapNotNull {
						PlayerData.getData(it) ?: run {
							main.logger.warning("Null PlayerData: ${it.name}")
							null
						}
					}
					?.forEach {
						it.addPlayTime()
						QuestAttempt(it, QuestObjective.ONLINE)
					}
			}
			mmslave?.let {main.dropTasks?.dropped?.forEach { it.applyNearest(mmslave) } }
		} finally {
			tick++
			if (tick >= 12000) tick = 0
		}
	}
}