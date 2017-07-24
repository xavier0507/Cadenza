package xy.hippocampus.cadenza.controller.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.adapter.pager.HomeViewPagerAdapter;
import xy.hippocampus.cadenza.controller.manager.FragmentStackManager;

/**
 * Created by Xavier Yin on 2017/7/4.
 */

public class HomeFragment extends Fragment {
    public static final String FRAG_HOME_TAG = HomeFragment.class.getSimpleName();

    private Toolbar toolbar;
    private TabLayout mediaTabLayout;
    private ViewPager mediaViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_view_pager, container, false);

        this.toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        this.mediaTabLayout = (TabLayout) root.findViewById(R.id.tabs);
        this.mediaViewPager = (ViewPager) root.findViewById(R.id.pager);

        ((AppCompatActivity) this.getActivity()).setSupportActionBar(this.toolbar);
        this.toolbar.setNavigationIcon(R.drawable.ic_clef_note);

        this.mediaTabLayout.addTab(this.mediaTabLayout.newTab().setText(this.getString(R.string.tab_layout_media_title_1)));
        this.mediaTabLayout.addTab(this.mediaTabLayout.newTab().setText(this.getString(R.string.tab_layout_media_title_2)));
//        this.mediaTabLayout.addTab(this.mediaTabLayout.newTab().setText(this.getString(R.string.tab_layout_media_title_3)));
        this.mediaViewPager.setAdapter(new HomeViewPagerAdapter(this.getActivity().getSupportFragmentManager(), this.initFragList()));

        this.mediaTabLayout.setOnTabSelectedListener(this.createOnTabSelectedListener());
        this.mediaViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(this.mediaTabLayout));

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (FragmentStackManager.getInstance().getSize() == 0
                && !FragmentStackManager.getInstance().includesKey(FRAG_HOME_TAG)) {
            FragmentStackManager.getInstance().pushFragment(FRAG_HOME_TAG, this);
        }
    }

    private List<Fragment> initFragList() {
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(ComposerListFragment.newInstance());
        fragmentList.add(ComposerListFragment.newInstance());

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

    public static final Fragment newInstance() {
        return new HomeFragment();
    }
}
