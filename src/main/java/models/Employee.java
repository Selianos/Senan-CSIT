
package models;

import java.util.Objects;

public class Employee {
    // Main Employee Details
    private int empId;
    private String username;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    private String role;
    private double share;

    // Contact Information
    private String personalEmail;
    private String officialEmail;
    private String personalPhone;
    private String officialPhone;
    private Integer officeNumber;

    // Constructors
    public Employee() {}

    // Full constructor
    public Employee(int empId, String username, String password, 
                   String firstName, String middleName, String lastName, 
                   String role, double share) {
        this.empId = empId;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.role = role;
        this.share = share;
    }

    // Getters and Setters
    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public double getShare() { return share; }
    public void setShare(double share) { this.share = share; }

    // Contact Information Getters and Setters
    public String getPersonalEmail() { return personalEmail; }
    public void setPersonalEmail(String personalEmail) { this.personalEmail = personalEmail; }

    public String getOfficialEmail() { return officialEmail; }
    public void setOfficialEmail(String officialEmail) { this.officialEmail = officialEmail; }

    public String getPersonalPhone() { return personalPhone; }
    public void setPersonalPhone(String personalPhone) { this.personalPhone = personalPhone; }

    public String getOfficialPhone() { return officialPhone; }
    public void setOfficialPhone(String officialPhone) { this.officialPhone = officialPhone; }

    public Integer getOfficeNumber() { return officeNumber; }
    public void setOfficeNumber(Integer officeNumber) { this.officeNumber = officeNumber; }

    // Role Check Methods
    public boolean isAdmin() {
        return "Admin".equalsIgnoreCase(role);
    }

    public boolean isStaff() {
        return "Staff".equalsIgnoreCase(role);
    }

    // Password Validation
    public boolean validatePassword(String inputPassword) {
        return Objects.equals(this.password, inputPassword);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "empId=" + empId +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
