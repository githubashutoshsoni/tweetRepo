/*
 * Copyright 2020 R3BL LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.r3bl.stayawake;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.text.util.LinkifyCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.r3bl.stayawake.database.Task;
import com.r3bl.stayawake.database.ToDoDatabase;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "SA_MainActivity";
    LocationCallback mLocationCallBack;
    Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyTileService.coldStart(this, false);
        TextView textTweetData = findViewById(R.id.tweet_data);
        AppExecutors appExecutors = new AppExecutors();


        getLocationPermission();


        mLocationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                mLastLocation = locationResult.getLastLocation();
                Log.d(TAG, "Location callback is working" + mLastLocation.getLongitude() + " " + mLastLocation.getLatitude());


                Geocoder geocoder = new Geocoder(getApplication());

                if (Geocoder.isPresent()) {

                    Log.d(TAG, "geocder is present");

                } else {
                    Log.e(TAG, "onLocationResult: geocode needed to preooceed");
                }


                appExecutors.diskIO().execute(() -> {
                    try {
                        Address address = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1).get(0);
                        String city = address.getLocality();

                        appExecutors.mainThread().execute(() -> {

                            TextView textView = findViewById(R.id.your_location);
                            textView.setText(city + " lat:" + mLastLocation.getLatitude() + " long: " + mLastLocation.getLongitude());
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
            }
        };


        appExecutors.diskIO().execute(() -> {

            List<Task> tasksList = ToDoDatabase.getInstance(MainActivity.this).taskDao().getTasks();
            if (tasksList.isEmpty()) {
                textTweetData.setText("Nothing fetched from the url");
            } else {

                for (Task task
                        : tasksList
                ) {
                    textTweetData.setText(task.getContent());
                }
            }


        });
    }

    public static final int REQUEST_LOCATION_PERMISSION = 1;

    void getLocationPermission() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);


        } else {

            getLocation();
            Log.d(TAG, "getLocation: permissions granted");
        }

    }

    FusedLocationProviderClient fusedLocationProvider;

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }


    void getLocation() {

        Log.d(TAG, "getLocation is called");

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProvider.getLastLocation().addOnSuccessListener(location -> {
            fusedLocationProvider.requestLocationUpdates(getLocationRequest(), mLocationCallBack, null);

            Log.d(TAG, " onSuccess fusedLocationProvider getLastLocation is called");
//                Completed: Always get the live location and start the LandingActivity or else there is always a location in paramater you can check
            Log.d(TAG, " location is null requesting location updates");

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_LOCATION_PERMISSION && resultCode == RESULT_OK) {
            //todo make retry otp call
            Log.d(TAG, " onActivityResult Location permission granted");
            getLocation();
//            resendOtp();
//            setUpCountDownTimer();
        } else {

            getLocationPermission();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    public void buttonStartAwakeClicked(View view) {
        MyTileService.coldStart(this, true);
    }
}
