package org.techtown.foodtruck.DO;

public class Order {

    private Truck truck;
    private String food_name;
    private String food_cost;
    private int food_number;

    public Order() {
    }

    public Order(Truck truck, String food_name, String food_cost, int food_number) {
        this.truck = truck;
        this.food_name = food_name;
        this.food_cost = food_cost;
        this.food_number = food_number;
    }

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) {
        this.truck = truck;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getFood_cost() {
        return food_cost;
    }

    public void setFood_cost(String food_cost) {
        this.food_cost = food_cost;
    }

    public int getFood_number() {
        return food_number;
    }

    public void setFood_number(int food_number) {
        this.food_number = food_number;
    }


}
