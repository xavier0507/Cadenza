package xy.hippocampus.cadenza.controller.activity.common.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import xy.hippocampus.cadenza.util.LogUtil;

/**
 * Created by Xavier Yin on 5/31/17.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected final LogUtil logUtil = LogUtil.getInstance(this.getClass());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.layoutResourceId());
        this.preProcess();
        this.findView();
        this.assignViewSettings();
        this.addEvents();
        this.postProcess();
    }

    protected void preProcess() {
        // Do nothing for overriding
    }

    protected void findView() {
        // Do nothing for overriding
    }

    protected void assignViewSettings() {
        // Do nothing for overriding
    }

    protected void addEvents() {
        // Do nothing for overriding
    }

    protected void postProcess() {
        // Do nothing for overriding
    }

    abstract protected int layoutResourceId();
}
