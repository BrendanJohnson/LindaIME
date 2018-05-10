package com.example.brendanjohnson.linda;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import android.view.KeyEvent;
import android.view.inputmethod.InputConnection;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class LindaInputMethodService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {


    private CandidateView myCandidateView;
    private KeyboardView keyboardView;
    private StringBuilder composing = new StringBuilder();
    private List<String> mySuggestions;

    NetListener l = new NetListener(){

        @Override
        public void onRemoteCallComplete(JSONArray output) {
            ArrayList<String> list = new ArrayList<String>();
            try {
                JSONArray suggestions = output.getJSONArray(1);
                for (int i=0;i<suggestions.length();i++){
                    list.add(suggestions.getString(i));
                }
                setSuggestions(list, true, true);
            }
            catch (JSONException e) {
                Log.d("mydebug", "NO SUGGESTIONS OUTPUT");
                setSuggestions(null, true, true);
            }
        }

    };

    /**
     * Helper function to commit any text being composed in to the editor.
     */
    private void commitTyped(InputConnection inputConnection) {
        if (composing.length() > 0) {
            inputConnection.commitText(composing, composing.length());
            composing.setLength(0);
            updateCandidates();
        }
    }

    /**
     * Update the list of available candidates from the current composing
     * text.  This will need to be filled in by however you are determining
     * candidates.
     */
    private void updateCandidates() {
        if (composing.length() > 0) {
            FetchPrediction predictionClient = new FetchPrediction(l);
            predictionClient.execute(composing);
        } else {
            setSuggestions(null, false, false);
        }
    }

    public void setSuggestions(List<String> suggestions, boolean completions,
                               boolean typedWordValid) {
        if (suggestions != null && suggestions.size() > 0) {
            setCandidatesViewShown(true);
        } else if (isExtractViewShown()) {
            setCandidatesViewShown(true);
        }
        mySuggestions = suggestions;
        if (myCandidateView != null) {
            myCandidateView.setSuggestions(suggestions, completions, typedWordValid);
        }
    }

    @Override
    public View onCreateInputView() {
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view, null);
        Keyboard keyboard = new Keyboard(this, R.xml.cangjie);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);
        return keyboardView;
    }

    @Override
    public View onCreateCandidatesView() {
        Log.d("mydebug", "rendering candidates view");
        myCandidateView = new CandidateView(this);
        myCandidateView.setService(this);
        return myCandidateView;
    }


    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    public void pickDefaultCandidate() {
        pickSuggestionManually(0);
    }

    public void pickSuggestionManually(int index) {
        if (composing.length() > 0) {
            commitTyped(getCurrentInputConnection());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // The InputMethodService already takes care of the back
                // key for us, to dismiss the input method if it is shown.
                // However, our keyboard could be showing a pop-up window
                // that back should dismiss, so we first allow it to do that.
                if (event.getRepeatCount() == 0 && keyboardView != null) {
                    if (keyboardView.handleBack()) {
                        return true;
                    }
                }
                break;

            case KeyEvent.KEYCODE_DEL:

                // Special handling of the delete key: if we currently are
                // composing text for the user, we want to modify that instead
                // of let the application to the delete itself.
                if (composing.length() > 0) {
                    onKey(Keyboard.KEYCODE_DELETE, null);
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_ENTER:
                // Let the underlying text editor always handle these.
                return false;
            default:

        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection != null && primaryCode == Keyboard.KEYCODE_DELETE) {
            final int length = composing.length();
            if (length > 1) {
                composing.delete(length - 1, length);
                inputConnection.setComposingText(composing, 1);
                updateCandidates();
            } else if (length > 0) {
                composing.setLength(0);
                inputConnection.commitText("", 0);
                updateCandidates();
            } else {
                keyDownUp(KeyEvent.KEYCODE_DEL);
            }
        }
        else if (inputConnection != null) {
            composing.append((char) primaryCode);
            inputConnection.setComposingText(composing, 1);
            updateCandidates();
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

