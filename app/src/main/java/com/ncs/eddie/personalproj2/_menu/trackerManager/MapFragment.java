package com.ncs.eddie.personalproj2._menu.trackerManager;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

/**
 * Created by eddie on 2017. 5. 28..
 */

public class MapFragment extends Fragment implements OnMapReadyCallback {
    GoogleMap googleMap = null;
    private Messenger mService;
    private Messenger mMessenger = null;

    private static final String TAG = "MapFragment";
    private static final int MSG_REGISTER_CLIENT = 1;
    private static final int MSG_SET_VALUE = 2;
    private static final int MSG_UNREGISTER_CLIENT = 3;
    private static boolean mIsBound = false;

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage( Message msg ){
            switch ( msg.what ){
                case MapFragment.MSG_SET_VALUE:
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
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        mMessenger = new Messenger( new IncomingHandler() );

        Intent i = new Intent("com.ncs.eddie.personalproj2._menu.trackerManager.MapFragment");
        getActivity().bindService( i, conn, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    public void onDestroy(){
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

}
