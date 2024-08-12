package net.minevn.minigames.items.types;

import net.minevn.minigames.PlayerData;
import net.minevn.minigames.cases.Case;
import net.minevn.minigames.items.PlayerItem;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CaseKeyPI extends PlayerItem {
	private Case _case;

	public CaseKeyPI(PlayerData owner, int id, Case _case, long expire, long obtained) {
		super("wow", owner, id, _case.getKeyIcon().getType(), _case.getKeyIcon().getDurability(),
				expire, obtained);
		this._case = _case;
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = super.getItem();
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(_case.getName());
		List<String> lores = new ArrayList<>(getDescription());
		lores.add("§f");
		lores.add("yeah");
		im.setLore(lores);
		item.setItemMeta(im);
		return item;
	}

	@Override
	public String getData() {
		return _case.getID();
	}

	@Override
	public List<String> getDescription() {
		var desc = _case.getCaseIcon().getItemMeta().getLore();
		return desc == null ? new ArrayList<>() : desc;
	}

	@Override
	public PlayerItem clone() {
		return new CaseKeyPI(null, -1, _case, 0, System.currentTimeMillis());
	}

	@Override
	public void onClick(InventoryClickEvent e) {
	}

	// static
	public static CaseKeyPI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		var _case = Case.get(data);
		if (_case == null) throw new IllegalArgumentException("case does not exist: " + data);
		return new CaseKeyPI(owner, id, _case, expire, obtained);
	}
}
