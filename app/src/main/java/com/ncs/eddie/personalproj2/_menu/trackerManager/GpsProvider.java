package com.ncs.eddie.personalproj2._menu.trackerManager;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ncs.eddie.personalproj2.Constants;

import java.util.ArrayList;

/**
 * Created by eddie on 2017. 5. 28..
 */

public class GpsProvider extends Service implements android.location.LocationListener {

    static final int MSG_REGISTER_CLIENT = 1;
    static final int MSG_UNREGISTER_CLIENT = 2;
    static final int MSG_SET_VALUE = 3;

    ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    Messenger mMessenger = new Messenger(new IncomingHandler());
    int mValue = 0;


    private Context mCtx;
    boolean isGpsEnabled = false;
    boolean isNetworkEnabled = false;
    boolean isGetLocation = false;

    double lat = 500.0, lon = 500.0;
    Location currentPosition = new Location("gps");


    LocationManager locationManager;

    Runnable runnable = null;
    Handler thread = new Handler();
    boolean mIsDestroied = false;

    public GpsProvider(){

    }

    public GpsProvider(Context context){
        this.mCtx = context;
    }

    public Location getCurrentPosition() {
        try {
            locationManager = (LocationManager) mCtx
                    .getSystemService(LOCATION_SERVICE);

            // GPS 정보 가져오기
            isGpsEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // 현재 네트워크 상태 값 알아오기
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGpsEnabled && !isNetworkEnabled) {
                // GPS 와 네트워크사용이 가능하지 않을때 소스 구현
            } else {
                this.isGetLocation = true;
                // 네트워크 정보로 부터 위치값 가져오기
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            Constants.UPDATE_INTERVAL,
                            Constants.MIN_DIST_FOR_UPDATE, this);

                    if (locationManager != null) {
                        currentPosition = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (currentPosition != null) {
                            // 위도 경도 저장
                            lat = currentPosition.getLatitude();
                            lon = currentPosition.getLongitude();
                        }
                    }
                }

                if (isGpsEnabled) {
                    if (currentPosition == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                Constants.UPDATE_INTERVAL,
                                Constants.MIN_DIST_FOR_UPDATE, this);
                        if (locationManager != null) {
                            currentPosition = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (currentPosition != null) {
                                lat = currentPosition.getLatitude();
                                lon = currentPosition.getLongitude();
                            }
                        }
                    }
                }
            }
        }catch (SecurityException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentPosition;
    }

    /**
     * GPS 종료
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GpsProvider.this);
        }
    }

    /**
     * 위도값을 가져옵니다.
     * */
    public double getLatitude(){
        if(currentPosition != null){
            lat = currentPosition.getLatitude();
        }
        return lat;
    }

    /**
     * 경도값을 가져옵니다.
     * */
    public double getLongitude(){
        if(currentPosition != null){
            lon = currentPosition.getLongitude();
        }
        return lon;
    }

    /**
     * GPS 나 wife 정보가 켜져있는지 확인합니다.
     * */
    public boolean isGetLocation() {
        return this.isGetLocation;
    }

    /**
     * GPS 정보를 가져오지 못했을때
     * 설정값으로 갈지 물어보는 alert 창
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCtx);

        alertDialog.setTitle("GPS 사용유무셋팅");
        alertDialog.setMessage("GPS 셋팅이 되지 않았을수도 있습니다.\n 설정창으로 가시겠습니까?");

        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mCtx.startActivity(intent);
                    }
                });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentPosition = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void sendGPS(){
        currentPosition.setLatitude(lat);
        currentPosition.setLongitude(lon);

        runnable = new Runnable() {
            @Override
            public void run() {

                //if(mIsDestroied) return;
                if (currentPosition != null){
                    Log.d("THREAD", "ONGOING");
                    Message msgLocation = Message.obtain();
                    msgLocation.what = MSG_SET_VALUE;
                    msgLocation.obj = currentPosition;
                    for (int i=mClients.size()-1; i>=0; i--){
                        try{
                            mClients.get(i).send( msgLocation );
                        }
                        catch( RemoteException e){
                            mClients.remove( i );
                        }
                    }
                }
                thread.postDelayed(runnable, 500);

            }
        };
        this.thread.post(runnable);
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage( Message msg ){
            Log.d("SERVICE", "Called");
            switch( msg.what ){
                case MSG_REGISTER_CLIENT:
                    mClients.add( msg.replyTo );
                    mIsDestroied = false;
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove( msg.replyTo );
                    mIsDestroied = true;
                    break;
                case MSG_SET_VALUE:
                    //mValue = msg.arg1;
                    sendGPS();
                    break;
                default:
                    super.handleMessage( msg );
            }
        }
    }

}
