package net.prospectmc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A Query is a String with methods to execute it as a MySQL query
 *
 * @author Cody
 */
public class Query {
    private String query;

    /**
     * Creates a new Query of the given String
     *
     * @param query The full query statement
     */
    public Query(String query) {
        //System.out.print(query);
        this.query = query;
    }

    /**
     * Executes the query asynchronously
     * This should be used whenever you do not care about the return value
     */
    public void async() {
        DatabaseAPI.asyncQuery(query);
    }

    /**
     * Executes the SELECT query
     *
     * @return The result of the query
     */
    public ResultSet executeSelect() {
        return DatabaseAPI.select(query);
    }

    /**
     * Executes the UPDATE/INSERT/DELETE query
     *
     * @return The result of the query
     */
    public int executeUpdate() {
        return DatabaseAPI.update(query);
    }

    /**
     * Executes the UPDATE/INSERT/DELETE query
     *
     * @return The number of rows affected
     */
    public int getRowCount() {
        return executeUpdate();
    }

    /**
     * Executes the UPDATE/INSERT/DELETE query
     *
     * @return true if the query successfully modified at least 1 row
     */
    public boolean getSuccess() {
        return getRowCount() > 0;
    }

    /**
     * Executes the SELECT query
     *
     * @throws SQLException If the query produces invalid results
     * @return the single String that results
     */
    public String getString() throws SQLException {
        ResultSet result = executeSelect();
        result.first();
        return result.getString(1);
    }

    /**
     * Executes the SELECT query
     *
     * @throws SQLException If the query produces invalid results
     * @return the array of Strings that result
     */
    public ArrayList<String> getStringArray() throws SQLException {
        ResultSet result = executeSelect();
        ArrayList<String> list = new ArrayList<String>();
        while (result.next()) {
            list.add(result.getString(1));
        }
        return list;
    }

    /**
     * Executes the SELECT query
     *
     * @throws SQLException If the query produces invalid results
     * @return the single integer that results
     */
    public int getInt() throws SQLException {
        ResultSet result = executeSelect();
        result.first();
        return result.getInt(1);
    }

    /**
     * Executes the SELECT query
     *
     * @throws SQLException If the query produces invalid results
     * @return the single double that results
     */
    public double getDouble() throws SQLException {
        ResultSet result = executeSelect();
        result.first();
        return result.getDouble(1);
    }

    /**
     * Executes the SELECT query
     *
     * @throws SQLException If the query produces invalid results
     * @return the single boolean that results
     */
    public boolean getBoolean() throws SQLException {
        ResultSet result = executeSelect();
        result.first();
        return result.getBoolean(1);
    }
}
