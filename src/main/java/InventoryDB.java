import java.sql.*;

public class InventoryDB {
    private Connection connection;

    public InventoryDB(String dbUrl, String dbUser, String dbPassword) throws SQLException {
        this.connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    // Allow subclasses to override how SELECT results are handled
    protected void handleResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columns = meta.getColumnCount();

        // Print column headers
        for (int i = 1; i <= columns; i++) {
            System.out.print(meta.getColumnName(i) + "\t");
        }
        System.out.println();

        // Print row data
        while (rs.next()) {
            for (int i = 1; i <= columns; i++) {
                System.out.print(rs.getString(i) + "\t");
            }
            System.out.println();
        }
    }

    // General SELECT query
    public void executeQuery(String query) {
        try (
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)
        ) {
            handleResultSet(rs);
        } catch (SQLException e) {
            System.err.println("Query execution error:");
            e.printStackTrace();
        }
    }

    // General INSERT, UPDATE, DELETE query
    public int executeUpdate(String query) {
        try (Statement stmt = connection.createStatement()) {
            return stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Update error:");
            e.printStackTrace();
            return -1;
        }
    }

    // For parameterized queries (PreparedStatement)
    public int executePreparedUpdate(String query, Object... params) {
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Prepared update error:");
            e.printStackTrace();
            return -1;
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
