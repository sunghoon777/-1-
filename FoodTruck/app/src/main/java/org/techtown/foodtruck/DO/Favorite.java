package org.techtown.foodtruck.DO;

public class Favorite {

    private String truckName;
    private String truckId;

    public Favorite() {
    }

    public Favorite(String truckName, String truckId) {
        this.truckName = truckName;
        this.truckId = truckId;
    }

    public String getTruckName() {
        return truckName;
    }

    public void setTruckName(String truckName) {
        this.truckName = truckName;
    }

    public String getTruckId() {
        return truckId;
    }

    public void setTruckId(String truckId) {
        this.truckId = truckId;
    }
}
