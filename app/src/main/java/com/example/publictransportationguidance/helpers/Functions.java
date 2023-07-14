package com.example.publictransportationguidance.helpers;

import static com.example.publictransportationguidance.BuildConfig.MAPS_API_KEY;
import static com.example.publictransportationguidance.api.Api.GOOGLE_MAPS_BASE_URL;
import static com.example.publictransportationguidance.helpers.GlobalVariables.BUS;
import static com.example.publictransportationguidance.helpers.GlobalVariables.METRO;
import static com.example.publictransportationguidance.helpers.GlobalVariables.MODE;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.pojo.estimatedTimeResponse.Duration;
import com.example.publictransportationguidance.pojo.estimatedTimeResponse.Route;
import com.example.publictransportationguidance.tracking.trackingModule.trackingHelpers.TripsTimeCallback;
import com.example.publictransportationguidance.api.RetrofitClient;
import com.example.publictransportationguidance.blindMode.ArrowFunction;
import com.example.publictransportationguidance.pojo.estimatedTimeResponse.EstimatedTime;
import com.example.publictransportationguidance.pojo.pathsResponse.NearestPaths;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import retrofit2.Response;

public class Functions {

    public static final int LISTEN_TO_RAW_LOCATION_NAME = 9999;
    public static final int LISTEN_TO_SPECIFIED_LOCATION_NAME = 8888;
    public static final int LISTEN_TO_RAW_DESTINATION_NAME = 7777;
    public static final int LISTEN_TO_SPECIFIED_DESTINATION_NAME = 6666;
    public static final int LISTEN_TO_SORTING_CRITERIA = 5555;
    public static final int LISTEN_TO_CHOSEN_MODE = 4444;
    public static final int LISTEN_TO_CHOOSING_PATH_OR_NOT =3333; //Afnan: in PathResults for Blind Mode
    public static final int LISTEN_TO_TRACKING_OR_NOT =2222;      //Afnan: in SelectedPath for Blind Mode
    public static final int LISTEN_TO_RE_SPEAK_ROUTE_OR_NOT=1111;

    public static String[] listToArray(List<String> list) {
        String[] array = new String[list.size()];
        for (int i = 0; i < list.size(); i++) array[i] = list.get(i);
        return array;
    }

    /* M Osama: Used to print only the main place name from the retrieved googleName */
    public static String deleteFromSequence(String inputString, String sequenceToStart) {
        int index = inputString.indexOf(sequenceToStart);
        if (index == -1) {
            // If the sequence is not found in the string, return the original string
            return inputString;
        } else {
            // Return the string after deleting all characters starting from the entered sequence of characters
            return inputString.substring(0, index);
        }
    }

    /* M Osama: form the stop String that be send in the API request */
    public static String getStopLatLong(double lat, double lng) {
        return lat + "," + lng;
    }

    /* M Osama: functions helps in dealing with autoComplete dataSource(array,list,...)*/
    public static String getSelectedItem(AdapterView parent, int positionInDropList) {
        return (String) parent.getItemAtPosition(positionInDropList);
    }

    public static int getDataSourceIndex(String[] dataSource, String selectedItem) {
        return Arrays.asList(dataSource).indexOf(selectedItem);
    }

    public static String getLocationName(Context context, Double latitude, Double longitude) {
        Geocoder myLocation = new Geocoder(context, new Locale("ar"));
        List<Address> myList;
        try {
            myList = myLocation.getFromLocation(latitude, longitude, 1);
            Address address = myList.get(0);
            Log.i("TAG","From(Functions)"+address.getLocality()+"");
            return address.getLocality();
        } catch (IOException e) {
            e.printStackTrace();
            return "هناك مشكلة في الانترنت";
        }
    }

    public static String getStringBetweenFirstAndThirdComma(String input) {
        int firstCommaIndex = input.indexOf('،');
        if (firstCommaIndex != -1) {
            int secondCommaIndex = input.indexOf('،', firstCommaIndex + 1);
            if (secondCommaIndex != -1) {
                int thirdCommaIndex = input.indexOf('،', secondCommaIndex + 1);
                if (thirdCommaIndex != -1) {
                    return deleteFirstAndLastComma(input.substring(firstCommaIndex, thirdCommaIndex + 1));
                }
            }
        }
        return "";
    }

    public static String deleteFirstAndLastComma(String input) {
        String result = input;
        if (!result.isEmpty()) {
            if (result.charAt(0) == '،') {
                result = result.substring(1);
            }
            if (result.charAt(result.length() - 1) == '،') {
                result = result.substring(0, result.length() - 1);
            }
        }
        return result;
    }

