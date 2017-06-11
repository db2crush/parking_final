package com.yalo.erunn.parking;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by erunn on 2017-06-04.
 */

public class Parking {
    private String name;
    private String address;
    private int quantity;
    private String free;
    private String days;
    private LatLng latLng;

    public Parking(String name, String address, int quantity, String free, String days) {
        this.name = name;
        this.address = address;
        this.quantity = quantity;
        this.free = free;
        this.days = days;
    };

    public void setLatLng(LatLng latLng){this.latLng = latLng;};

    public String getName() {return this.name;};
    public String getAddress() {return this.address;};
    public int getQuantity() {return this.quantity;};
    public String getFree() {return this.free;};
    public String getDays() {return this.days;};
    public LatLng getLatLng(){return this.latLng;};
}
