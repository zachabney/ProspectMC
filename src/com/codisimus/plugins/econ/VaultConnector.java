package com.codisimus.plugins.econ;

import com.codisimus.plugins.banker.Bank;
import com.codisimus.plugins.banker.Banker;
import java.util.List;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

/**
 * Provides the vault interface, so that the economy adapter in vault does not need to be changed.
 *
 * @author jast (modified by Codisimus for personal use)
 *
 */
public class VaultConnector implements Economy {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "Econ";
    }

    @Override
    public boolean hasBankSupport() {
        return true;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double amount) {
        return "§6" + getInt(amount) + currencyNamePlural();
    }

    @Override
    public String currencyNamePlural(){
        return "gp";
    }

    @Override
    public String currencyNameSingular(){
        return "gp";
    }

    @Override
    public boolean hasAccount(String playerName) {
        return createPlayerAccount(playerName);
    }

    @Override
    public double getBalance(String playerName) {
        return Econ.getAccount(playerName).getBalance();
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= getInt(amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        Account account = Econ.getAccount(playerName);
        return account.take(getInt(amount))
               ? new EconomyResponse(getInt(amount), account.getBalance(), ResponseType.SUCCESS, null)
               : new EconomyResponse(0, account.getBalance(), ResponseType.FAILURE, "§4Insufficient funds!");
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        Account account = Econ.getAccount(playerName);
        account.give(getInt(amount));
        return new EconomyResponse(getInt(amount), account.getBalance(), ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "§4Each Player has their own bank account");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "§4Each Player has their own bank account");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        int balance = Banker.getBank(name).balance();
        return new EconomyResponse(balance, balance, ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        int balance = Banker.getBank(name).balance();
        ResponseType response = balance >= getInt(amount)
                                ? ResponseType.SUCCESS
                                : ResponseType.FAILURE;
        return new EconomyResponse(balance, balance, response, null);
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        Bank account = Banker.getBank(name);
        return account.takeMoney(getInt(amount))
               ? new EconomyResponse(getInt(amount), account.balance(), ResponseType.SUCCESS, null)
               : new EconomyResponse(0, Econ.getAccount(name).getBalance(), ResponseType.FAILURE, "§4Insufficient funds!");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        Bank account = Banker.getBank(name);
        account.giveMoney(getInt(amount));
        return new EconomyResponse(getInt(amount), account.balance(), ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return name.equals(playerName)
               ? new EconomyResponse(0, 0, ResponseType.SUCCESS, null)
               : new EconomyResponse(0, 0, ResponseType.FAILURE, "§4Each Player has their own bank account");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return name.equals(playerName)
               ? new EconomyResponse(0, 0, ResponseType.SUCCESS, null)
               : new EconomyResponse(0, 0, ResponseType.FAILURE, "§4Each Player has their own bank account");
    }

    @Override
    public List<String> getBanks() {
        return Econ.getAccountNames();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        Econ.getAccount(playerName);
        return true;
    }


//    @Override
    public boolean createPlayerAccount(String playerName, String world) {
        return createPlayerAccount(playerName);
    }


//    @Override
    public EconomyResponse depositPlayer(String player, String world, double amount) {
        return depositPlayer(player, amount);
    }


//    @Override
    public double getBalance(String player, String world) {
        return getBalance(player);
    }


//    @Override
    public boolean has(String player, String world, double amount) {
        return has(player, amount);
    }


//    @Override
    public boolean hasAccount(String player, String world) {
        return hasAccount(player);
    }


//    @Override
    public EconomyResponse withdrawPlayer(String player, String world, double amount) {
        return withdrawPlayer(player, amount);
    }

    private int getInt(double amount) {
        return (int) amount;
    }
}
