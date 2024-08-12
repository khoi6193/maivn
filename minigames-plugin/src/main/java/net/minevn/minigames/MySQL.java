package net.minevn.minigames;

import com.google.common.base.Strings;
import net.minevn.minigames.award.PlayerAward;
import net.minevn.minigames.items.CustomizablePI;
import net.minevn.minigames.items.PlayerItem;
import net.minevn.minigames.items.StackablePI;
import net.minevn.minigames.items.UsablePI;
import net.minevn.minigames.mail.Mail;
import net.minevn.minigames.mail.MailRecipient;
import net.minevn.minigames.quests.PlayerQuest;
import net.minevn.minigames.quests.Quest;
import net.minevn.minigames.shop.ShopCategory;
import net.minevn.minigames.shop.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.stream.Collectors;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
public class MySQL {
	//region Constructor
	private Connection sql;
	private Minigames main;
	private ReentrantLock mailLocker = new ReentrantLock();

	public MySQL(Minigames main, String host, String dbname, String user, String pwd, int port) {
		try {
			main.getLogger().info("Dang ket noi den MySQL...");
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				Class.forName("com.mysql.jdbc.Driver");
			}
			sql = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + dbname, user, pwd);
			this.main = main;
			_instance = this;
			main.getLogger().info("Ket noi den MySQL thanh cong.");
		} catch (Exception ex) {
			main.getLogger().log(Level.SEVERE, "Khong the ket noi MySQL", ex);
			main.getServer().shutdown();
		}
	}

	// cleaner
	private void cleanup(ResultSet result, Statement statement) {
		if (result != null) {
			try {
				result.close();
			} catch (SQLException e) {
				main.getLogger().log(Level.SEVERE, "SQLException on cleanup", e);
			}
		}
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				main.getLogger().log(Level.SEVERE, "SQLException on cleanup", e);
			}
		}
	}
	//endregion

	//region SQL
	// player data
	private static final String PLAYER_DATA_GET = "select * from minigames_players where uuid = ?";
	private static final String PLAYER_DATA_SAVE = """
		insert into minigames_players(uuid, name, exp, questpoints)
		values(?, @name:=?, @exp:=?, @questpoints:=?)
		on duplicate key update name = @name, exp = @exp, questpoints = @questpoints
	""";
	private static final String PLAYER_DATA_GET_UUID = """
		select uuid from minigames_players where name = ?
	""";

	// player money
	private static final String PLAYER_MONEY_SELECT = "select money from minigames_players where uuid = ?";
	private static final String PLAYER_MONEY_ADD = """
		INSERT INTO minigames_players(uuid, money) VALUES (?, @money:=?)
		ON DUPLICATE KEY UPDATE money = money + @money
	""";

	// player inventory
	private static final String PLAYER_INVENTORY = """
		select * from minigames_inventory where uuid=? and (expire=0 or expire>?)
		order by obtainedtime desc, id desc
	""";
	private static final String PLAYER_INVENTORY_ITEM_INSERT = """
		insert into minigames_inventory(uuid, type, data, expire, obtainedtime, isusing, amount, itemdata)
		values (?,?,?,?,?,?,?,?)
	""";
	private static final String PLAYER_INVENTORY_ITEM_UPDATE = """
		update minigames_inventory
		set uuid=?, type=?, data=?, expire=?, obtainedtime=?, isusing=?, amount=?, itemdata=?
		where id = ?
	""";
	private static final String PLAYER_INVENTORY_ITEM_REMOVE = "delete from minigames_inventory where id = ?";

	// player quests
	private static final String PLAYER_QUESTS_GET = "select * from minigames_quests where uuid = ?";
	private static final String PLAYER_QUESTS_SAVE = """
		insert into minigames_quests(uuid, quest, done, nextreset, finished, obtained)
		values(?, ?, @done:=?, @nextreset:=?, @finished:=?, @obtained:=?)
		on duplicate key update done = @done, nextreset = @nextreset, finished = @finished, obtained = @obtained
	""";

	// shop
	private static final String SHOP_LOADALLITEMS = "select * from minigames_shop order by `order` desc, id desc";
	private static final String SHOP_STOCK_GET = "select stock from minigames_shop where id = ?";
	private static final String SHOP_STOCK_REMOVE = """
		update minigames_shop
		set stock = if(stock < 0, stock, if((@LEFT := (stock - ?)) < 0, 0, @LEFT))
		where id = ?
	""";

	// top
	private static final String TOP_ADD = """
		INSERT INTO minigames_top(uuid, toptype, `value`) values (?, ?, @value:=?)
		ON DUPLICATE KEY UPDATE `value` = `value` + @value
	""";
	private static final String TOP_REPLACE = """
		INSERT INTO minigames_top(uuid, toptype, `value`) values (?, ?, @value:=?)
		ON DUPLICATE KEY UPDATE `value` =  @value
	""";
	private static final String TOP_SELECT_BY_USER = "SELECT `value` FROM minigames_top WHERE uuid = ? AND toptype = ?";
	private static final String TOP_SELECT_ALL_BY_USER = "SELECT * FROM minigames_top WHERE uuid = ?";
	private static final String TOP_SELECT = """
		SELECT a.uuid, b.name, a.`value`
		FROM
			minigames_top a
			LEFT JOIN minigames_players b ON a.`uuid` = b.`uuid`
		WHERE toptype = ?
		ORDER BY `value` DESC limit 10
	""";
	private static final String TOP_SELECT_LEVEL = """
		SELECT `uuid`, `name`, `exp` as `value`
		FROM minigames_players WHERE `exp` > 0
		ORDER BY `exp` DESC
		limit 10
	""";
	private static final String TOP_SELECT_MSLEVEL = """
		SELECT `uuid`, `name`, `exp` as `value`
		FROM minestrike_stats
		WHERE `exp` > 0
		ORDER BY `exp` DESC
		limit 10
	""";
	private static final String TOP_SELECT_QUESTPOINTS = """
		SELECT `uuid`, `name`, `questpoints` as `value`
		FROM minigames_players
		WHERE `questpoints` > 0
		ORDER BY `questpoints` DESC
		limit 10
	""";

	// giftcodes
	private static final String GIFTCODE_GET = "select * from minigames_giftcodes where code = ? and isused = 0";
	private static final String GIFTCODE_INSERT = "insert into minigames_giftcodes(code, itemset) values (?, ?)";
	private static final String GIFTCODE_USE = """
		update minigames_giftcodes
		set isused = 1, usedby = ?, usedtime = ?
		where code = ?
	""";
	private static final String GIFTCODE_VALIDATE = "select * from minigames_giftcodes where code in ";
	private static final String GIFTCODE_BUNDLE_USED = """
		select * from minigames_giftcodes
		where itemset = ? and usedby = ?
	""";
	// endregion

	// region mail
	private static final String MAIL_GET = """
		SELECT
			m.*, r.read AS `read`, r.attachment AS attachment, s.name AS sendername, r.deleted AS deleted,
			(SELECT COUNT(1) FROM minigames_mail_attachment a WHERE a.mailid = m.id) AS attachments
		FROM
			minigames_mail_recipient r
			INNER JOIN minigames_mail m ON r.mailid = m.id
			LEFT JOIN minigames_players s ON s.`uuid` = m.sender
		WHERE
			r.`uuid` = ? AND r.deleted = 0 %exact_query%
		ORDER BY m.date DESC;
	""";
	private static final String MAIL_SENT = """
		SELECT
			m.*, GROUP_CONCAT(ri.`name` SEPARATOR ' ') AS receivers,
			(SELECT COUNT(1) FROM minigames_mail_attachment a WHERE a.mailid = m.id) AS attachments,
			CASE
				WHEN EXISTS (
					SELECT * FROM minigames_mail_recipient WHERE mailid = m.id AND attachment > 0
				) THEN 1
				ELSE 0
			END AS attachment_received
		FROM
			minigames_mail m
			JOIN minigames_mail_recipient r ON m.id = r.mailid
			JOIN minigames_players ri ON r.`uuid` = ri.`uuid`
		WHERE
			m.sender = ? %exact_query%
		GROUP BY
			m.id
		ORDER BY
			m.date DESC;
	""";
	private static final String MAIL_INSERT = """
		INSERT INTO minigames_mail(sender, title, message, points, money, `date`)
		VALUES (?, ?, ?, ?, ?, ?);
	""";
	private static final String MAIL_RECIPIENT = """
		INSERT INTO minigames_mail_recipient(mailid, `uuid`, `read`, attachment)
		VALUES(?, ?, ?, ?);
	""";
	private static final String MAIL_ATTACHMENT_ADD = """
        INSERT INTO minigames_mail_attachment(mailid, type, `data`, amount, itemdata)
        VALUES(?, ?, ?, ?, ?);
    """;

	private static final String MAIL_ATTACHMENT_GET = """
		SELECT * FROM minigames_mail_attachment WHERE mailid = ?
	""";

	private static final String MAIL_READ = """
		UPDATE minigames_mail_recipient SET `read` = ? WHERE mailid = ?
	""";

	private static final String MAIL_ATTACHMENT_CLAIM = """
		UPDATE minigames_mail_recipient SET attachment = ? WHERE mailid = ?
	""";
	private static final String MAIL_ATTACHMENT_REVOKE = """
		UPDATE minigames_mail SET revoked = ? WHERE id = ?
	""";

	private static final String MAIL_DELETE = """
		UPDATE minigames_mail_recipient SET deleted = ? WHERE mailid = ? AND `uuid` = ?
	""";
	private static final String MAIL_RECIPIENT_GET = """
		SELECT
			r.*, p.name
		FROM
			minigames_mail_recipient r
			JOIN minigames_players p ON r.`uuid` = p.`uuid`
		WHERE
			mailid = ?;
	""";
	// endregion

	// region logging
	private static final String LOG = """
		INSERT INTO minigames_logs(`uuid`, `action`, `data`, `note`, `date`)
		VALUES(?, ?, ?, ?, ?);
	""";
	// endregion

	//region Player inventory
	public List<PlayerItem> getItems(PlayerData owner) throws SQLException {
		List<PlayerItem> items = new CopyOnWriteArrayList<>();
		PreparedStatement ps = null;
		ResultSet r = null;
		try {
			ps = sql.prepareStatement(PLAYER_INVENTORY);
			ps.setString(1, owner.getUUID().toString());
			ps.setLong(2, System.currentTimeMillis());
			r = ps.executeQuery();
			while (r.next()) {
				int id = r.getInt("id");
				String type = r.getString("type");
				String data = r.getString("data");
				long expire = r.getLong("expire");
				long obtained = r.getLong("obtainedtime");
				boolean isUsing = r.getBoolean("isusing");
				int amount = r.getInt("amount");
				String itemData = r.getString("itemdata");
				try {
					PlayerItem item = PlayerItem.fromData(type, owner, data, id, expire, obtained, isUsing, amount);
					if (!Strings.isNullOrEmpty(itemData) && item instanceof CustomizablePI<?> ci) {
						ci.initData(itemData);
					}
					items.add(item);
				} catch (Exception e) {
					main.getLogger().log(Level.SEVERE, "Khong the load item id " + id, e);
				}
			}
		} finally {
			cleanup(r, ps);
		}
		return items;
	}

	public void saveItem(PlayerItem item) {
		// main thread warning
		if (Bukkit.isPrimaryThread()) {
			try {
				throw new IllegalStateException();
			} catch (Exception e) {
				main.getLogger().warning("Calling save item on main thread, move it to async now!");
				e.printStackTrace();
			}
		}

		PreparedStatement ps = null;
		ResultSet r = null;
		boolean isUsing = item instanceof UsablePI ui && ui.isUsing();
		int amount = item instanceof StackablePI si
			? si.getAmount()
			: 0;
		try {
			if (item.isDefaultItem()) return;
			ps = item.getID() < 0
				? sql.prepareStatement(PLAYER_INVENTORY_ITEM_INSERT, Statement.RETURN_GENERATED_KEYS)
				: sql.prepareStatement(PLAYER_INVENTORY_ITEM_UPDATE);
			ps.setString	(1, item.getOwner().getUUID().toString());
			ps.setString	(2, item.getType());
			ps.setString	(3, item.getData());
			ps.setLong		(4, item.getExpire());
			ps.setLong		(5, item.getObtainedTime());
			ps.setBoolean	(6, isUsing);
			ps.setInt		(7, amount);
			String itemData = "";
			if (item instanceof CustomizablePI) {
				itemData = ((CustomizablePI<?>) item).getItemData().toJson();
			}
			ps.setString(8, itemData);
			if (item.getID() < 0) {
				ps.executeUpdate();
				r = ps.getGeneratedKeys();
				while (r.next()) {
					item.setID(r.getInt(1));
				}
				main.getLogger().info("created item #" + item.getID());
			} else {
				ps.setInt(9, item.getID());
				ps.executeUpdate();
			}
		} catch (Exception e) {
			main.getLogger().log(
				Level.SEVERE,
				"Can not save item #" + item.getID() + ", owner: " + item.getOwner().getPlayer() + ", type: "
					+ item.getClass().getName() + ", data: " + item.getData() + ", expire: " + item.getExpire()
					+ ", obtained time: " + item.getObtainedTime() + ", isusing: "
					+ isUsing,
				e
			);
			Player player = item.getOwner().getPlayer();
			if (player != null && player.isOnline())
				player.sendMessage("§cCó lỗi xảy ra khi lưu " + item.getItem().getItemMeta().getDisplayName()
					+ " §cvào kho đồ của bạn, vui lòng báo cho admin càng sớm càng tốt.");
		} finally {
			cleanup(r, ps);
		}
	}
	//endregion

	//region Shop
	public LinkedHashMap<Integer, ShopItem> shopGetItems() {
		LinkedHashMap<Integer, ShopItem> items = new LinkedHashMap<>();
		PreparedStatement ps = null;
		ResultSet r = null;
		try {
//			if (category == null)
			ps = sql.prepareStatement(SHOP_LOADALLITEMS);
//			else {
//				ps = sql.prepareStatement(SHOP_LOADITEMS_FROM_CATEGORY);
//				ps.setString(1, category.name());
//			}
			r = ps.executeQuery();
			while (r.next()) {
				int id = r.getInt(1);
				try {
					String[] cateslist = r.getString(2).split(",");
					List<ShopCategory> categories = new ArrayList<>();
					for (String catename : cateslist) {
						try {
							categories.add(ShopCategory.get(catename));
						} catch (IllegalArgumentException ignored) {
						}
					}

//					ShopCategory shopcat = ShopCategory.valueOf(r.getString(2));
					String prices = r.getString(3);
					int level = r.getInt(4);
					long createdTime = r.getLong(5);
					boolean activated = r.getBoolean(6);
					String type = r.getString(7);
					String data = r.getString(8);
					int sale = r.getInt(10);
					int stock = r.getInt(11);
					PlayerAward item = PlayerAward.fromData(type, data);
					if (item == null)
						throw new IllegalStateException("Item không hợp lệ? " + type + ":" + data);
					ShopItem si = new ShopItem(id, item, prices);
					si.setRequiredLevel(level);
					si.setCreatedTime(createdTime);
					si.setActivated(activated);
					si.setCategories(categories);
					if (sale >= 0 && sale <= 100)
						si.setSale(sale);
					else
						main.getLogger().warning("shop item #" + id + " có sale không hợp lệ: " + sale);
					if (stock >= 0) {
						si.setStock(stock);
					}
					items.put(si.getID(), si);
				} catch (Exception e) {
					main.getLogger().log(Level.SEVERE, "Không thể khởi tạo shop item #" + id, e);
				}
			}
		} catch (Exception e) {
			main.getLogger().log(Level.SEVERE, "Không thể load shop "/* + (category != null ? category.name() : "") */,
					e);
		} finally {
			cleanup(r, ps);
		}
		return items;
	}

	private final ReentrantLock stockLocker = new ReentrantLock();

	public int getShopStock(int itemID) {
		int stock = 0;
		PreparedStatement ps = null;
		ResultSet r = null;
		stockLocker.lock();
		try {
			ps = sql.prepareStatement(SHOP_STOCK_GET);
			ps.setInt(1, itemID);
			r = ps.executeQuery();
			if (r.next()) {
				stock = r.getInt(1);
			}
		} catch (Exception e) {
			main.getLogger().log(Level.WARNING, "Không thể lấy tồn kho shopitem #" + itemID, e);
		} finally {
			stockLocker.unlock();
			cleanup(r, ps);
		}
		return stock;
	}

	public void removeShopStock(int itemID, int stock) {
		PreparedStatement ps = null;
		stockLocker.lock();
		try {
			ps = sql.prepareStatement(SHOP_STOCK_REMOVE);
			ps.setInt(1, stock);
			ps.setInt(2, itemID);
			ps.executeUpdate();
		} catch (Exception e) {
			main.getLogger().log(Level.WARNING, "Không thể cập nhật tồn kho shopitem #" + itemID, e);
		} finally {
			stockLocker.unlock();
			cleanup(null, ps);
		}
	}
	//endregion

	//region Player data
	public void getData(PlayerData data) throws SQLException {
		getPlayerQuest(data);
		getStats(data);
		PreparedStatement ps = null;
		ResultSet r = null;
		var uuid = data.getUUID().toString();
		try {
			ps = sql.prepareStatement(PLAYER_DATA_GET);
			ps.setString(1, uuid);
			r = ps.executeQuery();
			if (r.next()) {
				data.setMoney(r.getDouble("money"));
				data.setExp(r.getInt("exp"));
				data.setQuestPoints(r.getInt("questpoints"));
			}
		} finally {
			cleanup(r, ps);
		}
	}

	public void saveData(PlayerData data) {
		PreparedStatement ps = null;
		var uuid = data.getUUID().toString();
		for (PlayerQuest pq : data.getQuests().values()) {
			savePlayerQuest(uuid, pq);
		}
		try {
			ps = sql.prepareStatement(PLAYER_DATA_SAVE);
			ps.setString(1, uuid);
			ps.setString(2, data.getName());
			ps.setInt(3, data.getExp());
			ps.setInt(4, data.getQuestPoints());
			ps.executeUpdate();
			data.setLastSaved();
			data.log("data saved");
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "Khong the luu thong tin cua " + uuid, e);
		} finally {
			cleanup(null, ps);
		}
	}

	public void getPlayerQuest(PlayerData data) throws SQLException {
		PreparedStatement ps = null;
		ResultSet r = null;
		var uuid = data.getUUID().toString();
		try {
			ps = sql.prepareStatement(PLAYER_QUESTS_GET);
			ps.setString(1, uuid);
			r = ps.executeQuery();
			var map = data.getQuests();
			map.clear();
			while (r.next()) {
				var quest = Quest.get(r.getString("quest"));
				if (quest == null) continue;
				var done = r.getInt("done");
				var nextReset = r.getLong("nextreset");
				var finished = r.getBoolean("finished");
				var obtained = r.getBoolean("obtained");
				var pq = new PlayerQuest(quest, done, nextReset, finished, obtained);
				map.put(quest, pq);
			}
		} finally {
			cleanup(r, ps);
		}
	}

	public void savePlayerQuest(String uuid, PlayerQuest playerQuest) {
		//if (playerQuest.isNewlyQuest()) return;
		PreparedStatement ps = null;
		try {
			ps = sql.prepareStatement(PLAYER_QUESTS_SAVE);
			ps.setString(1, uuid);
			ps.setString(2, playerQuest.getQuest().getId());
			ps.setInt(3, playerQuest.getDone());
			ps.setLong(4, playerQuest.getNextReset());
			ps.setInt(5, playerQuest.getFinished() ? 1 : 0);
			ps.setInt(6, playerQuest.getObtained() ? 1 : 0);
			ps.executeUpdate();
		} catch (SQLException ex) {
			main.getLogger().log(Level.SEVERE, "Khong the luu thong tin quests cua " + uuid, ex);
		} finally {
			cleanup(null, ps);
		}
	}

	public UUID getPlayerUUID(String playerName) {
		PreparedStatement ps = null;
		ResultSet r = null;
		UUID uuid = null;
		try {
			ps = sql.prepareStatement(PLAYER_DATA_GET_UUID);
			ps.setString(1, playerName);
			r = ps.executeQuery();
			if (r.next()) {
				uuid = UUID.fromString(r.getString("uuid"));
			}
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "Khong the lay thong tin uuid cua " + playerName, e);
		} finally {
			cleanup(r, ps);
		}
		return uuid;
	}
	//endregion

	//region Player money
	public void getMoney(PlayerData data) {
		data.setMoney(getMoney(data.getUUID().toString()));
	}

	public double getMoney(String uuid) {
		PreparedStatement ps = null;
		ResultSet r = null;
		double money = 0;
		try {
			ps = sql.prepareStatement(PLAYER_MONEY_SELECT);
			ps.setString(1, uuid);
			r = ps.executeQuery();
			if (r.next()) money = r.getDouble("money");
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "Khong the lay thong tin money cua " + uuid, e);
		} finally {
			cleanup(r, ps);
		}
		return money;
	}

	public void addMoney(String uuid, double money) {
		PreparedStatement ps = null;
		try {
			ps = sql.prepareStatement(PLAYER_MONEY_ADD);
			ps.setString(1, uuid);
			ps.setDouble(2, money);
			ps.executeUpdate();
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "Khong the lay thong tin money cua " + uuid, e);
		} finally {
			cleanup(null, ps);
		}
	}

	public void addMoney(PlayerData data, double money) {
		addMoney(data.getUUID().toString(), money);
		getMoney(data);
	}

	public void removeItem(PlayerItem item) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = sql.prepareStatement(PLAYER_INVENTORY_ITEM_REMOVE);
			ps.setInt(1, item.getID());
			ps.executeUpdate();
		} finally {
			cleanup(null, ps);
		}
	}

	//endregion

	//region Stat
	public void updateStat(UUID uuid, String type, double value, boolean replace) {
		PreparedStatement ps = null;
		try {
			ps = sql.prepareStatement(replace ? TOP_REPLACE : TOP_ADD);
			ps.setString(1, uuid.toString());
			ps.setString(2, type);
			ps.setDouble(3, value);
			ps.executeUpdate();
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "SQL ERROR", e);
		} finally {
			cleanup(null, ps);
		}
	}

	public void resetStat(UUID uuid, String type) {
		PreparedStatement ps = null;
		try {
			ps = sql.prepareStatement(TOP_REPLACE);
			ps.setString(1, uuid.toString());
			ps.setString(2, type);
			ps.setDouble(3, 0);
			ps.executeUpdate();
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "SQL ERROR", e);
		} finally {
			cleanup(null, ps);
		}
	}

	public LinkedHashMap<String, Double> getStats(String key) {
		LinkedHashMap<String, Double> tops = new LinkedHashMap<>();
		if (key == null) return tops;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			switch (key) {
				case "level" -> statement = sql.prepareStatement(TOP_SELECT_LEVEL);
				case "mslevel" -> statement = sql.prepareStatement(TOP_SELECT_MSLEVEL);
				case "questpoints" -> statement = sql.prepareStatement(TOP_SELECT_QUESTPOINTS);
				default -> {
					statement = sql.prepareStatement(TOP_SELECT);
					statement.setString(1, key);
				}
			}
			result = statement.executeQuery();
			while (result.next()) {
				tops.put(result.getString("name"), result.getDouble("value"));
			}
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "SQL ERROR", e);
		} finally {
			cleanup(result, statement);
		}
		return tops;
	}

	public double getStat(UUID uuid, String key) {
		if (uuid == null || key == null) return 0;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			statement = sql.prepareStatement(TOP_SELECT_BY_USER);
			statement.setString(1, uuid.toString());
			statement.setString(2, key);
			result = statement.executeQuery();
			if (result.next()) {
				return result.getDouble("value");
			}
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "SQL ERROR", e);
		} finally {
			cleanup(result, statement);
		}
		return 0;
	}

	public void getStats(PlayerData data) throws SQLException {
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			statement = sql.prepareStatement(TOP_SELECT_ALL_BY_USER);
			statement.setString(1, data.getUUID().toString());
			result = statement.executeQuery();
			while (result.next()) {
				data.getStats().put(result.getString("toptype"), result.getDouble("value"));
			}
		} finally {
			cleanup(result, statement);
		}
	}
	//endregion

	// region giftcodes

	/**
	 * @return ItemBundle id or null if not found
	 */
	public String getGiftCode(String code) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = sql.prepareStatement(GIFTCODE_GET);
			ps.setString(1, code);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("itemset");
			}
		} finally {
			cleanup(rs, ps);
		}
		return null;
	}

	public void useGiftCode(String code, String user) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = sql.prepareStatement(GIFTCODE_USE);
			ps.setString(1, user);
			ps.setLong(2, System.currentTimeMillis());
			ps.setString(3, code);
			ps.executeUpdate();
		} finally {
			cleanup(null, ps);
		}
	}

	// create giftcode, input: itemset id, array of codes
	public void createGiftCode(String itemset, String[] codes) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = sql.prepareStatement(GIFTCODE_INSERT);
			for (String code : codes) {
				ps.setString(1, code);
				ps.setString(2, itemset);
				ps.addBatch();
			}
			ps.executeBatch();
		} finally {
			cleanup(null, ps);
		}
	}

	public boolean validate(String[] codes) throws SQLException {
		// join codes to single string
		String codesString = Arrays.stream(codes).map(x -> "'" + x +"'").collect(Collectors.joining(", "));
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = sql.prepareStatement(GIFTCODE_VALIDATE + " (" + codesString + ")");
			rs = ps.executeQuery();
			return !rs.next();
		} finally {
			cleanup(rs, ps);
		}
	}

	public boolean bundleUsed(String itemset, String player) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = sql.prepareStatement(GIFTCODE_BUNDLE_USED);
			ps.setString(1, itemset);
			ps.setString(2, player);
			rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "SQL ERROR", e);
		} finally {
			cleanup(rs, ps);
		}
		return false;
	}
	// endregion

	// region mail
	public List<Mail> getMails(String uuid) {
		return getMails(uuid, -1);
	}

	public List<Mail> getMails(String uuid, int mailId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		var list = new ArrayList<Mail>();
		try {
			String exact = mailId == -1
				? ""
				: ("and m.id = " + mailId);
			ps = sql.prepareStatement(MAIL_GET.replace("%exact_query%", exact));
			ps.setString(1, uuid);
			rs = ps.executeQuery();
			while (rs.next()) {
				var id = rs.getInt("id");
				var sender = rs.getString("sender");
				var senderName = rs.getString("sendername");
				var title = rs.getString("title");
				var message = rs.getString("message");
				var points = rs.getInt("points");
				var money = rs.getInt("money");
				var attachments = rs.getInt("attachments");
				var read = rs.getLong("read");
				var attachment = rs.getLong("attachment");
				var date = rs.getLong("date");
				var mail = new Mail(
					id, sender, senderName, date, title, message,
					points, money, attachments, read, attachment
				);
				mail.setDeleted(rs.getLong("deleted") > 0);
				mail.setRevoked(rs.getLong("revoked"));
				list.add(mail);
			}
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "Error getting mail of " + uuid, e);
		} finally {
			cleanup(rs, ps);
		}
		return list;
	}

	public List<Mail> getSentMails(String uuid) {
		return getSentMails(uuid, -1);
	}

	public List<Mail> getSentMails(String uuid, int mailId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		var list = new ArrayList<Mail>();
		try {
			String exact = mailId == -1
				? ""
				: ("and m.id = " + mailId);
			ps = sql.prepareStatement(MAIL_SENT.replace("%exact_query%", exact));
			ps.setString(1, uuid);
			rs = ps.executeQuery();
			while (rs.next()) {
				var id = rs.getInt("id");
				var sender = rs.getString("sender");
				var title = rs.getString("title");
				var message = rs.getString("message");
				var points = rs.getInt("points");
				var money = rs.getInt("money");
				var attachments = rs.getInt("attachments");
				var date = rs.getLong("date");
				var mail = new Mail(
					id, sender, "YOU", date, title, message,
					points, money, attachments, 0, 0
				);
				mail.setDeleted(false);
				mail.setReceivers(rs.getString("receivers"));
				mail.setRevoked(rs.getLong("revoked"));
				mail.setAttachmentReceived(rs.getBoolean("attachment_received"));
				list.add(mail);
			}
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "Error getting mail of " + uuid, e);
		} finally {
			cleanup(rs, ps);
		}
		return list;
	}

	public List<PlayerItem> getAttachments(Mail mail) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		var list = new ArrayList<PlayerItem>();
		try {
			ps = sql.prepareStatement(MAIL_ATTACHMENT_GET);
			ps.setInt(1, mail.getId());
			rs = ps.executeQuery();
			while (rs.next()) {
				var type = rs.getString("type");
				var data = rs.getString("data");
				var amount = rs.getInt("amount");
				var itemData = rs.getString("itemdata");
				try {
					var item = PlayerItem.fromData(type, null, data, -1,
							0, 0, false, amount);
				if (!Strings.isNullOrEmpty(itemData) && item instanceof CustomizablePI<?> ci) {
					ci.initData(itemData);
				}
				list.add(item);
				} catch (Exception e) {
					main.getLogger().log(Level.SEVERE, "Khong the load item trong mail id " + mail.getId()
							+ "(" + type + ":" + data + ")", e);
				}
			}
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "Error getting attachments of " + mail, e);
		} finally {
			cleanup(rs, ps);
		}
		return list;
	}

	public void readContent(Mail mail) {
		PreparedStatement ps = null;
		try {
			ps = sql.prepareStatement(MAIL_READ);
			ps.setLong(1, System.currentTimeMillis());
			ps.setInt(2, mail.getId());
			ps.executeUpdate();
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "Error reading mail " + mail, e);
		} finally {
			cleanup(null, ps);
		}
	}

	public void readAttachment(Mail mail) {
		PreparedStatement ps = null;
		try {
			mailLocker.lock();
			ps = sql.prepareStatement(MAIL_ATTACHMENT_CLAIM);
			ps.setLong(1, System.currentTimeMillis());
			ps.setInt(2, mail.getId());
			ps.executeUpdate();
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "Error reading attachment of " + mail, e);
		} finally {
			cleanup(null, ps);
			mailLocker.unlock();
		}
	}

	public void revokeAttachment(Mail mail) {
		PreparedStatement ps = null;
		try {
			mailLocker.lock();
			ps = sql.prepareStatement(MAIL_ATTACHMENT_REVOKE);
			ps.setLong(1, System.currentTimeMillis());
			ps.setInt(2, mail.getId());
			ps.executeUpdate();
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "Error revoking attachment of " + mail, e);
		} finally {
			cleanup(null, ps);
			mailLocker.unlock();
		}
	}

	public void deleteMail(Mail mail, String uuid) {
		PreparedStatement ps = null;
		try {
			ps = sql.prepareStatement(MAIL_DELETE);
			ps.setLong(1, System.currentTimeMillis());
			ps.setInt(2, mail.getId());
			ps.setString(3, uuid);
			ps.executeUpdate();
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "Error deleting mail " + mail.getId(), e);
		} finally {
			cleanup(null, ps);
		}
	}

	public void sendMail(Mail mail, List<UUID> receivers) {
		int id = createMail(mail);
		if (id == -1) return;
		if (mail.getAttachmentCount() > 0) addAttachments(mail, id);
		addRecipients(mail, receivers, id);
	}

	private int createMail(Mail mail) {
		PreparedStatement ps = null;
		ResultSet r = null;
		int id = mail.getId();
		try {
			ps = sql.prepareStatement(MAIL_INSERT, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, mail.getSender());
			ps.setString(2, mail.getTitle());
			ps.setString(3, mail.getMessage());
			ps.setInt(4, mail.getPoints());
			ps.setInt(5, mail.getMoney());
			ps.setLong(6, mail.getDate());
			ps.executeUpdate();
			r = ps.getGeneratedKeys();
			while (r.next()) {
				id = r.getInt(1);
			}
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "SQL ERROR", e);
		} finally {
			cleanup(r, ps);
		}
		if (r == null) {
			main.getLogger().log(Level.SEVERE, "CAN'T FIND MAIL ID FOR " + mail.getTitle());
		}
		return id;
	}

	private void addAttachments(Mail mail, int id) {
		for (PlayerItem pi : mail.getAttachments()) {
			PreparedStatement ps = null;
			try {
				ps = sql.prepareStatement(MAIL_ATTACHMENT_ADD);
				ps.setInt(1, id);
				ps.setString(2, pi.getType());
				ps.setString(3, pi.getData());
				ps.setInt(4, pi.getItem().getAmount());
				String data = pi instanceof CustomizablePI<?> custom ? custom.getItemData().toJson() : "";
				ps.setString(5, data);
				ps.executeUpdate();
			} catch (SQLException e) {
				main.getLogger().log(Level.SEVERE, "SQL ERROR", e);
			} finally {
				cleanup(null, ps);
			}
		}
	}

	private void addRecipients(Mail mail, List<UUID> receivers, int id) {
		for (UUID uuid : receivers) {
			PreparedStatement ps = null;
			try {
				ps = sql.prepareStatement(MAIL_RECIPIENT);
				ps.setInt(1, id);
				ps.setString(2, uuid.toString());
				ps.setLong(3, mail.getRead());
				ps.setLong(4, mail.getAttachment());
				ps.executeUpdate();
			} catch (SQLException e) {
				main.getLogger().log(Level.SEVERE, "SQL ERROR", e);
			} finally {
				cleanup(null, ps);
			}
			var playerData = PlayerData.getData(uuid);
			if (playerData != null) {
				playerData.noticeMail();
			}
		}
	}

	public List<MailRecipient> getRecipient(int mailId) {
		PreparedStatement ps = null;
		ResultSet r = null;
		List<MailRecipient> result = new ArrayList<>();
		try {
			ps = sql.prepareStatement(MAIL_RECIPIENT_GET);
			ps.setInt(1, mailId);
			r = ps.executeQuery();
			while (r.next()) {
				var name = r.getString("name");
				var attachment = r.getLong("attachment");
				var read = r.getLong("read");
				var recipient = new MailRecipient(name, attachment, read);
				result.add(recipient);
			}
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "SQL ERROR", e);
		} finally {
			cleanup(null, ps);
		}
		return result;
	}

	public void updateMail(String uuid, Mail mail, boolean sentMode) {
		try {
			mailLocker.lock();
			List<Mail> check;
			if (!sentMode) {
				check = getMails(uuid, mail.getId());
				if (check.isEmpty() || check.get(0).getAttachment() > 0) {
					mail.setAttachment(100L);
				}
			} else {
				check = getSentMails(uuid, mail.getId());
				if (check.isEmpty() || check.get(0).isAttachmentReceived()) {
					mail.setAttachmentReceived(true);
				}
			}
			if (check.isEmpty() || check.get(0).getRevoked() > 0) {
				mail.setRevoked(100L);
			}
		} finally {
			mailLocker.unlock();
		}
	}
	// endregion

	// region logging
	public void log(String uuid, String action, String data, String note) {
		PreparedStatement ps = null;
		try {
			ps = sql.prepareStatement(LOG);
			ps.setString(1, uuid);
			ps.setString(2, action);
			ps.setString(3, data);
			ps.setString(4, note);
			ps.setLong(5, System.currentTimeMillis());
			ps.executeUpdate();
		} catch (SQLException e) {
			String msg = "Logging error for " + uuid + ", action: " + action + ", data: " + data
					+ ", note: " + note;
			main.getLogger().log(Level.SEVERE, msg, e);
		} finally {
			cleanup(null, ps);
		}
	}
	// endregion

	//region statics
	private static MySQL _instance;

	public static MySQL getInstance() {
		return Objects.requireNonNull(_instance);
	}
	//endregion
}

