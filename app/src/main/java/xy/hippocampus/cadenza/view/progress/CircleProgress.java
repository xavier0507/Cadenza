package xy.hippocampus.cadenza.view.progress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Xavier Yin on 5/2/17.
 */

public class CircleProgress extends View {
    private Context context;

    private int viewWidth;
    private int viewHeight;
    private int color;
    private int numberOfCircle;
    private int onLayoutWidth;
    private int onLayoutHeight;

    private PointF center;
    private Circle[] circles;
    private float[] rotates;

    public CircleProgress(Context context) {
        this(context, null);
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context);
    }

    private void init(Context context) {
        this.context = context;
        this.viewWidth = 150;
        this.viewHeight = 150;
        this.color = Color.parseColor("#FFF8BBD0");
        this.numberOfCircle = 6;
        this.rotates = new float[this.numberOfCircle];
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int measuredWidth = resolveSize(this.viewWidth, widthMeasureSpec);
        final int measuredHeight = resolveSize(this.viewHeight, heightMeasureSpec);
        this.setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.initLayoutSize();
        this.initCircles();
        this.prepareAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.drawGraphic(canvas);
    }

    private void drawGraphic(Canvas canvas) {
        for (int i = 0; i < this.numberOfCircle; i++) {
            canvas.save();
            canvas.rotate(this.rotates[i], this.center.x, this.center.y);
            this.circles[i].draw(canvas);
            canvas.restore();
        }
    }

    private void initLayoutSize() {
        this.onLayoutWidth = this.getWidth();
        this.onLayoutHeight = this.getHeight();
        this.center = new PointF(this.onLayoutWidth / 2.0f, this.onLayoutHeight / 2.0f);
    }

    private void initCircles() {
        final float size = Math.min(this.onLayoutWidth, this.onLayoutHeight);
        final float circleRadius = size / 8.0f;
        this.circles = new Circle[this.numberOfCircle];

        for (int i = 0; i < this.numberOfCircle; i++) {
            this.circles[i] = new Circle();
            this.circles[i].setCenter(this.center.x, circleRadius);
            this.circles[i].setColor(this.color);
            this.circles[i].setRadius(circleRadius - circleRadius * i / 6);
        }
    }

    private void prepareAnim() {
        for (int i = 0; i < this.numberOfCircle; i++) {
            final int index = i;

            ValueAnimator fadeAnimator = ValueAnimator.ofFloat(360, 0, 0, 360);
            fadeAnimator.setRepeatCount(ValueAnimator.INFINITE);
            fadeAnimator.setDuration(2500);
            fadeAnimator.setStartDelay(index * 100);
            fadeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    rotates[index] = (float) animation.getAnimatedValue();
                    reDraw();
                }
            });

            fadeAnimator.start();
        }
    }

    private void reDraw() {
        this.invalidate();
    }
}
