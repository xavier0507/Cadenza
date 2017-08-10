package xy.hippocampus.cadenza.controller.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;

import xy.hippocampus.cadenza.R;

/**
 * Created by Xavier Yin on 6/4/17.
 */

public class PrefsManager {
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String PREF_LOGIN_POPUP_MESSAGE = "accountLogin";
    private static final String PREF_IS_SHUFFLE = "isShuffle";
    private static final String PREF_IS_REPEAT_ONE = "isRepeatOne";
    private static final String PREF_PRIMARY_COLOR = "primaryColor";

    private static PrefsManager instance = null;

    private Context context;

    private PrefsManager(Context context) {
        this.context = context;
    }

    public synchronized static final PrefsManager getInstance(Context context) {
        return instance = ((instance != null) ? instance : new PrefsManager(context));
    }

    /**
     * Account Area
     */
    public void putAccountName(String accountName) {
        this.putStringContents(PREF_ACCOUNT_NAME, accountName);
    }

    public String acquireAccountName() {
        SharedPreferences preferences = this.context.getSharedPreferences(PREF_ACCOUNT_NAME, Context.MODE_PRIVATE);
        return preferences.getString(PREF_ACCOUNT_NAME, null);
    }

    public void clearAccountName() {
        SharedPreferences settings =  this.context.getSharedPreferences(PREF_ACCOUNT_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(PREF_ACCOUNT_NAME);
        editor.apply();
    }

    /**
     * Popup Message Area
     */
    public void putLoginPopupMessageStatus() {
        this.putBooleanContents(PREF_LOGIN_POPUP_MESSAGE, true);
    }

    public boolean acquireLoginPopupMessageStatus() {
        SharedPreferences preferences = this.context.getSharedPreferences(PREF_LOGIN_POPUP_MESSAGE, Context.MODE_PRIVATE);
        return preferences.getBoolean(PREF_LOGIN_POPUP_MESSAGE, false);
    }

    public void clearPopupMessageStatus() {
        SharedPreferences settings = this.context.getSharedPreferences(PREF_LOGIN_POPUP_MESSAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(PREF_LOGIN_POPUP_MESSAGE);
        editor.apply();
    }

    /**
     * Is Shuffle Area
     */
    public void putIsShuffleStatus(boolean isShuffleMode) {
        this.putBooleanContents(PREF_IS_SHUFFLE, isShuffleMode);
    }

    public boolean acquireIsShuffleStatus() {
        SharedPreferences preferences = this.context.getSharedPreferences(PREF_IS_SHUFFLE, Context.MODE_PRIVATE);
        return preferences.getBoolean(PREF_IS_SHUFFLE, false);
    }

    /**
     * Is RepeatOne Area
     */
    public void putIsRepeatOneStatus(boolean isRepeatOneMode) {
        this.putBooleanContents(PREF_IS_REPEAT_ONE, isRepeatOneMode);
    }

    public boolean acquireIsRepeatOneStatus() {
        SharedPreferences preferences = this.context.getSharedPreferences(PREF_IS_REPEAT_ONE, Context.MODE_PRIVATE);
        return preferences.getBoolean(PREF_IS_REPEAT_ONE, false);
    }

    /**
     * Colors
     */
    public void putPrimaryColor(int primaryColor) {
        this.putIntContents(PREF_PRIMARY_COLOR, primaryColor);
    }

    public int acquirePrimaryColor() {
        SharedPreferences preferences = this.context.getSharedPreferences(PREF_PRIMARY_COLOR, Context.MODE_PRIVATE);
        return preferences.getInt(PREF_PRIMARY_COLOR, ContextCompat.getColor(this.context, R.color.colorPrimary));
    }

    /**
     * Private Methods
     */
    private void putStringContents(String key, String value) {
        SharedPreferences settings = this.context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void putBooleanContents(String key, boolean value) {
        SharedPreferences settings = this.context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void putIntContents(String key, int value) {
        SharedPreferences settings = this.context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
    }
}
