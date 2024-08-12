package net.minevn.minigames.items.types;

import net.minevn.minigames.Messages;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.gadgets.HitSound;
import net.minevn.minigames.items.PlayerItem;
import net.minevn.minigames.items.UsablePI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class HitSoundPI extends UsablePI {

	private HitSound hs;

	public HitSoundPI(PlayerData owner, int id, HitSound hs, long expire, long obtained, boolean isUsing) {
		super(Messages.PI_CATEGORY_HITSOUND, owner, id, hs.getMaterial(), hs.getData(), expire, obtained, isUsing);
		this.hs = hs;
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = super.getItem();
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(hs.getName());
		List<String> lores = new ArrayList<>(getDescription());
		lores.add("§f");
		addExpireDate(lores);
		lores.add(Messages.PI_CLICK_TO_LISTEN);
		addUsingStatus(lores);
		im.setLore(lores);
		item.setItemMeta(im);
		return item;
	}

	@Override
	public String getData() {
		return hs.getID();
	}

	@Override
	public List<String> getDescription() {
		return hs.getDescription();
	}

	@Override
	public boolean isPreviewable() {
		return true;
	}

	@Override
	public void preview(Player p) {
		hs.preview(p);
	}

	@Override
	public PlayerItem clone() {
		return new HitSoundPI(null, -1, hs, expire, System.currentTimeMillis(), false);
	}

	public HitSound getHitSound() {
		return hs;
	}

	// static
	public static HitSoundPI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		var hs = HitSound.get(data);
		if (hs == null) throw new IllegalArgumentException("hit sound does not exist: " + data);
		return new HitSoundPI(owner, id, hs, expire, obtained, isUsing);
	}
}
