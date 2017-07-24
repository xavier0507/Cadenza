package xy.hippocampus.cadenza.controller.activity.common;

import android.app.SearchManager;
import android.content.Intent;
import android.widget.TextView;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.activity.common.base.BaseAppBarActivity;

/**
 * Created by Xavier Yin on 5/31/17.
 */

public class SearchResultsActivity extends BaseAppBarActivity {
    private TextView searchResultsText;

    private String query;

    @Override
    protected int layoutResourceId() {
        return R.layout.activity_search_results;
    }

    @Override
    protected void preProcess() {
        super.preProcess();

        if (this.getIntent().getAction().equals(Intent.ACTION_SEARCH)) {
            this.query = this.getIntent().getStringExtra(SearchManager.QUERY);
        }
    }

    @Override
    protected void findView() {
        super.findView();

        this.searchResultsText = (TextView) this.findViewById(R.id.text_search_results);
    }

    @Override
    protected void assignViewSettings() {
        super.assignViewSettings();

        this.searchResultsText.setText(this.query);
    }
}
