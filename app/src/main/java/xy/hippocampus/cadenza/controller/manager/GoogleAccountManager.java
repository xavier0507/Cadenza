package xy.hippocampus.cadenza.controller.manager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;

import java.util.Arrays;

import xy.hippocampus.cadenza.controller.activity.app.CadenzaApplication;

/**
 * Created by Xavier Yin on 6/3/17.
 */

public class GoogleAccountManager {
    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    public static final String[] SCOPES = {YouTubeScopes.YOUTUBE_READONLY};

    private static GoogleAccountManager instance = null;
    private static GoogleAccountCredential credential;

    private Activity activity;

    private GoogleAccountManager(Activity activity) {
        credential = GoogleAccountCredential
                .usingOAuth2(CadenzaApplication.getCadenzaContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        this.activity = activity;
    }

    public synchronized static final GoogleAccountManager getInstance(Activity activity) {
        if (instance == null) {
            instance = new GoogleAccountManager(activity);
        }

        return instance;
    }

    public GoogleAccountCredential getCredential() {
        return (credential == null) ? null : credential;
    }

    public boolean isCredentialAvailable() {
        return (credential.getSelectedAccountName() == null) ? true : false;
    }

    public boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) this.activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this.activity);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    public void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this.activity);

        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            this.showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    public void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(this.activity, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }
}
