
package model;


import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ; 

public class Return {
    
    private int returnId;
    private Date returnDate;
    private int orderId;
    private Order order;
    private List<ReturnItem> returnItems;

    public Return() {
        returnItems = new ArrayList<>();
    }

    public Return(int returnId, Date returnDate, int orderId) {
        this();
        this.returnId = returnId;
        this.returnDate = returnDate;
        this.orderId = orderId;
    }

    // Getters and Setters
    public int getReturnId() { return returnId; }
    public void setReturnId(int returnId) { this.returnId = returnId; }

    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public List<ReturnItem> getReturnItems() { return returnItems; }
    public void addReturnItem(ReturnItem item) { this.returnItems.add(item); }

    @Override
    public String toString() {
        return "Return{" +
                "returnId=" + returnId +
                ", returnDate=" + returnDate +
                ", orderId=" + orderId +
                '}';
    }   
}
