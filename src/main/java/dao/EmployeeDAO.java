
package dao;

import model.ContactInfo ;
import model.Employee ;



import java.sql.* ; 
import java.util.ArrayList ;
import java.util.List ;


public class EmployeeDAO {
    
    private Connection connection ;
    
    public EmployeeDAO(){
        this.connection = DatabaseConnection.getConnection() ; 
    }
    
    public Employee authenticate(String username , String password){
        String sql = "SELECT * FROM Employee WHERE Username = ? AND Password = ?" ;
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, username);
            stmt.setString(2,password) ;
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                Employee employee = extractEmployeeFromResultSet(rs);
                loadEmployeeContactInfo(employee);
                return employee ; 
            }
            
        }catch(SQLException e){
                e.printStackTrace();
        }
        
        return null ;
    }
    
     public Employee getEmployeeById(int empId) {
        String sql = "SELECT * FROM Employee WHERE EMP_ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, empId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Employee employee = extractEmployeeFromResultSet(rs);
                loadEmployeeContactInfo(employee);
                return employee;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM Employee";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Employee employee = extractEmployeeFromResultSet(rs);
                loadEmployeeContactInfo(employee);
                employees.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    public boolean addEmployee(Employee employee) {
        String sql = "INSERT INTO Employee (Username, Password, First_name, Middle_name, Last_name, Role, Share) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, employee.getUsername());
            stmt.setString(2, employee.getPassword());
            stmt.setString(3, employee.getFirstName());
            stmt.setString(4, employee.getMiddleName());
            stmt.setString(5, employee.getLastName());
            stmt.setString(6, employee.getRole());
            stmt.setDouble(7, employee.getShare());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                
                try(ResultSet generatedKeys = stmt.getGeneratedKeys()){
                    if(generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        employee.setEmpId(generatedId);
                    }
                }
                // Save contact info
                for (ContactInfo info : employee.getContactInfo()) {
                    addEmployeeContactInfo(employee.getEmpId(), info);
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE Employee SET Username = ?, Password = ?, First_name = ?, " +
                     "Middle_name = ?, Last_name = ?, Role = ?, Share = ? WHERE EMP_ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, employee.getUsername());
            stmt.setString(2, employee.getPassword());
            stmt.setString(3, employee.getFirstName());
            stmt.setString(4, employee.getMiddleName());
            stmt.setString(5, employee.getLastName());
            stmt.setString(6, employee.getRole());
            stmt.setDouble(7, employee.getShare());
            stmt.setInt(8, employee.getEmpId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Update contact info
                // First delete existing
                deleteEmployeeContactInfo(employee.getEmpId());
                // Then add new ones
                for (ContactInfo info : employee.getContactInfo()) {
                    addEmployeeContactInfo(employee.getEmpId(), info);
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteEmployee(int empId) {
        // First delete related records
        deleteEmployeeContactInfo(empId);
        
        // Then delete employee
        String sql = "DELETE FROM Employee WHERE EMP_ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, empId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void loadEmployeeContactInfo(Employee employee) {
        String sql = "SELECT * FROM EMP_CONTACT_INFO WHERE EMP_ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, employee.getEmpId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ContactInfo info = new ContactInfo();
                info.setOwnerId(rs.getInt("EMP_ID"));
                info.setType(rs.getString("Contact_type"));
                info.setEmail(rs.getString("email"));
                info.setPhone(rs.getString("phone"));
                info.setOfficeNumber(rs.getInt("Office_number"));
                employee.addContactInfo(info);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean addEmployeeContactInfo(int empId, ContactInfo info) {
        String sql = "INSERT INTO EMP_CONTACT_INFO (EMP_ID, Contact_type, email, phone, Office_number) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, empId);
            stmt.setString(2, info.getType());
            stmt.setString(3, info.getEmail());
            stmt.setString(4, info.getPhone());
            if (info.getOfficeNumber() != null) {
                stmt.setInt(5, info.getOfficeNumber());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean deleteEmployeeContactInfo(int empId) {
        String sql = "DELETE FROM EMP_CONTACT_INFO WHERE EMP_ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, empId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Employee extractEmployeeFromResultSet(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmpId(rs.getInt("EMP_ID"));
        employee.setUsername(rs.getString("Username"));
        employee.setPassword(rs.getString("Password"));
        employee.setFirstName(rs.getString("First_name"));
        employee.setMiddleName(rs.getString("Middle_name"));
        employee.setLastName(rs.getString("Last_name"));
        employee.setRole(rs.getString("Role"));
        employee.setShare(rs.getDouble("Share"));
        return employee;
    }
}
