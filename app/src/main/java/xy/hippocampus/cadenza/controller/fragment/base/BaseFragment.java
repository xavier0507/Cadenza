package xy.hippocampus.cadenza.controller.fragment.base;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xy.hippocampus.cadenza.BuildConfig;
import xy.hippocampus.cadenza.controller.fragment.MainListFragment;
import xy.hippocampus.cadenza.controller.manager.PrefsManager;
import xy.hippocampus.cadenza.util.ColorPalette;
import xy.hippocampus.cadenza.util.LogUtil;

import static android.app.Activity.RESULT_OK;
import static xy.hippocampus.cadenza.controller.manager.GoogleAccountManager.REQUEST_AUTHORIZATION;

/**
 * Created by Xavier Yin on 2017/7/16.
 */

public abstract class BaseFragment extends Fragment {
    protected LogUtil logUtil = LogUtil.getInstance(MainListFragment.class);
    protected PrefsManager prefsManager = PrefsManager.getInstance(this.getActivity());

    protected static INotifyProgress NOTIFY_PROGRESS_CALLBACK;
    protected boolean isCheckingAccountStatus;

    protected int primaryDarkColor;
    protected int primaryColor;
    protected int accentColor;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    this.onAuthSuccess();
                }
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(this.layoutResourceId(), container, false);
        this.preProcess(view);
        this.findView(view);
        this.assignViewSettings(view);
        this.addEvents(view);
        this.postProcess(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.updateUiElements();
    }

    protected void preProcess(View root) {
        this.updateTheme();
    }

    protected void findView(View root) {
        // Do nothing for overriding
    }

    protected void assignViewSettings(View root) {
        // Do nothing for overriding
    }

    protected void addEvents(View root) {
        // Do nothing for overriding
    }

    protected void postProcess(View root) {
        // Do nothing for overriding
    }

    protected void onAuthSuccess() {
        // Do nothing for overriding
    }

    protected void updateTheme() {
        this.primaryColor = prefsManager.acquirePrimaryColor();
        this.primaryDarkColor = ColorPalette.getColorSuite(this.getActivity(), this.primaryColor)[0];
        this.accentColor = ColorPalette.getColorSuite(this.getActivity(), this.primaryColor)[2];
    }

    protected void updateUiElements() {
        this.setStatusBarColor();
        this.setSupportActionBarColor();
        this.setRecentApp();
    }

    protected void setStatusBarColor() {
        this.getActivity().getWindow().setStatusBarColor(this.primaryDarkColor);
    }

    protected void setSupportActionBarColor() {
        ((AppCompatActivity) this.getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + Integer.toHexString(this.primaryColor))));
    }

    protected void setRecentApp() {
    }

    public INotifyProgress getProgressCallback() {
        return NOTIFY_PROGRESS_CALLBACK;
    }

    public boolean isDebugVersion() {
        return BuildConfig.DEBUG;
    }

    public String getCurrentFlavor() {
        return BuildConfig.FLAVOR;
    }

    public String getCurrentBuildType() {
        return BuildConfig.BUILD_TYPE;
    }

    public String getCurrentVersion() {
        return BuildConfig.VERSION_NAME;
    }

    public static void setINotifyProgress(INotifyProgress notifyProgress) {
        NOTIFY_PROGRESS_CALLBACK = notifyProgress;
    }

    abstract protected int layoutResourceId();
}
