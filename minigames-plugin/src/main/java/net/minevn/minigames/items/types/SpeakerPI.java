package net.minevn.minigames.items.types;

import net.minevn.minigames.Configs;
import net.minevn.minigames.Messages;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.items.StackablePI;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SpeakerPI extends StackablePI {

	public SpeakerPI(PlayerData owner, int id, long obtained, int amount) {
		super(Messages.PI_CATEGORY_OTHERS, owner, id, Configs.getSpeakerIcon(), Configs.getSpeakerData(), 0,
				obtained, amount);
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = super.getItem();
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(Messages.PI_SPEAKERWORLD);
		im.setUnbreakable(true);
		im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
		im.setLore(getDescription());
		item.setItemMeta(im);
		item.setAmount(amount);
		return item;
	}

	@Override
	public String getData() {
		return "";
	}

	@Override
	public List<String> getDescription() {
		return Messages.PI_SPEAKERWORLD_DESC;
	}

	@Override
	public void onClick(InventoryClickEvent e) {
	}

	public SpeakerPI clone() {
		return new SpeakerPI(owner, id, obtained, amount);
	}

	@Override
	public int getShippingCost() {
		return 0;
	}

	// static
	public static SpeakerPI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		return new SpeakerPI(owner, id, obtained, amount);
	}
}
