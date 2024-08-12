package net.minevn.minigames.drops

import me.clip.placeholderapi.PlaceholderAPI
import net.minevn.guiapi.XMaterial
import net.minevn.minigames.*
import net.minevn.minigames.Utils.randomCircle
import net.minevn.minigames.quests.QuestAttempt
import net.minevn.minigames.quests.QuestObjective
import net.minevn.mmclient.MatchMakerClient
import net.minevn.mmclient.slaves.SlaveHandler
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.logging.Level

class DropEntity {
	private var entity: ArmorStand? = null
	val location = if (locations.isEmpty()) null else locations.random()

	fun getItem(): ItemStack? {
		if (material == null) return null
		val item = ItemStack(material!!, 1, data)
		val im = item.itemMeta
		im.setDisplayName(name)
		im.isUnbreakable = true
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES)
		item.itemMeta = im
		return item
	}

	fun spawn(): ArmorStand? {
		if (location == null || material == null) return null
		if (entity != null) return entity!!
		val loc = randomCircle(location.first, location.second)
		while (!loc.block.isEmpty) loc.y = loc.y + 1
		entity = Minigames.nms.createArmorStand(loc, name, getItem(), true, Minigames.getInstance())
		return entity!!
	}

	fun apply(p: Player) {
		despawn()
		p.playSound(p.location, "misc.drop_loot", 1f, 1f)
		QuestAttempt(PlayerData.getData(p), QuestObjective.COLLECT_DROP)
	}

	fun applyNearest(slave: SlaveHandler?) {
		if (entity == null || slave == null) return
		val player = slave.players.firstOrNull {
			it.gameMode != GameMode.CREATIVE && it.gameMode != GameMode.SPECTATOR &&
					it.world == entity!!.world && it.location.distance(entity!!.location) < 1 &&
					PlaceholderAPI.setPlaceholders(it, "%minevn_dead%").isEmpty()
		} ?: return
		Utils.runSync { apply(player) }
	}

	fun despawn() {
		entity!!.remove()
		entity = null
	}

	companion object {
		var name: String? = null
			private set
		var material: Material? = null
			private set
		var data: Short = 0
			private set

		var locations = mutableListOf<Pair<Location, Double>>()
			private set

		@JvmStatic
		fun load() {
			val main = Minigames.getInstance()
			main.logger.info("Drop entity...")
			val configFile = File("${Configs.getMasterPath()}drop-entity.yml")
			if (!configFile.exists()) configFile.createNewFile()
			val config = YamlConfiguration.loadConfiguration(configFile)
			try {
				name = config.getString("name")?.colorCodes()
				material = XMaterial.quickMatch(config.getString("material", ""))
				data = config.getInt("data").toShort()
			} catch (e: Exception) {
				main.logger.log(Level.WARNING, "Can't load drop entity", e)
			} finally {
				Utils.runLater({
					val folder = File( "${Configs.getMasterPath()}drops")
					if (!folder.exists()) folder.mkdirs()
					val mapId = MatchMakerClient.getInstance().info.mapID
					val file = File(folder, "$mapId.yml")
					if (!file.exists()) {
						main.logger.warning("No drop entity in map $mapId, file not found")
						return@runLater
					}
					val locationConfig = YamlConfiguration.loadConfiguration(file).getConfigurationSection("locations")
					if (locationConfig == null) {
						main.logger.warning("No drop entity in map $mapId, no locations set")
						return@runLater
					}
					locationConfig.getKeys(false).forEach {
						locations.add(Pair(locationConfig.getString("$it.location").getDeserializedLocation()!!,
							locationConfig.getDouble("$it.radius", 0.0)))
					}
					main.logger.info("Loaded drop entity")
				},3 * 20)
			}
		}
	}
}