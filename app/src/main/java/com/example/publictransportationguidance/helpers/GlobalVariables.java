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

    /* M Osama: to be used in Google Maps API requests */
    public final static String BUS="bus";
    public final static String METRO="subway";
    public final static String MODE="transit";


    /* M Osama: to track sortingCriteria */
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

    //Afnan : used in pathResults
    public static final String[] CHOOSING_PATH_OR_NOT = { "إختيار","اختيار","أختيار","التالي","التالى", "سماع المسار" , "سمع المسار" , "سماع المسر" , "سمع المسر"};
    public static final String PATH = "الطريق";
    public static final String ITS_COST = "و تكلفتهُ   ";
    public static final String ITS_DISTANCE = "و مسافتهُ   ";
    public static final String ITS_TIME = "و سيستغرِقُ ";
    public static final String POUND = "جنيهاً  ";
    public static final String KM = "كيلومتراً  ";
    public static final String MINUTE = "دقيقةً  ";
    public static final String LISTEN_TO_PATH = "سماع المسار. ";
    public static final String NEXT = "التالي. ";
    public static final String CHOOSE = "إختيار. ";
    public static final String OR = "أَم.";
    public static final String DOT = ".";
    public static final String YOUR_PATH_IS = "مسارك هو. ";
    public static final String SORRY = "عفوا !";
    public static final String DO_YOU_WANT = "هل تريد ";
    public static final String[] TRACKING_OR_NOT ={"نعم","اة","أيوة","ايوة","آة","اوك","تمام","ماشي","لا","لأ","ليه","لية"};




    /* M Osama: Firebase collections */
    public static final String FIRESTORE_ADD_NEW_ROUTE_COLLECTION_NAME = "Nodes";
    public static final String FIRESTORE_AUTHENTICATION_COLLECTION_NAME = "users";
    public static final String SHARE_LOCATION_COLLECTION_NAME= "Friendships";


    /* M Osama: Fields of Review Collection */
    public static final String LIKES_FIELD="Likes";
    public static final String BAD_PATH_DISLIKES_FIELD="BadPathDislikes";
    public static final String UNFOUND_PATH_DISLIKES_FIELD="UnFoundPathDislikes";

}
