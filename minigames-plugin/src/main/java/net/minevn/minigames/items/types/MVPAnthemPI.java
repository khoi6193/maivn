package net.minevn.minigames.items.types;

import net.minevn.minigames.Messages;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.gadgets.MVPAnthem;
import net.minevn.minigames.items.PlayerItem;
import net.minevn.minigames.items.UsablePI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MVPAnthemPI extends UsablePI {
	private MVPAnthem mvp;

	public MVPAnthemPI(PlayerData owner, int id, MVPAnthem mvp, long expire, long obtained,
					   boolean isUsing) {
		super(Messages.PI_CATEGORY_MVPANTHEM, owner, id, mvp.getMaterial(), mvp.getData(), expire, obtained, isUsing);
		this.mvp = mvp;
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = super.getItem();
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(mvp.getName());
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
	public boolean isPreviewable() {
		return true;
	}

	@Override
	public void preview(Player p) {
		mvp.preview(p);
	}

	@Override
	public PlayerItem clone() {
		return new MVPAnthemPI(null, -1, mvp, expire, System.currentTimeMillis(), false);
	}

	@Override
	public String getData() {
		return mvp.getID();
	}

	@Override
	public List<String> getDescription() {
		return mvp.getDescription();
	}

	public MVPAnthem getMusic() {
		return mvp;
	}

	@Override
	public int getShippingCost() {
		return 1;
	}

	// static
	public static MVPAnthemPI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		var mvp = MVPAnthem.get(data);
		if (mvp == null) throw new IllegalArgumentException("hit sound does not exist: " + data);
		return new MVPAnthemPI(owner, id, mvp, expire, obtained, isUsing);
	}
}
