package com.example.publictransportationguidance.HelperClasses;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.AdapterView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
        List<Address> myList = null;
        try {
            myList = myLocation.getFromLocation(latitude,longitude, 1);
            Address address = (Address) myList.get(0);
            Toast.makeText(context, address.getLocality()+"", Toast.LENGTH_SHORT).show();
            return address.getLocality();
        } catch (IOException e) {  e.printStackTrace();   return "هناك مشكلة في الانترنت"; }
    }



}
