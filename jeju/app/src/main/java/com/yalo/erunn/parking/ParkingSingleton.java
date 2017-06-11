package com.yalo.erunn.parking;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by erunn on 2017-06-11.
 */

public class ParkingSingleton {
    private ArrayList<Parking> parkings = new ArrayList<Parking>();

    private static ParkingSingleton instance;

    private ParkingSingleton() {
        System.out.println("call ParkingSingleton constructor");
    }

    public static ParkingSingleton getInstance() {
        if (instance == null)
            instance = new ParkingSingleton();
        return instance;

    }

    public void setParkings(Parking parking){this.parkings.add(parking);};

    public ArrayList<Parking> getParkings(){return this.parkings;};

    public void setLatLng(LatLng latLng, int index){this.parkings.get(index).setLatLng(latLng);};

}
