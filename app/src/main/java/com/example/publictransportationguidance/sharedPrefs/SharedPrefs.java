package com.example.publictransportationguidance.sharedPrefs;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class SharedPrefs {
    private static SharedPreferences sharedPrefs =null;
    private static String SHARED_PREFS_NAME="TawsilaSharedPrefs";                                    /*xml file name*/
    private static SharedPreferences.Editor prefsEditor;

    public static void init(Context context){
        if(sharedPrefs==null){
            sharedPrefs=context.getSharedPreferences(SHARED_PREFS_NAME,Activity.MODE_PRIVATE);
        }
    }

    public static SharedPreferences getSharedPreferences() {
        return sharedPrefs;
    }

    //Overloaden Reading Methods
    public static String readMap(String key, String defValue)          { return sharedPrefs.getString(key, defValue); }
    public static boolean readMap(String key, boolean defValue)        { return sharedPrefs.getBoolean(key, defValue); }
    public static Integer readMap(String key, int defValue)            { return sharedPrefs.getInt(key, defValue); }
    public static Float readMap(String key, float defValue)             { return sharedPrefs.getFloat(key,defValue); }
    public static Long readMap(String key, Long defValue)               { return sharedPrefs.getLong(key,defValue); }
    public static Set<String> readMap(String key, Set<String> defValue){ return sharedPrefs.getStringSet(key,defValue); }
    public static Map readMap()                                        { return sharedPrefs.getAll(); }
    public static ArrayList readValues()                               { Collection<String> vals= readMap().values(); return new ArrayList<String>(vals);}
    public static ArrayList readKeys()                                 { Collection<String> keys=readMap().keySet(); return new ArrayList<String>(keys); }

    //Overloaden Writing Methods
    public static void write(String key, String value)      { prefsEditor = sharedPrefs.edit(); prefsEditor.putString(key, value).commit();}
    public static void write(String key, boolean value)         { prefsEditor = sharedPrefs.edit(); prefsEditor.putBoolean(key, value).commit();}
    public static void write(String key, Integer value)         { prefsEditor = sharedPrefs.edit(); prefsEditor.putInt(key, value).commit();}
    public static void write(String key, Set<String> value)     { prefsEditor = sharedPrefs.edit(); prefsEditor.putStringSet(key, value).commit();}
    public static void write(String key, Float value)           { prefsEditor = sharedPrefs.edit(); prefsEditor.putFloat(key, value).commit();}
    public static void write(String key, Long value)            { prefsEditor = sharedPrefs.edit(); prefsEditor.putLong(key, value).commit();}

    //Removing Value
    public static String remove(String key){
        String removedVal="";
        if(getSize()>0){
            removedVal=(String) sharedPrefs.getString(key,"Not Found");
            prefsEditor= sharedPrefs.edit(); prefsEditor.remove(key).commit();
        }
        return removedVal;
    }

    public static void clear(){ write("",""); prefsEditor.clear().commit();}        //Clear Preferences
    public static int getSize(){return readMap().size();}                           //Return Preferences size
}
