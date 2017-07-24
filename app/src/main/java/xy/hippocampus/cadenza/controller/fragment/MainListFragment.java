package xy.hippocampus.cadenza.controller.fragment;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.View;

import java.util.List;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.activity.app.CadenzaApplication;
import xy.hippocampus.cadenza.controller.adapter.base.BaseRVAdapter;
import xy.hippocampus.cadenza.controller.adapter.base.IOnClickedListener;
import xy.hippocampus.cadenza.controller.adapter.helper.MainListAdapterSpanSizeLookup;
import xy.hippocampus.cadenza.controller.adapter.recycler.MainListItemRVAdapter;
import xy.hippocampus.cadenza.controller.fragment.base.BaseFragment;
import xy.hippocampus.cadenza.controller.manager.FragmentStackManager;
import xy.hippocampus.cadenza.model.bean.ErrorMessage;
import xy.hippocampus.cadenza.model.bean.MainListItemInfo;
import xy.hippocampus.cadenza.model.database.base.IDataSourceStrategy;
import xy.hippocampus.cadenza.model.database.mainlist.DataSourceHelper;
import xy.hippocampus.cadenza.model.database.mainlist.FirebaseDataSourceImp;
import xy.hippocampus.cadenza.model.database.mainlist.LocalDataSourceImp;
import xy.hippocampus.cadenza.view.ImageViewTransition;

import static xy.hippocampus.cadenza.model.constant.Constants.FRAG_MAIN_LIST_TAG;
import static xy.hippocampus.cadenza.model.constant.Constants.FRAG_PLAYLIST_TAG;
import static xy.hippocampus.cadenza.model.constant.IntentExtra.INTENT_EXTRA_COMPOSER_INFO;

/**
 * Created by Xavier Yin on 5/28/17.
 */

public class MainListFragment extends BaseFragment implements IOnClickedListener<MainListItemInfo>, FirebaseDataSourceImp.IValueAddListener {
    private Toolbar toolbar;

    private RecyclerView composerRV;
    private BaseRVAdapter<MainListItemInfo> composerRVAdapter;

    private IDataSourceStrategy dataSourceStrategy;

    @Override
    protected int layoutResourceId() {
        return R.layout.fragment_main_list;
    }

    @Override
    protected void findView(View root) {
        super.findView(root);

        this.toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        this.composerRV = (RecyclerView) root.findViewById(R.id.rv_composer_list);
    }

    @Override
    protected void assignViewSettings(View root) {
        super.assignViewSettings(root);

        this.addDataSourceStrategy();

        ((AppCompatActivity) this.getActivity()).setSupportActionBar(this.toolbar);
        this.toolbar.setNavigationIcon(R.drawable.ic_clef_note);

        GridLayoutManager layoutManager = new GridLayoutManager(this.getActivity(), 6);
        layoutManager.setSpanSizeLookup(new MainListAdapterSpanSizeLookup(this.composerRVAdapter));
        this.composerRV.setLayoutManager(layoutManager);
        this.composerRV.addItemDecoration(new MainListItemRVAdapter.ItemOffsetDecoration(25));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!FragmentStackManager.getInstance().includesKey(FRAG_MAIN_LIST_TAG)) {
            FragmentStackManager.getInstance().pushFragment(FRAG_MAIN_LIST_TAG, this);
        }
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

    @Override
    public void onDataChange(List<MainListItemInfo> mainListItemInfos) {
        this.getProgressCallback().notifyProgressAppear(true);
        this.composerRVAdapter.setList(mainListItemInfos);
        this.composerRV.setAdapter(this.composerRVAdapter);
        this.composerRVAdapter.removeAllViews();
        this.composerRVAdapter.notifyDataSetChanged();
        this.getProgressCallback().notifyProgressDisappear();
    }

    @Override
    public void onCancelled(ErrorMessage databaseError) {
        this.getProgressCallback().notifyProgressAppear(true);
        this.logUtil.i("ErrorCode: " + databaseError.getErrorCode());
        this.logUtil.i("ErrorMessage: " + databaseError.getErrorMessage());
        this.logUtil.i("ErrorDetails: " + databaseError.getErrorDetails());
        this.getProgressCallback().notifyProgressDisappear();
    }

    public static MainListFragment newInstance() {
        return new MainListFragment();
    }

    private void addDataSourceStrategy() {
        boolean isFirebaseSource = CadenzaApplication.isFirebaseSource();

        if (isFirebaseSource) {
            this.dataSourceStrategy = new FirebaseDataSourceImp(this);
            DataSourceHelper.setDataSourceStrategy(this.dataSourceStrategy);
            this.composerRVAdapter = new MainListItemRVAdapter(this.getActivity(), this);
            DataSourceHelper.retrieveData();

            logUtil.i("Source from firebase");
        } else {
            this.getProgressCallback().notifyProgressAppear(true);

            this.dataSourceStrategy = new LocalDataSourceImp();
            DataSourceHelper.setDataSourceStrategy(this.dataSourceStrategy);
            this.composerRVAdapter = new MainListItemRVAdapter(this.getActivity(), DataSourceHelper.retrieveData(), this);
            this.composerRV.setAdapter(this.composerRVAdapter);

            logUtil.i("Source from Local");

            this.getProgressCallback().notifyProgressDisappear();
        }
    }
}
