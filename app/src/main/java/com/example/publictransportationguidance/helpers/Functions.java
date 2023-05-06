package com.example.publictransportationguidance.helpers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.publictransportationguidance.pojo.nearby.Nearby;

import java.io.IOException;
import java.util.ArrayList;
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

    public static List<Nearby> sortByDistance(List<Nearby> nearbyList) {
        int n = nearbyList.size();
        List<Nearby> sortedList = new ArrayList<>(nearbyList);
        for (int i = 0; i < n-1; i++) {
            for (int j = 0; j < n-i-1; j++) {
                if (sortedList.get(j).getDistance() > sortedList.get(j+1).getDistance()) {
                    Nearby temp = sortedList.get(j);
                    sortedList.set(j, sortedList.get(j+1));
                    sortedList.set(j+1, temp);
                }
            }
        }
        return sortedList;
    }


}
