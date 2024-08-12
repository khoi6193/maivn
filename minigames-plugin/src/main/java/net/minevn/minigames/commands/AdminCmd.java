package net.minevn.minigames.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.minevn.minigames.*;
import net.minevn.minigames.award.PlayerAward;
import net.minevn.minigames.cases.Case;
import net.minevn.minigames.gadgets.*;
import net.minevn.minigames.gui.InventoryCategory;
import net.minevn.minigames.quests.PlayerQuest;
import net.minevn.minigames.quests.Quest;
import net.minevn.minigames.quests.QuestCategory;
import net.minevn.minigames.shop.ShopCategory;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;

import static net.minevn.mmclient.utils.MMUtils.runNotSync;

public class AdminCmd {
	public static void init() {
		// give item command
		new CommandAPICommand("award")
			.withAliases("mgive")
			.withPermission("minigames.award")
			.withArguments(
				new PlayerArgument("player"),
				new StringArgument("type")
					.replaceSuggestions(ArgumentSuggestions.strings(info -> PlayerAward.types)),
				new GreedyStringArgument("data")
					.replaceSuggestions(ArgumentSuggestions.strings(info -> {
						var type = (String) info.previousArgs()[1];
						return switch (type) {
							case "StackableAW" -> stackableItemSuggestions;
							case "TimedAW", "ActivableTimedAW" -> getTimedItemSuggestions();
							case "ItemAW" -> getItemSuggestions();
							case "CaseBundleAW" -> getCaseBundleSuggestions();
							default -> new String[0];
						};
					}))
			)
			.executes((s, a) -> {
				var main = Minigames.getInstance();
				Utils.runAsync(() -> {
					try {
						var player = (Player) a[0];
						if (player == null || !player.isOnline()) {
							s.sendMessage("player must be online");
							return;
						}
						String type = (String) a[1];
						String data = (String) a[2];
						var item = PlayerAward.fromData(type, data);
						item.apply(player, "given by " + s.getName());
						s.sendMessage("§fGiven " + item.getName() + " §b(" + item.getUnit() + ") §fto "
								+ player.getName());
					} catch (Exception e) {
						s.sendMessage("Invalid item (check console): " + e.getMessage());
						main.getLogger().log(Level.SEVERE, "Invalid item", e);
					}
				});
			}).register();

		// reload command
		new CommandAPICommand("mgreload")
			.withAliases("mgr")
			.withPermission("minigames.reload")
			.executes((s, a) -> {
				var main = Minigames.getInstance();
				HitSound.load();
				Sword.load();
				ArrowTrail.load();
				Hat.load();
				ChatColors.load();
				MVPAnthem.load();
				Tomb.load();
				NamePreset.load();
				Case.loadCases(Configs.getMasterPath());
				ShopCategory.load();
				QuestCategory.load();
				InventoryCategory.load();
				Quest.load();
				ItemBundle.load();
				// reload all players quests
				var sql = MySQL.getInstance();
				runNotSync(() -> {
					for (PlayerData pd : PlayerData.getAllData()) {
						for (PlayerQuest pq : new HashSet<>(pd.getQuests().values())) {
							sql.savePlayerQuest(pd.getUUID().toString(), pq);
						}
						try {
							sql.getPlayerQuest(pd);
						} catch (Exception e) {
							KUtils.severe(e, s, "Failed to reload quests for " + pd.getName());
						}
					}
				});
				s.sendMessage("§fReloaded");
			}).register();
	}

	private static final String[] stackableItemSuggestions = new String[] {
		"SpeakerPI$1",
		"MSPVEMedicLargePI$1",
		"MSPVEMedicPI$1",
		"MSPVERetryPI$1"
	};
	private static String[] timedItemSuggestions = null;
	private static String[] itemSuggestions = null;

	private static String[] getTimedItemSuggestions() {
		if (timedItemSuggestions == null) {
			var list = new ArrayList<String>();
			list.addAll(Hat.getSuggestions());
			list.addAll(ArrowTrail.getSuggestions());
			list.addAll(ChatColors.getSuggestions());
			list.addAll(HitSound.getSuggestions());
			list.addAll(MVPAnthem.getSuggestions());
			list.addAll(Sword.getSuggestions());
			list.addAll(Tomb.getSuggestions());
			list.addAll(NamePreset.getSuggestions());
			var gm = Minigames.getInstance().getGMHook();
			if (gm != null) gm.getSuggestions(list);
			timedItemSuggestions = list.toArray(String[]::new);
		}
		return timedItemSuggestions;
	}

	private static String[] getItemSuggestions() {
		if (itemSuggestions == null) {
			var list = new ArrayList<String>();
			list.addAll(Case.getCaseSuggestions());
			list.addAll(Case.getKeySuggestions());
			list.addAll(ItemBundle.getSuggestions());
			list.addAll(NamePreset.getGunTagSuggestions());
			list.add("MSSpeedBoostPI");
			list.add("MSZombieGrenadePI");
			list.add("MSCreeperGrenadePI");
			list.add("MSDeadlyShotPI");
			list.add("MSHumanShieldPI");
//			list.addAll(RideTicketPI.getSuggestions());
			itemSuggestions = list.toArray(String[]::new);
		}
		return itemSuggestions;
	}

	public static String[] getCaseBundleSuggestions() {
		return Case.getBundleSuggestions();
	}
}
