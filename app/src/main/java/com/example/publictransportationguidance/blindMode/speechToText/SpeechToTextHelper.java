package com.example.publictransportationguidance.blindMode.speechToText;

import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;

public class SpeechToTextHelper {
    public static final int RECOGNIZER_RESULT = 1;

    private static SpeechToTextHelper instance;
    private Intent speechToTextIntent;

    private SpeechToTextHelper(String language) {
        speechToTextIntent = createSpeechRecognitionIntent(language);
    }

    public static SpeechToTextHelper getInstance(String language) {
        if (instance == null) {
            instance = new SpeechToTextHelper(language);
        }
        return instance;
    }

    public void startSpeechRecognition(Activity activity) {
        activity.startActivityForResult(speechToTextIntent, RECOGNIZER_RESULT);
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
}
