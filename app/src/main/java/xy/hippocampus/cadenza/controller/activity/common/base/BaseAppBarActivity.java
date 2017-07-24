package xy.hippocampus.cadenza.controller.activity.common.base;

import android.support.v7.widget.Toolbar;

import xy.hippocampus.cadenza.R;

/**
 * Created by Xavier Yin on 5/31/17.
 */

public abstract class BaseAppBarActivity extends BaseActivity {
    protected Toolbar toolbar;

    @Override
    protected void findView() {
        this.toolbar = (Toolbar) this.findViewById(R.id.toolbar);

        if (this.isToolbarEmpty()) {
            throw new IllegalStateException(this.getString(R.string.exception_no_tool_bar));
        }
    }

    @Override
    protected void assignViewSettings() {
        this.setSupportActionBar(this.toolbar);
        this.toolbar.setNavigationIcon(R.drawable.ic_clef_note);
    }

    protected boolean isToolbarEmpty() {
        return (this.toolbar == null) ? true : false;
    }
}
