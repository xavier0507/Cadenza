package xy.hippocampus.cadenza.view.player;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.manager.PrefsManager;
import xy.hippocampus.cadenza.util.ColorPalette;
import xy.hippocampus.cadenza.util.LogUtil;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by Xavier Yin on 2017/7/31.
 */

public class FloatingWindowMediaPlayerPanel extends LinearLayout implements View.OnClickListener {
    private static final LogUtil logUtil = LogUtil.getInstance(FloatingWindowMediaPlayerPanel.class);
    private static final int CLICK_ACTION_THRESHOLD = 50;

    private Context context;

    private WindowManager.LayoutParams updatedParameters;
    private PlayCallback playCallback;

    private PlayerViewPropertyManager playerViewPropertyManager;
    private WindowManager windowManager;
    private TouchHelper touchHelper;
    private PrefsManager prefsManager;

    private View mediaPlayerPanelParent;
    private View mediaPlayerPanelHeader;
    private View mediaPlayerPanel;
    private View mediaPlayerPanelController;

    private ImageButton scaleBtn;
    private ImageButton closeBtn;
    private ImageButton shuffleBtn;
    private ImageButton sequenceBtn;
    private ImageButton prevBtn;
    private ImageButton playBtn;
    private ImageButton pauseBtn;
    private ImageButton nextBtn;
    private ImageButton repeatOneBtn;
    private ImageButton loopAllBtn;

    private int mediaPanelColor;
    private int originalWidth;
    private int originalHeight;
    private int enlargedWidth;
    private int enlargedHeight;
    private int narrowedWidth;
    private int narrowedHeight;

    private double x;
    private double y;
    private double pressedX;
    private double pressedY;

    private boolean isFirstMeasure = true;
    private boolean isEnlarged;
    private boolean isClicked;
    private int primaryAlphaColor;

    public FloatingWindowMediaPlayerPanel(Context context) {
        this(context, null);
    }

    public FloatingWindowMediaPlayerPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingWindowMediaPlayerPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        this.touchHelper.handleTouchEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        this.mediaPlayerPanelParent = this.findViewById(R.id.player_view_parent);
        this.mediaPlayerPanelHeader = this.findViewById(R.id.media_player_panel_header);
        this.mediaPlayerPanel = this.findViewById(R.id.web_youtube);
        this.mediaPlayerPanelController = this.findViewById(R.id.media_player_panel_controller);

        this.scaleBtn = (ImageButton) this.findViewById(R.id.media_player_panel_btn_scale);
        this.closeBtn = (ImageButton) this.findViewById(R.id.media_player_panel_btn_close);
        this.shuffleBtn = (ImageButton) this.findViewById(R.id.media_player_panel_btn_shuffle);
        this.sequenceBtn = (ImageButton) this.findViewById(R.id.media_player_panel_btn_sequence);
        this.prevBtn = (ImageButton) this.findViewById(R.id.media_player_panel_btn_prev);
        this.playBtn = (ImageButton) this.findViewById(R.id.media_player_panel_btn_play);
        this.pauseBtn = (ImageButton) this.findViewById(R.id.media_player_panel_btn_pause);
        this.nextBtn = (ImageButton) this.findViewById(R.id.media_player_panel_btn_next);
        this.repeatOneBtn = (ImageButton) this.findViewById(R.id.media_player_panel_btn_repeat_one);
        this.loopAllBtn = (ImageButton) this.findViewById(R.id.media_player_panel_btn_loop_all);

        this.mediaPlayerPanelHeader.setBackgroundColor(this.mediaPanelColor);
        this.mediaPlayerPanelController.setBackgroundColor(this.mediaPanelColor);

        this.scaleBtn.setOnClickListener(this);
        this.closeBtn.setOnClickListener(this);
        this.shuffleBtn.setOnClickListener(this);
        this.sequenceBtn.setOnClickListener(this);
        this.prevBtn.setOnClickListener(this);
        this.playBtn.setOnClickListener(this);
        this.pauseBtn.setOnClickListener(this);
        this.nextBtn.setOnClickListener(this);
        this.repeatOneBtn.setOnClickListener(this);
        this.loopAllBtn.setOnClickListener(this);

        this.playerViewPropertyManager = new PlayerViewPropertyManager();
        this.updateThemeColor();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        this.logUtil.i("before originalWidth: " + this.getMeasuredWidth());
        this.logUtil.i("before originalHeight: " + this.getMeasuredHeight());

