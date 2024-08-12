package net.minevn.minigames;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.minevn.minigames.items.types.ChatColorsPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class Expansions extends PlaceholderExpansion {
	@Override
	public @NotNull String getAuthor() {
		return "MineVN";
	}

	@Override
	public @NotNull String getIdentifier() {
		return "minigames";
	}

	@Override
	public @NotNull String getVersion() {
		return "1.0.0";
	}

	@Override
	public String onPlaceholderRequest(Player p, @NotNull String args) {
		if (p == null) return "";
		var data = PlayerData.getData(p);
		if (data == null) return "";

		String[] argsvar = args.split("_");
		if (argsvar.length > 2 && argsvar[0].equals("stats")) {
			// %minigames_stats_weekly_bw_kill%
			// %minigames_stats_monthly_bw_kill%
			// %minigames_stats_all_bw_kill%
			return switch (argsvar[1]) {
				case "weekly" -> {
					var type = args.replace("stats_weekly_", "");
					yield ((int) data.getStat(KUtils.topDateType(type, "week_top"))) + "";
				}
				case "monthly" -> {
					var type = args.replace("stats_monthly_", "");
					yield ((int) data.getStat(KUtils.topDateType(type, "month_top"))) + "";
				}
				case "all" -> ((int) data.getStat(args.replace("stats_all_", ""))) + "";
				default -> null;
			};
		}
		if (argsvar.length > 4 && argsvar[0].equals("top")) {
			// %minigames_top_weekly_player_1_bw_kill%
			// %minigames_top_weekly_value_1_bw_kill%
			var s = "top_" + argsvar[1] + "_" + argsvar[2] + "_" + argsvar[3] + "_";
			var type = args.replace(s, "");
			var finalType = "";
			switch (argsvar[1]) {
				case "weekly" -> finalType = KUtils.topDateType(type, "week_top");
				case "monthly" -> finalType = KUtils.topDateType(type, "month_top");
				default -> finalType = type;
			}
			var top = Stat.get(finalType);
			var n = Integer.parseInt(argsvar[3]) - 1;

			return switch (argsvar[2]) {
				case "player" -> n < 0 || n >= top.size() ? "Chưa xếp hạng" : (String) top.keySet().toArray()[n];
				case "value" -> n < 0 || n >= top.size() ? "0" : ((Double) top.values().toArray()[n]).intValue() + "";
				case "level" -> {
					if ((!type.equals("level") && !type.equals("mslevel")) || n < 0 || n >= top.size()) {
						yield "1";
					}
					var exp = ((Double) top.values().toArray()[n]).intValue();
					yield Utils.getLevel(exp) + "";
				}
				case "levelicon" -> {
					if ((!type.equals("level") && !type.equals("mslevel")) || n < 0 || n >= top.size()) {
						yield Utils.getLevelIcon(1);
					}
					var exp = ((Double) top.values().toArray()[n]).intValue();
					yield Utils.getLevelIcon(Utils.getLevel(exp));
				}
				default -> null;
			};
		}

		var cc = data.getUsingItem(ChatColorsPI.class);

		return switch (args) {
			case "yw_week" -> KUtils.getWeekOfYear(new Date()).getWeek() + "";
			case "yw_year" -> KUtils.getWeekOfYear(new Date()).getYear() + "";
			case "week_start" -> KUtils.formatDate(KUtils.getStartOfWeek(new Date()));
			case "week_end" -> KUtils.formatDate(KUtils.getEndOfWeek(new Date()));
			case "chatcolor" -> cc == null ? "§7" : cc.getChatColors().getColor();
			case "exp" -> data.getExp() + "";
			case "level" -> data.getLevel() + "";
			case "level_icon" -> data.getLevelIcon();
			case "level_next_icon" -> data.getNextLevelIcon();
			case "level_progress" -> data.getLevelProgress() + "";
			case "level_progress_bar" -> data.getLevelProgressBar();
			case "displayname" -> data.getDisplayName();
			case "quest_points" -> data.getQuestPoints() + "";
			default -> "";
		};
	}
}
