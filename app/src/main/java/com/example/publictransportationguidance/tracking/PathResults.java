package com.example.publictransportationguidance.tracking;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
import static com.example.publictransportationguidance.api.Api.NEO4J_BASE_URL;
import static com.example.publictransportationguidance.blindMode.speechToText.SpeechToTextHelper.convertHaaToTaaMarbuta;
import static com.example.publictransportationguidance.helpers.Functions.LISTEN_TO_CHOOSING_PATH_OR_NOT;
import static com.example.publictransportationguidance.helpers.Functions.calcEstimatedTripsTime;
import static com.example.publictransportationguidance.helpers.Functions.checkInternetConnectionToast;
import static com.example.publictransportationguidance.helpers.Functions.convertMathIntoThoma;
import static com.example.publictransportationguidance.helpers.Functions.extractNodesLatLng;
import static com.example.publictransportationguidance.helpers.Functions.failedToEstimateTime;
import static com.example.publictransportationguidance.helpers.Functions.noAvailablePathsToast;
import static com.example.publictransportationguidance.helpers.Functions.sortingByCostToast;
import static com.example.publictransportationguidance.helpers.Functions.sortingByDistanceToast;
import static com.example.publictransportationguidance.helpers.Functions.sortingByTimeToast;
import static com.example.publictransportationguidance.helpers.Functions.stringIsFound;
import static com.example.publictransportationguidance.helpers.Functions.tryAgainToast;
import static com.example.publictransportationguidance.helpers.GlobalVariables.ARABIC;
import static com.example.publictransportationguidance.helpers.GlobalVariables.BUNDLE_PATH;
import static com.example.publictransportationguidance.helpers.GlobalVariables.CHOOSE;
import static com.example.publictransportationguidance.helpers.GlobalVariables.CHOOSE_PATH;
import static com.example.publictransportationguidance.helpers.GlobalVariables.COST;
import static com.example.publictransportationguidance.helpers.GlobalVariables.DISTANCE;
import static com.example.publictransportationguidance.helpers.GlobalVariables.DOT;
import static com.example.publictransportationguidance.helpers.GlobalVariables.DO_YOU_WANT;
import static com.example.publictransportationguidance.helpers.GlobalVariables.GO_TO_NEXT_PATH;
import static com.example.publictransportationguidance.helpers.GlobalVariables.INTENT_PATH;
import static com.example.publictransportationguidance.helpers.GlobalVariables.ITS_COST;
import static com.example.publictransportationguidance.helpers.GlobalVariables.ITS_DISTANCE;
import static com.example.publictransportationguidance.helpers.GlobalVariables.ITS_TIME;
import static com.example.publictransportationguidance.helpers.GlobalVariables.KM;
import static com.example.publictransportationguidance.helpers.GlobalVariables.LISTEN_TO_PATH;
import static com.example.publictransportationguidance.helpers.GlobalVariables.LISTEN_TO_PATH_NODES;
import static com.example.publictransportationguidance.helpers.GlobalVariables.MINUTE;
import static com.example.publictransportationguidance.helpers.GlobalVariables.NEXT;
import static com.example.publictransportationguidance.helpers.GlobalVariables.OR;
import static com.example.publictransportationguidance.helpers.GlobalVariables.PATH;
import static com.example.publictransportationguidance.helpers.GlobalVariables.POUND;
import static com.example.publictransportationguidance.helpers.GlobalVariables.SELECTED_PATH;
import static com.example.publictransportationguidance.helpers.GlobalVariables.SORRY;
import static com.example.publictransportationguidance.helpers.GlobalVariables.SORTING_CRITERIA;
import static com.example.publictransportationguidance.helpers.GlobalVariables.TIME;
import static com.example.publictransportationguidance.helpers.GlobalVariables.YOUR_PATH_IS;
import static com.example.publictransportationguidance.helpers.GlobalVariables.selectedPathNumberInWheel;
import static com.example.publictransportationguidance.helpers.PathsTokenizer.detailedPathToPrint;
import static com.example.publictransportationguidance.helpers.PathsTokenizer.getPathCost;
import static com.example.publictransportationguidance.helpers.PathsTokenizer.getPathDistance;
import static com.example.publictransportationguidance.helpers.PathsTokenizer.getStringPathToPopulateRoom;
import static com.example.publictransportationguidance.helpers.PathsTokenizer.listToArray;
import static com.example.publictransportationguidance.helpers.PathsTokenizer.enumeratePaths;
import static com.example.publictransportationguidance.helpers.PathsTokenizer.stopsAndMeans;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.publictransportationguidance.tracking.trackingModule.reviews.MyApp;
import com.example.publictransportationguidance.blindMode.speechToText.SpeechToTextHelper;
import com.example.publictransportationguidance.blindMode.textToSpeech.TextToSpeechHelper;
import com.example.publictransportationguidance.pojo.pathsResponse.PathInfo;
import com.example.publictransportationguidance.api.RetrofitClient;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.room.AppRoom;
import com.example.publictransportationguidance.room.PathsDao;
import com.example.publictransportationguidance.pojo.pathsResponse.NearestPaths;
import com.example.publictransportationguidance.databinding.ActivityPathResultsBinding;
import com.example.publictransportationguidance.sharedPrefs.SharedPrefs;
import com.example.publictransportationguidance.tracking.trackingModule.trackingHelpers.TextAnimator;
import com.example.publictransportationguidance.tracking.trackingModule.trackingHelpers.TripsTimeCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PathResults extends AppCompatActivity implements TripsTimeCallback {
    ActivityPathResultsBinding binding;
    PathsDao dao;
    int numberOfVisits=0;

    /* M Osama: instance to put loadingDialog inFront of UI */
    LoadingDialog loadingDialog = new LoadingDialog(PathResults.this);

    /* M Osama: instance to deal with stt*/
    private SpeechToTextHelper speechToTextHelper;
    private TextToSpeechHelper textToSpeechHelper;

    TextAnimator textAnimator;

    String stringToBeAnimated="";

    private static List<PathInfo> TRANSPORTATION =null ; //Afnan: To use it in Blind Mode in Choosing a path
    private static String[] HeardTransportation;
    int wheelValue=0;           //Afnan: To use it in Blind Mode in Choosing a path
    String pathNumber;          //Afnan: To use it in Blind Mode in Choosing a path

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_path_results);

        /* M Osama: initializing tts & stt */
        initializeTTSandSTT();
        SharedPrefs.init(this);

        String LOCATION = "",DESTINATION = "";
        int pointer;
        /* M Osama: reading values from homeFragment */
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            LOCATION = extras.getString("LOCATION");
            DESTINATION = extras.getString("DESTINATION");
            pointer= extras.getInt("POINTER");
            switch (pointer){
                case 1: binding.sortByCost.setChecked(true); break;
                case 2: binding.sortByDistance.setChecked(true); break;
                case 3: binding.sortByTime.setChecked(true); break;
            }
        }


        dao = AppRoom.getInstance(getApplication()).pathsDao();
        loadingDialog.startLoadingDialog();

        textAnimator=new TextAnimator();

        /** M Osama: for testing the blindMode here because of the googleAPI key problem */
