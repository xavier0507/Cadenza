package xy.hippocampus.cadenza.controller.activity.common.base;

import android.Manifest;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.manager.GoogleAccountManager;
import xy.hippocampus.cadenza.controller.manager.PrefsManager;

import static xy.hippocampus.cadenza.controller.manager.GoogleAccountManager.REQUEST_ACCOUNT_PICKER;
import static xy.hippocampus.cadenza.controller.manager.GoogleAccountManager.REQUEST_AUTHORIZATION;
import static xy.hippocampus.cadenza.controller.manager.GoogleAccountManager.REQUEST_GOOGLE_PLAY_SERVICES;
import static xy.hippocampus.cadenza.controller.manager.GoogleAccountManager.REQUEST_PERMISSION_GET_ACCOUNTS;

/**
 * Created by Xavier Yin on 2017/7/11.
 */

public abstract class BaseEPMIActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    protected GoogleAccountManager googleAccountManager;
    protected PrefsManager prefsManager;

    protected boolean isCheckingAccountStatus;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.processRequests(requestCode, resultCode, data);
        this.onFinish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        this.chooseAccount();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        Toast.makeText(this, this.getString(R.string.hint_cannot_work_without_permissions), Toast.LENGTH_LONG).show();
//        this.openPMIsManagerOnPermissionsDenied();
    }

    @Override
    protected void preProcess() {
        super.preProcess();
        this.googleAccountManager = GoogleAccountManager.getInstance(this);
        this.prefsManager = PrefsManager.getInstance(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.logUtil.i("HomeActivity onStart");
        this.getResultsFromApi();
    }

    protected void chooseAccount() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = this.prefsManager.acquireAccountName();

            if (accountName != null) {
                this.logUtil.i("onPermissionsGranted - accountName: " + accountName);

                this.googleAccountManager.getCredential().setSelectedAccountName(accountName);
                this.getResultsFromApi();
            } else {
                if (!this.isCheckingAccountStatus) {
                    this.isCheckingAccountStatus = true;
                    this.startActivityForResult(this.googleAccountManager.getCredential().newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
                }
            }
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    this.getString(R.string.hint_request_permissions),
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
            this.logout();
        }
    }

    protected void getResultsFromApi() {
        if (!this.googleAccountManager.isGooglePlayServicesAvailable()) {
            this.googleAccountManager.acquireGooglePlayServices();
        } else if (this.googleAccountManager.isCredentialAvailable()) {
            this.chooseAccount();
        } else if (!this.googleAccountManager.isDeviceOnline()) {
            Toast.makeText(this, this.getString(R.string.toast_check_account_is_device_online), Toast.LENGTH_LONG).show();
        } else {
            if (!this.prefsManager.acquireLoginPopupMessageStatus()) {
                Toast.makeText(this, this.getString(R.string.toast_check_account_login_success), Toast.LENGTH_LONG).show();
                this.prefsManager.putLoginPopupMessageStatus();
            }

            this.onSuccess();
            this.isCheckingAccountStatus = false;
        }
    }

    protected void openPMIsManagerOnPermissionsDenied() {
        new AppSettingsDialog
                .Builder(this)
                .setRationale(this.getString(R.string.hint_cannot_work_open_app_settings))
                .build()
                .show();
    }

    protected void processRequests(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
//            case AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE:
//                if (resultCode == RESULT_CANCELED) {
//                    Toast.makeText(this, this.getString(R.string.hint_cannot_work_without_permissions), Toast.LENGTH_LONG).show();
//                }
//                break;

            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this, this.getString(R.string.toast_check_account_need_google_play_service), Toast.LENGTH_LONG).show();
                } else {
                    this.getResultsFromApi();
                }
                break;

            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

                    if (accountName != null) {
                        this.logUtil.i("REQUEST_ACCOUNT_PICKER - accountName: " + accountName);

                        this.prefsManager.clearPopupMessageStatus();
                        this.prefsManager.putAccountName(accountName);

                        this.googleAccountManager.getCredential().setSelectedAccountName(accountName);
                        this.getResultsFromApi();
                    }
                } else {
                    Toast.makeText(this, this.getString(R.string.toast_check_account_need_account_picker_fail), Toast.LENGTH_LONG).show();
                    this.isCheckingAccountStatus = false;
                }
                break;

            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    this.getResultsFromApi();
                }
                break;
        }
    }

    protected void logout() {
        this.prefsManager.clearPopupMessageStatus();
        this.prefsManager.clearAccountName();
    }

    protected void onSuccess() {
        // Do nothing for overriding
    }

    protected void onFinish() {
        // Do nothing for overriding
    }
}
