
package model;


public class ContactInfo {
    private int id ; 
    private int ownerId ;
    private String type ;
    private String email ; 
    private String phone ;
    private Integer officeNumber ; 
    
    
    public ContactInfo(){
        
    }
    
    public ContactInfo(int id, int ownerId, String type, String email, String phone, Integer officeNumber) {
        this.id = id;
        this.ownerId = ownerId;
        this.type = type;
        this.email = email;
        this.phone = phone;
        this.officeNumber = officeNumber;
    } 
    
     // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Integer getOfficeNumber() { return officeNumber; }
    public void setOfficeNumber(Integer officeNumber) { this.officeNumber = officeNumber; }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (email != null) sb.append("Email: ").append(email).append(" ");
        if (phone != null) sb.append("Phone: ").append(phone).append(" ");
        if (officeNumber != null) sb.append("Office: ").append(officeNumber);
        return sb.toString().trim();
    }
    
    
}
