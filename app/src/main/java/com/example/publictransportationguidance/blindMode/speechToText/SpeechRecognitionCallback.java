package com.example.publictransportationguidance.blindMode.speechToText;

import android.widget.EditText;

public interface SpeechRecognitionCallback {
    void onSpeechRecognitionResult(String result, EditText targetEditText);
    void onSpeechRecognitionResult(String result);
}