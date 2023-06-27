package com.example.publictransportationguidance.blindMode.speechToText;

import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;

import androidx.annotation.Nullable;

import com.example.publictransportationguidance.Fragments.HomeFragment;

import java.util.ArrayList;

public class SpeechToTextHelper {
    public static final int MAIN_ACTIVITY_RECOGNIZER = 1;

    private static SpeechToTextHelper instance;
    private Intent speechToTextIntent;
    private HomeFragment homeFragment;

    private SpeechToTextHelper(String language) {
        speechToTextIntent = createSpeechRecognitionIntent(language);
    }

    public static SpeechToTextHelper getInstance(String language) {
        if (instance == null) {
            instance = new SpeechToTextHelper(language);
        }
        return instance;
    }

//    public void startSpeechRecognition(HomeFragment homeFragment) {
//        homeFragment.startActivityForResult(speechToTextIntent, RECOGNIZER_RESULT);
//    }

    public void startSpeechRecognition(Activity activity) {
        activity.startActivityForResult(speechToTextIntent, MAIN_ACTIVITY_RECOGNIZER);
    }

    public void startSpeechRecognition(Activity activity,int recognizer){
        activity.startActivityForResult(speechToTextIntent,recognizer);
    }

    public void startSpeechRecognition(HomeFragment homeFragment,int recognizer) {
        homeFragment.startActivityForResult(speechToTextIntent, recognizer);
    }

    private Intent createSpeechRecognitionIntent(String language) {
        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "speech to text");
        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 500);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        return speechIntent;
    }

    public static String convertHaaToTaaMarbuta(String input) {
        String pattern = "(?<=\\p{L})ه(?=\\s)|ه$";
        String replacement = "ة";
        return input.replaceAll(pattern, replacement);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == MAIN_ACTIVITY_RECOGNIZER && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && !matches.isEmpty()) {
                String speechText = matches.get(0);
                String convertedText = convertHaaToTaaMarbuta(speechText);
                // Do something with the converted text
            }
        }
    }

}

