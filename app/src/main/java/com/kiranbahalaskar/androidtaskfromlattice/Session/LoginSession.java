package com.kiranbahalaskar.androidtaskfromlattice.Session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.kiranbahalaskar.androidtaskfromlattice.Authentication.ActivityRegistration;
import java.util.HashMap;

public class LoginSession {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context cxt;
    int PRIVATE = 0;

    private static final String PREF_NAME = "Login";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_CITY_NAME = "city";

    public LoginSession(Context context) {

        this.cxt = context;
        pref = cxt.getSharedPreferences(PREF_NAME, PRIVATE);
        editor = pref.edit();

    }

    public void createLoginSession(String city_name) {

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_CITY_NAME, city_name);
        editor.commit();
    }

    public void checkLogin() {

        if (!this.isLoggedIn()) {

            Intent i = new Intent(cxt, ActivityRegistration.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            cxt.startActivity(i);
        }
    }

    public HashMap<String, String> getUserDetails() {

        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_CITY_NAME, pref.getString(KEY_CITY_NAME, null));
        return user;

    }

    public void logoutUser() {

        editor.clear();
        editor.commit();

        Intent i = new Intent(cxt, ActivityRegistration.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        cxt.startActivity(i);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }


}
