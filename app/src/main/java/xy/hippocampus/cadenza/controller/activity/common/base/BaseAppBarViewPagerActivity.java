package xy.hippocampus.cadenza.controller.activity.common.base;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.adapter.pager.HomeViewPagerAdapter;
import xy.hippocampus.cadenza.controller.fragment.MainListFragment;

/**
 * Created by Xavier Yin on 5/31/17.
 */

public abstract class BaseAppBarViewPagerActivity extends BaseAppBarActivity {
    private TabLayout mediaTabLayout;
    private ViewPager mediaViewPager;

    @Override
    protected void findView() {
        super.findView();
        this.mediaTabLayout = (TabLayout) this.findViewById(R.id.tabs);
        this.mediaViewPager = (ViewPager) this.findViewById(R.id.pager);

        if (this.isMediaTabLayoutEmpty()) {
            throw new IllegalStateException(this.getString(R.string.exception_no_media_tab_layout));
        }

        if (this.isMediaViewPagerEmpty()) {
            throw new IllegalStateException(this.getString(R.string.exception_no_media_media_view_pager));
        }
    }

    @Override
    protected void assignViewSettings() {
        super.assignViewSettings();
        this.mediaTabLayout.addTab(this.mediaTabLayout.newTab().setText(this.getString(R.string.tab_layout_media_title_1)));
        this.mediaTabLayout.addTab(this.mediaTabLayout.newTab().setText(this.getString(R.string.tab_layout_media_title_2)));
        this.mediaTabLayout.addTab(this.mediaTabLayout.newTab().setText(this.getString(R.string.tab_layout_media_title_3)));
        this.mediaViewPager.setAdapter(new HomeViewPagerAdapter(this.getSupportFragmentManager(), this.initFragList()));
    }

    @Override
    protected void addEvents() {
        this.mediaTabLayout.setOnTabSelectedListener(this.createOnTabSelectedListener());
        this.mediaViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(this.mediaTabLayout));
    }

    protected boolean isMediaTabLayoutEmpty() {
        return (this.mediaTabLayout == null) ? true : false;
    }

    protected boolean isMediaViewPagerEmpty() {
        return (this.mediaViewPager == null) ? true : false;
    }

    private List<Fragment> initFragList() {
        List<Fragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            fragmentList.add(MainListFragment.newInstance());
        }

        return fragmentList;
    }

    private TabLayout.OnTabSelectedListener createOnTabSelectedListener() {
        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mediaViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };
    }
}
