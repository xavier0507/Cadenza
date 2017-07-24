package xy.hippocampus.cadenza.controller.ytapi.base;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.activity.app.CadenzaApplication;
import xy.hippocampus.cadenza.controller.manager.GoogleAccountManager;
import xy.hippocampus.cadenza.util.LogUtil;

/**
 * Created by Xavier Yin on 6/15/17.
 */

public abstract class BaseAsyncTask<X, Y, Z> extends AsyncTask<X, Y, Z> {
    protected static final LogUtil logutil = LogUtil.getInstance(BaseAsyncTask.class);

    protected Activity activity;
    protected IAsyncTaskActCallback<X, Y, Z> actCallback;

    protected com.google.api.services.youtube.YouTube service = null;
    protected Exception lastError = null;

    public BaseAsyncTask(Activity activity, IAsyncTaskActCallback actCallback, GoogleAccountCredential credential) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        this.activity = activity;
        this.actCallback = actCallback;
        this.service = new com.google.api.services.youtube.YouTube.Builder(transport, jsonFactory, credential)
                .setApplicationName(CadenzaApplication.getCadenzaResources().getResourceName(R.string.app_name))
                .build();
    }

    @Override
    protected void onPreExecute() {
        this.actCallback.onActPreExecute();
    }

    @Override
    protected Z doInBackground(X... params) {
        try {
            return this.getDataFromApi(params);
        } catch (Exception e) {
            this.lastError = e;
            this.cancel(true);
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Y... values) {
        this.actCallback.onActProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Z z) {
        if (z == null) {
            Toast.makeText(this.activity, "無結果", Toast.LENGTH_LONG).show();
        } else {
            this.actCallback.onActPostExecute(z);
        }
    }

    @Override
    protected void onCancelled() {
        if (this.lastError != null) {
            if (this.lastError instanceof GooglePlayServicesAvailabilityIOException) {
                GoogleAccountManager.getInstance(this.activity).showGooglePlayServicesAvailabilityErrorDialog(((GooglePlayServicesAvailabilityIOException) this.lastError).getConnectionStatusCode());
            } else if (this.lastError instanceof UserRecoverableAuthIOException) {
                this.actCallback.onActCancelled(this.lastError);
            } else {
                if (this.lastError.getCause() != null) {
                    Toast.makeText(this.activity, "發生下列錯誤請求:\n" + this.lastError.getCause(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this.activity, "發生錯誤請求", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(this.activity, "存取請求取消。", Toast.LENGTH_LONG).show();
        }

        this.actCallback.onActCancelFinished();
    }

    protected abstract Z getDataFromApi(X... params) throws IOException;
}
