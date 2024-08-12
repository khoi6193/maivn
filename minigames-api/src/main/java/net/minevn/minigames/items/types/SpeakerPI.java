package net.minevn.minigames.items.types;

import net.minevn.minigames.PlayerData;
import net.minevn.minigames.items.StackablePI;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class SpeakerPI extends StackablePI {

	public SpeakerPI(PlayerData owner, int id, long obtained, int amount) {
		super("thanh hoang qua ngu", owner, id, Material.PAPER, (short) 0, 0, obtained, amount);
	}

	@Override
	public String getData() {
		return "";
	}

	@Override
	public List<String> getDescription() {
		return null;
	}

	@Override
	public void onClick(InventoryClickEvent e) {
	}

	public SpeakerPI clone() {
		return new SpeakerPI(owner, id, obtained, amount);
	}

	// static
	public static SpeakerPI fromData(
			PlayerData owner,
			String data,
			int id, long expire, long obtained, boolean isUsing, int amount) {
		return new SpeakerPI(owner, id, obtained, amount);
	}
}
