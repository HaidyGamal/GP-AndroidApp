package com.example.publictransportationguidance.tracking.trackingModule.trackingModule;

import static com.example.publictransportationguidance.tracking.trackingModule.trackingModule.TrackLiveLocation.googleMap;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.tracking.SelectedPath;
import com.example.publictransportationguidance.tracking.trackingModule.util.logic.MapUtils;
import com.example.publictransportationguidance.tracking.trackingModule.util.logic.PathUtils;
import com.example.publictransportationguidance.tracking.trackingModule.util.ui.CameraUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class LocationUpdatesService extends Service {

    private static final String PACKAGE_NAME = "com.example.trackingbeforefinal";

    private static final String TAG = LocationUpdatesService.class.getSimpleName();

    /* Notification Channel Name */
    private static final String CHANNEL_ID = "channel_01";
    public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
    public static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME + ".started_from_notification";

    private final IBinder mBinder = new LocalBinder();

    /* Update Interval */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    /* Fastest Update rate */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /* notification ID */
    private static final int NOTIFICATION_ID = 12345678;

    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of orientation change. We create a foreground service notification only if the former take place.*/
    private boolean mChangingConfiguration = false;

    private NotificationManager mNotificationManager;

    /* Used to track user's location */
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    private Handler mServiceHandler;

    /* Track user current location */
    private Location mLocation;

    public LocationUpdatesService() {}

    @Override
    public void onCreate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };

        createLocationRequest();
        getLastLocation();

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) mNotificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT));

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started");
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION, false);

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {removeLocationUpdates();  stopSelf();}
        return START_NOT_STICKY;        // Tells the system to not try to recreate the service after it has been killed.
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    // Called when a client (MainActivity in case of this sample) comes to the foreground and binds with this service. The service should cease to be a foreground service when that happens.
    @Override
    public IBinder onBind(Intent intent) {
        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    // Called when a client (MainActivity in case of this sample) returns to the foreground and binds once again with this service. The service should cease to be a foreground service when that happens.
    @Override
    public void onRebind(Intent intent) {
        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // Called when the last client (MainActivity in case of this sample) unbinds from this service. If this method is called due to a configuration change in MainActivity, we do nothing. Otherwise, we make this service a foreground service.
        if (!mChangingConfiguration && Utils.requestingLocationUpdates(this)) {
            Log.i(TAG, "Starting foreground service");
            startForeground(NOTIFICATION_ID, getNotification());
        }
        return true; // Ensures onRebind() is called when a client re-binds.
    }

    @Override
    public void onDestroy() { mServiceHandler.removeCallbacksAndMessages(null); }

    /* Request Location Updates */
    @SuppressLint("MissingPermission")
    public void requestLocationUpdates() {
        Utils.setRequestingLocationUpdates(this, true);
        startService(new Intent(getApplicationContext(), LocationUpdatesService.class));
        try { mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());}
        catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates(this, false);
        }
    }

    /* Remove location updates */
    public void removeLocationUpdates() {
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Utils.setRequestingLocationUpdates(this, false);
            stopSelf();
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates(this, true);
        }
    }

    /* Return notification channel which's a part of the Foreground service */
    private Notification getNotification() {
        Intent intent = new Intent(this, LocationUpdatesService.class);

        CharSequence text = Utils.getLocationText(mLocation);
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);         // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);   // The PendingIntent that leads to a call to onStartCommand() in this service.
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, SelectedPath.class), 0|PendingIntent.FLAG_IMMUTABLE);         // The PendingIntent to launch activity.

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .addAction(R.drawable.ic_launcher_foreground, getString(R.string.return_to_activity), activityPendingIntent)
                .addAction(R.drawable.ic_launcher_background, getString(R.string.remove_location_updates), servicePendingIntent)
                .setContentText(text)
                .setContentTitle(Utils.getLocationTitle(this,mLocation))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.icon)
                .setTicker(text)
                .setWhen(System.currentTimeMillis());

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) builder.setChannelId(CHANNEL_ID); // Channel ID

        return builder.build();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        try { mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> { if (task.isSuccessful() && task.getResult() != null) mLocation = task.getResult(); }); }
        catch (SecurityException unlikely) {}
    }

    private void onNewLocation(Location location) {
        mLocation = location;

        // Notify anyone listening for broadcasts about the new location.
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, location);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        // Update notification content if running as a foreground service.
        if (serviceIsRunningInForeground(this)) mNotificationManager.notify(NOTIFICATION_ID, getNotification());

        LatLng currentLocation = new LatLng(location.getLatitude(),location.getLongitude());

        Toast.makeText(getApplicationContext(), currentLocation.latitude+","+currentLocation.longitude, Toast.LENGTH_SHORT).show();

        TrackLiveLocation.listOfActualPathNodes.add(currentLocation);
        MapUtils.moveCar(googleMap,getApplicationContext(), currentLocation);
        CameraUtils.moveCamera(googleMap,currentLocation);
        PathUtils.showActualPath(googleMap, TrackLiveLocation.listOfActualPathNodes, Color.GREEN);

    }

    /* LocationRequest Parameters */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /* Class used for the client Binder.  Since this service runs in the same process as its clients, we don't need to deal with IPC. */
    public class LocalBinder extends Binder {
        public LocationUpdatesService getService() { return LocationUpdatesService.this; }
    }

    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) if (service.foreground) return true;
        }
        return false;
    }
}