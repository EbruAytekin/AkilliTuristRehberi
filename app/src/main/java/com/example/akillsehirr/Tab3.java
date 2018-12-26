package com.example.akillsehirr;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class Tab3 extends Fragment implements LocationListener {

    private Button button;
    private TextView textView;
    private TextView textView2;
    public LocationManager locationManager;
    private LocationListener locationListener;

    private Location mMevcutKonum;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3, container, false);
        textView = (TextView) rootView.findViewById(R.id.konumm);
        textView2 = (TextView) rootView.findViewById(R.id.konumm2);
        button = (Button) rootView.findViewById(R.id.konumal);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                getLocation();
            }
        });


        return rootView;
    }
    public Location getLocation(){
        try
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, this);
            mMevcutKonum = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            textView.setText(" "+ mMevcutKonum.getLatitude());
            textView2.setText(" "+mMevcutKonum.getLongitude());
        }
        catch (SecurityException ex)
        {
            Toast.makeText(getActivity().getApplicationContext(),ex.getMessage().toString(),Toast.LENGTH_LONG).show();
        }
        return mMevcutKonum;
    }
    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
