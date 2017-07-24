package xy.hippocampus.cadenza.controller.activity.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xy.hippocampus.cadenza.R;

/**
 * Created by Xavier Yin on 5/29/17.
 */

public class OkHttpSampleActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_sample_okhttp);

        this.findView();
        this.configSettings();
        this.launchRequest();
    }

    private void findView() {
        this.toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.textView = (TextView) this.findViewById(R.id.text);
    }

    private void configSettings() {
        this.setSupportActionBar(this.toolbar);
        this.toolbar.setNavigationIcon(R.drawable.ic_clef_note);
    }

    private void launchRequest() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://www.google.com").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String resStr = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(resStr);
                    }
                });
            }
        });
    }
}
