package xy.hippocampus.cadenza.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.manager.PrefsManager;
import xy.hippocampus.cadenza.util.ColorPalette;
import xy.hippocampus.cadenza.util.LogUtil;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by Xavier Yin on 2017/6/22.
 */

@SuppressLint({"NewApi"})
public class MediaPlayerPanel extends LinearLayout {
    private static final LogUtil logUtil = LogUtil.getInstance(MediaPlayerPanel.class);

    private static final int MIN_PLAYER_VIEW_MARGIN_DP = 0;
    private static final float MIN_COMMON_SCALE_RATIO = 0.58f;
    private static final float MIN_LANDSCAPE_SCALE_RATIO = 0.25f;
    private static final float PLAYER_VIEW_RATIO = 16f / 13f;

    private Context context;

    private PlayerCallback playerCallback;
    private PlayerViewPropertyManager playerViewPropertyManager;

    private View playerPanelView;
    private View mediaPlayerPanelHeader;
    private View mediaPlayerPanelController;
    private View contentView;

    private int minPlayerViewMarginPx;
    private float currentScaleRatio;
    private float minScaleRatio;

    private int originalWidth;
    private int originalHeight;
    private float completeScrollRange;
    private float intoSmallestRange;

    private boolean isFirstMeasure = true;
    private boolean canHide = false;

    private PlayerTouchHelper playerTouchHelper;

    private PrefsManager prefsManager;
    private int primaryAlphaColor;

    public MediaPlayerPanel(Context context) {
        this(context, null);
    }

