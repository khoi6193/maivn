package net.minevn.minigames.items.types;

import net.minevn.minigames.PlayerData;
import net.minevn.minigames.gadgets.HitSound;
import net.minevn.minigames.items.UsablePI;
import org.bukkit.entity.Player;

import java.util.List;

public class HitSoundPI extends UsablePI {

	private HitSound hs;

	public HitSoundPI(PlayerData owner, int id, HitSound hs, long expire, long obtained, boolean isUsing) {
		super("thanh hoang re rach", owner, id, hs.getMaterial(), hs.getData(), expire, obtained, isUsing);
		this.hs = hs;
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
