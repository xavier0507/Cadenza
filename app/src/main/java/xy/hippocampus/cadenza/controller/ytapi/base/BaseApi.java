package xy.hippocampus.cadenza.controller.ytapi.base;

import android.app.Activity;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

/**
 * Created by Xavier Yin on 16/06/2017.
 */

public class BaseApi {
    protected Activity activity;
    protected GoogleAccountCredential credential;

    protected BaseApi(Activity activity, GoogleAccountCredential credential) {
        this.activity = activity;
        this.credential = credential;
    }
}
