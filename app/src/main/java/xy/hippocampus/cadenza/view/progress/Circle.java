package xy.hippocampus.cadenza.view.progress;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * Created by Xavier Yin on 5/2/17.
 */

public class Circle {
    protected Paint paint;
    private PointF center;
    private float radius;

    public void setColor(int color) {
        this.paint.setColor(color);
    }

    public Circle() {
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.center = new PointF();
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setCenter(float x, float y) {
        this.center.set(x, y);
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(this.center.x, this.center.y, this.radius, this.paint);
    }
}
