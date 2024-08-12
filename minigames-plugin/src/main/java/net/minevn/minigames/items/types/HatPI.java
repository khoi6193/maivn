package net.minevn.minigames.items.types;

import com.google.common.collect.ImmutableList;
import net.minevn.minigames.Messages;
import net.minevn.minigames.Minigames;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.gadgets.Hat;
import net.minevn.minigames.items.PlayerItem;
import net.minevn.minigames.items.UsablePI;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class HatPI extends UsablePI {
	private Hat hat;

	public HatPI(PlayerData owner, int id, Hat hat, long expire, long obtained, boolean isUsing) {
		super(Messages.PI_CATEGORY_COSTUME, owner, id, hat.getType(), hat.getData(), expire, obtained, isUsing);
		this.hat = hat;
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = super.getItem();
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(hat.getName());
		List<String> lores = new ArrayList<>(getDescription());
		lores.add("§f");
		if (getExpire() > 0) {
			lores.add(Messages.PI_EXPIRE_DATE.replace("%date%", getExpireDate()));
			lores.add("§f");
		}
		lores.add(isUsing() ? Messages.PI_CLICK_TO_UNUSE : Messages.PI_CLICK_TO_USE);
		im.setLore(lores);
		item.setItemMeta(im);
		return item;
	}

	@Override
	public String getData() {
		return hat.getID();
	}

	@Override
	public List<String> getDescription() {
		return hat.getDescription();
	}

	@Override
	public PlayerItem clone() {
		return new HatPI(null, -1, hat, expire, System.currentTimeMillis(), false);
	}

	public Hat getHat() {
		return hat;
	}

	@Override
	public void onUsingToggle() {
		var player = owner.getPlayer();
		Minigames.nms.sendFakeItem(player, 5, player.getInventory().getHelmet());
		for (var other : ImmutableList.copyOf(Bukkit.getOnlinePlayers())) {
			Minigames.nms.sendArmor(player, other);
		}
	}

	// static
	public static HatPI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		var hat = Hat.get(data);
		if (hat == null) throw new IllegalArgumentException("hat " + data + " khong ton tai");
		return new HatPI(owner, id, hat, expire, obtained, isUsing);
	}
}
