

package project.oop2project;


import dao.DatabaseConnection;
import dao.EmployeeDAO;
import model.ContactInfo;
import model.Employee;
import java.util.List ; 

public class OOP2Project {

    public static void main(String[] args) {
        
        
        // Ensure connection is established (or attempt it)
        // The DAO constructor will call DatabaseConnection.getConnection()
        if (DatabaseConnection.getConnection() == null) {
            System.err.println("Failed to get initial database connection. Exiting.");
            return;
        }

        EmployeeDAO employeeDAO = new EmployeeDAO();

        System.out.println("--- Testing Employee DAO ---");

        // 1. Add a new Employee
        System.out.println("\n1. Testing addEmployee...");
        Employee newEmp = new Employee();
        newEmp.setUsername("testuser");
        newEmp.setPassword("password123"); // Store hashed passwords in real apps!
        newEmp.setFirstName("Test");
        newEmp.setLastName("User");
        newEmp.setRole("Tester");
        newEmp.setShare(0.05);

        ContactInfo workContact = new ContactInfo();
        workContact.setType("Work");
        workContact.setEmail("test.user@company.com");
        workContact.setPhone("555-0101");
        workContact.setOfficeNumber(303);
        newEmp.addContactInfo(workContact);

        ContactInfo personalContact = new ContactInfo();
        personalContact.setType("Personal");
        personalContact.setEmail("tester@email.com");
        newEmp.addContactInfo(personalContact); // Phone and office null

        boolean added = employeeDAO.addEmployee(newEmp);
        System.out.println("Employee added successfully: " + added);

        // 2. Get Employee by ID
        System.out.println("\n2. Testing getEmployeeById (ID: 101)...");
        Employee fetchedEmp = employeeDAO.getEmployeeById(1000);
        if (fetchedEmp != null) {
            System.out.println("Fetched Employee: " + fetchedEmp);
            System.out.println("Contact Info Count: " + fetchedEmp.getContactInfo().size());
            for (ContactInfo ci : fetchedEmp.getContactInfo()) {
                System.out.println("  - " + ci);
            }
        } else {
            System.out.println("Employee with ID 101 not found.");
        }

        System.out.println("\n--- Testing Complete ---");

        // Close the database connection when done
        DatabaseConnection.closeConnection();
    }
}

