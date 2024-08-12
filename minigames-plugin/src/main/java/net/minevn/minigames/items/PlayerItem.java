package net.minevn.minigames.items;

import com.mojang.authlib.GameProfile;
import net.minevn.minigames.Messages;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract class PlayerItem {

	protected Material type;
	private short data;
	private boolean glow = false, toberemoved = false;
	protected long expire = 0, obtained;
	protected int id;
	protected PlayerData owner;
	protected String catename;

	public PlayerItem(
		String catename, PlayerData owner, int id, Material type, short data, long expire, long obtained
	) {
		this(catename, owner, id, type, data, expire, obtained, false);
	}

	public PlayerItem(
		String catename, PlayerData owner, int id, Material type, short data, long expire, long obtained, boolean glow
	) {
		this.catename = catename;
		this.owner = owner;
		this.id = id;
		this.type = type;
		this.data = data;
		this.expire = expire;
		this.obtained = obtained;
		this.glow = glow;
	}

	// public abstract void use(Player p);

	public ItemStack getItem() {
		ItemStack item = new ItemStack(type, 1, data);
		ItemMeta im = item.getItemMeta();
		if (glow) {
			im.addEnchant(Enchantment.DURABILITY, 1, true);
			im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		if (type.getMaxDurability() > 0) {
			im.setUnbreakable(true);
			im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		}
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(im);
//        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
//        NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
//        compound.setInt("itemid", id);
//        nmsStack.setTag(compound);
//        return CraftItemStack.asBukkitCopy(nmsStack);
		return item;
	}

	public String getExpireDate() {
		return Utils.getDate(expire);
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	public void remove() {
		toberemoved = true;
	}

	public boolean toBeRemoved() {
		return toberemoved;
	}

	public void onClick(InventoryClickEvent e) {
	}

	public boolean isExpired() {
		return expire > 0 && System.currentTimeMillis() > expire;
	}

	public PlayerData getOwner() {
		return owner;
	}

	public void setOwner(PlayerData owner) {
		this.owner = owner;
	}

	public void setObtainedTime(long time) {
		this.obtained = time;
	}

	public long getObtainedTime() {
		return obtained;
	}

	public long getExpire() {
		return expire;
	}

	public void setExpire(long expire) {
		this.expire = expire;
	}

	public abstract String getData();

	public String getType() {
		return getClass().getSimpleName();
	}

	public String getCategoryName() {
		return catename;
	}

	public abstract List<String> getDescription();

	public boolean isPreviewable() {
		return false;
	}

	public void preview(Player p) {
	}

	public void setGlowing(boolean glow) {
		this.glow = glow;
	}

	protected void addDescription(List<String> lores) {
		var desc = getDescription();
		if (desc != null && !desc.isEmpty()) {
			lores.addAll(desc);
			lores.add("§f");
		}
	}

	protected void addExpireDate(List<String> lores) {
		if (getExpire() > 0) {
			lores.add(Messages.PI_EXPIRE_DATE.replace("%date%", getExpireDate()));
			lores.add("§f");
		}
	}

	public abstract PlayerItem clone();

	/**
	 * Flag ID cho default items là -68
	 * Default items sẽ không được lưu
	 */
	public boolean isDefaultItem() {
		return id == -68;
	}

	/**
	 * @return head texture
	 */
	public GameProfile getGameProfile() {
		return null;
	}

	public String getCategoryRegex() {
		return this.getClass().getSimpleName() + "@" + getData();
	}

	public int getShippingCost() {
		return -1;
	}

	final public String getShippingCostLine() {
		var shippingCost = getShippingCost() > -1 ? "§b" + getShippingCost() + " Points" : Messages.PI_CANNOT_SHIP;
		return "§c§l" + Messages.PI_ATTACHMENT_SHIPPING_COST.replace("%cost%", shippingCost);
	}

	// static
	public static PlayerItem fromData(
		String type,
		PlayerData owner,
		String data,
		int id, long expire, long obtained, boolean isUsing, int amount
	) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		var clazz = Class.forName("net.minevn.minigames.items.types." + type);
		var method = clazz.getMethod(
			"fromData",
			PlayerData.class,
			String.class,
			int.class,
			long.class,
			long.class,
			boolean.class,
			int.class
		);
		return (PlayerItem) method.invoke(null, owner, data, id, expire, obtained, isUsing, amount);
	}
}
