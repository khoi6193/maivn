package net.minevn.minigames.items.types;

import net.minefs.MineStrike.Guns.Gun;
import net.minevn.minigames.Messages;
import net.minevn.minigames.MySQL;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.Utils;
import net.minevn.minigames.gui.InventoryGui;
import net.minevn.minigames.items.CustomizablePI;
import net.minevn.minigames.items.ItemData;
import net.minevn.minigames.items.PlayerItem;
import net.minevn.minigames.items.datatypes.MSGunData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MSGunPI extends PlayerItem implements CustomizablePI<MSGunData> {
	private Gun gun;
	private MSGunData gunData = new MSGunData();

	public MSGunPI(PlayerData owner, int id, Gun gun, long expire, long obtained) {
		super(Messages.PI_CATEGORY_MSGUN, owner, id, gun.getItem().getType(),
				gun.isDual() ? gun.getDdata() : gun.getItem().getData(), expire, obtained);
		this.gun = gun;
	}

	@Override
	public String getData() {
		return gun.getName();
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = super.getItem();
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(gun.getItem().getName() + " §f" + gun.getSymbol());
		List<String> lores = new ArrayList<String>();
		var name = gunData.getName();
		if (name != null) {
			lores.add(name);
			lores.add("§f");
		}
		var desc = getDescription();
		if (desc != null) {
			lores.addAll(desc);
			lores.add("§f");
		}
		if (getExpire() > 0) {
			lores.add(Messages.PI_EXPIRE_DATE.replace("%date%", getExpireDate()));
			lores.add("§f");
		}
		if (!isDefaultItem() && gun.getStartRole() != null) {
			if (gunData.isDefaultGun()) lores.add(Messages.PI_CLICK_TO_UNUSE);
			else lores.add(Messages.PI_CLICK_TO_USE);
			lores.add("§f");
		}
		im.setLore(lores);
		item.setItemMeta(im);
		return item;
	}

	@Override
	public List<String> getDescription() {
		return gun.getGuiIcon().getLore();
	}

	@Override
	public PlayerItem clone() {
		return new MSGunPI(null, -1, gun, expire, System.currentTimeMillis());
	}

	public Gun getGun() {
		return gun;
	}

	@Override
	public String getCategoryRegex() {
		return "gun" +
			"@" + gun.getName() +
			"@" + gun.getCategory().name();
	}

	@Override
	public int getShippingCost() {
		return Utils.getMineStrikeItemPrice(gun.getItem().getName());
	}

	// region item data
	@NotNull
	@Override
	public MSGunData getItemData() {
		return gunData;
	}

	@NotNull
	@Override
	public Class<MSGunData> getDataClass() {
		return MSGunData.class;
	}

	@Override
	public void initData(@NotNull String json) {
		gunData = ItemData.parseItem(json, getDataClass());
	}
	// endregion

	// region static
	public static MSGunPI fromData(
		PlayerData owner,
		String data,
		int id, long expire, long obtained, boolean isUsing, int amount
	) {
		var gun = Gun.getGun(data);
		if (gun == null) throw new IllegalArgumentException("gun does not exist: " + data);
		return new MSGunPI(owner, id, gun, expire, obtained);
	}

	@Override
	public void onClick(InventoryClickEvent e) {
		if (!(e.getInventory().getHolder() instanceof InventoryGui ig)) return;
		Player p = (Player) e.getWhoClicked();
		var data = PlayerData.getData(p);
		var sql = MySQL.getInstance();
		if (e.isShiftClick() && gun.getStartRole() != null && !isDefaultItem()) {
			ig.lock();
			Utils.runNotSync(() -> {
				if (!gunData.isDefaultGun()) {
					// unuse other items
					data.getItems(MSGunPI.class).stream()
						.filter(x -> x.gunData.isDefaultGun() && x.getGun().getStartRole() == gun.getStartRole())
						.forEach(x -> {
							x.gunData.setDefaultGun(false);
							sql.saveItem(x);
						});
				}
				gunData.setDefaultGun(!gunData.isDefaultGun());
				sql.saveItem(this);
				ig.build();
			});
		}
	}
	// endregion
}
