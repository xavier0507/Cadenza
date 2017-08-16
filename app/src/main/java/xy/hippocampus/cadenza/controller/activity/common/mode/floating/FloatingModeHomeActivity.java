package xy.hippocampus.cadenza.controller.activity.common.mode.floating;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.activity.app.CadenzaApplication;
import xy.hippocampus.cadenza.controller.activity.common.base.BaseEPMIActivity;
import xy.hippocampus.cadenza.controller.adapter.base.BaseRVAdapter;
import xy.hippocampus.cadenza.controller.adapter.base.IOnClickedListener;
import xy.hippocampus.cadenza.controller.adapter.helper.MainListAdapterSpanSizeLookup;
import xy.hippocampus.cadenza.controller.adapter.recycler.MainListItemRVAdapter;
import xy.hippocampus.cadenza.model.bean.ErrorMessage;
import xy.hippocampus.cadenza.model.bean.MainListItemInfo;
import xy.hippocampus.cadenza.model.database.base.IDataSourceStrategy;
import xy.hippocampus.cadenza.model.database.mainlist.DataSourceHelper;
import xy.hippocampus.cadenza.model.database.mainlist.FirebaseDataSourceImp;
import xy.hippocampus.cadenza.model.database.mainlist.LocalDataSourceImp;
import xy.hippocampus.cadenza.view.ProgressHelper;

import static xy.hippocampus.cadenza.model.constant.IntentExtra.INTENT_EXTRA_COMPOSER_INFO;

/**
 * Created by Xavier Yin on 2017/8/10.
 */

public class FloatingModeHomeActivity extends BaseEPMIActivity implements IOnClickedListener<MainListItemInfo>, FirebaseDataSourceImp.IValueAddListener {
    private Toolbar toolbar;

    private RecyclerView composerRV;
    private BaseRVAdapter<MainListItemInfo> composerRVAdapter;

    private ProgressHelper progressHelper;
    private IDataSourceStrategy dataSourceStrategy;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = this.getMenuInflater();
        menuInflater.inflate(R.menu.menu_app_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                // TODO
                Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_settings:
                // TODO
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected int layoutResourceId() {
        return R.layout.activity_mode_floating_home;
    }

    @Override
    protected void preProcess() {
        super.preProcess();
        this.progressHelper = new ProgressHelper.Builder(this).withBlock(true).build();
    }

    @Override
    protected void findView() {
        super.findView();
        this.toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.composerRV = (RecyclerView) this.findViewById(R.id.rv_composer_list);
    }

    @Override
    protected void assignViewSettings() {
        super.assignViewSettings();

        this.addDataSourceStrategy();
        this.setSupportActionBar(this.toolbar);
        this.toolbar.setNavigationIcon(R.drawable.ic_clef_note);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 6);
        layoutManager.setSpanSizeLookup(new MainListAdapterSpanSizeLookup(this.composerRVAdapter));
        this.composerRV.setLayoutManager(layoutManager);
        this.composerRV.addItemDecoration(new MainListItemRVAdapter.ItemOffsetDecoration(25));
    }

    @Override
    public void onAdapterClicked(RecyclerView.ViewHolder viewHolder, MainListItemInfo param, int position) {
        Intent intent = new Intent();
        intent.putExtra(INTENT_EXTRA_COMPOSER_INFO, param);
//        Fragment fragment = PlayListFragment.newInstance();
//        fragment.setArguments(intent.getExtras());
//
//        FragmentUtil.addFragment(
//                (AppCompatActivity) this.getActivity(),
//                fragment,
//                R.id.frag_home,
//                FRAG_PLAYLIST_TAG);
    }

    @Override
    public void onDataChange(List<MainListItemInfo> mainListItemInfos) {
        this.progressHelper.showProgressBar();
        this.composerRVAdapter.setList(mainListItemInfos);
        this.composerRV.setAdapter(this.composerRVAdapter);
        this.composerRVAdapter.removeAllViews();
        this.composerRVAdapter.notifyDataSetChanged();
        this.progressHelper.hideProgressBar();
    }

    @Override
    public void onCancelled(ErrorMessage databaseError) {
        this.progressHelper.showProgressBar();
        this.logUtil.i("ErrorCode: " + databaseError.getErrorCode());
        this.logUtil.i("ErrorMessage: " + databaseError.getErrorMessage());
        this.logUtil.i("ErrorDetails: " + databaseError.getErrorDetails());
        this.progressHelper.hideProgressBar();
    }

    private void addDataSourceStrategy() {
        boolean isFirebaseSource = CadenzaApplication.isFirebaseSource();

        if (isFirebaseSource) {
            this.dataSourceStrategy = new FirebaseDataSourceImp(this);
            DataSourceHelper.setDataSourceStrategy(this.dataSourceStrategy);
            this.composerRVAdapter = new MainListItemRVAdapter(this, this);
            DataSourceHelper.retrieveData();

            logUtil.i("Source from firebase");
        } else {
            this.progressHelper.showProgressBar();
            this.dataSourceStrategy = new LocalDataSourceImp();
            DataSourceHelper.setDataSourceStrategy(this.dataSourceStrategy);
            this.composerRVAdapter = new MainListItemRVAdapter(this, DataSourceHelper.retrieveData(), this);
            this.composerRV.setAdapter(this.composerRVAdapter);
            this.progressHelper.hideProgressBar();

            logUtil.i("Source from Local");
        }
    }
}
