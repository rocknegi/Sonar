package com.example.sourabh.sonar.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.example.sourabh.sonar.activities.base.AbsPermissionActivity;
import com.example.sourabh.sonar.mvcviews.base.HomeView;
import com.example.sourabh.sonar.mvcviews.HomeViewImp;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class HomeActivity extends AbsPermissionActivity implements HomeView.OnLocationSendListener {

    private HomeView mHomeView;
    private FusedLocationProviderClient mFusedLocaitonProvideClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeView = new HomeViewImp(getLayoutInflater(), this);
        setContentView(mHomeView.getRootView());
        requestLocationPermission();
        requestMessagePermission();
        mHomeView.setOnLocationSendListener(this);
        mFusedLocaitonProvideClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == ACCESS_LOCATION_PERMISSION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                mHomeView.setOnLocationSendListener(this);
            }
        }else if(requestCode == SEND_SMS_PERMISSION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                mHomeView.setOnLocationSendListener(this);
            }
        }
    }

    @Override
    public void OnOneTimeSend() {
        if(requestLocationPermission()) {
            try {
                mFusedLocaitonProvideClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                String msg = "Last known location: (" + location.getLatitude() + "," +
                                        location.getLongitude() + ")";
                                Log.d("debug",msg);
                                if(requestMessagePermission()){
                                    SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
                                    String distressNumber = sharedPreferences.getString(MainActivity.DISTRESS_NUMBER, null);
                                    if(distressNumber != null) {
                                        SmsManager smsManager = SmsManager.getDefault();
                                        smsManager.sendTextMessage(distressNumber, null, msg, null, null );
                                        Toast.makeText(getApplicationContext(), "Location sent (probably)", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        });

            }catch (SecurityException exc){
                exc.printStackTrace();
            }
        }
    }

    @Override
    public void OnSonar() {
        Toast.makeText(getApplicationContext(), "Not yet implemented :p", Toast.LENGTH_SHORT).show();
    }
}