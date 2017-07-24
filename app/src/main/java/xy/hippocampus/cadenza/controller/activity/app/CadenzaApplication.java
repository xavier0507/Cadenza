package xy.hippocampus.cadenza.controller.activity.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import xy.hippocampus.cadenza.util.LogUtil;

/**
 * Created by Xavier Yin on 5/31/17.
 */

public class CadenzaApplication extends Application {
    private static final LogUtil logUtil = LogUtil.getInstance(CadenzaApplication.class);

    protected static Context cadenzaContext;
    protected static Resources cadenzaResources;

    private static FirebaseRemoteConfig firebaseRemoteConfig;
    private static FirebaseRemoteConfigSettings remoteConfigSettings;
    private static long cacheExpiration = 3600;

    private static boolean mIsForceUpdate;
    private static boolean mIsFirebaseSource;
    private static String mAppVersion;

    @Override
    public void onCreate() {
        super.onCreate();
        this.init();
        this.obtainRemoteConfig();
    }

    public static Context getCadenzaContext() {
        return cadenzaContext;
    }

    public static Resources getCadenzaResources() {
        return cadenzaResources;
    }

    public static boolean isForceUpdate() {
        return mIsForceUpdate;
    }

    public static void setForceUpdate(boolean isForceUpdate) {
        mIsForceUpdate = isForceUpdate;
    }

    public static String getAppVersion() {
        return mAppVersion;
    }

    public static boolean isFirebaseSource() {
        return mIsFirebaseSource;
    }

    public static void setFirebaseSource(boolean isFirebaseSource) {
        mIsFirebaseSource = isFirebaseSource;
    }

    public static void setAppVersion(String AppVersion) {
        mAppVersion = AppVersion;
    }

    private void init() {
        CadenzaApplication.cadenzaContext = this;
        CadenzaApplication.cadenzaResources = this.getResources();
    }

    private void obtainRemoteConfig() {
        this.firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        this.remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(false).build();
        this.firebaseRemoteConfig.setConfigSettings(this.remoteConfigSettings);

        if (this.firebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            this.cacheExpiration = 0;
        }

        this.firebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    firebaseRemoteConfig.activateFetched();

                    boolean isForceUpdate = firebaseRemoteConfig.getBoolean("isForceUpdate");
                    boolean isFirebaseSource = firebaseRemoteConfig.getBoolean("isFirebaseSource");
                    String appVersion = firebaseRemoteConfig.getString("version");

                    setForceUpdate(isForceUpdate);
                    setFirebaseSource(isFirebaseSource);
                    setAppVersion(appVersion);

                    logUtil.i("isForceUpdate: " + isForceUpdate);
                    logUtil.i("isFirebaseSource: " + isFirebaseSource);
                    logUtil.i("version: " + appVersion);
                }
            }
        });
    }
}
