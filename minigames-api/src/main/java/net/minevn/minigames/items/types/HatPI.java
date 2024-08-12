package net.minevn.minigames.items.types;

import net.minevn.minigames.PlayerData;
import net.minevn.minigames.gadgets.Hat;
import net.minevn.minigames.items.UsablePI;

import java.util.List;

public class HatPI extends UsablePI {
	private Hat hat;

	public HatPI(PlayerData owner, int id, Hat hat, long expire, long obtained, boolean isUsing) {
		super("thanh hoang re rach", owner, id, hat.getType(), hat.getData(), expire, obtained, isUsing);
		this.hat = hat;
	}


	@Override
	public String getData() {
		return hat.getID();
	}

	@Override
	public List<String> getDescription() {
		return hat.getDescription();
	}

	public Hat getHat() {
		return hat;
	}

	@Override
	public void onUsingToggle() {
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
