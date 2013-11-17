package com.codisimus.plugins.econ;

import java.io.Serializable;
import java.sql.SQLException;
import net.prospectmc.QueryBuilder;
import net.prospectmc.QueryBuilder.Clause;
import org.bukkit.Bukkit;

/**
 * @author Codisimus
 */
public class Account implements Serializable {
    private static final String TABLE = "Wallets";
    private static final String COLUMN = "amount";
    private static final String[] WHERE_COLUMN = {"username", "currency"};
    private static final String CURRENCY = "GP";
    private String name;
    private int pocket = 0;

    public Account(String name) {
        this.name = name;
        sync();
    }

    public final void sync() {
        try {
            pocket = QueryBuilder.select(TABLE, COLUMN, getWhereClause()).getInt();
        } catch (SQLException ex) {
            QueryBuilder.insert(TABLE, WHERE_COLUMN, new String[] {name, CURRENCY}).async();
        }
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return pocket;
    }

    public void give(int amount) {
        QueryBuilder.increment(TABLE, COLUMN, amount, getWhereClause()).async();
        pocket += amount;
        updateWallet();
    }

    public boolean take(int amount) {
        if (QueryBuilder.decrement(TABLE, COLUMN, amount, getWhereClause()).getSuccess()) {
            pocket -= amount;
            updateWallet();
            return true;
        } else {
            return false;
        }
    }

    public void updateWallet() {
        Wallet.updateWallet(Bukkit.getPlayerExact(name));
    }

    private String getWhereClause() {
        return Clause.where(WHERE_COLUMN, new String[] {name, CURRENCY});
    }
}
