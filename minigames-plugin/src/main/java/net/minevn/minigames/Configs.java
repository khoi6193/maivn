package net.minevn.minigames;

import net.minevn.guiapi.XMaterial;
import org.bukkit.Material;

public class Configs {
	private static String db_host;
	private static int db_port;
	private static String db_name;
	private static String db_username;
	private static String db_pwd;
	private static String master;
	private static String speakerPrefix;
	private static Material speakerIcon;
	private static short speakerData;

	/**
	 * Số địa chỉ IP trong phòng tối thiểu cần để tính EXP và các chỉ số đua top<br />
	 * Tăng lên sau khi làm Auto-Room
	 */
	public static final int minUniqueIPs = 2;

	public static void loadConfig() {
		var main = Minigames.getInstance();
		main.saveDefaultConfig();
		var config = main.getConfig();
		master = config.getString("root", "/home/minigame/lobby/plugins/Minigames/");
		var db = config.getConfigurationSection("mysql");
		if (db != null) {
			db_host = db.getString("host", "localhost");
			db_port = db.getInt("port", 3306);
			db_name = db.getString("database", "minigames");
			db_username = db.getString("username", "minigames");
			db_pwd = db.getString("password", "");
		} else {
			main.getLogger().warning("Khong co config MySQL");
		}
		speakerPrefix = config.getString("speaker-prefix", "§6§l❖ §b§l[Minigames§b§l]");
		speakerIcon = XMaterial.quickMatch(config.getString("items.speaker.icon",  "PAPER"));
		speakerData = (short) config.getInt("items.speaker.data",  0);
	}

	public static String getDbHost() {
		return db_host;
	}

	public static int getDbPort() {
		return db_port;
	}

	public static String getDbName() {
		return db_name;
	}

	public static String getDbUsername() {
		return db_username;
	}

	public static String getDbPassword() {
		return db_pwd;
	}

	public static String getMasterPath() {
		return master;
	}

	public static String getSpeakerPrefix() {
		return speakerPrefix;
	}

	public static Material getSpeakerIcon() {
		return speakerIcon;
	}

	public static short getSpeakerData() {
		return speakerData;
	}
}
