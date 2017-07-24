package xy.hippocampus.cadenza.controller.fragment;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.adapter.base.BaseRVAdapter;
import xy.hippocampus.cadenza.controller.adapter.base.IOnClickedListener;
import xy.hippocampus.cadenza.controller.adapter.recycler.MainListItemRVAdapter;
import xy.hippocampus.cadenza.controller.fragment.base.BaseFragment;
import xy.hippocampus.cadenza.controller.manager.FragmentStackManager;
import xy.hippocampus.cadenza.model.bean.MainListItemInfo;
import xy.hippocampus.cadenza.model.database.mainlist.ComposerNameMappingProcessor;
import xy.hippocampus.cadenza.view.ImageViewTransition;

import static xy.hippocampus.cadenza.model.constant.Constants.FRAG_PLAYLIST_TAG;
import static xy.hippocampus.cadenza.model.constant.IntentExtra.INTENT_EXTRA_COMPOSER_INFO;

/**
 * Created by Xavier Yin on 5/28/17.
 */

public class ComposerListFragment extends BaseFragment implements IOnClickedListener<MainListItemInfo> {
    private ComposerNameMappingProcessor COMPOSER_NAME_MAPPING_PROCESSOR = ComposerNameMappingProcessor.getInstance();

    private RecyclerView composerRV;
    private BaseRVAdapter<MainListItemInfo> composerRVAdapter;
    private List<MainListItemInfo> composerList;

    @Override
    protected int layoutResourceId() {
        return R.layout.fragment_main_list;
    }

    @Override
    protected void preProcess(View root) {
        super.preProcess(root);

        this.composerList = new ArrayList<>();
        for (int i = 0; i < COMPOSER_NAME_MAPPING_PROCESSOR.getComposerSortedNameMapSize(); i++) {
            MainListItemInfo composer = new MainListItemInfo();
            composer.setTitle(COMPOSER_NAME_MAPPING_PROCESSOR.retrieveOriginalComposerNameWithIndex(i));
            composer.setSubTitle(COMPOSER_NAME_MAPPING_PROCESSOR.retrieveTranslationComposerNameWithIndex(i));
            composer.setUrl(COMPOSER_NAME_MAPPING_PROCESSOR.retrieveComposerPhotoWithIndex(i));
            composer.setPlaylistId(COMPOSER_NAME_MAPPING_PROCESSOR.retrievePlaylistWithOriginalName(i));
            composerList.add(composer);
        }
    }

    @Override
    protected void findView(View root) {
        super.findView(root);

        this.composerRV = (RecyclerView) root.findViewById(R.id.rv_composer_list);
    }

    @Override
    protected void assignViewSettings(View root) {
        super.assignViewSettings(root);

        this.composerRVAdapter = new MainListItemRVAdapter(this.getActivity(), this.composerList, this);
        this.composerRV.setLayoutManager(new GridLayoutManager(this.getActivity(), 3));
        this.composerRV.setAdapter(this.composerRVAdapter);
    }

    @Override
    public void onAdapterClicked(RecyclerView.ViewHolder viewHolder, MainListItemInfo param, int position) {
        Intent intent = new Intent();
        intent.putExtra(INTENT_EXTRA_COMPOSER_INFO, param);

        FragmentManager fragmentManager = this.getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment = PlayListFragment.newInstance();
        FragmentStackManager.getInstance().pushFragment(FRAG_PLAYLIST_TAG, fragment);
        fragment.setArguments(intent.getExtras());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.setSharedElementEnterTransition(new ImageViewTransition());
            fragment.setEnterTransition(new Fade());
            fragment.setExitTransition(new Fade());
            fragment.setSharedElementReturnTransition(new ImageViewTransition());
        } else {
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        }

        fragmentTransaction
                .add(R.id.frag_home, fragment, FRAG_PLAYLIST_TAG)
                .commit();
    }

    public static ComposerListFragment newInstance() {
        return new ComposerListFragment();
    }
}
