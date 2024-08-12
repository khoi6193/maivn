package net.minevn.minigames;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;

public class VaultHandler implements Economy {
	private final String name = "MineVN";
	private Minigames main;
	private MySQL sql;

	public VaultHandler() {
		main = Minigames.getInstance();
		sql = MySQL.getInstance();
	}

	@Override
	public EconomyResponse bankBalance(String arg0) {
		return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not supported");
	}

	@Override
	public EconomyResponse bankDeposit(String arg0, double arg1) {
		return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not supported");
	}

	@Override
	public EconomyResponse bankHas(String arg0, double arg1) {
		return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not supported");
	}

	@Override
	public EconomyResponse bankWithdraw(String arg0, double arg1) {
		return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not supported");
	}

	@Override
	public EconomyResponse createBank(String arg0, String arg1) {
		return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not supported");
	}

	@Override
	public EconomyResponse createBank(String arg0, OfflinePlayer arg1) {
		return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not supported");
	}

	@Override
	public boolean createPlayerAccount(String arg0) {
		throw new UnsupportedOperationException("This method is deprecated");
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer arg0) {
		return true;
	}

	@Override
	public boolean createPlayerAccount(String arg0, String arg1) {
		throw new UnsupportedOperationException("This method is deprecated");
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer arg0, String arg1) {
		return true;
	}

	@Override
	public String currencyNamePlural() {
		return "MG";
	}

	@Override
	public String currencyNameSingular() {
		return "MG";
	}

	@Override
	public EconomyResponse deleteBank(String arg0) {
		return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not supported");
	}

	@Override
	public EconomyResponse depositPlayer(String arg0, double arg1) {
		return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not supported");
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer arg0, double arg1) {
		var uuid = arg0.getUniqueId();
		var data = PlayerData.getData(uuid);
		Utils.runNotSync(() -> {
			if (data != null) sql.addMoney(data, arg1);
			else sql.addMoney(uuid.toString(), arg1);
		});
		return new EconomyResponse(arg1, 0d, EconomyResponse.ResponseType.SUCCESS, "");
	}

	@Override
	public EconomyResponse depositPlayer(String arg0, String arg1, double arg2) {
		return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not supported");
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer arg0, String arg1, double arg2) {
		return depositPlayer(arg0, arg2);
	}

	@Override
	public String format(double arg0) {
		return "" + ((int) arg0);
	}

	@Override
	public int fractionalDigits() {
		return -1;
	}

	@Override
	public double getBalance(String arg0) {
		return 0;
	}

	@Override
	public double getBalance(OfflinePlayer arg0) {
		var uuid = arg0.getUniqueId();
		var data = PlayerData.getData(uuid);
		if (data != null) return data.getMoney();
		else return sql.getMoney(uuid.toString());
	}

	@Override
	public double getBalance(String arg0, String arg1) {
		return getBalance(arg0);
	}

	@Override
	public double getBalance(OfflinePlayer arg0, String arg1) {
		return getBalance(arg0);
	}

	@Override
	public List<String> getBanks() {
		return new ArrayList<>();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean has(String arg0, double arg1) {
		throw new UnsupportedOperationException("This method is deprecated");
	}

	@Override
	public boolean has(OfflinePlayer arg0, double arg1) {
		var uuid = arg0.getUniqueId();
		var data = PlayerData.getData(uuid);
		if (data != null) return data.getMoney() >= arg1;
		else return sql.getMoney(uuid.toString()) >= arg1;
	}

	@Override
	public boolean has(String arg0, String arg1, double arg2) {
		throw new UnsupportedOperationException("This method is deprecated");
	}

	@Override
	public boolean has(OfflinePlayer arg0, String arg1, double arg2) {
		return has(arg0, arg2);
	}

	@Override
	public boolean hasAccount(String arg0) {
		throw new UnsupportedOperationException("This method is deprecated");
	}

	@Override
	public boolean hasAccount(OfflinePlayer arg0) {
		return true;
	}

	@Override
	public boolean hasAccount(String arg0, String arg1) {
		throw new UnsupportedOperationException("This method is deprecated");
	}

	@Override
	public boolean hasAccount(OfflinePlayer arg0, String arg1) {
		throw new UnsupportedOperationException("This method is deprecated");
	}

	@Override
	public boolean hasBankSupport() {
		return false;
	}

	@Override
	public EconomyResponse isBankMember(String arg0, String arg1) {
		return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not supported");
	}

	@Override
	public EconomyResponse isBankMember(String arg0, OfflinePlayer arg1) {
		return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not supported");
	}

	@Override
	public EconomyResponse isBankOwner(String arg0, String arg1) {
		return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not supported");
	}

	@Override
	public EconomyResponse isBankOwner(String arg0, OfflinePlayer arg1) {
		return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not supported");
	}

	@Override
	public boolean isEnabled() {
		return main != null;
	}

	@Override
	public EconomyResponse withdrawPlayer(String arg0, double arg1) {
		return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not supported");
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer arg0, double arg1) {
		var uuid = arg0.getUniqueId();
		var data = PlayerData.getData(uuid);
		Utils.runNotSync(() -> {
			if (data != null) sql.addMoney(data, -arg1);
			else sql.addMoney(uuid.toString(), -arg1);
		});
		return new EconomyResponse(arg1, 0d, EconomyResponse.ResponseType.SUCCESS, "");
	}

	@Override
	public EconomyResponse withdrawPlayer(String arg0, String arg1, double arg2) {
		return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not supported");
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer arg0, String arg1, double arg2) {
		return withdrawPlayer(arg0, arg2);
	}

}
