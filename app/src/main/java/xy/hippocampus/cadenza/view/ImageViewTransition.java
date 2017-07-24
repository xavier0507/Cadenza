package xy.hippocampus.cadenza.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.util.AttributeSet;

/**
 * Created by Xavier Yin on 2017/7/5.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ImageViewTransition extends TransitionSet {

    public ImageViewTransition() {
        this.init();
    }

    public ImageViewTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds()).
                addTransition(new ChangeTransform()).
                addTransition(new ChangeImageTransform());
    }
}
