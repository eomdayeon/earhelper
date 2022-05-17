package com.example.myapplication;

import android.app.Application;

import java.util.ArrayList;

public class SavedWords extends Application {
    public static ArrayList<String> savedWords = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
    }
    public static ArrayList<String> getSavedWords() {
        return savedWords;
    }
}
