package com.teknisio.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.teknisio.app.data.api.ApiContract;
import com.teknisio.app.data.model.AuthUserResponse;

public class SessionManager {
  private static final String PREF_NAME = "TeknisioSession";
  private static final String KEY_TOKEN = "token";
  private static final String KEY_TOKEN_TYPE = "token_type";
  private static final String KEY_EXPIRES_IN_MS = "expires_in_ms";
  private static final String KEY_USER_ID = "user_id";
  private static final String KEY_TECHNICIAN_PROFILE_ID = "technician_profile_id";
  private static final String KEY_USER_NAME = "user_name";
  private static final String KEY_USER_EMAIL = "user_email";
  private static final String KEY_USER_PHONE = "user_phone";
  private static final String KEY_PROFILE_PHOTO = "profile_photo";
  private static final String KEY_ADDRESS = "address";
  private static final String KEY_ROLE = "role";
  private static final String KEY_ACCOUNT_STATUS = "account_status";
  private static final String KEY_ONBOARDING_SEEN = "onboarding_seen";
  private final SharedPreferences prefs;

  public SessionManager(Context context) {
    prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
  }

  public void saveAuthSession( String accessToken, String tokenType, Long expiresInMs, AuthUserResponse user) {
    if (user == null) {
      return;
    }

    SharedPreferences.Editor editor = prefs.edit();

    editor.putString(KEY_TOKEN, accessToken);
    editor.putString(KEY_TOKEN_TYPE, tokenType);
    editor.putLong(KEY_EXPIRES_IN_MS, expiresInMs != null ? expiresInMs : 0L);
    editor.putString(KEY_USER_ID, user.getUserId());
    editor.putString(KEY_TECHNICIAN_PROFILE_ID, user.getTechnicianProfileId());
    editor.putString(KEY_USER_NAME, user.getName());
    editor.putString(KEY_USER_EMAIL, user.getEmail());
    editor.putString(KEY_USER_PHONE, user.getPhoneNumber());
    editor.putString(KEY_PROFILE_PHOTO, user.getProfilePhoto());
    editor.putString(KEY_ADDRESS, user.getAddress());
    editor.putString(KEY_ROLE, user.getRole());
    editor.putString(KEY_ACCOUNT_STATUS, user.getAccountStatus());
    editor.apply();
  }

  public String getToken() {
    return prefs.getString(KEY_TOKEN, null);
  }

  public String getAuthorizationHeader() {
    return ApiContract.bearerToken(getToken());
  }

  public boolean isLoggedIn() {
    String token = getToken();
    return token != null && !token.trim().isEmpty();
  }

  public String getTokenType() {
    return prefs.getString(KEY_TOKEN_TYPE, "Bearer");
  }

  public long getExpiresInMs() {
    return prefs.getLong(KEY_EXPIRES_IN_MS, 0L);
  }

  public String getUserId() {
    return prefs.getString(KEY_USER_ID, null);
  }

  public String getTechnicianProfileId() {
    return prefs.getString(KEY_TECHNICIAN_PROFILE_ID, null);
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

  public String getProfilePhoto() {
    return prefs.getString(KEY_PROFILE_PHOTO, null);
  }

  public String getAddress() {
    return prefs.getString(KEY_ADDRESS, "");
  }

  public String getRole() {
    return prefs.getString(KEY_ROLE, null);
  }

  public String getAccountStatus() {
    return prefs.getString(KEY_ACCOUNT_STATUS, null);
  }

  public boolean isCustomer() {
    return "CUSTOMER".equals(getRole());
  }

  public boolean isTechnician() {
    return "TECHNICIAN".equals(getRole());
  }

  public boolean isAdmin() {
    return "ADMIN".equals(getRole());
  }

  public boolean hasSeenOnboarding() {
    return prefs.getBoolean(KEY_ONBOARDING_SEEN, false);
  }

  public void setOnboardingSeen() {
    prefs.edit().putBoolean(KEY_ONBOARDING_SEEN, true).apply();
  }
  public void clearSession() {
    boolean onboardingSeen = hasSeenOnboarding();
    prefs.edit().clear().apply();
    prefs.edit().putBoolean(KEY_ONBOARDING_SEEN, onboardingSeen).apply();
  }
}
