package com.example.publictransportationguidance.helpers;

import android.Manifest;

public class GlobalVariables {

    /* M Osama: Cairo dimensions */
    public static final double SOUTH=29.696354;
    public static final double WEST=31.133331;
    public static final double NORTH=30.186870;
    public static final double EAST=31.454545;
    /* M Osama: AutoComplete Footer */
    public static final String FOOTER = "تحديد من الخريطة";

    /* M Osama: To track app Mode */
    public static int ON_BLIND_MODE;

    /* M Osama: to track loggedIn */
    public static int IS_LOGGED_IN;

    /* M Osama: to differentiate locationOnItemClick & destinationOnItemClick */
    public static int LOCATION=0;
    public static int DESTINATION=1;

    /* M Osama : intents/bundles keys */
    public static String LATITUDE_KEY="LATITUDE";
    public static String LONGITUDE_KEY="LONGITUDE";
    public static String LOCATION_NAME_KEY="LOCATION_NAME";

    /* haidy: intent tag between pathResults & selectedPath */
    public static final String TAG="route";

    /* M Osama: requestCode from MapActivity */
    public static final int REQUEST_CODE=1234;

    /* M Osama: selected PickOnLocation tracker */
    public static int LAST_CLICKED_FOOTER_VIEW=0;

    /* M Osama: locationTracker constants */
    public static final int GPS_TIME_INTERVAL = 1000 * 60 * 2;                             // get gps location every 1 min
    public static final int GPS_DISTANCE = 1000;                                           // set the distance value in meter
    public static final int HANDLER_DELAY = 1000 * 60 * 2;
    public static final int START_HANDLER_DELAY = 0;
    public static final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    public static final int PERMISSION_ALL = 1;
    public static String TRACKER_BASE_TXT="أنت الآن في ";

    public final static int NAVIGATING_TO_LIVE_LOCATION_REQUEST_CODE =100;

    /* M Osama: search by distance or cost */
    public static boolean SEARCH_BY_COST = true;
    public static boolean SEARCH_BY_DISTANCE = false;

    /* M Osama: path TAG for intent between PathResults & SelectedPaths */
    public static String SELECTED_PATH ="path";

    /* M Osama: to track which path the used choosed */
    public static int selectedPathNumberInWheel = 0;

    public static String BUNDLE_PATH="";
    public static String INTENT_PATH="";

}
