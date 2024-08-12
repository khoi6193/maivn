package net.minevn.minigames;

import net.minevn.minigames.items.PlayerItem;
import net.minevn.minigames.items.StackablePI;
import net.minevn.minigames.items.UsablePI;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class PlayerData {
	private UUID uuid;
	private String name;
	private Player player;
	private double money;
	private List<PlayerItem> items;
	private long lastSaved = 0;
	private boolean loading;
	private ReentrantLock itemLocker;
	private int exp;

	public PlayerData(Player player, boolean temp) {
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
	}

	public void log(String message) {
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

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public boolean haveEXPBooster() {
		return false;
	}

	public void updateTop(TopType type, double value) {}

	//region items
	public void addItem(PlayerItem item) {
	}

	public void removeItem(PlayerItem item) {
	}

	public <T extends StackablePI> List<T> getStackableItems(Class<T> type) {
		try {
			itemLocker.lock();
			if (loading) return null;
			return items.stream().filter(i -> i.getClass() == type && !i.isExpired()).map(type::cast)
					.collect(Collectors.toList());
		} finally {
			itemLocker.unlock();
		}
	}

	public <T extends PlayerItem> T getTimedItem(Class<T> type, String data) {
		try {
			itemLocker.lock();
			if (loading) return null;
			return items.stream().filter(item -> item.getClass() == type && item.getData().equals(data)
					&& item.getExpire() > 0 && !item.isExpired()).map(type::cast).findFirst().orElse(null);
		} finally {
			itemLocker.unlock();
		}
	}

	public <T extends StackablePI> int getAmount(Class<T> type) {
		try {
			itemLocker.lock();
			if (loading) return 0;
			return items.stream().filter(i -> i.getClass() == type && !i.isExpired())
					.mapToInt(i -> type.cast(i).getAmount()).sum();
		} finally {
			itemLocker.unlock();
		}
	}

	/**
	 * @param <T>    loai StackablePI can thanh toan
	 * @param type   StackablePI
	 * @param amount so luong
	 * @return true neu tru item thanh cong
	 */
	public <T extends StackablePI> boolean useStackableItem(Class<T> type, int amount) {
		return new Random().nextBoolean();
	}

	public void useItem(UsablePI item) {
	}

	public <T extends UsablePI> T getUsingItem(Class<T> type) { // type phải là class con của UsablePI
		try {
			itemLocker.lock();
			if (loading) return null;
			for (var item : items) {
				if (item.getClass() != type) continue;
				var casted = type.cast(item);
				if (casted.isUsing()) return casted;
			}
			return null;
		} finally {
			itemLocker.unlock();
		}
	}

	public <T extends UsablePI> List<T> getStackedUsingItem(Class<T> type) { // type phải là class con của UsablePI
		try {
			itemLocker.lock();
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
		} finally {
			itemLocker.unlock();
		}
	}

	public <T extends PlayerItem> List<T> getItems(Class<T> type) {
		try {
			itemLocker.lock();
			if (loading) return null;
			return items.stream().filter(i -> i.getClass() == type && !i.isExpired()).map(type::cast)
					.collect(Collectors.toList());
		} finally {
			itemLocker.unlock();
		}
	}

	public <T extends PlayerItem> T getItem(Class<T> type) {
		return getItem(type, null);
	}

	public <T extends PlayerItem> T getItem(Class<T> type, String data) {
		try {
			itemLocker.lock();
			if (loading) return null;
			return items.stream()
					.filter(i -> i.getClass() == type && !i.isExpired() && (data == null || i.getData().equals(data)))
					.findFirst()
					.map(type::cast)
					.orElse(null);
		} finally {
			itemLocker.unlock();
		}
	}
	//endregion

	//region Singleton
	private static Map<UUID, PlayerData> map = new ConcurrentHashMap<>();

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
		//        if (data != null) {
//            data.setPlayer(player);
//        }
		return map.get(player.getUniqueId());
	}

	public static PlayerData getData(UUID uuid) {
		return map.get(uuid);
	}
	//endregion
}
