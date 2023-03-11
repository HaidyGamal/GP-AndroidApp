package com.example.publictransportationguidance.HelperClasses;

public class Constants {
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
}
