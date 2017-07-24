package xy.hippocampus.cadenza.controller.activity.sample;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.activity.common.base.BaseActivity;
import xy.hippocampus.cadenza.controller.adapter.base.BaseRVAdapter;
import xy.hippocampus.cadenza.controller.adapter.base.IOnClickedListener;
import xy.hippocampus.cadenza.controller.adapter.recycler.ComposerSampleRVAdapter;
import xy.hippocampus.cadenza.model.bean.MainListItemInfo;
import xy.hippocampus.cadenza.model.database.mainlist.ComposerNameMappingProcessor;

/**
 * Created by Xavier Yin on 2017/7/15.
 */

public class HorizontalRecyclerViewSampleActivity extends BaseActivity {
    private static final ComposerNameMappingProcessor COMPOSER_NAME_MAPPING_PROCESSOR = ComposerNameMappingProcessor.getInstance();

    private RecyclerView rv;
    private BaseRVAdapter<MainListItemInfo> composerInfoAdapter;

    @Override
    protected int layoutResourceId() {
        return R.layout.activity_horizontal_recycler_view_sample;
    }

    @Override
    protected void findView() {
        super.findView();

        this.rv = (RecyclerView) this.findViewById(R.id.rv_url);
    }

    @Override
    protected void assignViewSettings() {
        super.assignViewSettings();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        this.rv.setLayoutManager(linearLayoutManager);
        this.composerInfoAdapter = new ComposerSampleRVAdapter(this, this.getMockDataList(), new IOnClickedListener() {

            @Override
            public void onAdapterClicked(RecyclerView.ViewHolder viewHolder, Object param, int position) {
                Toast.makeText(HorizontalRecyclerViewSampleActivity.this, "position: " + position, Toast.LENGTH_LONG).show();
            }
        });
        this.rv.setAdapter(this.composerInfoAdapter);
        this.rv.scrollToPosition(5);
        new LinearSnapHelper().attachToRecyclerView(this.rv);
    }

    private List<MainListItemInfo> getMockDataList() {
        List<MainListItemInfo> composerList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            MainListItemInfo composer = new MainListItemInfo();
            composer.setTitle(COMPOSER_NAME_MAPPING_PROCESSOR.retrieveOriginalComposerNameWithIndex(i));
            composer.setSubTitle(COMPOSER_NAME_MAPPING_PROCESSOR.retrieveTranslationComposerNameWithIndex(i));
            composer.setUrl(COMPOSER_NAME_MAPPING_PROCESSOR.retrieveComposerPhotoWithIndex(i));
            composer.setPlaylistId(COMPOSER_NAME_MAPPING_PROCESSOR.retrievePlaylistWithOriginalName(i));
            composerList.add(composer);
        }

        return composerList;
    }
}
