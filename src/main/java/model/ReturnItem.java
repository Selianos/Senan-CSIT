
package model;

public class ReturnItem {
  private int returnId;
    private int itemId;
    private int quantity;
    private Item item;

    public ReturnItem() {
    }

    public ReturnItem(int returnId, int itemId, int quantity) {
        this.returnId = returnId;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getReturnId() { return returnId; }
    public void setReturnId(int returnId) { this.returnId = returnId; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    @Override
    public String toString() {
        return "ReturnItem{" +
                "returnId=" + returnId +
                ", itemId=" + itemId +
                ", quantity=" + quantity +
                '}';
    }   
}
