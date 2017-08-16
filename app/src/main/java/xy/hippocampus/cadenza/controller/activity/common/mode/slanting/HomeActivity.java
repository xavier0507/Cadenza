package xy.hippocampus.cadenza.controller.activity.common.mode.slanting;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.activity.common.base.BaseEPMIActivity;
import xy.hippocampus.cadenza.controller.fragment.MainListFragment;
import xy.hippocampus.cadenza.controller.fragment.base.BaseFragment;
import xy.hippocampus.cadenza.controller.fragment.base.INotifyProgress;
import xy.hippocampus.cadenza.controller.manager.FragmentStackManager;
import xy.hippocampus.cadenza.view.ProgressHelper;

import static xy.hippocampus.cadenza.model.constant.Constants.FRAG_MAIN_LIST_TAG;
import static xy.hippocampus.cadenza.model.constant.Constants.FRAG_MEDIA_PLAYER_PANEL_TAG;

/**
 * Created by Xavier Yin on 2017/7/4.
 */

public class HomeActivity extends BaseEPMIActivity implements INotifyProgress {
    private FragmentStackManager stackManager = FragmentStackManager.getInstance();
    private Fragment fragment;

    private ProgressHelper progressHelper;

    public static void updateAllTheme() {
        for (Fragment fragment : FragmentStackManager.getInstance().getValues()) {
            if (fragment != null) {
                ((BaseFragment) fragment).notifyChangeTheme();
            }
        }
    }

    @Override
    protected int layoutResourceId() {
        return R.layout.activity_mode_slanting_home;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        savedInstanceState = null;
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            this.fragment = MainListFragment.newInstance();
            this.stackManager.pushFragment(FRAG_MAIN_LIST_TAG, fragment);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction
                    .replace(R.id.frag_home, fragment, FRAG_MAIN_LIST_TAG)
                    .commit();
        }
    }

    @Override
    protected void preProcess() {
        super.preProcess();

        BaseFragment.setINotifyProgress(this);
        this.progressHelper = new ProgressHelper.Builder(this).withBlock(true).build();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        this.progressHelper.hideProgressBar();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.logUtil.i("HomeActivity onResume!");

        if (!this.stackManager.includesKey(FRAG_MAIN_LIST_TAG) && this.fragment != null) {
            this.stackManager.pushFragment(FRAG_MAIN_LIST_TAG, this.fragment);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.logUtil.i("HomeActivity onDestroy");

        this.stackManager.clearAll();
    }

    @Override
    public void onBackPressed() {
        this.processStack();
    }

    @Override
    public void notifyProgressAppear(boolean isBlocked) {
        this.progressHelper.changeBlockStatus(isBlocked);
        this.progressHelper.showProgressBar();
    }

    @Override
    public void notifyProgressDisappear() {
        this.progressHelper.hideProgressBar();
    }

    private void processStack() {
        if (this.stackManager.getSize() == 0) {
            this.confirmBack();
        }

        if (this.stackManager.getSize() == 1) {
            if (this.stackManager.includesKey(FRAG_MAIN_LIST_TAG) &&
                    this.stackManager.getContent(FRAG_MAIN_LIST_TAG).isVisible()) {
                this.confirmBack();
            } else {
                this.confirmBack();
            }
        }

        if (this.stackManager.getSize() == 2) {
            if (this.stackManager.includesKey(FRAG_MAIN_LIST_TAG) &&
                    this.stackManager.includesKey(FRAG_MEDIA_PLAYER_PANEL_TAG)) {
                this.confirmBack();
            }
        }

        if (this.stackManager.getSize() > 1) {
            for (Fragment fragment : this.stackManager.getValues()) {
                this.logUtil.i("current fragment: " + fragment.getTag());

                if (fragment != null) {
                    if (!fragment.getTag().equals(FRAG_MAIN_LIST_TAG) &&
                            !fragment.getTag().equals(FRAG_MEDIA_PLAYER_PANEL_TAG) &&
                            fragment.isVisible()) {
                        this.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                        this.stackManager.popFragment(fragment.getTag());

                        this.logUtil.i("fragment order: " + fragment.getTag());
                        this.logUtil.i("fragment isVisible: " + fragment.isVisible());
                        break;
                    }
                }
            }
        }

        this.logUtil.i("fragment stack size: " + this.stackManager.getSize());
    }

    private void confirmBack() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.exit_dialog_title))
                .setMessage(R.string.exit_dialog_message)
                .setCancelable(false)
                .setPositiveButton(R.string.action_confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}
