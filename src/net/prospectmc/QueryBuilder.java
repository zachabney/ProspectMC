package net.prospectmc;

/**
 * Builds Simple UPDATE/INSERT Queries
 *
 * @author Cody
 */
public class QueryBuilder {
    /**
     * Creates a new UPDATE query to increment a value by 1
     *
     * @param table The name of the table to be modified
     * @param column The column to be incremented
     * @param where The WHERE condition (i.e. id = 7 AND name = Bob)
     * @return The new Query object to be executed
     */
    public static Query increment(String table, String column, String where) {
        return increment(table, column, 1, where);
    }

    /**
     * Creates a new UPDATE query to increment a value by a specified amount
     *
     * @param table The name of the table to be modified
     * @param column The column to be incremented
     * @param value The amount to increment by
     * @param where The WHERE condition (i.e. id = 7 AND name = Bob)
     * @return The new Query object to be executed
     */
    public static Query increment(String table, String column, int value, String where) {
        return update(table, column, column + " + '" + value + "'", where);
    }

    /**
     * Creates a new UPDATE query to decrement a value by 1
     *
     * @param table The name of the table to be modified
     * @param column The column to be decremented
     * @param where The WHERE condition (i.e. id = 7 AND name = Bob)
     * @return The new Query object to be executed
     */
    public static Query decrement(String table, String column, String where) {
        return increment(table, column, 1, where);
    }

    /**
     * Creates a new UPDATE query to decrement a value by a specified amount
     *
     * @param table The name of the table to be modified
     * @param column The column to be decremented
     * @param value The amount to decrement by
     * @param where The WHERE condition (i.e. id = 7 AND name = Bob)
     * @return The new Query object to be executed
     */
    public static Query decrement(String table, String column, int value, String where) {
        return update(table, column, column + " - '" + value + "'", where);
    }

    /**
     * Creates a new INSERT query
     *
     * @param table The name of the table to be modified
     * @param column The column to be set
     * @param value The value of the column
     * @return The new Query object to be executed
     */
    public static Query insert(String table, String column, String value) {
        return insert(table, new String[] {column}, new String[] {value});
    }

    /**
     * Creates a new INSERT query
     *
     * @param table The name of the table to be modified
     * @param values The values to set the columns to
     * @return The new Query object to be executed
     */
    public static Query insert(String table, String[] values) {
        return insert(table, null, values);
    }

    /**
     * Creates a new INSERT query
     *
     * @param table The name of the table to be modified
     * @param columns The columns to be set
     * @param values The values to set the columns to
     * @return The new Query object to be executed
     */
    public static Query insert(String table, String[] columns, String[] values) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(table);
        if (columns != null) {
            sb.append('(');
            for (String column : columns) {
                if (!column.equals("*")) {
                    column = "`" + column + "`";
                }
                sb.append(column);
                sb.append(",");
            }
            sb.setLength(sb.length() - 1);
            sb.append(')');
        }
        sb.append(" VALUES(");
        for (String value : values) {
            sb.append("'");
            sb.append(value);
            sb.append("',");
        }
        sb.setLength(sb.length() - 1);
        sb.append(");");
        return new Query(sb.toString());
    }

    /**
     * Creates a new UPDATE query
     *
     * @param table The name of the table to be modified
     * @param column The column to be updated
     * @param value The new value
     * @param where The WHERE condition (i.e. id = 7 AND name = Bob)
     * @return The new Query object to be executed
     */
    public static Query update(String table, String column, String value, String where) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ");
        sb.append(table);
        sb.append(" SET ");
        if (!column.equals("*")) {
            column = "`" + column + "`";
        }
        sb.append(column);
        sb.append('=');
        if (value.contains("'")) {
            sb.append(value);
        } else {
            sb.append("'");
            sb.append(value);
            sb.append("'");
        }
        sb.append(" WHERE ");
        sb.append(where);
        sb.append(';');
        return new Query(sb.toString());
    }

    /**
     * Creates a new DELETE query
     *
     * @param table The name of the table to be modified
     * @param where The WHERE condition (i.e. id = 7 AND name = Bob)
     * @return The new Query object to be executed
     */
    public static Query delete(String table, String where) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");
        sb.append(table);
        sb.append(" WHERE ");
        sb.append(where);
        sb.append(';');
        return new Query(sb.toString());
    }

    /**
     * Creates a new SELECT query
     *
     * @param table The name of the table to be selected
     * @param column The name of the column to be selected
     * @return The new Query object to be executed
     */
    public static Query select(String table, String column) {
        return select(table, column, null);
    }

    /**
     * Creates a new SELECT query
     *
     * @param table The name of the table to be selected
     * @param column The name of the column to be selected
     * @param where The WHERE condition (i.e. id = 7 AND name = Bob)
     * @return The new Query object to be executed
     */
    public static Query select(String table, String column, String where) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        if (!column.equals("*")) {
            column = "`" + column + "`";
        }
        sb.append(column);
        sb.append(" FROM ");
        sb.append(table);
        if (where != null) {
            sb.append(" WHERE ");
            sb.append(where);
        }
        sb.append(';');
        return new Query(sb.toString());
    }

    /**
     * A Clause may be easily built with the given methods
     */
    public static class Clause {
        /**
         * Builds a WHERE clause for the specified column/value
         *
         * @param column The column to check for a value
         * @param value The value which column must equal
         * @return The clause that was built
         */
        public static String where(String column, String value) {
            return column + "='" + value + "'";
        }

        /**
         * Builds a WHERE clause for the specified columns/values
         *
         * @param columnOne The first column to check for a value
         * @param valueOne The value which columnOne must equal
         * @param columnTwo The second column to check for a value
         * @param valueTwo The value which columnTwo must equal
         * @return The clause that was built
         */
        public static String where(String columnOne, String valueOne, String columnTwo, String valueTwo) {
            return columnOne + "='" + valueOne + "' AND " + columnTwo + "='" + valueTwo + "'";
        }

        /**
         * Builds a WHERE clause for the specified columns/values
         *
         * @param columns The columns to be set
         * @param values The values to set the columns to
         * @return The clause that was built
         */
        public static String where(String[] columns, String[] values) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < columns.length; i++) {
                sb.append(where(columns[i], values[i]));
                if (i + 1 < columns.length) {
                    sb.append(" AND ");
                }
            }
            return sb.toString();
        }
    }
}
