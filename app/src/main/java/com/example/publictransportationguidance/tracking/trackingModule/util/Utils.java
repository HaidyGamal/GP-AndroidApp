package com.example.publictransportationguidance.tracking.trackingModule.util;

import static com.example.publictransportationguidance.helpers.Functions.getLocationName;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;
import com.example.publictransportationguidance.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates";

    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The {@link Context}.
     */
    public static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }

    /* Stores the location updates state in SharedPreferences */
    public static void setRequestingLocationUpdates(Context context, boolean requestingLocationUpdates) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates).apply();
    }

    /* NotificationContent */
    public static String getLocationText(Location location) {
        return location == null ? "Unknown location" : "(" + location.getLatitude() + ", " + location.getLongitude() + ")";
    }

    /* NotificationTitle */
    public static String getLocationTitle(Context context, Location location) {
        if(location!=null) return context.getString(R.string.current_location_is) + " " + getLocationName(context, location.getLatitude(), location.getLongitude()) + " ... " + new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        else return "";
    }

}