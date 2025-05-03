
package model;

import java.util.Date ; 


public class OrderItem {
    private int orderId;
    private int itemId;
    private Date exitDate;
    private int quantity;
    private Item item;

    public OrderItem() {
    }

    public OrderItem(int orderId, int itemId, Date exitDate, int quantity) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.exitDate = exitDate;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public Date getExitDate() { return exitDate; }
    public void setExitDate(Date exitDate) { this.exitDate = exitDate; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderId=" + orderId +
                ", itemId=" + itemId +
                ", exitDate=" + exitDate +
                ", quantity=" + quantity +
                '}';
    }
}
