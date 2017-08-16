package xy.hippocampus.cadenza.controller.activity.sample;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.activity.common.base.BaseActivity;
import xy.hippocampus.cadenza.controller.service.FloatingWindowService;

/**
 * Created by Xavier Yin on 2017/7/31.
 */

public class FloatingWindowSampleActivity extends BaseActivity {
    private Button startBtn;

    @Override
    protected int layoutResourceId() {
        return R.layout.activity_sample_floating_window;
    }

    @Override
    protected void findView() {
        super.findView();

        this.startBtn = (Button) this.findViewById(R.id.btn_start);
    }

    @Override
    protected void addEvents() {
        super.addEvents();

        this.startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FloatingWindowSampleActivity.this, FloatingWindowService.class);
                startService(intent);
                onBackPressed();
            }
        });
    }
}
