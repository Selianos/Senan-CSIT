
package model;

import java.util.ArrayList ;
import java.util.Date ; 
import java.util.List; 

public class Order {
     private int orderId;
    private Date orderDate;
    private double totalPrice;
    private int empId;
    private Employee employee;
    private List<OrderItem> orderItems;
    private List<Return> returns;

    public Order() {
        orderItems = new ArrayList<>();
        returns = new ArrayList<>();
    }

    public Order(int orderId, Date orderDate, double totalPrice, int empId) {
        this();
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.empId = empId;
    }

    // Getters and Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void addOrderItem(OrderItem item) { 
        this.orderItems.add(item);
        // Update total price
        this.totalPrice += item.getItem().getUnitPrice() * item.getQuantity();
    }

    public List<Return> getReturns() { return returns; }
    public void addReturn(Return returnObj) { this.returns.add(returnObj); }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", orderDate=" + orderDate +
                ", totalPrice=" + totalPrice +
                ", empId=" + empId +
                '}';
    }
}
