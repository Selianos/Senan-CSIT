
package model;


import java.util.ArrayList;
import java.util.List ; 


public class Employee {
       
    private int empId; 
    private String username; 
    private String password ;
    private String firstName ;
    private String middleName ;
    private String lastName ; 
    private String role ; 
    private double share ; 
    private List<ContactInfo> contactInfo ;
    private List<Item> managedItems ;
    
    
    public Employee() {    
        contactInfo = new ArrayList<>() ; 
        managedItems = new ArrayList<>() ;
   
    }

    public Employee(String username, String password, String firstName, String middleName, String lastName, String role, double share) {
        this() ; 
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.role = role;
        this.share = share;
    }

    // getters and setters 

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getShare() {
        return share;
    }

    public void setShare(double share) {
        this.share = share;
    }
    
    public List<ContactInfo> getContactInfo(){
        return contactInfo ;    
    }
    
    public void addContactInfo(ContactInfo info){
        this.contactInfo.add(info) ; 
    }
    
    public List<Item> getManagedItems() { return managedItems; }
    public void addManagedItem(Item item) { this.managedItems.add(item); }

    public String getFullName() {
        StringBuilder name = new StringBuilder(firstName);
        if (middleName != null && !middleName.isEmpty()) {
            name.append(" ").append(middleName);
        }
        name.append(" ").append(lastName);
        return name.toString();
    }
    
    public boolean isAdmin(){
        return "Admin".equalsIgnoreCase(role) ; 
    }
    
    @Override
    public String toString() {
        return "Employee{" +
                "empId=" + empId +
                ", username='" + username + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
   
}
