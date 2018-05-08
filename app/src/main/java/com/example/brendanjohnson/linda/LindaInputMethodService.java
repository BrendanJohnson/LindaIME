package com.example.brendanjohnson.linda;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.util.Log;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import android.view.inputmethod.InputConnection;

public class LindaInputMethodService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    NetListener l = new NetListener(){

        @Override
        public void onRemoteCallComplete(String output) {
            // add code to act on the JSON object that is returned
            Log.d("mydebug", "got server output");
            Log.d("mydebug", output);
        }

    };

    @Override
    public View onCreateInputView() {
        KeyboardView keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view, null);
        Keyboard keyboard = new Keyboard(this, R.xml.cangjie);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);
        return keyboardView;
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection inputConnection = getCurrentInputConnection();

        if (inputConnection != null) {
            Log.d("mydebug", "key pressed");
            char code = (char) primaryCode;
            inputConnection.commitText(String.valueOf(code), 1);

            FetchPrediction predictionClient = new FetchPrediction(l);
            predictionClient.execute();



        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}

