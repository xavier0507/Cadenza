package xy.hippocampus.cadenza.util;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;

import xy.hippocampus.cadenza.controller.manager.FragmentStackManager;
import xy.hippocampus.cadenza.view.ImageViewTransition;

/**
 * Created by Xavier Yin on 2017/7/26.
 */

public class FragmentUtil {

    public static void addFragment(AppCompatActivity appCompatActivity, Fragment fragment, int addedFragmentId, String Tag) {
        FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentStackManager.getInstance().pushFragment(Tag, fragment);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.setSharedElementEnterTransition(new ImageViewTransition());
            fragment.setEnterTransition(new Fade());
            fragment.setExitTransition(new Fade());
            fragment.setSharedElementReturnTransition(new ImageViewTransition());
        } else {
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        }

        fragmentTransaction
                .add(addedFragmentId, fragment, Tag)
                .commit();
    }
}