    /* M Osama: to be used instead of the GeoCoder */
    public static String getLocationNameDetailsInArabic(Context context, Double latitude, Double longitude) {
        Geocoder myLocation = new Geocoder(context, new Locale("ar"));
        List<Address> myList;
        try {
            myList = myLocation.getFromLocation(latitude, longitude, 1);
            Address address = myList.get(0);
            return getStringBetweenFirstAndThirdComma(address.getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
            return "هناك مشكلة في الانترنت";
        }
    }

    public static String removeEgyptWithSpaceBefore(String input) {
        return input.replaceAll(" \\bEgypt\\b", "");
    }

    public static String removeNonAlphabeticCharacters(String input) {
        // Check if the string is free from English characters
        boolean isArabic = input.matches("\\p{InArabic}+");
        if (!isArabic) {
            return input;
        }

        // Remove any non-alphabetic characters from the string
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isAlphabetic(c)) {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public static String removeCommaWithSpaceAfter(String input) {
        return input.replaceAll("[,،]\\s", " ");
    }

    public static String removeSingleComma(String input) {
        // Count the number of commas in the input string
        int commaCount = input.length() - input.replaceAll("[,،]", "").length();

        // If there is only one comma in the string, remove it
        if (commaCount == 1) {
            return input.replaceAll("[,،]", "");
        }

        return input;
    }

    public static String removeVerticalBarWithSpaces(String input) {
        // Remove the vertical bar if it's preceded by one space and followed by two spaces
        String temp = input.replaceAll("\\s\\|\\s{2}", "");

        // Trim any spaces from the beginning or end of the string
        temp = temp.trim();

        // Remove the word "Egypt" if it appears at the end of the string
        temp = temp.replaceAll("(?i)Egypt$", "");

        return temp;
    }

    public static String stringEnhancer(String str) {
        return removeVerticalBarWithSpaces(removeSingleComma(removeCommaWithSpaceAfter(removeNonAlphabeticCharacters(removeEgyptWithSpaceBefore(str)))));
    }

    public static void noAvailablePathsToast(Context context) {
        Toast.makeText(context, "نأسف لعدم وجود طريق متوفرة", Toast.LENGTH_SHORT).show();
    }

    public static void checkInternetConnectionToast(Context context) {
        Toast.makeText(context, R.string.CheckInternetConnection, Toast.LENGTH_SHORT).show();
    }

    public static void failedToEstimateTime(Context context) {
        Toast.makeText(context, "هناك مشكلة أدت للفشل في تقدير الوقت", Toast.LENGTH_SHORT).show();
    }
    public static void sortingByCostToast(Context context) {
        Toast.makeText(context, R.string.PathsSortedAccordingToCost, Toast.LENGTH_SHORT).show();
    }

    public static void sortingByDistanceToast(Context context) {
        Toast.makeText(context, R.string.PathsSortedAccordingToDistance, Toast.LENGTH_SHORT).show();
    }

    public static void sortingByTimeToast(Context context) {
        Toast.makeText(context, R.string.PathsSortedAccordingToTime, Toast.LENGTH_SHORT).show();
    }

    public static void tryAgainToast(Context context) {
        Toast.makeText(context, R.string.TryAgain, Toast.LENGTH_SHORT).show();
    }

    public static String getDurationText(Response<EstimatedTime> response) {
        List<Route> routes = response.body().getRoutes();
        if(routes.size()>0) {
            Duration duration = routes.get(0).getLegs().get(0).getDuration();
            if (duration != null) return duration.getText();
        }
        return "-";
    }

    public static int extractMinutes(String duration) {
        if(!duration.equals("-")) {
            String numberString = duration.replaceAll("[^0-9]", "");
            return Integer.parseInt(numberString);
        }
        else return 0;
    }

    public static List<NearestPaths> getPathByNumber(List<List<NearestPaths>> paths, int pathNumber) {
        return paths.get(pathNumber);
    }

    public static ArrayList<String> getPathNodes(List<NearestPaths> path) {
        ArrayList<String> nodes = new ArrayList<>();
        for (int i = 0; i < getPathSize(path); i++) {
            double latitude = path.get(i).getLatitude();
            double longitude = path.get(i).getLongitude();
            nodes.add(latitude + "," + longitude);
        }
        return nodes;
    }

    public static ArrayList<String> getPathMeans(List<NearestPaths> path) {
        ArrayList<String> means = new ArrayList<>();
        for (int i = 0; i < getPathSize(path); i++) {
            String mean = path.get(i).getTransportationType();
            if (mean.equals("bus") || mean.equals("microbus")) means.add(BUS);
            else means.add(METRO);
        }
        return means;
    }

    public static int getPathSize(List<NearestPaths> path) {
        return path.size();
    }


//   M Osama: Faster method to calculate estimated time by Google
    public static void calcEstimatedTripsTime(List<List<NearestPaths>> paths, TripsTimeCallback callback) {

        /* M Osama: to store the estimated time of each Possible Path */
        int[] globalDurations = new int[paths.size()];
        Arrays.fill(globalDurations, 0);

        ExecutorService executor = Executors.newFixedThreadPool(paths.size());                      /* M Osama: Creating multiple threads to calculate the duration between each two intermediate Nodes */

        List<Future<List<String>>> responses = new ArrayList<>();

        for (int pathNumber = 0; pathNumber < paths.size(); pathNumber++) {
            List<NearestPaths> path = getPathByNumber(paths, pathNumber);
            int numberOfIntermediatePaths = getPathSize(path) - 1;

            ArrayList<String> nodes = getPathNodes(path);
            ArrayList<String> means = getPathMeans(path);

            Callable<List<String>> task = () -> {
                List<String> localDurations = new ArrayList<>();

                for (int nodeNumber = 0; nodeNumber < numberOfIntermediatePaths; nodeNumber++) {
                    int constantNodeNumberForRequest = nodeNumber;
                    Response<EstimatedTime> response = RetrofitClient.getInstance(GOOGLE_MAPS_BASE_URL)
                            .getApi().getEstimatedTime(nodes.get(constantNodeNumberForRequest), nodes.get(constantNodeNumberForRequest + 1), MODE, means.get(constantNodeNumberForRequest), MAPS_API_KEY).execute();
                    localDurations.add(getDurationText(response));                          /* M Osama: Getting a response from the multiple responses we runned then Getting duration of moving between two intermediate Nodes */
                }

                return localDurations;
            };

            Future<List<String>> future = executor.submit(task);
            responses.add(future);
        }

        executor.shutdown();                                    // Shutdown the executor after completing the tasks

        for (int i = 0; i < responses.size(); i++) {
            try {
                List<String> localDurations = responses.get(i).get();
                for (String duration : localDurations) {
                    globalDurations[i] += extractMinutes(duration);                     /* M Osama: Summing all subDurations to calculate the totalDuration */
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        callback.onTripsTimeCalculated(globalDurations);
    }


    /* M Osama: execute any function without the need of buttons */
    public static void execute(ArrowFunction function) {
        Handler handler = new Handler();
        handler.postDelayed(function::run, 1000);
    }

    public static boolean stringIsFound(String stringToCheck,String[] stringArray) {
        boolean isMatch = false;
        for(String word :stringArray) {
            if (word.equals(stringToCheck)) {
                isMatch = true;
                break;
            }
        }
        return isMatch;
    }

    /** M Osama: blindMode functions from HomeFragment */
    public static String deleteFromPenultimateComma(String input) {
        int lastCommaIndex = input.lastIndexOf("،");
        if (lastCommaIndex != -1) {
            int penultimateCommaIndex = input.lastIndexOf("،", lastCommaIndex - 1);
            if (penultimateCommaIndex != -1) { return input.substring(0, penultimateCommaIndex); }
        }
        return input;
    }

    public static String deleteFromFistComma(String input) {
        int firstCommaIndex = input.indexOf("،");
        if (firstCommaIndex != -1) {
            return input.substring(firstCommaIndex + 1).trim();
        }
        return input;
    }

    public static String getSubstringBeforeFirstComma(String input) {
        int firstCommaIndex = input.indexOf("،");
        if (firstCommaIndex != -1) {
            return input.substring(0, firstCommaIndex).trim();
        }
        return input;
    }

    public static String availableStopsToBeRead(boolean firstTime,List<String> items) {
        StringBuilder resultBuilder = new StringBuilder();

        if(firstTime)   {
            resultBuilder.append("تم استقبال اختياركم بنجاح    |     ");
            resultBuilder.append("أي من هذهِ الأماكن تريد | |");
        }
        else resultBuilder.append("أي من التالي تريد | |");

        int count = items.size();
        for (int i = 0; i < count; i++) {
            String currentItem = items.get(i);
            resultBuilder.append(currentItem);
            if (i + 1 < count) {
                resultBuilder.append(" | أَم | ");
            }
        }
        return resultBuilder.toString();
    }

    public static String removeCommas(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("،", "");
    }
    public static String convertToAleph(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("أ", "ا");
    }

    public static String[] splitLatLng(String latLng) {
        String[] splitCoordinates = latLng.split(",");

        String latitude = splitCoordinates[0].trim();
        String longitude = splitCoordinates[1].trim();

        return new String[] { latitude, longitude };
    }

    //Afnan: To replace <- in the path with a meaningful word
    public static String convertMathIntoThoma(String input) {
        return input.replace(">", "إلى");
    }

    public static String convertSlashIntoSharta(String input) {
        return input.replace("/", "شْرطة");
    }

    public static String addDotBeforeThum(String input) {
        return input.replaceAll("ثم", ".ثم");
    }

    public static String adjustErkab(String input) {
        return input.replaceAll("اركب", "اِركَب");
    }

}