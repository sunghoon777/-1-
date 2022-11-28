package com.stepbystep.bossapp.DO;

public class Traffic {
    private String hubName;
    private double latitude;
    private double longitude;
    private int totalCount;
    private String region;
    private String percentageRankInRegion;
    private String percentageRankInTotalRegion;
    private double distance;

    public Traffic(String hubName, double latitude, double longitude, int totalCount, String region, String percentageRankInRegion, String percentageRankInTotalRegion) {
        this.hubName = hubName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.totalCount = totalCount;
        this.region = region;
        this.percentageRankInRegion = percentageRankInRegion;
        this.percentageRankInTotalRegion = percentageRankInTotalRegion;
    }

    public Traffic() {

    }

    public String getHubName() {
        return hubName;
    }

    public void setHubName(String hubName) {
        this.hubName = hubName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPercentageRankInRegion() {
        return percentageRankInRegion;
    }

    public void setPercentageRankInRegion(String percentageRankInRegion) {
        this.percentageRankInRegion = percentageRankInRegion;
    }

    public String getPercentageRankInTotalRegion() {
        return percentageRankInTotalRegion;
    }

    public void setPercentageRankInTotalRegion(String percentageRankInTotalRegion) {
        this.percentageRankInTotalRegion = percentageRankInTotalRegion;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
