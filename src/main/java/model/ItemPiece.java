
package model;

import java.util.Date ;

public class ItemPiece {
     private int itemId;
    private String serialNumber;
    private boolean returnState;
    private Date expiryDate;

    public ItemPiece() {
    }

    public ItemPiece(int itemId, String serialNumber, boolean returnState, Date expiryDate) {
        this.itemId = itemId;
        this.serialNumber = serialNumber;
        this.returnState = returnState;
        this.expiryDate = expiryDate;
    }

    // Getters and Setters
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public boolean isReturnState() { return returnState; }
    public void setReturnState(boolean returnState) { this.returnState = returnState; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    @Override
    public String toString() {
        return "ItemPiece{" +
                "itemId=" + itemId +
                ", serialNumber='" + serialNumber + '\'' +
                ", returnState=" + returnState +
                ", expiryDate=" + expiryDate +
                '}';
    }
}
