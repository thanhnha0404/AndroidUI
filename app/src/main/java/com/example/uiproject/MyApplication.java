package com.example.uiproject;

import android.app.Application;


import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Map config = new HashMap();
        config.put("cloud_name", "dk6kku8el");
        config.put("api_key", "462585933415641");
        config.put("api_secret", "w2ee_T1aj5cAVjuvJJXBMLukmpw");
        config.put("secure", true); // ép Cloudinary trả HTTPS
        MediaManager.init(this, config);
    }
}
