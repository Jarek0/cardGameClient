package edu.pollub.pl.cardgameclient.common.activity;

import android.content.SharedPreferences;

public abstract class SharedPreferencesActivity extends LoadingActivity {

    private static final String PREF_ID = "ID";

    public void saveString(String key, String value) {
        SharedPreferences sharedPref = this.getSharedPreferences(PREF_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String fetchString(String key) {
        SharedPreferences sharedPref = this.getSharedPreferences(PREF_ID, MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }

    public void removeString(String key) {
        SharedPreferences sharedPref = this.getSharedPreferences(PREF_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.apply();
    }

}
