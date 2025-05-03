package dao;

import GUI.InventoryDB;
import models.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeDAO {
    // Authenticate employee
    public Employee authenticateEmployee(String username, String password) {
        String query = "SELECT e.*, " +
                       "pi.email AS personal_email, pi.phone AS personal_phone, " +
                       "oi.email AS official_email, oi.phone AS official_phone, " +
                       "oi.Office_number " +
                       "FROM Employee e " +
                       "LEFT JOIN EMP_CONTACT_INFO pi ON e.EMP_ID = pi.EMP_ID AND pi.Contact_type = 'Personal' " +
                       "LEFT JOIN EMP_CONTACT_INFO oi ON e.EMP_ID = oi.EMP_ID AND oi.Contact_type = 'Official' " +
                       "WHERE e.Username = ? AND e.Password = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = InventoryDB.getConnection() ;
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Employee employee = new Employee(
                    rs.getInt("EMP_ID"),
                    rs.getString("Username"),
                    rs.getString("Password"),
                    rs.getString("First_name"),
                    rs.getString("Middle_name"),
                    rs.getString("Last_name"),
                    rs.getString("Role"),
                    rs.getDouble("Share")
                );

                // Set contact information
                employee.setPersonalEmail(rs.getString("personal_email"));
                employee.setPersonalPhone(rs.getString("personal_phone"));
                employee.setOfficialEmail(rs.getString("official_email"));
                employee.setOfficialPhone(rs.getString("official_phone"));
                employee.setOfficeNumber(rs.getObject("Office_number") != null ? 
                                          rs.getInt("Office_number") : null);

                return employee;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
 
}