    public MediaPlayerPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaPlayerPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.prefsManager = PrefsManager.getInstance((Activity) this.context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (this.getChildCount() < 2) {
            throw new RuntimeException(this.getResources().getString(R.string.exception_youtube_player_view_child_less_than_two));
        }

        if (this.getChildAt(0).getId() != R.id.player_view_parent) {
            throw new RuntimeException(this.getResources().getString(R.string.exception_youtube_player_view_no_id));
        }

        this.playerPanelView = this.getChildAt(0);
        this.mediaPlayerPanelHeader = this.findViewById(R.id.media_player_panel_header);
        this.mediaPlayerPanelController = this.findViewById(R.id.media_player_panel_controller);
        this.contentView = this.getChildAt(1);

        this.updateColor();
        this.init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (this.isFirstMeasure) {
            this.completeScrollRange = this.getMeasuredHeight() - this.minScaleRatio * this.originalHeight - this.minPlayerViewMarginPx;
            this.intoSmallestRange = this.completeScrollRange;
            this.isFirstMeasure = false;

            this.intoSmallestPlayerView();

            this.logUtil.i("completeScrollRange: " + this.completeScrollRange);
            this.logUtil.i("intoSmallestRange: " + this.intoSmallestRange);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        logUtil.d("event: " + event.getAction());
        this.playerTouchHelper.handleTouchEvent(event);
        return super.onInterceptTouchEvent(event);
    }

    public void setPlayerCallback(PlayerCallback playerCallback) {
        this.playerCallback = playerCallback;
    }

    public void show() {
        this.setVisibility(VISIBLE);
        this.enlargePlayerView();
    }

    public void enlargePlayerView() {
        this.updateColor();

        this.mediaPlayerPanelHeader.setVisibility(View.VISIBLE);
        this.mediaPlayerPanelController.setVisibility(View.VISIBLE);

        if (this.currentScaleRatio == this.minScaleRatio) {
            ViewGroup.LayoutParams params = getLayoutParams();
            params.width = MATCH_PARENT;
            params.height = MATCH_PARENT;
            this.setLayoutParams(params);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(this.playerViewPropertyManager, "width", this.playerViewPropertyManager.getWidth(), this.originalWidth),
                ObjectAnimator.ofFloat(this.playerViewPropertyManager, "height", this.playerViewPropertyManager.getHeight(), this.originalHeight * 2),
                ObjectAnimator.ofInt(this.playerViewPropertyManager, "marginTop", this.playerViewPropertyManager.getMarginTop(), 0),
                ObjectAnimator.ofInt(this.playerViewPropertyManager, "marginRight", this.playerViewPropertyManager.getMarginRight(), 0),
                ObjectAnimator.ofInt(this.playerViewPropertyManager, "contentMarginTop", this.playerViewPropertyManager.getContentMarginTop(), 0),
                ObjectAnimator.ofInt(this.playerViewPropertyManager, "contentMarginRight", this.playerViewPropertyManager.getContentMarginRight(), 0),
                ObjectAnimator.ofFloat(this.playerViewPropertyManager, "z", this.playerViewPropertyManager.getZ(), 0f),
                ObjectAnimator.ofFloat(this.mediaPlayerPanelHeader, "alpha", this.mediaPlayerPanelHeader.getAlpha(), 1.0f),
                ObjectAnimator.ofFloat(this.mediaPlayerPanelController, "alpha", this.mediaPlayerPanelController.getAlpha(), 1.0f)
        );
        animatorSet.setDuration(200);
        animatorSet.start();

        this.currentScaleRatio = 1.0f;
        this.canHide = false;
    }

    public void intoSmallestPlayerView() {
        this.mediaPlayerPanelHeader.setVisibility(View.INVISIBLE);
        this.mediaPlayerPanelController.setVisibility(View.INVISIBLE);
        this.updateVideoView(2500);
        this.startNarrowAnimator(0);
    }

    public void narrowPlayerView() {
        this.updateColor();
        this.startNarrowAnimator(200);
    }

    public boolean isCanHide() {
        return this.canHide;
    }

    public void updateColor() {
        this.primaryAlphaColor = ColorPalette.getColorSuite(this.context, this.prefsManager.acquirePrimaryColor())[1];
        this.mediaPlayerPanelHeader.setBackgroundColor(this.primaryAlphaColor);
        this.mediaPlayerPanelController.setBackgroundColor(this.primaryAlphaColor);
    }

    private void init() {
        this.playerTouchHelper = new PlayerTouchHelper();
        this.playerViewPropertyManager = new PlayerViewPropertyManager();

        this.minPlayerViewMarginPx = MIN_PLAYER_VIEW_MARGIN_DP * (this.getContext().getResources().getDisplayMetrics().densityDpi / 160);
        this.currentScaleRatio = 1f;

        final int orientation = this.playerPanelView.getContext().getResources().getConfiguration().orientation;
        this.minScaleRatio = (orientation == Configuration.ORIENTATION_LANDSCAPE) ? MIN_LANDSCAPE_SCALE_RATIO : MIN_COMMON_SCALE_RATIO;

        this.originalWidth = this.playerPanelView.getContext().getResources().getDisplayMetrics().widthPixels;
        this.originalHeight = this.originalWidth;

        ViewGroup.LayoutParams params = this.playerPanelView.getLayoutParams();
        params.width = this.originalWidth;
        params.height = this.originalHeight * 2;
        this.playerPanelView.setLayoutParams(params);

        this.mediaPlayerPanelHeader.setOnTouchListener(this.onTouchListenerForBlockingEventThroughtout);
        this.mediaPlayerPanelController.setOnTouchListener(this.onTouchListenerForBlockingEventThroughtout);
    }

    private void updateVideoView(int newMarginY) {
        if (this.currentScaleRatio == this.minScaleRatio) {
            ViewGroup.LayoutParams params = this.getLayoutParams();
            params.width = MATCH_PARENT;
            params.height = MATCH_PARENT;
            this.setLayoutParams(params);
        }

        this.canHide = false;

        if (newMarginY > this.completeScrollRange) {
            newMarginY = (int) this.completeScrollRange;
        }

        if (newMarginY < 0) {
            newMarginY = 0;
        }

        float marginPercent = (this.completeScrollRange - newMarginY) / this.completeScrollRange;
        float playerSizePercent = this.minScaleRatio + (1f - this.minScaleRatio) * marginPercent;

        this.playerViewPropertyManager.setWidth(this.originalWidth * playerSizePercent);
        this.playerViewPropertyManager.setHeight(this.originalHeight * 2 * playerSizePercent);

        this.contentView.setAlpha(marginPercent);

        int mr = (int) ((1f - marginPercent) * minPlayerViewMarginPx);
        this.playerViewPropertyManager.setZ(mr / 2);
        this.playerViewPropertyManager.setMarginTop(newMarginY);
        this.playerViewPropertyManager.setMarginRight(mr);
        this.playerViewPropertyManager.setContentMarginTop(mr);
    }

    private void dismissView() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this.playerPanelView, "alpha", 1f, 0);
        objectAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(INVISIBLE);
                playerPanelView.setAlpha(1f);
            }
        });
        objectAnimator.setDuration(300);
        objectAnimator.start();

        if (this.playerCallback != null) {
            this.playerCallback.onPlayerViewHide();
        }
    }

    private void confirmState(float yVelocity, int dy) {
        if (this.currentScaleRatio == 1f) {
            if (this.playerPanelView.getWidth() <= this.originalWidth * 0.75f || (yVelocity > 15 && dy > 0)) {
                this.narrowPlayerView();
            } else {
                this.enlargePlayerView();
            }
        } else {
            if (this.playerPanelView.getWidth() >= this.originalWidth * 0.75f || (yVelocity > 15 && dy < 0)) {
                this.enlargePlayerView();
            } else {
                this.narrowPlayerView();
            }
        }
    }

    private void startNarrowAnimator(int duration) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(this.playerViewPropertyManager, "width", this.playerViewPropertyManager.getWidth(), this.originalWidth * this.minScaleRatio),
                ObjectAnimator.ofFloat(this.playerViewPropertyManager, "height", this.playerViewPropertyManager.getHeight(), this.originalHeight * this.minScaleRatio),
                ObjectAnimator.ofInt(this.playerViewPropertyManager, "marginTop", this.playerViewPropertyManager.getMarginTop(), (int) this.completeScrollRange),
                ObjectAnimator.ofInt(this.playerViewPropertyManager, "marginRight", this.playerViewPropertyManager.getMarginRight(), this.minPlayerViewMarginPx),
                ObjectAnimator.ofInt(this.playerViewPropertyManager, "contentMarginTop", this.playerViewPropertyManager.getContentMarginTop(), this.minPlayerViewMarginPx),
                ObjectAnimator.ofInt(this.playerViewPropertyManager, "contentMarginRight", this.playerViewPropertyManager.getContentMarginRight(), this.minPlayerViewMarginPx),
                ObjectAnimator.ofFloat(this.playerViewPropertyManager, "z", this.playerViewPropertyManager.getZ(), this.minPlayerViewMarginPx / 2),
                ObjectAnimator.ofFloat(this.mediaPlayerPanelHeader, "alpha", this.mediaPlayerPanelHeader.getAlpha(), 0f),
                ObjectAnimator.ofFloat(this.mediaPlayerPanelController, "alpha", this.mediaPlayerPanelController.getAlpha(), 0f)
        );
        animatorSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                canHide = true;

                ViewGroup.LayoutParams params = getLayoutParams();
                params.width = WRAP_CONTENT;
                params.height = WRAP_CONTENT;
                setLayoutParams(params);

                currentScaleRatio = minScaleRatio;
            }
        });
        animatorSet.setDuration(duration);
        animatorSet.start();
    }

    /*
     *
     * Inner Classes and Interfaces
     *
     */
    private class PlayerTouchHelper {
        private int downX;
        private int downY;
        private int lastX;
        private int lastY;
        private int dy;

        private boolean isClicked = false;
        private VelocityTracker velocityTracker;
        private int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        void handleTouchEvent(MotionEvent event) {
            logUtil.i("onTouch");

            int x = (int) event.getRawX();
            int y = (int) event.getRawY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    this.isClicked = true;
                    this.velocityTracker = VelocityTracker.obtain();
                    this.downX = (int) event.getRawX();
                    this.downY = (int) event.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    this.velocityTracker.addMovement(event);
                    this.dy = y - this.lastY;

                    int dx = x - this.lastX;
                    int newMarginX = playerViewPropertyManager.getMarginRight() - dx;
                    int newMarginY = playerViewPropertyManager.getMarginTop() + this.dy;
                    int newDownX = x - this.downX;
                    int newDownY = y - this.downY;

                    if (Math.abs(newDownX) > touchSlop || Math.abs(newDownY) > touchSlop) {
                        this.isClicked = false;

                        if (Math.abs(newDownX) > Math.abs(newDownY) && canHide) {
                            playerViewPropertyManager.setMarginRight(newMarginX);
                        } else {
                            updateVideoView(newMarginY);
                        }
                    }
                    break;

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (this.isClicked) {
                        if (currentScaleRatio == 1f && playerCallback != null) {
                            playerCallback.onPlayerClick();
                        } else {
                            enlargePlayerView();
                        }
                        break;
                    }

                    velocityTracker.computeCurrentVelocity(100);

                    float yVelocity = Math.abs(velocityTracker.getYVelocity());
                    velocityTracker.clear();
                    velocityTracker.recycle();

                    if (canHide) {
                        if (yVelocity > touchSlop || Math.abs(playerViewPropertyManager.getMarginRight()) > minScaleRatio * originalWidth) {
                            dismissView();
                        } else {
                            narrowPlayerView();
                        }
                    } else {
                        confirmState(yVelocity, dy);
                    }
                    break;
            }

            this.lastX = x;
            this.lastY = y;
        }
    }

    private class PlayerViewPropertyManager {
        private LayoutParams playerViewParams;
        private LayoutParams contentParams;

        PlayerViewPropertyManager() {
            this.playerViewParams = (LayoutParams) playerPanelView.getLayoutParams();
            this.contentParams = (LayoutParams) contentView.getLayoutParams();
            this.playerViewParams.gravity = Gravity.END;
        }

        void setWidth(float width) {
            if (width == originalWidth) {
                this.playerViewParams.width = MATCH_PARENT;
                this.playerViewParams.setMargins(0, 0, 0, 0);
            } else {
                this.playerViewParams.width = (int) width;
            }

            playerPanelView.setLayoutParams(this.playerViewParams);
        }

        int getWidth() {
            logUtil.i("getWidth: " + this.playerViewParams.width);
            return this.playerViewParams.width < 0 ? originalWidth : this.playerViewParams.width;
        }

        void setHeight(float height) {
            this.playerViewParams.height = (int) height;
            playerPanelView.setLayoutParams(this.playerViewParams);
        }

        int getHeight() {
            logUtil.i("getHeight: " + this.playerViewParams.height);
            return this.playerViewParams.height < 0 ? originalHeight : this.playerViewParams.height;
        }

        void setMarginTop(int marginTop) {
            this.playerViewParams.topMargin = marginTop;
            playerPanelView.setLayoutParams(this.playerViewParams);
        }

        int getMarginTop() {
            logUtil.i("getMarginTop: " + this.playerViewParams.topMargin);
            return this.playerViewParams.topMargin;
        }

        void setMarginRight(int marginRight) {
            this.playerViewParams.rightMargin = marginRight;
            playerPanelView.setLayoutParams(this.playerViewParams);
        }

        int getMarginRight() {
            logUtil.i("getMarginRight: " + this.playerViewParams.rightMargin);
            return this.playerViewParams.rightMargin;
        }

        void setZ(float z) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                playerPanelView.setTranslationZ(z);
            }
        }

        float getZ() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return playerPanelView.getTranslationZ();
            } else {
                return 0;
            }
        }

        void setContentMarginTop(int contentMarginTop) {
            this.contentParams.topMargin = contentMarginTop;
            contentView.setLayoutParams(this.contentParams);
        }

        int getContentMarginTop() {
            logUtil.i("getContentMarginTop: " + this.contentParams.topMargin);
            return this.contentParams.topMargin;
        }

        void setContentMarginRight(int contentMarginRight) {
            this.contentParams.rightMargin = contentMarginRight;
            contentView.setLayoutParams(this.contentParams);
        }

        int getContentMarginRight() {
            return this.contentParams.rightMargin;
        }
    }

    public interface PlayerCallback {
        void onPlayerViewHide();

        void onPlayerClick();
    }

    /**
     * Inner Classes
     */
    private OnTouchListener onTouchListenerForBlockingEventThroughtout = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    };
}
