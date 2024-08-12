package net.minevn.minigames;

import net.minevn.minigames.chatactions.BookListener;
import net.minevn.minigames.chatactions.ChatListener;
import net.minevn.minigames.hooks.MineStrikeHook;
import net.minevn.minigames.hooks.slave.BedWarsListener;
import net.minevn.minigames.items.ItemLocker;
import net.minevn.minigames.items.PlayerItem;
import net.minevn.minigames.items.StackablePI;
import net.minevn.minigames.items.UsablePI;
import net.minevn.minigames.items.types.HatPI;
import net.minevn.minigames.items.types.NicknamePI;
import net.minevn.minigames.mail.Mail;
import net.minevn.minigames.quests.PlayerQuest;
import net.minevn.minigames.quests.Quest;
import net.minevn.minigames.quests.QuestAttempt;
import net.minevn.minigames.quests.QuestObjective;
import net.minevn.mmclient.MatchMakerClient;
import net.minevn.mmclient.utils.ItemUtils;
import net.minevn.mmclient.utils.MMUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PlayerData {
	private final UUID uuid;
	private final String name;
	private String displayName;
	private final Player player;
	private double money;
	private List<PlayerItem> items;
	private long lastSaved = 0;
	private boolean loading;
	private final ItemLocker itemLocker;
	private int exp, level;
	private final Map<Quest, PlayerQuest> quests;
	private final Map<String, Double> stats;
	private final MineStrikeHook ms;
	/**
	 * Thời gian chơi tính bằng phút<br />
	 * Cộng từ scheduler
	 */
	private int playTime;
	private ChatListener chatListener;
	private BookListener bookListener;
	private int questPoints;
	private boolean debug;

	public PlayerData(Player player, boolean temp) {
		loading = true;
		this.uuid = player.getUniqueId();
		this.name = player.getName();
		this.player = player;
		log(uuid + " loading...");
		quests = new HashMap<>();
		stats = new HashMap<>();
		ms = Minigames.getInstance().getMSHook();
		itemLocker = new ItemLocker(this);
		if (!temp) addPlayer(this);
		MMUtils.runNotSync(() -> {
			try {
//				itemLocker.lock();
				var sql = MySQL.getInstance();
				items = sql.getItems(this);
				if (ms != null) {
					// load súng mặc định minestrike
					items.addAll(ms.getDefaultGuns());
				}
				sql.getData(this);
				log("loaded " + items.size() + " item(s)");
				loading = false;

				// hat update
				var hat = getUsingItem(HatPI.class);
				if (hat != null) {
					hat.getHat().sendPacket(player);
				}

				// nickname update
				var nickname = getUsingItem(NicknamePI.class);
				if (nickname != null) {
					nickname.updateNickname();
				}

				Utils.runLater(() -> {
					updateGadgets();
					new QuestAttempt(this, QuestObjective.LOGIN);
				}, 10);

				noticeMail();
			} catch (Exception e) {
				Minigames.getInstance().getLogger().log(Level.SEVERE, "Error loading player data for "
						+ player.getName(), e);
				Utils.runSync(() -> player.kickPlayer("§fE001 §cCó lỗi xảy ra, vui lòng thử lại sau!"));
				destroy();
			}
//			finally {
//				itemLocker.unlock();
//			}
		});
	}

	public PlayerData(Player player) {
		this(player, false);
	}

	public UUID getUUID() {
		return uuid;
	}

	public Player getPlayer() {
		return player;
	}

	public void destroy() {
		if (getLastSaved() > 1000) MMUtils.runNotSync(() -> MySQL.getInstance().saveData(this));
		removeData(this);
	}

	public void log(String message) {
		Minigames.getInstance().getLogger().info("[PD." + name + "] " + message);
	}

	public List<PlayerItem> getItems() {
		return items;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public void updateMoney() {
		MySQL.getInstance().getMoney(this);
	}

	public String getName() {
		return name;
	}

	public void setLastSaved() {
		lastSaved = System.currentTimeMillis();
	}

	public long getLastSaved() {
		return System.currentTimeMillis() - lastSaved;
	}

	public void sendMessage(String message) {
		if (player != null && player.isOnline()) {
			MatchMakerClient.getInstance().sendMessage(player, message);
		}
	}

	public void debug(String message) {
		if (!debug) return;
		if (player != null && player.isOnline()) {
			MatchMakerClient.getInstance().sendMessage(player, "§dDEBUG: " + message);
		}
	}

	public int getPlayTime() {
		return playTime;
	}

	public void addPlayTime() {
		playTime++;
	}

	public void updateGadgets() {
		var gmhook = Minigames.getInstance().getGMHook();
		if (gmhook != null) {
			gmhook.updateGadgets(this);
		}
	}

	// region level
	public int getExp() {
		if (ms == null) return exp;
		var info = ms.getInfo(player);
		if (info == null) return 0;
		return info.getExp();
	}

	/**
	 *
	 * @param exp
	 * @return true if level up
	 */
	public boolean setExp(int exp) {
		this.exp = exp;
		int level = Utils.getLevel(exp);
		boolean levelUp = this.level > 0 && level > this.level;
		this.level = level;
		return levelUp;
	}

	public void addExp(int amount) {
		var levelUp = setExp(exp + amount);
		sendMessage(Messages.MSG_EXP_RECEIVE.replace("%exp%", String.valueOf(amount)));
		if (levelUp && player != null && player.isOnline()) {
			player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 1);
			sendMessage(Messages.MSG_LEVEL_UP
					.replace("%icon%", getLevelIcon())
					.replace("%level%", String.valueOf(level)));
		}
	}

	public int getLevel() {
		if (ms == null) return level;
		var info = ms.getInfo(player);
		if (info == null) return 1;
		return info.getLevel();
	}

	public String getLevelIcon() {
		return Utils.getLevelIcon(getLevel());
	}

	public String getNextLevelIcon() {
		return Utils.getLevelIcon(getLevel() + 1);
	}

	/**
	 *
	 * @return level up progress percent
	 */
	public int getLevelProgress() {
		if (ms != null) {
			var info = ms.getInfo(player);
			if (info != null) info.getPercentProgress();
		}
		double required = Utils.getExp(Utils.getLevel(this.exp) + 1) - Utils.getExp(level);
		double acquired = this.exp - Utils.getExp(level);
		return (int) ((acquired / required) * 100);
	}

	public String getLevelProgressBar() {
		return Utils.getProgressBar(getLevelProgress());
	}

	public ChatListener getChatListener() {
		return chatListener;
	}

	public void setChatListener(ChatListener chatListener) {
		this.chatListener = chatListener;
	}

	public BookListener getBookListener() {
		return bookListener;
	}

	public void setBookListener(BookListener bookListener) {
		this.bookListener = bookListener;
	}

	public boolean haveEXPBooster() {
		return false;
	}
	// endregion

	public Map<Quest, PlayerQuest> getQuests() {
		return quests;
	}

	public int getQuestPoints() {
		return questPoints;
	}

	public void setQuestPoints(int amount) {
		questPoints = amount;
	}

	public void addQuestPoints(int amount) {
		questPoints += amount;
	}

	// debug getter & setter
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isDebug() {
		return debug;
	}

	//region stat
	public Map<String, Double> getStats() {
		return stats;
	}

	public double getStat(String key) {
		return stats.getOrDefault(key, 0d);
	}

	public void updateStat(Stat type, double value) {
		updateStat(type, value, null);
	}

	public void updateStat(Stat type, double value, Map<String, String> params) {
		if (value == 0) return;

		// min 4 unique ips
		if (Minigames.getInstance().getSlaveListener() instanceof BedWarsListener sl
				&& sl.getUniqueIPs() < Configs.minUniqueIPs) {
			return;
		} else {
			var handler = MatchMakerClient.getInstance().getSlaveHandler();
			if (handler != null && handler.getOnlineUniqueIPs() < Configs.minUniqueIPs) {
				return;
			}
		}

		var all = type.toString(params);
		var week = KUtils.topDateType(all, "week_top");
		var month = KUtils.topDateType(all, "month_top");
		stats.put(all, stats.getOrDefault(type.toString(), 0d) + value);
		stats.put(week, stats.getOrDefault(week, 0d) + value);
		stats.put(month, stats.getOrDefault(month, 0d) + value);
		Utils.runNotSync(() -> {
			var sql = MySQL.getInstance();
			sql.updateStat(uuid, all, value, type.isReplace());
			sql.updateStat(uuid, week, value, type.isReplace());
			sql.updateStat(uuid, month, value, type.isReplace());
		});
	}

	public void resetStat(Stat stat) {
		resetStat(stat, null);
	}

	public void resetStat(Stat stat, Map<String, String> params) {
		var all = stat.toString(params);
		var week = KUtils.topDateType(all, "week_top");
		var month = KUtils.topDateType(all, "month_top");
		stats.put(all, 0d);
		stats.put(week, 0d);
		stats.put(month, 0d);
		Utils.runNotSync(() -> {
			var sql = MySQL.getInstance();
			sql.resetStat(uuid, all);
			sql.resetStat(uuid, week);
			sql.resetStat(uuid, month);
		});
	}
	// endregion

	// region items
	public void addItem(PlayerItem item) {
		addItem(item, "Unknown");
	}

	public void addItem(PlayerItem item, String note) {
		item = item.clone();
		item.setObtainedTime(System.currentTimeMillis());
		if (item.getOwner() != null && item.getID() != -1) throw new IllegalArgumentException("Invalid owner");
		item.setOwner(this);
		boolean added = false;
		int max = StackablePI.STACKABLE_MAXAMOUNT;
		var sql = MySQL.getInstance();
		try {
			itemLocker.lock("addItem");
			if (loading) throw new IllegalArgumentException("Adding item while loading!");
			if (!items.contains(item)) {
				if (item instanceof StackablePI stackable) {
					while (stackable.getAmount() > max) {
						StackablePI clone = stackable.clone();
						clone.setAmount(max);
						addItem(clone, note + " (Stackable cloning)");
						stackable.subtractAmount(max);
					}
					for (StackablePI i : getStackableItems(stackable.getClass())) {
						if (stackable.getAmount() <= 0)
							return;
						if (i.getAmount() >= max)
							continue;
						int maximum = max - i.getAmount();
						if (stackable.getAmount() >= maximum) {
							stackable.subtractAmount(maximum);
							i.addAmount(maximum);
							sql.saveItem(i);
						} else {
							i.addAmount(stackable.getAmount());
							stackable.setAmount(0);
							sql.saveItem(i);
							return;
						}
					}
					if (stackable.getAmount() > 0) {
						added = true;
						items.add(0, stackable);
					}
				} else if (item.getExpire() > 0) {
					PlayerItem similar = getTimedItem(item.getClass(), item.getData());
					if (similar != null && similar.getExpire() > 0) {
						long left = item.getExpire() - System.currentTimeMillis();
						if (left > 0) {
							similar.setExpire(similar.getExpire() + left);
							sql.saveItem(similar);
							return;
						}
					}
				}
				if (!added) items.add(0, item);
			}
			sql.saveItem(item);
		} finally {
			itemLocker.unlock();
			sql.log(uuid.toString(), "ITEM_ADD", item.getCategoryRegex(), note);
		}
	}

	public void removeItem(PlayerItem item) throws SQLException {
		removeItem(item, "Unknown");
	}

	public void removeItem(PlayerItem item, String note) throws SQLException {
		try {
			itemLocker.lock("removeItem");
			if (loading) throw new IllegalArgumentException("Removing item while loading!");
			if (item.getID() < 0) {
				return;
			}
			MySQL.getInstance().removeItem(item);
			items.remove(item);
		} finally {
			itemLocker.unlock();
			MySQL.getInstance().log(uuid.toString(), "ITEM_REMOVE", item.getCategoryRegex(), note);
		}
	}

	public <T extends StackablePI> List<T> getStackableItems(Class<T> type) {
//		try {
//			itemLocker.lock();
		if (loading) return null;
		return items.stream()
			.filter(i -> i.getClass() == type && !i.isExpired())
			.map(type::cast)
			.collect(Collectors.toList());
//		} finally {
//			itemLocker.unlock();
//		}
	}

	public <T extends PlayerItem> List<T> getItems(Class<T> type) {
		return getItems(type, null);
	}

	public <T extends PlayerItem> List<T> getItems(Class<T> type, String data) {
//		try {
//			itemLocker.lock();
		if (loading) return null;
		return items.stream()
			.filter(i -> i.getClass() == type && !i.isExpired() && (data == null || i.getData().equals(data)))
			.map(type::cast)
			.toList();
//		} finally {
//			itemLocker.unlock();
//		}
	}

	public <T extends PlayerItem> T getItem(Class<T> type) {
		return getItem(type, null);
	}

	public <T extends PlayerItem> T getItem(Class<T> type, String data) {
//		try {
//			itemLocker.lock();
		if (loading) return null;
		return items.stream()
			.filter(i -> i.getClass() == type && !i.isExpired() && (data == null || i.getData().equals(data)))
			.findFirst()
			.map(type::cast)
			.orElse(null);
//		} finally {
//			itemLocker.unlock();
//		}
	}

	public <T extends PlayerItem> T getTimedItem(Class<T> type, String data) {
//		try {
//			itemLocker.lock();
		if (loading) return null;
		return items.stream()
			.filter(item ->
				item.getClass() == type && item.getData().equals(data) && item.getExpire() > 0 && !item.isExpired()
			)
			.map(type::cast)
			.findFirst()
			.orElse(null);
//		} finally {
//			itemLocker.unlock();
//		}
	}

	public <T extends StackablePI> int getAmount(Class<T> type) {
//		try {
//			itemLocker.lock();
			if (loading) return 0;
			return items.stream()
				.filter(i -> i.getClass() == type && !i.isExpired())
				.mapToInt(i -> type.cast(i).getAmount()).sum();
//		} finally {
//			itemLocker.unlock();
//		}
	}

	/**
	 * @param <T>    loai StackablePI can thanh toan
	 * @param type   StackablePI
	 * @param amount so luong
	 * @return true neu tru item thanh cong
	 */
	public <T extends StackablePI> boolean useStackableItem(Class<T> type, int amount) {
		var sql = MySQL.getInstance();
		try {
			itemLocker.lock("useStackableItem");
			if (loading) return false;
			List<T> items = getStackableItems(type);
			if (items.stream().mapToInt(StackablePI::getAmount).sum() < amount)
				return false;
			for (T item : items) {
				if (item.getAmount() <= amount) {
					if (item.getID() < 0) {
						sql.saveItem(item);
					}
					try {
						amount -= item.getAmount();
						removeItem(item, "Stackable used");
						if (amount == 0) return true;
					} catch (Exception e) {
						Minigames.getInstance().getLogger().log(Level.SEVERE, "Khong the tru amount cho item #" + item.getID(), e);
						return false;
					}
				} else {
					item.subtractAmount(amount);
					sql.saveItem(item);
					return true;
				}
			}
			return false;
		} finally {
			itemLocker.unlock();
		}
	}

	public void useItem(UsablePI item) {
		try {
			itemLocker.lock("useItem");
			var sql = MySQL.getInstance();
			if (!item.isStackable()) {
				if (loading) return;
				for (var i : items) {
					if (i.getClass() == item.getClass() && ((UsablePI) i).isUsing() && i != item) {
						var ui = (UsablePI) i;
						ui.setUsing(false);
						ui.setGlowing(false);
						sql.saveItem(ui);
					}
				}
			}
			item.setUsing(true);
			item.setGlowing(true);
			sql.saveItem(item);
		} finally {
			itemLocker.unlock();
		}
	}

	@Override
	public String toString() {
		return getName();
	}

	public <T extends UsablePI> T getUsingItem(Class<T> type) { // type phải là class con của UsablePI
		// cho nay doc truc tiep tu list items
//		try {
//			itemLocker.lock();
//			if (loading) return null;
//			for (var item : items) {
//				if (item.getClass() != type) continue;
//				var casted = type.cast(item);
//				if (casted.isUsing()) return casted;
//			}
//			return null;
//		} finally {
//			itemLocker.unlock();
//		}
		// nen clone list items ra roi doc
		if (loading) return null;
//		var items = ImmutableList.copyOf(this.items);
		for (var item : items) {
			if (item.getClass() != type) continue;
			var casted = type.cast(item);
			if (casted.isUsing()) return casted;
		}
		return null;
	}

	public <T extends UsablePI> List<T> getStackedUsingItem(Class<T> type) { // type phải là class con của UsablePI
//		try {
//			itemLocker.lock();
			if (loading) return null;
			var list = new ArrayList<T>();
			var seenData = new HashSet<String>();
			for (var item : items) {
				if (item.getClass() != type) continue;
				var casted = type.cast(item);
				if (!casted.isUsing()) continue;
				if (seenData.add(casted.getData())) list.add(casted);
			}
			return list;
//		} finally {
//			itemLocker.unlock();
//		}
	}

	public String getDisplayName() {
		return displayName != null ? displayName : player.getName();
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	// endregion

	//region mail
	public List<Mail> getUnreadMail() {
		return MySQL.getInstance().getMails(uuid.toString()).stream().filter(Mail::isUnread).toList();
	}

	public void noticeMail() {
		var mails = getUnreadMail();
		boolean unread = false;
		if (!mails.isEmpty()) {
			unread = true;
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
			sendMessage(Messages.MSG_MAIL_UNREAD_NOTICE.replace("%amount%", mails.size() + ""));
		}

		var icon = unread
			? Material.ENCHANTED_BOOK
			: Material.BOOK;
		var item = ItemUtils.generateCommandItem(icon, "hopthu", "§a§lHộp thư");

		Utils.runSync(() -> player.getInventory().setItem(6, item));
	}
	//endregion

	// region Singleton
	private static final Map<UUID, PlayerData> map = new ConcurrentHashMap<>();

	private static synchronized void addPlayer(PlayerData data) {
		if (!map.containsKey(data.getUUID())) {
			map.put(data.getUUID(), data);
		} else {
			throw new IllegalArgumentException("Player data already exist for player " + data.name);
		}
	}

	private static synchronized void removeData(PlayerData data) {
		var uuid = data.getUUID();
		if (map.get(uuid) == data) map.remove(uuid);
	}

	public static PlayerData getData(Player player) {
		return map.get(player.getUniqueId());
	}

	public static PlayerData getData(UUID uuid) {
		return map.get(uuid);
	}

	public static Set<PlayerData> getAllData() {
		return new HashSet<>(map.values());
	}
	// endregion
}
