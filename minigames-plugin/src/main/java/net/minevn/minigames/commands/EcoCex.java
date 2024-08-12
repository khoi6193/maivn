package net.minevn.minigames.commands;

import net.minevn.minigames.Messages;
import net.minevn.minigames.Minigames;
import net.minevn.minigames.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class EcoCex implements CommandExecutor {
	@Override
	public boolean onCommand(
		@NotNull CommandSender s,
		@NotNull Command command,
		@NotNull String allies,
		@NotNull String[] args
	) {
		if (args.length < 3) return true;
		var main = Minigames.getInstance();
		switch (args[0].toLowerCase()) {
			case "give" -> {
				if (!s.hasPermission("minevn.admin.economy.give")) return true;
				Utils.runAsync(() -> {
					try {
						var player = Bukkit.getPlayer(args[1]);
						if (player == null || !player.isOnline()) {
							s.sendMessage("player must be online");
							return;
						}
						var amount = Double.parseDouble(args[2]);
						if (amount <= 0) {
							s.sendMessage("amount must be > 0");
							return;
						}
						main.getEconomy().depositPlayer(player, amount);
						player.sendMessage(Messages.ECO_GIVE.replace("%amount%", (int) amount + ""));
						s.sendMessage("ECO: ADDED " + player.getName() + " " + amount);
					} catch (Exception e) {
						s.sendMessage("Got a error (check console): " + e.getMessage());
						throw e;
					}
				});
			}
			case "take" -> {
				if (!s.hasPermission("minevn.admin.economy.take")) return true;
				Utils.runAsync(() -> {
					try {
						var player = Bukkit.getPlayer(args[1]);
						if (player == null || !player.isOnline()) {
							s.sendMessage("player must be online");
							return;
						}
						var amount = Double.parseDouble(args[2]);
						if (amount <= 0) {
							s.sendMessage("amount must be > 0");
							return;
						}
						main.getEconomy().withdrawPlayer(player, amount);
						player.sendMessage(Messages.ECO_TAKE.replace("%amount%", (int) amount + ""));
						s.sendMessage("ECO: TOOK " + player.getName() + " " + amount);
					} catch (Exception e) {
						s.sendMessage("Got a error (check console): " + e.getMessage());
						throw e;
					}
				});
			}
		}
		return true;
	}
}