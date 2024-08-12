package net.minevn.minigames;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MessagesConfig {
	public static File getConfigFile() {
		return new File(Configs.getMasterPath() + "messages.yml");
	}

	public static void load(File file) throws IllegalAccessException {
		var config = YamlConfiguration.loadConfiguration(file);
		var clazz = Messages.class;
		var main = Minigames.getInstance();
		for (var key : config.getKeys(false)) {
			try {
				var field = clazz.getDeclaredField(key);
				if (field.getType() == String.class) {
					field.set(null, ChatColor.translateAlternateColorCodes('&',
							Objects.requireNonNull(config.getString(key))));
				} else field.set(null, KUtils.colorCodes(config.getStringList(key)));
			} catch (NoSuchFieldException ex) {
				main.getLogger().warning("Message ID \"" + key + "\" does not exists.");
			}
		}
		main.getLogger().info("Messages config loaded.");
	}

	public static void save(File file) throws IllegalAccessException, IOException {
		var main = Minigames.getInstance();
		if (file.exists()) return;
		if (!file.createNewFile()) return;
		var config = YamlConfiguration.loadConfiguration(file);
		var clazz = Messages.class;
		for (var field : clazz.getFields()) {
			var m = field.get(null);
			if (m instanceof String) {
				m = ((String) m).replace("§", "&");
			} else {
				m = KUtils.deColorCodes((List<String>) m);
			}
			config.set(field.getName(), m);
		}
		config.save(file);
		main.getLogger().info("Messages config file generated.");
	}
}
