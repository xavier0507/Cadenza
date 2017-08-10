package xy.hippocampus.cadenza.view;

import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.util.LogUtil;
import xy.hippocampus.cadenza.view.progress.CircleProgress;

/**
 * Created by Xavier Yin on 2017/7/21.
 */

public class ProgressHelper implements View.OnTouchListener {
    private LogUtil logUtil = LogUtil.getInstance(this.getClass());

    private AppCompatActivity appCompatActivity;

    private ViewGroup rootView;
    private ViewGroup topView;
    private CircleProgress progressBar;

    private boolean isBlocked;

    public ProgressHelper(AppCompatActivity appCompatActivity,
                          boolean isBlocked) {
        this.appCompatActivity = appCompatActivity;
        this.isBlocked = isBlocked;

        this.init();
        this.findRoot();
        this.addProgressView();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return this.isBlocked;
    }

    public void showProgressBar() {
        this.updateProgressCircleColor();
        this.topView.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        this.topView.setVisibility(View.GONE);
    }

    public void changeBlockStatus(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    private void init() {
        if (this.topView == null) {
            this.topView = new FrameLayout(this.appCompatActivity);
            this.progressBar = new CircleProgress(this.appCompatActivity);
            this.progressBar.setId(R.id.progress_bar_id);

            this.topView.setLayoutParams(
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
            this.topView.setBackgroundResource(R.color.color_11000000);

            this.progressBar.setLayoutParams(
                    new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            Gravity.CENTER));

            this.topView.setOnTouchListener(this);
        }
    }

    private void findRoot() {
        this.rootView = (ViewGroup) this.appCompatActivity.findViewById(R.id.attached_root);

        if (this.rootView == null) {
            throw new RuntimeException("缺少RootView");
        }
    }

    private void addProgressView() {
        this.topView.removeAllViews();
        this.topView.addView(this.progressBar);
        this.rootView.addView(this.topView);
    }

    public void updateProgressCircleColor() {
        this.progressBar.updateCircle();
    }

    public static class Builder {
        private AppCompatActivity appCompatActivity;
        private boolean isBlocked;

        public Builder(AppCompatActivity appCompatActivity) {
            this.appCompatActivity = appCompatActivity;
        }

        public Builder withBlock(boolean isBlocked) {
            this.isBlocked = isBlocked;
            return this;
        }

        public ProgressHelper build() {
            return new ProgressHelper(this.appCompatActivity, this.isBlocked);
        }
    }
}
