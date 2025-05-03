package model;


import java.util.ArrayList ;
import java.util.List ;
 
public class Supplier {
    
    private int supplierId;
    private String supplierName;
    private List<ContactInfo> contactInfo;
    private List<Item> suppliedItems;

    public Supplier() {
        contactInfo = new ArrayList<>();
        suppliedItems = new ArrayList<>();
    }

    public Supplier(int supplierId, String supplierName) {
        this();
        this.supplierId = supplierId;
        this.supplierName = supplierName;
    }

    // Getters and Setters
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public List<ContactInfo> getContactInfo() { return contactInfo; }
    public void addContactInfo(ContactInfo info) { this.contactInfo.add(info); }

    public List<Item> getSuppliedItems() { return suppliedItems; }
    public void addSuppliedItem(Item item) { this.suppliedItems.add(item); }

    @Override
    public String toString() {
        return "Supplier{" +
                "supplierId=" + supplierId +
                ", supplierName='" + supplierName + '\'' +
                '}';
    }
 
}
