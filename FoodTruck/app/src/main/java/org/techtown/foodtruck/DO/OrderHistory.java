package org.techtown.foodtruck.DO;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderHistory implements Serializable {

    private String orderHistoryId;
    private String truckName;
    private ArrayList<Order> orders;
    private String date;
    private String image;
    private String truckId;
    private String userId;
    private String orderState;

    public OrderHistory() {
    }

    public OrderHistory(String orderHistoryId, String truckName, ArrayList<Order> orders, String date, String image, String truckId,String userId,String orderState) {
        this.orderHistoryId = orderHistoryId;
        this.truckName = truckName;
        this.orders = orders;
        this.date = date;
        this.image = image;
        this.truckId = truckId;
        this.userId = userId;
        this.orderState = orderState;
    }

    public String getOrderHistoryId() {
        return orderHistoryId;
    }

    public void setOrderHistoryId(String orderHistoryId) {
        this.orderHistoryId = orderHistoryId;
    }

    public String getTruckName() {
        return truckName;
    }

    public void setTruckName(String truckName) {
        this.truckName = truckName;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTruckId() {
        return truckId;
    }

    public void setTruckId(String truckId) {
        this.truckId = truckId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }
}
