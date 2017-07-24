package xy.hippocampus.cadenza.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import xy.hippocampus.cadenza.R;

/**
 * Created by Xavier Yin on 6/10/17.
 */

public class SquareImageViewBehavior extends CoordinatorLayout.Behavior<SquareImageView> {
    private Context context;

    private float startToolbarPosition;
    private int startHeight;

    private int startXPosition;
    private int startYPosition;
    private int finalYPosition;
    private int finalXPosition;

    public SquareImageViewBehavior(Context context, AttributeSet attrs) {
        this.initResources(context);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, SquareImageView child, View dependency) {
        return dependency instanceof Toolbar;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, SquareImageView child, View dependency) {
        this.maybeInitProperties(child, dependency);

        final int maxScrollDistance = (int) (this.startToolbarPosition);
        float expandedPercentageFactor = dependency.getY() / maxScrollDistance;
        float distanceYToSubtract = ((this.startYPosition - this.finalYPosition) * (1f - expandedPercentageFactor)) + (this.startHeight / 2);
        child.setX(this.startXPosition - child.getWidth() / 2);
        child.setY(this.startYPosition - distanceYToSubtract);

        return true;
    }

    private void initResources(Context context) {
        this.context = context;
    }

    private void maybeInitProperties(SquareImageView child, View dependency) {
        this.startToolbarPosition = dependency.getY();
        this.startHeight = child.getHeight();
        this.startXPosition = (int) (child.getX() + (child.getWidth() / 2));
        this.startYPosition = (int) (dependency.getY());
        this.finalXPosition = this.context.getResources().getDimensionPixelOffset(R.dimen.abc_action_bar_content_inset_material);
        this.finalYPosition = (dependency.getHeight() / 2);
    }
}
