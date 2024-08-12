package net.minevn.minigames.commands;

import net.minevn.minigames.*;
import net.minevn.minigames.items.types.ChatColorsPI;
import net.minevn.minigames.items.types.SpeakerPI;
import net.minevn.mmclient.MatchMakerClient;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LoaCex implements CommandExecutor {
	@Override
	public boolean onCommand(
		@NotNull CommandSender commandSender,
		@NotNull Command command,
		@NotNull String string,
		@NotNull String[] strings
	) {
		var mm = MatchMakerClient.getInstance();
		Utils.runAsync(() -> {
			if (!(commandSender instanceof Player player)) {
				commandSender.sendMessage("player only!");
				return;
			}
			if (strings.length == 0) {
				mm.sendMessage(player, "§fCách dùng: /" + command.getName() + " <nội dung>");
				return;
			}
			var data = PlayerData.getData(player);
			var cc = data.getUsingItem(ChatColorsPI.class);
			if (!data.useStackableItem(SpeakerPI.class, 1)) {
				mm.sendMessage(player, Messages.ERR_NO_SPEAKERWORLD);
				return;
			}
			StringBuilder message = new StringBuilder();
			for (String arg : strings) {
				message.append(arg).append(" ");
			}
			message = new StringBuilder(ChatColor.stripColor(message.toString()).replace(';', ' '));
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(stream);
			String color = cc == null ? "§e" : cc.getChatColors().getColor();
			String prefix = "§f ;" + Configs.getSpeakerPrefix() + "§f§l " + player.getName() + " §7§l> " + color;
			String suffix = "; §f";
			String action = "fsbc";
			String data2 = "";
			try {
				out.writeUTF(action);
				out.writeUTF(prefix + message + suffix);
				out.writeUTF(data2);
				player.sendPluginMessage(Minigames.getInstance(), "fs:minestrike", stream.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		return true;
	}
}
