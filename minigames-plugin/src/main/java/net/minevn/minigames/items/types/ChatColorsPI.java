package net.minevn.minigames.items.types;

import net.minevn.minigames.Messages;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.gadgets.ChatColors;
import net.minevn.minigames.items.PlayerItem;
import net.minevn.minigames.items.UsablePI;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ChatColorsPI extends UsablePI {

	private ChatColors cc;

	public ChatColorsPI(PlayerData owner, int id, ChatColors cc, long expire, long obtained, boolean isUsing) {
		super(Messages.PI_CATEGORY_CHATCOLORS, owner, id, cc.getMaterial(), cc.getData(), expire, obtained, isUsing);
		this.cc = cc;
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = super.getItem();
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(cc.getName());
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
		return cc.getID();
	}

	@Override
	public List<String> getDescription() {
		return cc.getDescription();
	}

	@Override
	public PlayerItem clone() {
		return new ChatColorsPI(null, -1, cc, getExpire(), System.currentTimeMillis(), false);
	}

	@Override
	public boolean isStackable() {
		return false;
	}

	public ChatColors getChatColors() {
		return cc;
	}

	// static
	public static ChatColorsPI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		var cc = ChatColors.get(data);
		if (cc == null) throw new IllegalArgumentException("chat color does not exist: " + data);
		return new ChatColorsPI(owner, id, cc, expire, obtained, isUsing);
	}
}
