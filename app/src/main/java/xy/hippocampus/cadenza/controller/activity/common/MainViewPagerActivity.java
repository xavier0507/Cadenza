package xy.hippocampus.cadenza.controller.activity.common;

import android.app.ProgressDialog;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.activity.common.base.BaseAppBarViewPagerActivity;

/**
 * Created by Xavier Yin on 5/28/17.
 */

public class MainViewPagerActivity extends BaseAppBarViewPagerActivity {
    private ProgressDialog progressDialog;

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = this.getMenuInflater();
//        menuInflater.inflate(R.menu.menu_app_bar, menu);
//
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//
//        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) searchItem.getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
//        searchView.setIconifiedByDefault(true);
//
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_account:
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    protected int layoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void findView() {
        super.findView();
        this.progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void assignViewSettings() {
        super.assignViewSettings();
        this.progressDialog.setMessage("處理中...");
    }
}
