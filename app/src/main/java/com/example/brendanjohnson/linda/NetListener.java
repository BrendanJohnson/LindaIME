package com.example.brendanjohnson.linda;

import org.json.JSONArray;

public interface NetListener {
    public void onRemoteCallComplete(JSONArray jsonFromNet);
}
