package xy.hippocampus.cadenza.controller.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.activity.app.CadenzaApplication;
import xy.hippocampus.cadenza.controller.activity.common.mode.slanting.HomeActivity;
import xy.hippocampus.cadenza.controller.adapter.base.BaseRVAdapter;
import xy.hippocampus.cadenza.controller.adapter.base.IOnClickedListener;
import xy.hippocampus.cadenza.controller.adapter.helper.MainListAdapterSpanSizeLookup;
import xy.hippocampus.cadenza.controller.adapter.recycler.MainListItemRVAdapter;
import xy.hippocampus.cadenza.controller.fragment.base.BaseFragment;
import xy.hippocampus.cadenza.controller.manager.FragmentStackManager;
import xy.hippocampus.cadenza.controller.manager.PrefsManager;
import xy.hippocampus.cadenza.model.bean.ErrorMessage;
import xy.hippocampus.cadenza.model.bean.MainListItemInfo;
import xy.hippocampus.cadenza.model.database.base.IDataSourceStrategy;
import xy.hippocampus.cadenza.model.database.mainlist.DataSourceHelper;
import xy.hippocampus.cadenza.model.database.mainlist.FirebaseDataSourceImp;
import xy.hippocampus.cadenza.model.database.mainlist.LocalDataSourceImp;
import xy.hippocampus.cadenza.util.FragmentUtil;
import xy.hippocampus.cadenza.view.theme.ColorSettings;

import static xy.hippocampus.cadenza.model.constant.Constants.FRAG_ABOUT_TAG;
import static xy.hippocampus.cadenza.model.constant.Constants.FRAG_MAIN_LIST_TAG;
import static xy.hippocampus.cadenza.model.constant.Constants.FRAG_PLAYLIST_TAG;
import static xy.hippocampus.cadenza.model.constant.Constants.SERVICE_CHANGE_COLOR;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.getActivity().getMenuInflater().inflate(R.menu.menu_app_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                FragmentUtil.addFragment(
                        (AppCompatActivity) this.getActivity(),
                        AboutFragment.newInstance(),
                        R.id.frag_home,
                        FRAG_ABOUT_TAG);
                break;

            case R.id.action_settings:
                new ColorSettings((HomeActivity) this.getActivity()).pickUpColor(R.string.menu_option_pickup, new ColorSettings.Callback() {

                    @Override
                    public void onColorSelected(int color) {
                        PrefsManager.getInstance(getActivity()).putPrimaryColor(color);
                        HomeActivity.updateAllTheme();
                        notifyService();
                    }
                });
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected int layoutResourceId() {
        return R.layout.fragment_main_list;
    }

    @Override
    protected void preProcess(View root) {
        super.preProcess(root);
        this.setHasOptionsMenu(true);
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

        this.logUtil.i("MainList onResume");

        if (!FragmentStackManager.getInstance().includesKey(FRAG_MAIN_LIST_TAG)) {
            FragmentStackManager.getInstance().pushFragment(FRAG_MAIN_LIST_TAG, this);
        }
    }

    @Override
    public void onAdapterClicked(RecyclerView.ViewHolder viewHolder, MainListItemInfo param, int position) {
        Intent intent = new Intent();
        intent.putExtra(INTENT_EXTRA_COMPOSER_INFO, param);

        Fragment fragment = PlayListFragment.newInstance();
        fragment.setArguments(intent.getExtras());

        FragmentUtil.addFragment(
                (AppCompatActivity) this.getActivity(),
                fragment,
                R.id.frag_home,
                FRAG_PLAYLIST_TAG);
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

    private void notifyService() {
        Intent intent = new Intent();
        intent.setAction(SERVICE_CHANGE_COLOR);
        this.getActivity().sendBroadcast(intent);
    }

    public static MainListFragment newInstance() {
        return new MainListFragment();
    }
}
