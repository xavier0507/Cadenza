package xy.hippocampus.cadenza.controller.activity.sample;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.activity.common.base.BaseActivity;
import xy.hippocampus.cadenza.controller.manager.GoogleAccountManager;
import xy.hippocampus.cadenza.controller.manager.PrefsManager;

import static xy.hippocampus.cadenza.controller.manager.GoogleAccountManager.REQUEST_ACCOUNT_PICKER;
import static xy.hippocampus.cadenza.controller.manager.GoogleAccountManager.REQUEST_AUTHORIZATION;
import static xy.hippocampus.cadenza.controller.manager.GoogleAccountManager.REQUEST_GOOGLE_PLAY_SERVICES;
import static xy.hippocampus.cadenza.controller.manager.GoogleAccountManager.REQUEST_PERMISSION_GET_ACCOUNTS;

/**
 * Created by Xavier Yin on 6/2/17.
 */

public class YoutubeDataApiSampleActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {
    private Button sendRequestBtn;
    private TextView dataApiText;
    private ProgressDialog progressDialog;

    private GoogleAccountManager googleAccountManager;
    private PrefsManager prefsManager;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.processRequests(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    @Override
    protected int layoutResourceId() {
        return R.layout.activity_sample_youtube_data_api;
    }

    @Override
    protected void preProcess() {
        super.preProcess();
        this.googleAccountManager = GoogleAccountManager.getInstance(this);
        this.prefsManager = PrefsManager.getInstance(this);
    }

    @Override
    protected void findView() {
        this.sendRequestBtn = (Button) this.findViewById(R.id.btn_send_request);
        this.dataApiText = (TextView) this.findViewById(R.id.text_data_api);
        this.progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void assignViewSettings() {
        this.dataApiText.setText("Youtube Data Api!");
        this.progressDialog.setMessage("Calling YouTube Data API ...");
    }

    @Override
    protected void addEvents() {
        this.sendRequestBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_request:
                this.getResultsFromApi();
                break;
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        logUtil.i("AfterPermissionGranted");

        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = this.prefsManager.acquireAccountName();

            logUtil.i("accountName: " + accountName);

            if (accountName != null) {
                this.googleAccountManager.getCredential().setSelectedAccountName(accountName);
                this.getResultsFromApi();
            } else {
                this.startActivityForResult(this.googleAccountManager.getCredential().newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
            }
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    private void processRequests(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    this.dataApiText.setText("This app requires Google Play Services. Please install "
                            + "Google Play Services on your device and relaunch this app.");
                } else {
                    this.getResultsFromApi();
                }
                break;

            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

                    logUtil.i("onResult accountName: " + accountName);

                    if (accountName != null) {
                        this.prefsManager.putAccountName(accountName);
                        this.googleAccountManager.getCredential().setSelectedAccountName(accountName);
                        this.getResultsFromApi();
                    }
                }
                break;

            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    this.getResultsFromApi();
                }
                break;
        }
    }

    private void getResultsFromApi() {
        if (!this.googleAccountManager.isGooglePlayServicesAvailable()) {
            this.googleAccountManager.acquireGooglePlayServices();
        } else if (this.googleAccountManager.isCredentialAvailable()) {
            this.chooseAccount();
        } else if (!this.googleAccountManager.isDeviceOnline()) {
            this.dataApiText.setText("No network connection available.");
        } else {
            new MakeRequestTask(this.googleAccountManager.getCredential()).execute();
        }
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, List<SearchResult>> {
        private com.google.api.services.youtube.YouTube service = null;
        private Exception lastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            this.service = new com.google.api.services.youtube.YouTube.Builder(transport, jsonFactory, credential)
                    .setApplicationName("YouTube Data API Android Quickstart")
                    .build();
        }

        @Override
        protected void onPreExecute() {
            dataApiText.setText("");
            progressDialog.show();
        }

        @Override
        protected List<SearchResult> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                this.lastError = e;
                this.cancel(true);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<SearchResult> output) {
            progressDialog.hide();

            if (output == null || output.size() == 0) {
                dataApiText.setText("No results returned.");
            } else {
//                output.add(0, "Data retrieved using the YouTube Data API:");

                StringBuilder sb = new StringBuilder();

                for (SearchResult searchResult : output) {
                    sb.append(searchResult.toString()).append(" ");
                }

                logUtil.d("result: " + sb);

                dataApiText.setText(sb);
            }
        }

        @Override
        protected void onCancelled() {
            progressDialog.hide();

            if (lastError != null) {
                if (lastError instanceof GooglePlayServicesAvailabilityIOException) {
                    googleAccountManager.showGooglePlayServicesAvailabilityErrorDialog(((GooglePlayServicesAvailabilityIOException) lastError).getConnectionStatusCode());
                } else if (lastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(((UserRecoverableAuthIOException) lastError).getIntent(), REQUEST_AUTHORIZATION);
                } else {
                    dataApiText.setText("The following error occurred:\n" + lastError.getCause());
                }
            } else {
                dataApiText.setText("Request cancelled.");
            }
        }

        private List<SearchResult> getDataFromApi() throws IOException {
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("part", "snippet");
            parameters.put("maxResults", "3");
            parameters.put("q", "Mozart");
            parameters.put("type", "video");

            YouTube.Search.List searchListByKeywordRequest = this.service.search().list(parameters.get("part").toString());

            if (parameters.containsKey("maxResults")) {
                searchListByKeywordRequest.setMaxResults(Long.parseLong(parameters.get("maxResults").toString()));
            }

            if (parameters.containsKey("q") && parameters.get("q") != "") {
                searchListByKeywordRequest.setQ(parameters.get("q").toString());
            }

            if (parameters.containsKey("type") && parameters.get("type") != "") {
                searchListByKeywordRequest.setType(parameters.get("type").toString());
            }

            SearchListResponse response = searchListByKeywordRequest.execute();
            List<SearchResult> results = response.getItems();

            logUtil.d("original json: " + response.toString());

            return results;
        }
    }
}
