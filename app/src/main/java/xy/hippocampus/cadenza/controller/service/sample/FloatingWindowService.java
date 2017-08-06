package xy.hippocampus.cadenza.controller.service.sample;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.manager.PlaylistManager;
import xy.hippocampus.cadenza.model.constant.IntentExtra;
import xy.hippocampus.cadenza.util.LogUtil;
import xy.hippocampus.cadenza.view.player.FloatingWindowMediaPlayerPanel;

import static android.widget.ListPopupWindow.WRAP_CONTENT;

/**
 * Created by Xavier Yin on 2017/7/31.
 */

public class FloatingWindowService extends Service implements FloatingWindowMediaPlayerPanel.PlayCallback {
    private static final int MEDIA_PLAYER_STATUS_PREV = 0;
    private static final int MEDIA_PLAYER_STATUS_PLAY = 1;
    private static final int MEDIA_PLAYER_STATUS_NEXT = 2;

    private LogUtil logUtil = LogUtil.getInstance(this.getClass());
    private PlaylistManager playlistManager = PlaylistManager.getInstance();

    private WindowManager windowManager;
    private LayoutInflater inflater;

    private FloatingWindowMediaPlayerPanel rootView;
    private WebView youtubeWeb;
    private TextView mediaInfoText;

    private String playListId;
    private String videoId;
    private int currentPlayerStatus = MEDIA_PLAYER_STATUS_PLAY;

    private Handler handler = new Handler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.playListId = intent.getStringExtra(IntentExtra.INTENT_EXTRA_PLAYLIST_ID);
        this.videoId = intent.getStringExtra(IntentExtra.INTENT_EXTRA_PLAYLIST_VIDEO_ID);
        this.playMedia(MEDIA_PLAYER_STATUS_PLAY);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        parameters.gravity = Gravity.CENTER | Gravity.CENTER;
        parameters.x = 0;
        parameters.y = 0;

        this.inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);

        this.rootView = (FloatingWindowMediaPlayerPanel) this.inflater.inflate(R.layout.service_floating_window, null);
        this.rootView.setUpdatedParameters(parameters);
        this.rootView.setCallBack(this);

        this.youtubeWeb = (WebView) this.rootView.findViewById(R.id.web_youtube);
        this.mediaInfoText = (TextView) this.rootView.findViewById(R.id.media_player_panel_title_text);

        this.youtubeWeb.getSettings().setJavaScriptEnabled(true);
        this.youtubeWeb.getSettings().setLoadWithOverviewMode(true);
        this.youtubeWeb.getSettings().setMediaPlaybackRequiresUserGesture(false);

        this.youtubeWeb.addJavascriptInterface(new JavaScriptInterface(this), "Android");
        this.youtubeWeb.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                youtubeWeb.loadUrl("javascript:init('" + videoId + "')");
            }
        });
        this.youtubeWeb.setWebChromeClient(new WebChromeClient());
        this.youtubeWeb.loadUrl("file:///android_asset/media_player_js_version.html");

        this.windowManager.addView(this.rootView, parameters);
    }

    @Override
    public void close() {
        this.windowManager.removeView(this.rootView);
        this.stopSelf();
    }

    @Override
    public void playPrev() {
        this.playMedia(MEDIA_PLAYER_STATUS_PREV);
    }

    @Override
    public void playNext() {
        this.playMedia(MEDIA_PLAYER_STATUS_NEXT);
    }

    private void playMedia(int currentMediaPlayerControlStatus) {
        if (currentMediaPlayerControlStatus == MEDIA_PLAYER_STATUS_PREV) {
            this.videoId = this.playlistManager.prevItem();
        } else if (currentMediaPlayerControlStatus == MEDIA_PLAYER_STATUS_PLAY) {
            // Do nothing
        } else if (currentMediaPlayerControlStatus == MEDIA_PLAYER_STATUS_NEXT) {
            this.videoId = this.playlistManager.nextItem();
        }

        this.currentPlayerStatus = currentMediaPlayerControlStatus;
        this.updateMediaInfo();
    }

    private void updateMediaInfo() {
        this.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                youtubeWeb.clearCache(true);
                youtubeWeb.reload();

                String index = Integer.toString(playlistManager.getCurrentIndex() + 1);
                mediaInfoText.setText("曲目: " + index + " - " + playlistManager.getCurrentItem().getSnippet().getTitle());
            }
        }, 0);
    }

    /**
     * Inner Classes
     */
    private class JavaScriptInterface {
        private Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showCurrentPlayId(String id) {
            logUtil.i("current id: " + id);
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void showCurrentPlayerStatus(String status) {
            logUtil.i("current status: " + status);

            if (status.equals("0010")) {
                playMedia(MEDIA_PLAYER_STATUS_NEXT);
                return;
            }
        }

        @JavascriptInterface
        public void showError(String error) {
            logUtil.i("current error: " + error);

            this.play();
        }

        private void play() {
            if (currentPlayerStatus == MEDIA_PLAYER_STATUS_PREV) {
                playMedia(MEDIA_PLAYER_STATUS_PREV);
            } else if (currentPlayerStatus == MEDIA_PLAYER_STATUS_NEXT) {
                playMedia(MEDIA_PLAYER_STATUS_NEXT);
            } else {
                playMedia(MEDIA_PLAYER_STATUS_NEXT);
            }
        }
    }
}
