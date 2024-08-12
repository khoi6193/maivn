package net.minevn.minigames.quests

import net.minevn.minigames.*
import net.minevn.minigames.award.PlayerAward
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.logging.Level

class Quest(
	val id: String,
	val name: String,
	val desc: List<String>,
	val done: Int,
	val doneMessage: List<String>,
	val category: QuestCategory,
	val objective: QuestObjective,
	private val conditions: List<Condition>,
	val awards: List<PlayerAward>,
	val randomAward: Boolean,
	private val timer: QuestTimer?,
	val cooldown: Long,
	/**
	 * Nhóm nhiệm vụ dành đối với nhiệm vụ phải ấn "nhận nhiệm vụ" <br />
	 * Không thể cùng lúc nhận nhiều nhiệm vụ cùng 1 nhóm
	 */
	val group: String? = null,
	/**
	 * = true nếu là nhiệm vụ phải ấn "nhận nhiệm vụ"
	 */
	val mustObtain: Boolean = false,
) {
	private var announceEvery: Int = 0
	private var announce: String? = null

	fun getPlayerQuest(player: PlayerData): PlayerQuest {
		val map = player.quests
		return map[this]
			?.takeIf { it.quest == this && (it.nextReset == 0L || it.nextReset >= System.currentTimeMillis()) }
			?: PlayerQuest(this, 0, getResetTime(), finished = false, obtained = false).also {
				map[this] = it
			}
	}

	fun checkCondition(a: QuestAttempt): Boolean {
		return conditions.all {
			try {
				it.check(a)
			} catch (e: Exception) {
				val msg = "Error checking condition ${it.type.name} of quest $id"
				Minigames.getInstance().logger.log(Level.SEVERE, msg, e)
				false
			}
		}
	}


	fun enableAnnouncement(every: Int, message: String?) {
		announceEvery = every
		announce = message ?: Messages.MSG_QUEST_ANNOUNCE
	}

	fun announce(player: Player, done: Int) {
		if (announceEvery != 0 && done % announceEvery == 0 && announce != null) {
			player.sendMessage(announce!!
				.replace("%name%", name)
				.replace("%done%", done.toString())
				.replace("%required%", this.done.toString())
			)
		}
	}

	fun isObtainable(player: PlayerData): Boolean {
		if (!mustObtain || group == null) return true
		if (getPlayerQuest(player).obtained) return false
		return player.quests.all {
			it.key.group != group || !it.value.obtained || it.value.finished
		}
	}

	private fun getResetTime() = if (timer != null && cooldown == 0L) timer.getResetTime() else 0L

	companion object {
		private val quests = LinkedHashMap<String, Quest>()
		private lateinit var questsByObjective: Map<QuestObjective, List<Quest>>
		private lateinit var questsByCategory: Map<QuestCategory, List<Quest>>

		/**
		 * Load quests from directory
		 */
		fun load(directory: File) {
			val main = Minigames.getInstance()
			if (!directory.exists() || !directory.isDirectory) return
			main.logger.info("Loading quests from folder ${directory.name}")
			for (file in directory.listFiles()!!) {
				if (file.isDirectory) {
					load(file)
					continue
				}
				if (file.extension != "yml") continue
				val config = YamlConfiguration.loadConfiguration(file)
				config.getKeys(false).forEach { id ->
					try {
						val q = config.getConfigurationSection(id)!!
						val name = q.getString("name")!!.colorCodes()
						val group = q.getString("group", "")
						val mustobtain = q.getBoolean("mustobtain")
						val done = q.getInt("done")
						val cooldown = q.getLong("cooldown")
						val desc = q.getStringList("desc").formatQuest(done)
						val donemess = q.getStringList("done-message").formatQuest(done)
						val category: QuestCategory = QuestCategory.get(q.getString("category")!!)!!
						val obj = QuestObjective.valueOf(q.getString("objective")!!)

						val conditions = q.getConfigurationSection("conditions")
							?.getKeys(false)
							?.map { condition ->
								val ctype = ConditionType.valueOf(condition)
								val value = q.getString("conditions.$condition")!!
								Condition(ctype, value)
							}
							?: listOf()

						val randomAward = q.getBoolean("random-award")
						val awards = q.getConfigurationSection("awards")
							?.getKeys(false)
							?.map {
								val section = q.getConfigurationSection("awards.$it")!!
								val itemType = section.getString("type")!!
								val itemData = section.getString("data", "")
								val display = section.getString("display")?.colorCodes()
								PlayerAward.fromData(itemType, itemData)!!.apply {
									displayName = display
								}
							}
							?: listOf()

						val timerName = q.getString("timer")
						val timer = if (timerName == null) null else QuestTimer.valueOf(timerName)

						val quest = Quest(id, name, desc, done, donemess, category, obj, conditions, awards,
							randomAward, timer, cooldown, group, mustobtain)
						q.getConfigurationSection("announcement")
							?.also {
								if (it.getBoolean("enabled")) {
									quest.enableAnnouncement(
										it.getInt("every").takeIf { i -> i > 0 }!!,
										it.getString("message")?.colorCodes()
									)
								}
							}
						quests[id] = quest
					} catch (ex: Exception) {
						main.logger.log(Level.WARNING, "Can't load quest $id in file ${file.name}", ex)
					}
				}

			}
		}

		@JvmStatic
		fun load() {
			quests.clear()
			val main = Minigames.getInstance()
			main.logger.info("Quests...")
			try {
				val root = File(Configs.getMasterPath(), "quest/quests")
				if (!root.exists() || !root.isDirectory) {
					main.logger.warning("Quests folder not valid")
					return
				}
				load(root)
				main.logger.info("Loaded ${quests.size} quests")
			} finally {
				questsByObjective = quests.values.groupBy { it.objective }
				questsByCategory = quests.values.groupBy { it.category }
			}
		}

		@JvmStatic
		fun getByObjective(objective: QuestObjective) = questsByObjective[objective]

		@JvmStatic
		fun getByCategory(category: QuestCategory) = questsByCategory[category]

		@JvmStatic
		fun get(id: String) = quests[id]
	}
}