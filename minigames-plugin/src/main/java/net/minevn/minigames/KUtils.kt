@file:JvmName("KUtils")
package net.minevn.minigames

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.minevn.guiapi.ClickAction
import net.minevn.guiapi.GuiInventory
import net.minevn.guiapi.GuiItemStack
import net.minevn.guiapi.XMaterial
import net.minevn.minigames.Utils.runSync
import net.minevn.mmclient.utils.MMUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta
import java.awt.Color
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Level
import kotlin.math.absoluteValue

private val monthParser = SimpleDateFormat("yyyy_'m'MM")
private val dateParser = SimpleDateFormat("dd/MM/yyyy")

/**
 * Chuyển mã màu & → §
 */
fun String.colorCodes() = ChatColor.translateAlternateColorCodes('&', this)
fun String.stripColors() = ChatColor.stripColor(this.colorCodes())!!
fun List<String>.colorCodes(): List<String> = this.map { it.colorCodes() }

/**
 * Chuyển mã màu § → &
 */
fun String.deColorCodes() = this.replace("§", "")
fun List<String>.deColorCodes() = this.map { it.deColorCodes() }

fun topDateType(topType: String, date: String) = topDateType(topType, date, Date())

fun topDateType(topType: String, date: String, time: Date) = when (date) {
	"month_top" -> "${topType}_${monthParser.format(time)}"
	"week_top" -> "${topType}_${getWeekOfYear(time)}"
	else -> topType
}


fun String.formatQuest(done: Int) = this.colorCodes().replace("%done%", done.toString())

fun List<String>.formatQuest(done: Int) = this.map { it.formatQuest(done) }

private fun getCalendar() = Calendar.getInstance(Locale.forLanguageTag("en-150"))!!

/**
 * = true nếu tuần hiện tại nằm ở năm trước nhiều ngày hơn
 */
fun weekBelongsToLastYear(time: Date) = getCalendar().run {
	this.time = time
	// lui về thứ 2 của tuần giữa 2 năm
	set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
	// nếu thứ 2 của tuần đầu năm rơi vào ngày 28/12 trở về trước của năm trước, thì tuần đầu năm
	// chiếm >= 4 ngày của năm trước và sẽ tính vào năm trước đó
	get(Calendar.MONTH) == 11 && get(Calendar.DAY_OF_MONTH) <= 28
}

/**
 * = true nếu tuần đầu tiên của năm nay nằm ở năm trước nhiều hơn
 */
fun firstWeekBelongsToLastYear(time: Date) = getCalendar().run {
	this.time = time
	// lui về ngày 1/1 của năm hiện tại
	set(Calendar.MONTH, 0)
	set(Calendar.DATE, 1)
	weekBelongsToLastYear(this.time)
}

fun getWeekOfYear(time: Date) = getCalendar().run {
	this.time = time
	var week = get(Calendar.WEEK_OF_YEAR)
	if (week == 1) {
		// tuần giữa 2 năm
		week = if (weekBelongsToLastYear(time)) {
			// tuần thuộc về năm trước
			// lui về 1 tuần rồi tính theo năm trước
			add(Calendar.DAY_OF_MONTH, -7)
			get(Calendar.WEEK_OF_YEAR)
		} else {
			// tuần thuộc về năm sau
			// tiến tới 1 tuần rồi tính theo năm sau
			add(Calendar.DAY_OF_MONTH, + 7)
			get(Calendar.WEEK_OF_YEAR) - 1
		}
	} else if (firstWeekBelongsToLastYear(time)) week--
	YearWeek(get(Calendar.YEAR), week)
}

fun formatDate(time: Date) = dateParser.format(time)!!

fun getStartOfWeek(time: Date) = getCalendar().run {
	this.time = time
	set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
	this.time!!
}

fun getEndOfWeek(time: Date) = getCalendar().run {
	this.time = time
	set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
	add(Calendar.DATE, 6)
	this.time!!
}

class YearWeek(var year: Int, var week: Int) {
	override fun toString() = "${year}_w${week.toString().padStart(2, '0')}"
}

fun ItemMeta.applyGameProfile(profile: GameProfile?) {
	if (profile == null) return
	try {
		val field = javaClass.getDeclaredField("profile")
		field.isAccessible = true
		field.set(this, profile)
	} catch (e: Exception) {
		Minigames.getInstance().logger.log(Level.WARNING, "SkullMeta error", e)
	}
}

fun GameProfile.createHeadItem() = XMaterial.PLAYER_HEAD.parseItem()!!.also {
	val im = it.itemMeta
	im.applyGameProfile(this)
	it.itemMeta = im
}

