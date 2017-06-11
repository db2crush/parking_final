package com.yalo.erunn.parking;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by erunn on 2017-06-06.
 */

public class SetMarker extends AsyncTask<Void, Integer, Void> {
    private GoogleMap mMap;
    private Context context;
    private Geocoder mGeoCoder;

    private String address, name;
    private double lat, lng;

    private ParkingSingleton parkingSingleton = ParkingSingleton.getInstance();

    SetMarker(GoogleMap map, Context context) {
        this.mMap = map;
        this.context = context;
        this.mGeoCoder = new Geocoder(context, Locale.KOREA);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        LatLng location = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions()
                .zIndex(values[0])
                .position(location)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.parking)));

        parkingSingleton.setLatLng(location, values[0]);
    }

    @Override
    protected Void doInBackground(Void... params) {
        List<Address> addressList;
        for (int i = 0; i < parkingSingleton.getParkings().size(); i++) {
            address = parkingSingleton.getParkings().get(i).getAddress();
            name = parkingSingleton.getParkings().get(i).getName();
            try {
                addressList = mGeoCoder.getFromLocationName(address, 1);
                if (!addressList.isEmpty()) {
                    lat = addressList.get(0).getLatitude();
                    lng = addressList.get(0).getLongitude();
                    publishProgress(i);
                } else
                    continue;
            } catch (IOException e) {
                Log.v("error", e + " in doInBackground");
            }
        }
        return null;
    }
}
