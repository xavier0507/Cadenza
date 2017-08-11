package xy.hippocampus.cadenza.controller.activity.common.mode;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Handler;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.activity.common.base.BaseEPMIActivity;
import xy.hippocampus.cadenza.controller.activity.common.mode.slanting.HomeActivity;

/**
 * Created by Xavier Yin on 2017/7/10.
 */

public class SplashActivity extends BaseEPMIActivity {
    private ViewGroup splashRootLayout;
    private ViewGroup splashTopContainerLayout;
    private ViewGroup splashMediumContainerLayout;
    private ViewGroup splashBottomContainerLayout;

    private ImageView clef01ImageView;
    private ImageView clef02ImageView;
    private TextView appNameText;

    @Override
    protected int layoutResourceId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void findView() {
        super.findView();

        this.splashRootLayout = (ViewGroup) this.findViewById(R.id.layout_splash_root);
        this.splashTopContainerLayout = (ViewGroup) this.findViewById(R.id.layout_top_container_root);
        this.splashMediumContainerLayout = (ViewGroup) this.findViewById(R.id.layout_medium_container_root);
        this.splashBottomContainerLayout = (ViewGroup) this.findViewById(R.id.layout_bottom_container_root);

        this.clef01ImageView = (ImageView) this.findViewById(R.id.img_clef01);
        this.clef02ImageView = (ImageView) this.findViewById(R.id.img_clef02);
        this.appNameText = (TextView) this.findViewById(R.id.text_app_name);
    }

    @Override
    protected void assignViewSettings() {
        super.assignViewSettings();

        this.splashRootLayout.setBackgroundColor(this.primaryColor);
        this.splashTopContainerLayout.setBackgroundColor(this.primaryColor);
        this.splashMediumContainerLayout.setBackgroundColor(this.accentColor);
        this.splashBottomContainerLayout.setBackgroundColor(this.primaryColor);
    }

    @Override
    protected void onSuccess() {
        this.startAnim();
    }

    private void startAnim() {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator objectAnimator01 = ObjectAnimator.ofFloat(this.clef01ImageView, "rotationY", 0, 180, 180, 0);
        objectAnimator01.setInterpolator(new DecelerateInterpolator(10));
        objectAnimator01.setDuration(500);

        ObjectAnimator objectAnimator02 = ObjectAnimator.ofFloat(this.clef02ImageView, "rotation", 0, -15, 5, 0);
        objectAnimator02.setInterpolator(new OvershootInterpolator(2));
        objectAnimator01.setDuration(500);
        objectAnimator02.setStartDelay(1200);

        ObjectAnimator objectAnimator03 = ObjectAnimator.ofFloat(this.clef01ImageView, "rotationX", 0, 180);
        objectAnimator03.setInterpolator(new DecelerateInterpolator(10));
        objectAnimator03.setStartDelay(1400);

        ObjectAnimator objectAnimator04 = ObjectAnimator.ofFloat(this.clef01ImageView, "rotation", 0, 10);
        objectAnimator04.setInterpolator(new DecelerateInterpolator(10));
        objectAnimator04.setStartDelay(1600);

        ObjectAnimator objectAnimator05 = ObjectAnimator.ofFloat(this.appNameText, "alpha", 1, 0, 1);
        objectAnimator05.setInterpolator(new DecelerateInterpolator(10));
        objectAnimator05.setStartDelay(1700);

        animatorSet.playTogether(
                objectAnimator01,
                objectAnimator02,
                objectAnimator03,
                objectAnimator04,
                objectAnimator05
        );
        animatorSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }, 500);
            }
        });

        animatorSet.setDuration(1600);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.start();
    }
}
