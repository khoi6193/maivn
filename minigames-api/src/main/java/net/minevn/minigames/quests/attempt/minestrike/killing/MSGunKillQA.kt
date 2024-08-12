package net.minevn.minigames.quests.attempt.minestrike.killing

import net.minefs.MineStrike.Guns.Ammo
import net.minefs.MineStrike.Guns.Gun
import net.minefs.MineStrike.Modes.Game
import net.minevn.minigames.PlayerData
import org.bukkit.entity.Player

class MSGunKillQA(
	player: PlayerData, victim: Player, arena: Game, val gun: Gun, val ammo: Ammo, val headShot: Boolean,
	val blockBang: Boolean
) : MSKillQA(player, victim, arena)