package net.minevn.minigames.items.types;

import net.minevn.minigames.PlayerData;
import net.minevn.minigames.gadgets.MVPAnthem;
import net.minevn.minigames.items.UsablePI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MVPAnthemPI extends UsablePI {
	private MVPAnthem mvp;

	public MVPAnthemPI(PlayerData owner, int id, MVPAnthem mvp, long expire, long obtained,
					   boolean isUsing) {
		super("huhu", owner, id, mvp.getMaterial(), mvp.getData(), expire, obtained, isUsing);
		this.mvp = mvp;
	}

	@Override
	public ItemStack getItem() {
		return new ItemStack(Material.AIR);
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
