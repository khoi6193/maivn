package net.minevn.minigames;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Predicate;

public class Utils {
	public static String getDate(long time) {
		Timestamp stamp = new Timestamp(time);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date date = new Date(stamp.getTime());
		return formatter.format(date);
	}

	public static String getRemainDays(long time) {
		time -= System.currentTimeMillis();
		if (time < 0)
			return "0 giờ";
		if (time < 86400000)
			return Math.round(time / 3600000d) + " giờ";
		return Math.round(time / 86400000d) + " ngày";
	}

	public static void runLater(Runnable r) {
		Bukkit.getScheduler().runTask(Minigames.getInstance(), r);
	}

	public static void runLater(Runnable r, int delay) {
		Bukkit.getScheduler().runTaskLater(Minigames.getInstance(), r, delay);
	}

	public static void runSync(Runnable r) {
		if (Bukkit.isPrimaryThread()) {
			r.run();
		} else {
			Bukkit.getScheduler().runTask(Minigames.getInstance(), r);
		}
	}

	public static void runNotSync(Runnable r) {
		if (!Bukkit.isPrimaryThread()) {
			r.run();
		} else {
			Bukkit.getScheduler().runTaskAsynchronously(Minigames.getInstance(), r);
		}
	}

	public static void runAsync(Runnable r) {
		Bukkit.getScheduler().runTaskAsynchronously(Minigames.getInstance(), r);
	}

	public static Player getPlayer(int id) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getEntityId() == id) {
				return player;
			}
		}
		return null;
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}

	public static ItemStack createItemStack(Material type, short data, String name, String... lores) {
		ItemStack item = new ItemStack(type, 1, data);
		ItemMeta im = item.getItemMeta();
		if (name != null)
			im.setDisplayName(name);
		if (lores != null)
			im.setLore(Arrays.asList(lores));
		item.setItemMeta(im);
		return item;
	}

	public static ItemStack createItemStack(Material type, short data, String name, List<String> lores) {
		ItemStack item = new ItemStack(type, 1, data);
		ItemMeta im = item.getItemMeta();
		if (name != null) {
			im.setDisplayName(name);
		}
		if (lores != null) {
			im.setLore(lores);
		}
		item.setItemMeta(im);
		return item;
	}

	public static ItemStack createItemStack(Material type, String name, String... lores) {
		ItemStack item = new ItemStack(type);
		ItemMeta im = item.getItemMeta();
		if (name != null) {
			im.setDisplayName(name);
		}
		if (lores != null) {
			im.setLore(Arrays.asList(lores));
		}
		item.setItemMeta(im);
		return item;
	}

	public static void glow(ItemStack item) {
		ItemMeta im = item.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(im);
	}

	public static void unGlow(ItemStack item) {
		for (Enchantment enchantment : item.getEnchantments().keySet()) {
			item.removeEnchantment(enchantment);
		}
	}

	public static int getMineStrikeItemPrice(String name) {
		int price = -1;
		if (name.contains("鈍")) {
			price = 2;
		}
		if (name.contains("鈑")) {
			price = 4;
		}
		if (name.contains("鈒")) {
			price = 7;
		}
		if (name.contains("鈓")) {
			price = 10;
		}
		if (name.contains("鈊")) {
			price = 12;
		}
		return price;
	}

	public static void makeUnbreakable(ItemStack item) {
		ItemMeta im = item.getItemMeta();
		im.setUnbreakable(true);
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
		item.setItemMeta(im);
	}

	public static void bungeeMessaging(Player p, String action, String data1, String data2) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		try {
			out.writeUTF(action);
			out.writeUTF(data1);
			out.writeUTF(data2);
			p.sendPluginMessage(Minigames.getInstance(),
					"fs:minestrike", stream.toByteArray());
//			p.sendMessage("bukkit sent action: " + action + " - " + data1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isInvisible(Player player) {
		return player.getPotionEffect(PotionEffectType.INVISIBILITY) != null;
	}

	// region level utils
	public static int getLevel(int exp) {
		if (exp < 2400)
			return 1;
		if (exp >= 81180000)
			return 450;
		return (int) ((Math.sqrt(160000d + 1600d * exp) - 400d) / 800d);
	}

	public static int getExp(int level) {
		if (level == 1)
			return 0;
		return 400 * level * (1 + level);
	}

	public static String getHex(int i) {
		String a = i + "";
		if (i == 10)
			a = "a";
		if (i == 11)
			a = "b";
		if (i == 12)
			a = "c";
		if (i == 13)
			a = "d";
		if (i == 14)
			a = "e";
		if (i == 15)
			a = "f";
		return a;
	}

	public static String getLevelIcon(int level) {
		if (level < 1) return getLevelIcon(1);
		if (level > 450) return getLevelIcon(450);
		level -= 1;
		String c = "\\u93";
		if (level > 255) {
			c = "\\u94";
			level -= 256;
		}
		int a = level / 16;
		int b = level % 16;
		String aa = getHex(a);
		String bb = getHex(b);
		return StringEscapeUtils.unescapeJava(c + aa + bb);
	}

	public static String getProgressBar(int percent) {
		int x = (percent / 10) + 2;
		String bar = "§a■■■■■■■■■■";
		return bar.substring(0, x) + "§7" + bar.substring(x);
	}
	// endregion

	public static List<Location> randomCircle(Location center, double radius, int size) {
		List<Location> result = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
			result.add(randomCircle(center, radius));
		return result;
	}

	public static Location randomCircle(Location center, double radius) {
		double a = randomDouble() * 2 * Math.PI;
		double r = radius * Math.sqrt(randomDouble());

		double x = r * Math.cos(a);
		double y = r * Math.sin(a);

		return center.add(x, 0, y);
	}

	public static double randomDouble() {
		return ThreadLocalRandom.current().nextDouble();
	}

}
