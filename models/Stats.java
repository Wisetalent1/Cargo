package com.nicargo.app.models;

import com.google.gson.annotations.SerializedName;

public class Stats {
    @SerializedName("total_shipments")
    private int totalShipments;
    
    @SerializedName("total_revenue")
    private double totalRevenue;
    
    @SerializedName("pending_shipments")
    private int pendingShipments;
    
    @SerializedName("delivered_shipments")
    private int deliveredShipments;
    
    @SerializedName("total_orders")
    private int totalOrders;
    
    @SerializedName("active_shipments")
    private int activeShipments;

    public int getTotalShipments() {
        return totalShipments;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public int getPendingShipments() {
        return pendingShipments;
    }

    public int getDeliveredShipments() {
        return deliveredShipments;
    }

    public int getTotalOrders() {
        return totalOrders > 0 ? totalOrders : totalShipments;
    }

    public int getActiveShipments() {
        return activeShipments > 0 ? activeShipments : pendingShipments;
    }
}