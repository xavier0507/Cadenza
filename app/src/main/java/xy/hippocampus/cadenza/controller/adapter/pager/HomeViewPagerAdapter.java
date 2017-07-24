package xy.hippocampus.cadenza.controller.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

import xy.hippocampus.cadenza.controller.adapter.base.BaseFragPagerAdapter;

/**
 * Created by Xavier Yin on 5/28/17.
 */

public class HomeViewPagerAdapter extends BaseFragPagerAdapter {

    public HomeViewPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm, fragmentList);
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragmentList.get(position);
    }
}
