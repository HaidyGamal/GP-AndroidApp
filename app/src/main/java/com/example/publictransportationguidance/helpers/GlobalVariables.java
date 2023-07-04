package com.example.publictransportationguidance.helpers;

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

    /* M Osama: selected PickOnLocation tracker */
    public static int LAST_CLICKED_FOOTER_VIEW=0;

    public final static int NAVIGATING_TO_LIVE_LOCATION_REQUEST_CODE =100;

    /* M Osama: path TAG for intent between PathResults & SelectedPaths */
    public static String SELECTED_PATH ="path";

    /* M Osama: to track which path the used choosed */
    public static int selectedPathNumberInWheel = 0;

    public static String BUNDLE_PATH="";
    public static String INTENT_PATH="";

    public final static String BUS="bus";
    public final static String METRO="subway";
    public final static String MODE="transit";

    public static final String COST="cost";
    public static final String TIME="time";
    public static final String DISTANCE="distance";
    public static String SORTING_CRITERIA=COST;

    /* M Osama: TTS & STT*/
    public static final String ARABIC="ar";
    public static final String ASKING_FOR_MODE="مرحبا بكم في تطبيقي توصيلة، هل تريدون العمل بنظام الضّريرِيِنْ أم لا";
    public static final String RE_ASKING_FOR_MODE="هل تريدون العمل بنظام الضّريرِيِنْ أم لا";
    public static final String[] BLIND_MODE_ACCEPTANCE = {"نعم","اة","أيوة","ايوة","آة","اوك","تمام","ماشي"};
    public static final String[] BLIND_MODE_REJECTANCE = {"لا","لأ","ليه","لية"};
    public static final String[] SORTING_CRITERIA_ACCEPTANCE = {"الوقت","وقت","المسافة","مسافة","التكلفة","تكلفة"};


    /* M Osama: Firebase collections */
    public static final String FIRESTORE_ADD_NEW_ROUTE_COLLECTION_NAME = "Nodes";
    public static final String FIRESTORE_AUTHENTICATION_COLLECTION_NAME = "users";
    public static final String SHARE_LOCATION_COLLECTION_NAME= "Friendships";
}












/** M Osama: old GlobalVariables to be deleted if we didn't need them*/
///* M Osama: locationTracker constants */
//public static final int GPS_TIME_INTERVAL = 1000 * 60 * 2;                             // get gps location every 1 min
//    public static final int GPS_DISTANCE = 1000;                                           // set the distance value in meter
//    public static final int HANDLER_DELAY = 1000 * 60 * 2;
//    public static final int START_HANDLER_DELAY = 0;
//    public static final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
//    public static final int PERMISSION_ALL = 1;
//    public static String TRACKER_BASE_TXT="أنت الآن في ";
//public static final String TAG="route";         /* haidy: intent tag between pathResults & selectedPath */
/* M Osama: requestCode from MapActivity */
//public static final int REQUEST_CODE=1234;