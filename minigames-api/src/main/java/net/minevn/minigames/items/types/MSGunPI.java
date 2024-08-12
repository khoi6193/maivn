package net.minevn.minigames.items.types;

import net.minefs.MineStrike.Guns.Gun;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.items.CustomizablePI;
import net.minevn.minigames.items.PlayerItem;
import net.minevn.minigames.items.datatypes.MSGunData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MSGunPI extends PlayerItem implements CustomizablePI<MSGunData> {
	private Gun gun;

	public MSGunPI(PlayerData owner, int id, Gun gun, long expire, long obtained) {
		super("thanh hoang tuoi lon", owner, id, gun.getItem().getType(), gun.getItem().getData(), expire, obtained);
		this.gun = gun;
	}

	@Override
	public String getData() {
		return gun.getName();
	}

	@Override
	public List<String> getDescription() {
		return gun.getGuiIcon().getLore();
	}

	public Gun getGun() {
		return gun;
	}

	@NotNull
	@Override
	public MSGunData getItemData() {
		return new MSGunData();
	}

	@NotNull
	@Override
	public Class<MSGunData> getDataClass() {
		return MSGunData.class;
	}

	@Override
	public void initData(@NotNull String json) {
	}

	// region static
	@SuppressWarnings("ConstantConditions")
	public static MSGunPI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		var gun = Gun.getGun(data);
		if (gun == null) throw new IllegalArgumentException("gun does not exist: " + data);
		return new MSGunPI(owner, id, gun, expire, obtained);
	}
	// endregion
}
