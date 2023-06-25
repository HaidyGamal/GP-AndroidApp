package com.example.publictransportationguidance.blindMode.textToSpeech;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

public class TextToSpeechHelper {

    private static TextToSpeechHelper instance;
    private TextToSpeech textToSpeech;

    private TextToSpeechHelper(Context context, String language) {
        textToSpeech = initialize(context, language);
    }

    public static TextToSpeechHelper getInstance(Context context, String language) {
        if (instance == null) instance = new TextToSpeechHelper(context, language);
        return instance;
    }

    private TextToSpeech initialize(Context context, String language) {
        return new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(new Locale(language));
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                    Log.e("TTS", "Language Not Supported");
            } else {
                Log.e("TTS", "Initialization Failed");
            }
        });
    }

    public void speak(String textToBeConvertedToSpeech, final Runnable onSpeechComplete) {
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                // Speech started, do nothing
            }

            @Override
            public void onDone(String utteranceId) {
                if (utteranceId.equals("utteranceId")) {
                    if (onSpeechComplete != null) {
                        new Handler(Looper.getMainLooper()).post(onSpeechComplete);
                    }
                }
            }

            @Override
            public void onError(String utteranceId) {
                // Speech error occurred, do nothing
            }
        });

        HashMap<String, String> params = new HashMap<>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");

        textToSpeech.speak(textToBeConvertedToSpeech, TextToSpeech.QUEUE_FLUSH, params);
    }

    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}