//        SharedPrefs.write("ON_BLIND_MODE",1);
        /** TO BE DELETED */

        if(SharedPrefs.readMap("ON_BLIND_MODE",0)==1){
            textToSpeechHelper.speak(getString(R.string.SearchingForAvaliablePaths), this::listenToNothing);
        }

        getNearestPaths(LOCATION,DESTINATION,this);

        textAnimator.startAnimation(binding.textView,"");
    }

    @Override
    protected void onStart() {
        super.onStart();            /* M Osama: complete reading the paths for the blind when he return from SelectedPath*/
        if(SharedPrefs.readMap("ON_BLIND_MODE",0)==1){
            if(numberOfVisits>0){  readPathsForBlinds();  }
            numberOfVisits++;
        }
    }

    public void getNearestPaths(String location, String destination, TripsTimeCallback callback) {

        Objects.requireNonNull(RetrofitClient.getInstance(NEO4J_BASE_URL)).getApi().getNearestPaths(location,destination).enqueue(new Callback<List<List<NearestPaths>>>() {
            @Override
            public void onResponse(@NonNull Call<List<List<NearestPaths>>> call, @NonNull Response<List<List<NearestPaths>>> response) {
                List<List<NearestPaths>> paths = response.body();
                Log.i("TAG",response.message());

                MyApp myApp = (MyApp) getApplication();
                myApp.setPaths(paths);

                if (paths != null) {
                    if (paths.size() > 0) {
                        cacheToRoom(enumeratePaths(paths),paths, extractNodesLatLng(paths));        /* M Osama: give each Path a number then cache them to AppRoom where each contains it's route nodes latLng*/
                        calcEstimatedTripsTime(paths,callback);
                        sortingOnClickListeners();                                                  /* M Osama: onClickListener for two sorting Buttons */
                    }
                    else { tryAgainToast(getApplicationContext());  noPathsFound();  noAvailablePathsToast(getApplicationContext()); }
                }
                else { checkInternetConnectionToast(getApplicationContext()); noPathsFound(); }
                loadingDialog.endLoadingDialog();
            }

            @Override
            public void onFailure(@NonNull Call<List<List<NearestPaths>>> call, @NonNull Throwable t) {
                noPathsFound();
                if(SharedPrefs.readMap("ON_BLIND_MODE",0)==1)       textToSpeechHelper.speak(getString(R.string.BadInternetConnection),()-> listenToNothing());

                loadingDialog.endLoadingDialog();
                Log.i("TAG",t.getMessage());
            }
        });

    }

    @Override
    public void onTripsTimeCalculated(int[] globalDurations) {
        for (int pathNumber = 0; pathNumber < globalDurations.length; pathNumber++) {   dao.updatePathTime(pathNumber,globalDurations[pathNumber]); }
        updateViewsOnReSorting(sortPathsAscUsing(SORTING_CRITERIA));
    }

    public void cacheToRoom(HashMap pathMap, List<List<NearestPaths>> shortestPaths,ArrayList<ArrayList<LatLng>> pathsNodesLatLng){
        /*ToBe Deleted*/
        dao.deleteAllPaths();

        /* M Osama: caching Paths in AppRoom (only if PathsTable is empty) */
        if (dao.getNumberOfRowsOfPathsTable() == 0) {
            for (int pathNum = 0; pathNum < pathMap.size(); pathNum++) {
                double tempDistance = getPathDistance(shortestPaths, pathNum);
                double tempCost = getPathCost(shortestPaths, pathNum);
                /* int tempTime is written down at onTripsTimeCalculated */
                String tempPath = getStringPathToPopulateRoom(pathMap).get(pathNum);
                String pickedPathDetails = detailedPathToPrint(stopsAndMeans(shortestPaths,pathNum));
                PathInfo pathInfo = new PathInfo(pathNum, tempDistance,tempCost, 0, tempPath,pickedPathDetails,pathsNodesLatLng.get(pathNum));
                dao.insertPath(pathInfo);
            }
        }
    }

    public void sortingOnClickListeners(){
        binding.sortByCost.setOnClickListener(v -> {      sortingByCostToast(this);       SORTING_CRITERIA=COST;      updateViewsOnReSorting(sortPathsAscUsing(COST));        stringToBeAnimated=sortPathsAscUsing(COST).get(0).getPath();      });
        binding.sortByDistance.setOnClickListener(v -> {  sortingByDistanceToast(this);   SORTING_CRITERIA=DISTANCE;  updateViewsOnReSorting(sortPathsAscUsing(DISTANCE));    stringToBeAnimated=sortPathsAscUsing(DISTANCE).get(0).getPath();  });
        binding.sortByTime.setOnClickListener(v -> {      sortingByTimeToast(this);       SORTING_CRITERIA=TIME;      updateViewsOnReSorting(sortPathsAscUsing(TIME));        stringToBeAnimated=sortPathsAscUsing(TIME).get(0).getPath();      });
    }

    public void updateViewsOnReSorting(List<PathInfo> newSortedPaths){
        initCostDistanceTime(newSortedPaths);                                                /* M Osama: put cost,distance,time of the best root */
        setWheelSettings(combineSinglePathNodes(newSortedPaths));                            /* M Osama: Build String Array from cachedPath Info & pass it to build Wheel */
        wheelOnClickListener(newSortedPaths);                                                /* M Osama: onClickListener for wheel representing choices */
        costDistanceTimeTracker(newSortedPaths);
        textAnimator.refreshAnimation(stringToBeAnimated,binding.textView);
    }

    public void initCostDistanceTime(List<PathInfo> paths){
        binding.cost.setText(paths.get(0).getCost()+"");
        binding.distance.setText(paths.get(0).getDistance()+"");
        binding.time.setText(paths.get(0).getTime()+"");
    }

    public void setWheelSettings(String[] transportations) {
        binding.wheel.setMinValue(0);                                   /* M Osama: wheel populated starting from index0 from source*/
        binding.wheel.setMaxValue(transportations.length - 1);          /* M Osama: wheel populated till index(len-1)*/
        binding.wheel.setValue(0);                                      /* M Osama: index0 content represent best result; to be edited after building database & mapping */
        binding.wheel.setDisplayedValues(transportations);              /* M Osama: populating wheel with data */
        binding.wheel.setTextSize(R.dimen.un_selected_size);
        binding.wheel.setSelectedTextColor(ContextCompat.getColor(this,R.color.transparent));
        stringToBeAnimated=transportations[0];                          /* M Osama: set inital value to be animated as the value at index0 */

        /*Afnan: Blind Mode*/
        if(SharedPrefs.readMap("ON_BLIND_MODE",0)==1) {
            binding.wheel.setValue(wheelValue);
            HeardTransportation= transportations;
            readPathsForBlinds();
        }
        else binding.wheel.setValue(0); /* M Osama: index0 content represent best result; to be edited after building database & mapping */

    }

    public void wheelOnClickListener(List<PathInfo> paths){
        binding.wheel.setOnClickListener((View v) -> {
            Intent selectedPathIntent = new Intent(PathResults.this, SelectedPath.class); /* haidy: to link between the 2 pages*/
            Bundle bundle = new Bundle();

            selectedPathNumberInWheel=binding.wheel.getValue();                                                              /* M Osama: to track which path the user chosen */

            bundle.putSerializable(BUNDLE_PATH,paths.get(selectedPathNumberInWheel).getCoordinates());                       /* M Osama: pass the nodes of selectedPath to be able to draw them on Map */
            selectedPathIntent.putExtra(INTENT_PATH,bundle);

            SharedPrefs.write("CHOSEN_CRITERIA",SORTING_CRITERIA);                                  /* M Osama: to use them in review*/
            SharedPrefs.write("CHOSEN_PATH_NUMBER",selectedPathNumberInWheel);

            selectedPathIntent.putExtra(SELECTED_PATH,paths.get(selectedPathNumberInWheel).getDetailedPath());               /* M Osama: pass Path details to print them on screen */

            Log.i("TAG",paths.get(selectedPathNumberInWheel).getPath());                                                /* M Osama: for debugging only */

            startActivity(selectedPathIntent);
        });
    }

    public void costDistanceTimeTracker(List<PathInfo> paths) {
        binding.wheel.setOnValueChangedListener((numberPicker, oldValue, newValue) -> {
            updateCostDistanceTime(newValue,paths);
            stringToBeAnimated=dao.getSortedPathsASC(SORTING_CRITERIA).get(newValue).getPath();
            textAnimator.refreshAnimation(stringToBeAnimated,binding.textView);
        });

        binding.wheel.setOnScrollListener((numberPicker, scrollState) -> {if (scrollState == SCROLL_STATE_IDLE) updateCostDistanceTime(binding.wheel.getValue(),paths);});
    }

    private void updateCostDistanceTime(int pathNumber,List<PathInfo> paths) {
        PathInfo path = paths.get(pathNumber);
        binding.cost.setText(path.getCost()+"");
        binding.distance.setText(path.getDistance()+"");
        binding.time.setText(path.getTime()+"");
        if(path.getTime()==0) failedToEstimateTime(getApplicationContext());
    }


    /* M Osama: Build String Array from cachedPath Info */
    public String[] combineSinglePathNodes(List<PathInfo> paths){
        ArrayList<String> transportationsTemp = new ArrayList<>();
        for (int pathNum = 0; pathNum < dao.getNumberOfRowsOfPathsTable(); pathNum++) {
            transportationsTemp.add(paths.get(pathNum).getPath());
        }
        return listToArray(transportationsTemp);
    }

    public List<PathInfo> sortPathsAscUsing(String sortingCriteria){
        List<PathInfo> paths = dao.getSortedPathsASC(sortingCriteria);
        dao.clearAllPaths();
        dao.insertPaths(paths);

        TRANSPORTATION=paths; // Afnan : Blind Mode to use in Choosing a path

        return paths;
    }

    public void noPathsFound(){
        binding.cost.setText(R.string.NotAvailable);
        binding.distance.setText(R.string.NotAvailable);
        binding.time.setText(R.string.NotAvailable);

        String[] temp = new String[100];
        Arrays.fill(temp, getString(R.string.NoPathsFound));

        if(SharedPrefs.readMap("ON_BLIND_MODE",0)==1){
            textToSpeechHelper.speak(getString(R.string.NoPathsFound), this::listenToNothing);
        }

        binding.wheel.setDisplayedValues(temp);
    }

    void initializeTTSandSTT(){
        textToSpeechHelper = TextToSpeechHelper.getInstance(this,ARABIC);
        speechToTextHelper = SpeechToTextHelper.getInstance(ARABIC);
    }

    private void listenToNothing(){}


    //Afnan : Blind Mode Choosing a path
    public void readPathsForBlinds(){
        updateCostDistanceTime(binding.wheel.getValue(), TRANSPORTATION);
        pathNumber = Integer.toString(wheelValue + 1);

        String pathSentence = PATH + pathNumber;
        String costSentence = ITS_COST + binding.cost.getText() + POUND;
        String distanceSentence = ITS_DISTANCE + binding.distance.getText() + KM;
        String timeSentence = ITS_TIME + binding.time.getText() + MINUTE;

        switch (SORTING_CRITERIA) {
            case COST:      textToSpeechHelper.speak(pathSentence + costSentence + distanceSentence + timeSentence + DOT + DO_YOU_WANT + LISTEN_TO_PATH + OR + NEXT + OR + CHOOSE, () -> listenToChangingPath(this));     break;
            case DISTANCE:  textToSpeechHelper.speak(pathSentence + distanceSentence + costSentence + timeSentence + DOT + DO_YOU_WANT + LISTEN_TO_PATH + OR + NEXT + OR + CHOOSE, () -> listenToChangingPath(this));     break;
            case TIME:      textToSpeechHelper.speak(pathSentence + timeSentence + costSentence + distanceSentence + DOT + DO_YOU_WANT + LISTEN_TO_PATH + OR + NEXT + OR + CHOOSE, () -> listenToChangingPath(this));     break;
            default:        break;
        }
    }

    @Override
    /* Afnan: receiving answer from the user in Blind Mode */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        speechToTextHelper.onActivityResult(requestCode, resultCode, data);                                         // Pass the onActivityResult event to the SpeechToTextHelper
        ArrayList<String> speechConvertedToText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

        switch (requestCode) {
            case LISTEN_TO_CHOOSING_PATH_OR_NOT:
                String ChooseOrNot = convertHaaToTaaMarbuta(speechConvertedToText.get(0));
                if(stringIsFound(ChooseOrNot,CHOOSE_PATH))  ChoosingThePath(TRANSPORTATION);
                else if(stringIsFound(ChooseOrNot,GO_TO_NEXT_PATH)) {
                    wheelValue = (wheelValue + 1) % binding.wheel.getMaxValue();
                    binding.wheel.setValue(wheelValue);
                    pathNumber = Integer.toString(wheelValue + 1);

                    updateCostDistanceTime(binding.wheel.getValue(), TRANSPORTATION);
                    textAnimator.refreshAnimation(TRANSPORTATION.get(binding.wheel.getValue()).getPath(), binding.textView);

                    String pathSentence = PATH + pathNumber;
                    String costSentence = ITS_COST + binding.cost.getText() + POUND;
                    String distanceSentence = ITS_DISTANCE + binding.distance.getText() + KM;
                    String timeSentence = ITS_TIME + binding.time.getText() + MINUTE;

                    switch (SORTING_CRITERIA) {
                        case COST:      textToSpeechHelper.speak(pathSentence + costSentence + distanceSentence + timeSentence + DOT + DO_YOU_WANT + LISTEN_TO_PATH + OR + NEXT + OR + CHOOSE, () -> listenToChangingPath(this));       break;
                        case DISTANCE:  textToSpeechHelper.speak(pathSentence + distanceSentence + costSentence + timeSentence + DOT + DO_YOU_WANT + LISTEN_TO_PATH + OR + NEXT + OR + CHOOSE, () -> listenToChangingPath(this));       break;
                        case TIME:      textToSpeechHelper.speak(pathSentence + timeSentence + costSentence + distanceSentence + DOT + DO_YOU_WANT + LISTEN_TO_PATH + OR + NEXT + OR + CHOOSE, () -> listenToChangingPath(this));       break;
                        default:        break;
                    }
                }
                else if(stringIsFound(ChooseOrNot,LISTEN_TO_PATH_NODES))    textToSpeechHelper.speak(YOUR_PATH_IS + convertMathIntoThoma(HeardTransportation[binding.wheel.getValue()])+DOT+CHOOSE+OR+NEXT, () -> listenToChangingPath(this));
                else                                                        textToSpeechHelper.speak(SORRY, () -> listenToChangingPath(this));

        }
    }

    public void ChoosingThePath(List<PathInfo> paths){
        Intent selectedPathIntent = new Intent(PathResults.this, SelectedPath.class);
        Bundle bundle = new Bundle();

        selectedPathNumberInWheel=binding.wheel.getValue();

        bundle.putSerializable(BUNDLE_PATH,paths.get(selectedPathNumberInWheel).getCoordinates());
        selectedPathIntent.putExtra(INTENT_PATH,bundle);
        selectedPathIntent.putExtra(SELECTED_PATH,paths.get(selectedPathNumberInWheel).getDetailedPath());

        startActivity(selectedPathIntent);
    }

    private void listenToChangingPath(PathResults pathResults) {
        speechToTextHelper.startSpeechRecognition(pathResults,LISTEN_TO_CHOOSING_PATH_OR_NOT);
    }

}