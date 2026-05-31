package com.teknisio.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "TeknisioSession";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_ONBOARDING_SEEN = "onboarding_seen";

    private SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveAuthSession(String token, String userId, String userName) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, userName);
        editor.apply();
    }

    public void saveUserProfile(String email, String phone) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_PHONE, phone);
        editor.apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "User");
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    public String getUserPhone() {
        return prefs.getString(KEY_USER_PHONE, "");
    }

    public boolean hasSeenOnboarding() {
        return prefs.getBoolean(KEY_ONBOARDING_SEEN, false);
    }

    public void setOnboardingSeen() {
        prefs.edit().putBoolean(KEY_ONBOARDING_SEEN, true).apply();
    }

    public void clearSession() {
        // Keep onboarding flag, only clear auth data
        boolean onboardingSeen = hasSeenOnboarding();
        prefs.edit().clear().apply();
        prefs.edit().putBoolean(KEY_ONBOARDING_SEEN, onboardingSeen).apply();
    }
}
