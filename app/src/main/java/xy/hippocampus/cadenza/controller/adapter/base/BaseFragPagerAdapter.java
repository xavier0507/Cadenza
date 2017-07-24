package xy.hippocampus.cadenza.controller.adapter.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Xavier Yin on 5/28/17.
 */

public abstract class BaseFragPagerAdapter extends FragmentPagerAdapter {
    protected List<Fragment> fragmentList;

    public BaseFragPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public int getCount() {
        return this.fragmentList.size();
    }
}
