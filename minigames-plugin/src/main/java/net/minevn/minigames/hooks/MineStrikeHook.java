package net.minevn.minigames.hooks;

import com.google.common.collect.ImmutableList;
import net.minefs.MineStrike.Cache.LevelCache;
import net.minefs.MineStrike.Cache.PlayerStatus;
import net.minefs.MineStrike.Main;
import net.minefs.MineStrike.Modes.Game;
import net.minevn.minigames.Minigames;
import net.minevn.minigames.items.types.MSGunPI;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MineStrikeHook {
	private final Main ms;
	private final Minigames main;
	private List<MSGunPI> defaultGuns;

	public MineStrikeHook() {
		main = Minigames.getInstance();
		main.getLogger().info("Hooking MineStrike...");
		ms = (Main) main.getServer().getPluginManager().getPlugin("MineStrike");
		Objects.requireNonNull(ms);
	}

	public List<MSGunPI> getDefaultGuns() {
		if (defaultGuns == null) {
			defaultGuns = ImmutableList.copyOf(ms.getGuns())
				.stream()
				.filter(x -> !x.isVip())
				.map(x -> new MSGunPI(null, -68, x, 0, 0)) // -68: flag không save item
				.collect(Collectors.toList());
		}
		return defaultGuns;
	}

	public Game getGame() {
		return ms.getManager().getGames().get(0);
	}

	public PlayerStatus getStatus(Player player) {
		return getGame().getStats().get(player.getUniqueId());
	}

	public LevelCache getInfo(Player player) {
		return ms.getLevelCache().get(player.getName());
	}
}