//fun String.rgb(prefix: String, suffix: String) = IridiumColorAPI.process("$prefix$this$suffix")

fun String.gradient(
	colors: List<Color>,
	bold: Boolean = false,
	italic: Boolean = false,
	underLine: Boolean = false,
	strikeThrough: Boolean = false
): String {
	val gradient = Gradient(colors, length)
	return map {char ->
		val color = gradient.nextColor()
		val colorText = Integer.toHexString(color.rgb)
			.substring(2)
			.map { "§$it" }.joinToString("")
		"§x$colorText" +
				(if (bold) "§l" else "") +
				(if (italic) "§o" else "") +
				(if (underLine) "§n" else "") +
				(if (strikeThrough) "§m" else "") +
				char
	}.joinToString("")
}

fun getGameProfile(texture: String?): GameProfile? {
	var texture1 = texture?.takeIf { it.isNotEmpty() } ?: return null
	if (!texture1.startsWith("http://textures.minecraft.net/texture/")) {
		texture1 = "http://textures.minecraft.net/texture/$texture1"
	}
	val encoded = Base64.getEncoder().encode("{textures:{SKIN:{url:\"$texture1\"}}}".toByteArray())
	val profile = GameProfile(UUID.randomUUID(), null)
	profile.properties.put("textures", Property("textures", String(encoded)))
	return profile
}

fun Location.getSerializedLocation(): String =
	"${this.world.name},${this.blockX + 0.5},${this.blockY},${this.blockZ + 0.5},${this.yaw},${this.pitch}"

fun String?.getDeserializedLocation(): Location? {
	if (this == null) return null
	val split = this.split(",")
	return Location(Bukkit.getWorld(split[0]), split[1].toDouble(), split[2].toDouble() + 1.0, split[3].toDouble(),
		split[4].toFloat(), split[5].toFloat())
}

fun Player.sendMessages(messages: List<String>) = messages.forEach { this.sendMessage(it) }

fun Player.isMonke() = MMUtils.getVersion(this) < 107

private val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun randomString(prefix: String, length: Int): String {
	return prefix + (1..length)
		.map { chars.random() }
		.joinToString("")
}

fun Exception.warning(message: String) = Minigames.getInstance().logger.log(Level.WARNING, message, this)

fun Exception.severe(message: String) = Minigames.getInstance().logger.log(Level.SEVERE, message, this)

fun Exception.severe(sender: CommandSender, message: String) = sender.sendMessage("§c$this: $message").also {
	this.severe(message)
}

fun log(message: String) = Minigames.getInstance().logger.info(message)

fun relativeTime(from: Long, to: Long): String {
	val seconds = ((to - from) / 1000L).toInt()
	val abs = seconds.absoluteValue
	return when {
		seconds < -172800 -> Messages.TIME_AFTER_DAYS.replace("%time%", "${abs / 86400}")
		seconds < -86400  -> Messages.TIME_TOMOROW
		seconds < -3600   -> Messages.TIME_AFTER_HOURS.replace("%time%", "${abs / 3600}")
		seconds < -60	  -> Messages.TIME_AFTER_MINUTES.replace("%time%", "${abs / 60}")
		seconds < 0		  -> Messages.TIME_RIGHTNOW
		seconds < 60 	  -> Messages.TIME_JUSTNOW
		seconds < 3600 	  -> Messages.TIME_MINUTES.replace("%time%", "${seconds / 60}")
		seconds < 86400   -> Messages.TIME_HOURS.replace("%time%", "${seconds / 3600}")
		seconds < 172800  -> Messages.TIME_YESTERDAY
		else 			  -> Messages.TIME_DAYS.replace("%time%", "${seconds / 86400}")
	}
}

fun relativeTime(from: Long) = relativeTime(from, System.currentTimeMillis())

fun GuiInventory.view(viewer: Player) {
	if (!isViewing(viewer)) runSync { viewer.openInventory(inventory) }
}

// region premade gui buttons
fun btnPagePrev(action: ClickAction) = GuiItemStack(Material.PAPER, Messages.GUI_BTN_PREV_PAGE).onClick(action)!!
fun btnPageNext(action: ClickAction) = GuiItemStack(Material.PAPER, Messages.GUI_BTN_NEXT_PAGE).onClick(action)!!
fun btnClose(action: ClickAction) = GuiItemStack(Material.BARRIER, Messages.GUI_BTN_CLOSE).onClick(action)!!
fun btnBack(action: ClickAction) = GuiItemStack(Material.BARRIER, Messages.GUI_BTN_BACK).onClick(action)!!
// endregion