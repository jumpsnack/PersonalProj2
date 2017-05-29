package com.ncs.eddie.personalproj2._menu.trackerManager;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.*;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.ncs.eddie.personalproj2.R;

import java.util.ArrayList;

/**
 * Created by eddie on 2017. 5. 24..
 */

public class TrackerMngFragment extends Fragment implements OnMapReadyCallback{

    GoogleMap googleMap = null;
    private Messenger mService;
    private Messenger mMessenger = null;

    private static final String TAG = "MapFragment";
    private static final int MSG_REGISTER_CLIENT = 1;
    private static final int MSG_UNREGISTER_CLIENT = 2;
    private static final int MSG_SET_VALUE = 3;
    private static boolean mIsBound = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracker_manager, container, false);

        getPermission();

        if (googleMap == null){
            SupportMapFragment supportMapFragment  = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_panel));
            supportMapFragment.getMapAsync(this);
        }

        mMessenger = new Messenger( new TrackerMngFragment.IncomingHandler() );

        Intent i = new Intent(getContext(), GpsProvider.class);
        getActivity().bindService( i, conn, Context.BIND_AUTO_CREATE);
        mIsBound = true;

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage( Message msg ){
            switch ( msg.what ){
                case TrackerMngFragment.MSG_SET_VALUE:
                    Location location = (Location) msg.obj;
                    Log.d( TAG, location.getLatitude() + " // " + location.getLongitude() );
                    break;
                default:
                    super.handleMessage( msg );
            }
        }
    }

    ServiceConnection conn = new ServiceConnection(){
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d( TAG, "onServiceDisconnected()");
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger( service );
            Log.d( TAG, "onServiceConnected()");

            try{
                Message msg = Message.obtain( null, MSG_REGISTER_CLIENT );
                msg.replyTo = mMessenger;
                mService.send( msg );

                    msg = Message.obtain( null, MSG_SET_VALUE, this.hashCode(), 0 );
                    mService.send( msg );

            }
            catch( RemoteException e ){  }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if ( mIsBound ){
            Message msg = Message.obtain( null, MSG_UNREGISTER_CLIENT );
            msg.replyTo = mMessenger;
            try{
                mService.send( msg );
            } catch (Exception e){
             /*DO NOTHING*/
            }
            getActivity().unbindService( conn );
            mIsBound = false;
        }
    }

    private void getPermission(){
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(getActivity(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };


        new TedPermission(getActivity())
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET)
                .check();
    }
}