        if (this.isFirstMeasure) {
            this.originalWidth = this.getMeasuredWidth();
            this.originalHeight = this.getMeasuredHeight();

            RelativeLayout.LayoutParams mediaPlayerPanelLayoutParams = (RelativeLayout.LayoutParams) this.mediaPlayerPanel.getLayoutParams();
            mediaPlayerPanelLayoutParams.width = this.getMeasuredWidth();
            mediaPlayerPanelLayoutParams.height = (int) (this.getMeasuredWidth() * 0.6f);
            this.mediaPlayerPanel.setLayoutParams(mediaPlayerPanelLayoutParams);

            int mediaPlayerPanelHeaderHeight = this.mediaPlayerPanelHeader.getMeasuredHeight();
            int mediaPlayerPanelHeight = this.mediaPlayerPanel.getMeasuredHeight();
            int mediaPlayerPanelControllerHeight = this.mediaPlayerPanelController.getMeasuredHeight();

            this.logUtil.i("mediaPlayerPanelHeader height: " + mediaPlayerPanelHeaderHeight);
            this.logUtil.i("mediaPlayerPanel height: " + mediaPlayerPanelHeight);
            this.logUtil.i("mediaPlayerPanelController height: " + mediaPlayerPanelControllerHeight);

            this.enlargedWidth = this.originalWidth;
            this.enlargedHeight = this.originalHeight + mediaPlayerPanelHeaderHeight + mediaPlayerPanelHeight + mediaPlayerPanelControllerHeight;
            this.narrowedWidth = (int) (this.enlargedWidth * 0.38f);
            this.narrowedHeight = (int) (this.narrowedWidth * 0.68f);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mediaPlayerPanelParent.getLayoutParams();
            layoutParams.width = this.narrowedWidth;
            layoutParams.height = this.narrowedHeight;
            layoutParams.gravity = Gravity.CENTER;
            this.mediaPlayerPanelParent.setLayoutParams(layoutParams);

            this.mediaPlayerPanelHeader.setVisibility(View.GONE);
            this.mediaPlayerPanelController.setVisibility(View.GONE);

            this.isFirstMeasure = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.media_player_panel_btn_scale:
                this.playCallback.onScale();
                break;

            case R.id.media_player_panel_btn_close:
                this.playCallback.onClose();
                break;

            case R.id.media_player_panel_btn_shuffle:
                this.playCallback.onShuffle();
                break;

            case R.id.media_player_panel_btn_sequence:
                this.playCallback.onSequence();
                break;

            case R.id.media_player_panel_btn_prev:
                this.playCallback.playPrev();
                break;

            case R.id.media_player_panel_btn_play:
                this.playCallback.play();
                break;

            case R.id.media_player_panel_btn_pause:
                this.playCallback.pause();
                break;

            case R.id.media_player_panel_btn_next:
                this.playCallback.playNext();
                break;

            case R.id.media_player_panel_btn_repeat_one:
                this.playCallback.onRepeatOne();
                break;

            case R.id.media_player_panel_btn_loop_all:
                this.playCallback.onLoopAll();
                break;
        }
    }

    public void setUpdatedParameters(WindowManager.LayoutParams updatedParameters) {
        this.updatedParameters = updatedParameters;
    }

    public void setCallBack(PlayCallback playCallback) {
        this.playCallback = playCallback;
    }

    public boolean isEnlarged() {
        return this.isEnlarged;
    }

    public void startEnlargeAnim() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(this.playerViewPropertyManager, "width", this.narrowedWidth, this.enlargedWidth),
                ObjectAnimator.ofFloat(this.playerViewPropertyManager, "height", this.narrowedHeight, this.enlargedHeight)
        );
        animatorSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                mediaPlayerPanelHeader.setVisibility(View.VISIBLE);
                mediaPlayerPanelController.setVisibility(View.VISIBLE);
                isEnlarged = true;
            }
        });
        animatorSet.setDuration(400);
        animatorSet.start();
    }

    public void startNarrowAnim() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(this.playerViewPropertyManager, "width", this.enlargedWidth, this.narrowedWidth),
                ObjectAnimator.ofFloat(this.playerViewPropertyManager, "height", this.enlargedHeight, this.narrowedHeight)
        );
        animatorSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                mediaPlayerPanelHeader.setVisibility(View.GONE);
                mediaPlayerPanelController.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isEnlarged = false;
            }
        });
        animatorSet.setDuration(400);
        animatorSet.start();
    }

    public void showPlayBtn(boolean isPlaying) {
        if (isPlaying) {
            this.pauseBtn.setVisibility(VISIBLE);
            this.playBtn.setVisibility(GONE);
        } else {
            this.pauseBtn.setVisibility(GONE);
            this.playBtn.setVisibility(VISIBLE);
        }
    }

    public void blockPlayAndPause(boolean isBlocked) {
        if (isBlocked) {
            this.pauseBtn.setEnabled(false);
            this.playBtn.setEnabled(false);
        } else {
            this.pauseBtn.setEnabled(true);
            this.playBtn.setEnabled(true);
        }
    }

    public void showShuffleBtn(boolean isShuffleMode) {
        if (isShuffleMode) {
            this.sequenceBtn.setVisibility(VISIBLE);
            this.shuffleBtn.setVisibility(GONE);
        } else {
            this.sequenceBtn.setVisibility(GONE);
            this.shuffleBtn.setVisibility(VISIBLE);
        }
    }

    public void showRepeatBtn(boolean isRepeatMode) {
        if (isRepeatMode) {
            this.loopAllBtn.setVisibility(VISIBLE);
            this.repeatOneBtn.setVisibility(GONE);
        } else {
            this.loopAllBtn.setVisibility(GONE);
            this.repeatOneBtn.setVisibility(VISIBLE);
        }
    }

    public void updateThemeColor() {
        this.primaryAlphaColor = ColorPalette.getColorSuite(this.context, this.prefsManager.acquirePrimaryColor())[1];
        this.mediaPlayerPanelHeader.setBackgroundColor(this.primaryAlphaColor);
        this.mediaPlayerPanelController.setBackgroundColor(this.primaryAlphaColor);
    }

    private void init(Context context) {
        this.context = context;
        this.windowManager = (WindowManager) this.context.getSystemService(WINDOW_SERVICE);
        this.touchHelper = new TouchHelper();
        this.prefsManager = PrefsManager.getInstance(this.getContext());
    }

    /**
     * Inner Classes
     */
    private class TouchHelper {

        private void handleTouchEvent(MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    logUtil.i("ACTION_DOWN");

                    isClicked = true;

                    x = updatedParameters.x;
                    y = updatedParameters.y;

                    pressedX = motionEvent.getRawX();
                    pressedY = motionEvent.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    logUtil.i("ACTION_MOVE");

                    if (Math.abs(motionEvent.getRawX() - pressedX) > CLICK_ACTION_THRESHOLD || Math.abs(motionEvent.getRawY() - pressedY) > CLICK_ACTION_THRESHOLD) {
                        updatedParameters.x = (int) (x + (motionEvent.getRawX() - pressedX));
                        updatedParameters.y = (int) (y + (motionEvent.getRawY() - pressedY));

                        logUtil.i("updated x: " + Math.abs(updatedParameters.x));
                        logUtil.i("updated y: " + Math.abs(updatedParameters.y));

                        windowManager.updateViewLayout(FloatingWindowMediaPlayerPanel.this, updatedParameters);

                        isClicked = false;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    logUtil.i("ACTION_UP | ACTION_CANCEL");

                    if (isClicked && !isEnlarged()) {
                        startEnlargeAnim();
                        mediaPlayerPanel.setEnabled(true);
                    }
                    break;
            }
        }
    }

    private class PlayerViewPropertyManager {
        private LinearLayout.LayoutParams playerViewParams;

        PlayerViewPropertyManager() {
            this.playerViewParams = (LinearLayout.LayoutParams) mediaPlayerPanelParent.getLayoutParams();
        }

        void setWidth(float width) {
//            if (width == originalWidth) {
//                this.playerViewParams.width = MATCH_PARENT;
//                this.playerViewParams.setMargins(0, 0, 0, 0);
//            } else {
//                this.playerViewParams.width = (int) width;
//            }

            this.playerViewParams.width = (int) width;
            mediaPlayerPanelParent.setLayoutParams(this.playerViewParams);
        }

        int getWidth() {
            logUtil.i("getWidth: " + this.playerViewParams.width);
            return this.playerViewParams.width < 0 ? enlargedWidth : this.playerViewParams.width;
        }

        void setHeight(float height) {
            this.playerViewParams.height = (int) height;
            mediaPlayerPanelParent.setLayoutParams(this.playerViewParams);
        }

        int getHeight() {
            logUtil.i("getHeight: " + this.playerViewParams.height);
            return this.playerViewParams.height < 0 ? enlargedWidth : this.playerViewParams.height;
        }
    }

    public interface PlayCallback {
        void onScale();

        void onClose();

        void onShuffle();

        void onSequence();

        void playPrev();

        void play();

        void pause();

        void playNext();

        void onRepeatOne();

        void onLoopAll();
    }
}
