package com.example.publictransportationguidance.helpers;

import static com.example.publictransportationguidance.BuildConfig.MAPS_API_KEY;
import static com.example.publictransportationguidance.api.Api.GOOGLE_MAPS_BASE_URL;
import static com.example.publictransportationguidance.helpers.GlobalVariables.BUS;
import static com.example.publictransportationguidance.helpers.GlobalVariables.METRO;
import static com.example.publictransportationguidance.helpers.GlobalVariables.MODE;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.TripsTimeCallback;
import com.example.publictransportationguidance.api.RetrofitClient;
import com.example.publictransportationguidance.pojo.estimatedTimeResponse.EstimatedTime;
import com.example.publictransportationguidance.pojo.pathsResponse.NearestPaths;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import retrofit2.Response;

public class Functions {

    public static String[] listToArray(List<String> list) {
        String[] array = new String[list.size()];
        for (int i = 0; i < list.size(); i++)   array[i] = list.get(i);
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
    public static String getSelectedItem(AdapterView parent , int positionInDropList){
        return (String) parent.getItemAtPosition(positionInDropList);
    }
    public static int getDataSourceIndex(String[] dataSource , String selectedItem){
        return Arrays.asList(dataSource).indexOf(selectedItem);
    }

    /*M Osama: used to take only the first two words of the place picked from the map */
    public static String extractAddress(String input) {
        String[] parts = input.split(",");
        if (parts.length >= 3) {
            return parts[1].trim() + ", " + parts[2].trim();
        } else {
            return "";
        }
    }

    public static boolean freeOfNumbers(String str) {
        return str.matches("[a-zA-Z]+");
    }

    public static String getLocationName(Context context, Double latitude, Double longitude){
        Geocoder myLocation = new Geocoder(context, new Locale("ar"));
        List<Address> myList;
        try {
            myList = myLocation.getFromLocation(latitude,longitude, 1);
            Address address = (Address) myList.get(0);
            Toast.makeText(context, address.getLocality()+"", Toast.LENGTH_SHORT).show();
            return address.getLocality();
        } catch (IOException e) {  e.printStackTrace();   return "هناك مشكلة في الانترنت"; }
    }

    public static String removeEgyptWithSpaceBefore(String input) {
        return input.replaceAll(" \\bEgypt\\b", "");
    }
    public static String removeNonAlphabeticCharacters(String input) {
        // Check if the string is free from English characters
        boolean isArabic = input.matches("\\p{InArabic}+");
        if (!isArabic) { return input; }

        // Remove any non-alphabetic characters from the string
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isAlphabetic(c)) { sb.append(c); }
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
    public static String deleteSinglePipe(String string) {
        if (string.indexOf('|') == string.lastIndexOf('|')) {
            string = string.replace("|", "");
        }
        return string;
    }
    public static String deleteSinglePipeIfSucceededByTwoSpaces(String string) {
        int index = string.indexOf("|  ");
        if (index != -1 && index == string.lastIndexOf("|  ")) {
            string = string.replace("|  ", "");
        }
        return string;
    }


    public static String stringEnhancer(String str){
        return removeVerticalBarWithSpaces(removeSingleComma(removeCommaWithSpaceAfter(removeNonAlphabeticCharacters(removeEgyptWithSpaceBefore(str)))));
    }

//    public static List<Nearby> sortByDistance(List<Nearby> nearbyList) {
//        int n = nearbyList.size();
//        List<Nearby> sortedList = new ArrayList<>(nearbyList);
//        for (int i = 0; i < n-1; i++) {
//            for (int j = 0; j < n-i-1; j++) {
//                if (sortedList.get(j).getDistance() > sortedList.get(j+1).getDistance()) {
//                    Nearby temp = sortedList.get(j);
//                    sortedList.set(j, sortedList.get(j+1));
//                    sortedList.set(j+1, temp);
//                }
//            }
//        }
//        return sortedList;
//    }

    public static void noAvailablePathsToast(Context context){
        Toast.makeText(context, "نأسف لعدم وجود طريق متوفرة", Toast.LENGTH_SHORT).show();
    }
    public static void checkInternetConnectionToast(Context context){
        Toast.makeText(context, R.string.CheckInternetConnection, Toast.LENGTH_SHORT).show();
    }
    public static void searchingForResultsToast(Context context){
        Toast.makeText(context, R.string.SearchingForPaths, Toast.LENGTH_SHORT).show();
    }
    public static void sortingByCostToast(Context context){
        Toast.makeText(context, R.string.PathsSortedAccordingToCost, Toast.LENGTH_SHORT).show();
    }
    public static void sortingByDistanceToast(Context context){
        Toast.makeText(context, R.string.PathsSortedAccordingToDistance, Toast.LENGTH_SHORT).show();
    }
    public static void sortingByTimeToast(Context context){
        Toast.makeText(context, R.string.PathsSortedAccordingToTime, Toast.LENGTH_SHORT).show();
    }
    public static void tryAgainToast(Context context) {
        Toast.makeText(context, R.string.TryAgain, Toast.LENGTH_SHORT);
    }

    public static String getDurationText(Response<EstimatedTime> response) {
        return response.body().getRoutes().get(0).getLegs().get(0).getDuration().getText();
    }

    public static int extractMinutes(String duration) {
        String numberString = duration.replaceAll("[^0-9]", "");
        return Integer.parseInt(numberString);
    }

    public static List<NearestPaths> getPathByNumber(List<List<NearestPaths>> paths, int pathNumber){
        return paths.get(pathNumber);
    }

    public static ArrayList<String> getPathNodes(List<NearestPaths> path){
        ArrayList<String> nodes = new ArrayList<>();
        for(int i = 0; i< getPathSize(path); i++){
            double latitude = path.get(i).getLatitude();
            double longitude = path.get(i).getLongitude();
            nodes.add(latitude+","+longitude);
        }
        return nodes;
    }

    public static ArrayList<String> getPathMeans(List<NearestPaths> path){
        ArrayList<String> means = new ArrayList<>();
        for(int i = 0; i< getPathSize(path); i++){
            String mean = path.get(i).getTransportationType();
            if(means.equals("bus") || means.equals("microbus")) means.add(BUS);
            else                                                means.add(METRO);
        }
        return means;
    }

    public static int getPathSize(List<NearestPaths> path){
        return path.size();
    }

    public static void calcEstimatedTripsTime(String location, String destination,int numberOfPaths,List<List<NearestPaths>> paths, Activity activity, TripsTimeCallback callback) {

        /* M Osama: to store the estimated time of each Possible Path */
        int[] globalDurations = new int[numberOfPaths];
        Arrays.fill(globalDurations, 0);

        for (int pathNumber = 0; pathNumber < numberOfPaths; pathNumber++) {
            List<NearestPaths> path = getPathByNumber(paths, pathNumber);
            int numberOfIntermediatePaths = getPathSize(path) - 1;

            ArrayList<String> nodes = getPathNodes(path);
            ArrayList<String> means = getPathMeans(path);
            ArrayList<String> localDurations = new ArrayList<>();
            ArrayList<Future<Response<EstimatedTime>>> responses = new ArrayList<>();

            ExecutorService executor = Executors.newFixedThreadPool(numberOfIntermediatePaths);     /* M Osama: Creating multiple threads to calculate the duration between each two intermediate Nodes */

            /* M Osama: iteration to calculate the totalTime of each possible Path */
            for (int nodeNumber = 0; nodeNumber < numberOfIntermediatePaths; nodeNumber++) {
                int constantNodeNumberForRequest = nodeNumber;
                Future<Response<EstimatedTime>> apiResponse = executor.submit(() -> RetrofitClient.getInstance(GOOGLE_MAPS_BASE_URL).getApi().getEstimatedTime(nodes.get(constantNodeNumberForRequest), nodes.get(constantNodeNumberForRequest + 1), MODE, means.get(constantNodeNumberForRequest), MAPS_API_KEY).execute());
                responses.add(apiResponse);

                /* M Osama: summing the time between pathNodes to calculate the Path total time */
                try {
                    localDurations.add(getDurationText(responses.get(nodeNumber).get()));       /* M Osama: Getting a response from the multiple responses we runned then Getting duration of moving between two intermediate Nodes */
                    globalDurations[pathNumber] += extractMinutes(localDurations.get(nodeNumber));        /* M Osama: Summing all subDurations to calculate the totalDuration */
                } catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }
            }

            executor.shutdown();                        // Shutdown the executor after completing the tasks
        }

        callback.onTripsTimeCalculated(globalDurations);

    }




}
