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

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.util.LogUtil;

import static android.content.Context.WINDOW_SERVICE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by Xavier Yin on 2017/7/31.
 */

public class FloatingWindowMediaPlayerPanel extends LinearLayout implements View.OnClickListener {
    private static final LogUtil logUtil = LogUtil.getInstance(FloatingWindowMediaPlayerPanel.class);
    private static final int CLICK_ACTION_THRESHOLD = 100;

    private WindowManager.LayoutParams updatedParameters;
    private PlayCallback playCallback;

    private double x;
    private double y;
    private double pressedX;
    private double pressedY;

    private Context context;

    private View mediaPlayerPanelParent;
    private View mediaPlayerPanelHeader;
    private View mediaPlayerPanel;
    private View mediaPlayerPanelController;

    private ImageButton scaleBtn;
    private ImageButton closeBtn;
    private ImageButton prevBtn;
    private ImageButton nextBtn;

    private int originalWidth;
    private int originalHeight;
    private int narrowedWidth;
    private int narrowedHeight;

    private boolean isFirstMeasure = true;
    private boolean isEnlarged;
    private boolean isClicked;

    private PlayerViewPropertyManager playerViewPropertyManager;
    private WindowManager windowManager;
    private TouchHelper touchHelper;

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

        this.prevBtn = (ImageButton) this.findViewById(R.id.media_player_panel_btn_prev);
        this.nextBtn = (ImageButton) this.findViewById(R.id.media_player_panel_btn_next);

        this.scaleBtn.setOnClickListener(this);
        this.closeBtn.setOnClickListener(this);
        this.prevBtn.setOnClickListener(this);
        this.nextBtn.setOnClickListener(this);

        this.playerViewPropertyManager = new PlayerViewPropertyManager();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (this.isFirstMeasure) {
            this.originalWidth = this.getMeasuredWidth();
            this.originalHeight = this.getMeasuredHeight();

            this.narrowedWidth = (int) (this.getMeasuredWidth() * 0.58);
            this.narrowedHeight = (int) ((this.getMeasuredHeight() - this.mediaPlayerPanelHeader.getMeasuredHeight() - this.mediaPlayerPanelController.getMeasuredHeight()) * 0.68);

            this.logUtil.i("header height: " + this.mediaPlayerPanelHeader.getMeasuredHeight());
            this.logUtil.i("controller height: " + this.mediaPlayerPanelController.getMeasuredHeight());

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
                this.startNarrowAnim();
                break;

            case R.id.media_player_panel_btn_close:
                this.playCallback.close();
                break;

            case R.id.media_player_panel_btn_prev:
                this.playCallback.playPrev();
                break;

            case R.id.media_player_panel_btn_next:
                this.playCallback.playNext();
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
                ObjectAnimator.ofFloat(this.playerViewPropertyManager, "width", this.narrowedWidth, this.originalWidth),
                ObjectAnimator.ofFloat(this.playerViewPropertyManager, "height", this.narrowedHeight, this.originalHeight)
        );
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mediaPlayerPanelHeader.setVisibility(View.VISIBLE);
                mediaPlayerPanelController.setVisibility(View.VISIBLE);
                isEnlarged = true;
            }
        });
        animatorSet.setDuration(200);
        animatorSet.start();
    }

    public void startNarrowAnim() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(this.playerViewPropertyManager, "width", this.originalWidth, this.narrowedWidth),
                ObjectAnimator.ofFloat(this.playerViewPropertyManager, "height", this.originalHeight, this.narrowedHeight)
        );
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mediaPlayerPanelHeader.setVisibility(View.GONE);
                mediaPlayerPanelController.setVisibility(View.GONE);
                isEnlarged = false;
            }
        });
        animatorSet.setDuration(200);
        animatorSet.start();
    }

    private void init(Context context) {
        this.context = context;
        this.windowManager = (WindowManager) this.context.getSystemService(WINDOW_SERVICE);
        this.touchHelper = new TouchHelper();
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

                    if (!isEnlarged()) {
                        if (Math.abs(motionEvent.getRawX() - pressedX) > CLICK_ACTION_THRESHOLD || Math.abs(motionEvent.getRawY() - pressedY) > CLICK_ACTION_THRESHOLD) {
                            updatedParameters.x = (int) (x + (motionEvent.getRawX() - pressedX));
                            updatedParameters.y = (int) (y + (motionEvent.getRawY() - pressedY));

                            logUtil.i("updated x: " + Math.abs(updatedParameters.x));
                            logUtil.i("updated y: " + Math.abs(updatedParameters.y));

                            windowManager.updateViewLayout(FloatingWindowMediaPlayerPanel.this, updatedParameters);

                            isClicked = false;
                        }
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
            if (width == originalWidth) {
                this.playerViewParams.width = MATCH_PARENT;
                this.playerViewParams.setMargins(0, 0, 0, 0);
            } else {
                this.playerViewParams.width = (int) width;
            }

            mediaPlayerPanelParent.setLayoutParams(this.playerViewParams);
        }

        int getWidth() {
            logUtil.i("getWidth: " + this.playerViewParams.width);
            return this.playerViewParams.width < 0 ? originalWidth : this.playerViewParams.width;
        }

        void setHeight(float height) {
            this.playerViewParams.height = (int) height;
            mediaPlayerPanelParent.setLayoutParams(this.playerViewParams);
        }

        int getHeight() {
            logUtil.i("getHeight: " + this.playerViewParams.height);
            return this.playerViewParams.height < 0 ? originalHeight : this.playerViewParams.height;
        }
    }

    public interface PlayCallback {
        void close();

        void playPrev();

        void playNext();
    }
}
