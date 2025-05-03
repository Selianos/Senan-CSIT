
package model;

import java.util.ArrayList ;
import java.util.List ; 


public class Item {

     private int itemId;
    private String itemName;
    private String itemQr;
    private String manufacturer;
    private String category;
    private int stockQuantity;
    private double unitPrice;
    private List<ItemPiece> pieces;
    private List<Supplier> suppliers;

    public Item() {
        pieces = new ArrayList<>();
        suppliers = new ArrayList<>();
    }

    public Item(int itemId, String itemName, String itemQr, String manufacturer, 
                String category, int stockQuantity, double unitPrice) {
        this();
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemQr = itemQr;
        this.manufacturer = manufacturer;
        this.category = category;
        this.stockQuantity = stockQuantity;
        this.unitPrice = unitPrice;
    }

    // Getters and Setters
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemQr() { return itemQr; }
    public void setItemQr(String itemQr) { this.itemQr = itemQr; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public List<ItemPiece> getPieces() { return pieces; }
    public void addPiece(ItemPiece piece) { this.pieces.add(piece); }

    public List<Supplier> getSuppliers() { return suppliers; }
    public void addSupplier(Supplier supplier) { this.suppliers.add(supplier); } 

    @Override
    public String toString(){
        return "Item{" +
                "itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", category='" + category + '\'' +
                ", stockQuantity=" + stockQuantity +
                ", unitPrice=" + unitPrice +
                '}';}
  
}
