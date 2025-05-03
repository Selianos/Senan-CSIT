package GUI;

import java.sql.*;

public class MariaDBTest {
    public static void main(String[] args) {
        // Database connection details
        String url = "jdbc:mysql://localhost:3306/inventory_management";
        String user = "root";
        String password = "0550109105Aa";

        // SQL query
        String query = "SELECT * FROM Employee";

        try (
                Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)
        ) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            System.out.println("Employee Table:");

            // Print column names
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println();

            // Print rows
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }

        } catch (SQLException e) {
            System.err.println("Database error:");
            e.printStackTrace();
        }
    }
}
