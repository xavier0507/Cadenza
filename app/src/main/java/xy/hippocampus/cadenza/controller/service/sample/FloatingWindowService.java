package xy.hippocampus.cadenza.controller.service.sample;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.activity.common.mode.SplashActivity;
import xy.hippocampus.cadenza.controller.manager.PlaylistManager;
import xy.hippocampus.cadenza.controller.manager.PrefsManager;
import xy.hippocampus.cadenza.model.constant.IntentExtra;
import xy.hippocampus.cadenza.util.LogUtil;
import xy.hippocampus.cadenza.view.player.FloatingWindowMediaPlayerPanel;

import static android.widget.ListPopupWindow.WRAP_CONTENT;

/**
 * Created by Xavier Yin on 2017/7/31.
 */

public class FloatingWindowService extends Service implements FloatingWindowMediaPlayerPanel.PlayCallback {
    private static LogUtil logUtil = LogUtil.getInstance(FloatingWindowService.class);

    private static final int MEDIA_PLAYER_STATUS_PREV = 0;
    private static final int MEDIA_PLAYER_STATUS_PLAY = 1;
    private static final int MEDIA_PLAYER_STATUS_NEXT = 2;
    private static final int MEDIA_PLAYER_STATUS_SHUFFLE = 3;
    private static final int MEDIA_PLAYER_STATUS_REPEAT = 4;

    private PlaylistManager playlistManager;
    private PrefsManager prefsManager;

    private WindowManager.LayoutParams parameters;
    private WindowManager windowManager;
    private LayoutInflater inflater;

    private FloatingWindowMediaPlayerPanel rootView;
    private WebView youtubeWeb;
    private TextView mediaInfoText;
    private TextView currentTimeText;
    private TextView durationText;
    private SeekBar seekBar;

    private String playListId;
    private String videoId;
    private int currentPlayerStatus = MEDIA_PLAYER_STATUS_PLAY;

    private Handler mediaPlayerHandler;
    private JavaScriptInterface javaScriptInterface;

    private boolean isShuffleMode;
    private boolean isRepeatOneMode;
    private boolean isSeekBarTouch;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.showNotification();
        this.acquireIntentData(intent);
        this.updateVideoId(MEDIA_PLAYER_STATUS_PLAY);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.initBasicResources();
        this.initUI();
        this.assignUISettings();
    }

    @Override
    public void onScale() {
        this.executeVideoScale();
    }

    @Override
    public void onClose() {
        this.executeVideoClose();
    }

    @Override
    public void onShuffle() {
        this.executeVideoShuffleMode(true);
    }

    @Override
    public void onSequence() {
        this.executeVideoShuffleMode(false);
    }

    @Override
    public void playPrev() {
        this.executeVideoPlayPrev();
    }

    @Override
    public void play() {
        this.javaScriptInterface.executeJSMediaPlayerPlay();
    }

    @Override
    public void pause() {
        this.javaScriptInterface.executeJSMediaPlayerPause();
    }

    @Override
    public void playNext() {
        this.executeVideoPlayNext();
    }

    @Override
    public void onRepeatOne() {
        this.executeVideoRepeatOneMode(true);
    }

    @Override
    public void onLoopAll() {
        this.executeVideoRepeatOneMode(false);
    }

    /**
     * Logical Area: UI Settings
     */
    private void initBasicResources() {
        this.playlistManager = PlaylistManager.getInstance();
        this.prefsManager = PrefsManager.getInstance(this);

        this.isShuffleMode = this.prefsManager.acquireIsShuffleStatus();
        this.isRepeatOneMode = this.prefsManager.acquireIsRepeatOneStatus();

        this.javaScriptInterface = new JavaScriptInterface(this);
        this.mediaPlayerHandler = new Handler(Looper.getMainLooper());
    }

    private void initUI() {
        this.parameters = new WindowManager.LayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        this.parameters.gravity = Gravity.CENTER | Gravity.CENTER;
        this.parameters.x = 0;
        this.parameters.y = 0;

        this.inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);

        this.rootView = (FloatingWindowMediaPlayerPanel) this.inflater.inflate(R.layout.service_floating_window, null);
        this.youtubeWeb = (WebView) this.rootView.findViewById(R.id.web_youtube);
        this.mediaInfoText = (TextView) this.rootView.findViewById(R.id.media_player_panel_title_text);
        this.currentTimeText = (TextView) this.rootView.findViewById(R.id.media_player_panel_play_time_text);
        this.durationText = (TextView) this.rootView.findViewById(R.id.media_player_panel_play_total_time_text);

        this.seekBar = (SeekBar) this.rootView.findViewById(R.id.media_player_panel_seekbar);
    }

    private void assignUISettings() {
        this.rootView.showShuffleBtn(this.isShuffleMode);
        this.rootView.showRepeatBtn(this.isRepeatOneMode);
        this.rootView.setUpdatedParameters(parameters);
        this.rootView.setCallBack(this);

        this.seekBar.setOnSeekBarChangeListener(this.seekBarChangeListener);

        this.youtubeWeb.setBackgroundColor(this.getResources().getColor(R.color.color_FF000000));
        this.youtubeWeb.getSettings().setJavaScriptEnabled(true);
        this.youtubeWeb.getSettings().setMediaPlaybackRequiresUserGesture(false);
        this.youtubeWeb.addJavascriptInterface(this.javaScriptInterface, "Android");

        this.youtubeWeb.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                logUtil.i("url: " + url);
                javaScriptInterface.executeJSMediaPlayerLoadVideo();
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (url.startsWith("https://www.youtube.com/embed")) {
                    try {
                        URI uri = URI.create(url);
                        URL url1 = uri.toURL();
                        HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                        conn.setRequestProperty("Access-Control-Allow-Origin", "*");
                        conn.setRequestProperty("Referer", "https://www.google.com.tw");

                        DataInputStream dis = new DataInputStream(conn.getInputStream());
                        return new WebResourceResponse("text/html", "UTF-8", dis);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return super.shouldInterceptRequest(view, url);
            }
        });
        this.youtubeWeb.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        Map<String, String> mHeaderMap = new HashMap<>();
        mHeaderMap.put("Access-Control-Allow-Origin", "*");
        mHeaderMap.put("Referer", "https://www.google.com.tw");
        this.youtubeWeb.loadUrl("file:///android_asset/media_player_js_version.html", mHeaderMap);

        this.windowManager.addView(this.rootView, this.parameters);
    }

    private void acquireIntentData(Intent intent) {
        this.playListId = intent.getStringExtra(IntentExtra.INTENT_EXTRA_PLAYLIST_ID);
        this.videoId = intent.getStringExtra(IntentExtra.INTENT_EXTRA_PLAYLIST_VIDEO_ID);
    }

    /**
     * Logical Area: Media Player Operations
     */
    private void executeVideoScale() {
        this.rootView.startNarrowAnim();
    }

    private void executeVideoClose() {
        this.youtubeWeb.removeCallbacks(this.displayCurrentTimeRunnable);
        this.youtubeWeb.destroy();
        this.windowManager.removeView(this.rootView);
        this.stopSelf();
    }

    private void executeVideoUpdateCurrentTime(final int currentTime, final int duration) {
        this.mediaPlayerHandler.post(new Runnable() {

            @Override
            public void run() {
                int finalTime = duration - currentTime;
                int ratio = (int) (((float) currentTime / (float) duration) * 100);
                currentTimeText.setText(formatTime(finalTime));
                seekBar.setProgress(ratio);
            }
        });

        this.youtubeWeb.postDelayed(displayCurrentTimeRunnable, 100);
    }

    private void executeVideoUpdateDuration(final int duration) {
        this.mediaPlayerHandler.post(new Runnable() {

            @Override
            public void run() {
                durationText.setText(formatTime(duration));
                youtubeWeb.loadUrl("javascript:passPlayerCurrentTime()");
            }
        });
    }

    private void executeVideoSeekTo(final int playedLength) {
        this.mediaPlayerHandler.post(new Runnable() {

            @Override
            public void run() {
                youtubeWeb.loadUrl("javascript:seekToJSPlayerVideo(" + playedLength + ")");
                isSeekBarTouch = false;
            }
        });
    }

    private void executeVideoShuffleMode(boolean isShuffleMode) {
        this.prefsManager.putIsShuffleStatus(isShuffleMode);
        this.isShuffleMode = isShuffleMode;
        this.rootView.showShuffleBtn(this.isShuffleMode);

        final String shuffleModeMessage;
        if (this.isShuffleMode) {
            shuffleModeMessage = this.getResources().getString(R.string.toast_video_shuffle_mode);
        } else {
            shuffleModeMessage = this.getResources().getString(R.string.toast_video_sequence_mode);
        }

        this.mediaPlayerHandler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(FloatingWindowService.this, shuffleModeMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void executeVideoPlayPrev() {
        if (this.isShuffleMode) {
//            if (this.isRepeatOneMode) {
//                this.updateVideoId(MEDIA_PLAYER_STATUS_REPEAT);
//            } else {
//                this.updateVideoId(MEDIA_PLAYER_STATUS_SHUFFLE);
//            }
            this.updateVideoId(MEDIA_PLAYER_STATUS_SHUFFLE);
        } else {
//            if (this.isRepeatOneMode) {
//                this.updateVideoId(MEDIA_PLAYER_STATUS_REPEAT);
//            } else {
//                this.updateVideoId(MEDIA_PLAYER_STATUS_PREV);
//            }
            this.updateVideoId(MEDIA_PLAYER_STATUS_PREV);
        }
    }

    private void executeVideoPlay() {
        if (currentPlayerStatus == MEDIA_PLAYER_STATUS_PREV) {
            this.updateVideoId(MEDIA_PLAYER_STATUS_PREV);
        } else if (currentPlayerStatus == MEDIA_PLAYER_STATUS_NEXT) {
            this.updateVideoId(MEDIA_PLAYER_STATUS_NEXT);
        } else {
            this.updateVideoId(MEDIA_PLAYER_STATUS_NEXT);
        }
    }

    private void executeVideoPlayNext() {
        if (this.isShuffleMode) {
//            if (this.isRepeatOneMode) {
//                this.updateVideoId(MEDIA_PLAYER_STATUS_REPEAT);
//            } else {
//                this.updateVideoId(MEDIA_PLAYER_STATUS_SHUFFLE);
//            }
            this.updateVideoId(MEDIA_PLAYER_STATUS_SHUFFLE);
        } else {
//            if (this.isRepeatOneMode) {
//                this.updateVideoId(MEDIA_PLAYER_STATUS_REPEAT);
//            } else {
//                this.updateVideoId(MEDIA_PLAYER_STATUS_NEXT);
//            }
            this.updateVideoId(MEDIA_PLAYER_STATUS_NEXT);
        }
    }

    private void executeVideoRepeatOneMode(boolean isRepeatOneMode) {
        this.prefsManager.putIsRepeatOneStatus(isRepeatOneMode);
        this.isRepeatOneMode = isRepeatOneMode;
        this.rootView.showRepeatBtn(this.isRepeatOneMode);

        final String repeatOneModeMessage;
        if (this.isRepeatOneMode) {
            repeatOneModeMessage = this.getResources().getString(R.string.toast_video_repeat_one_mode);
        } else {
            repeatOneModeMessage = this.getResources().getString(R.string.toast_video_loop_all_mode);
        }

        this.mediaPlayerHandler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(FloatingWindowService.this, repeatOneModeMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateVideoId(int currentMediaPlayerControlStatus) {
        this.currentPlayerStatus = currentMediaPlayerControlStatus;

        if (currentMediaPlayerControlStatus == MEDIA_PLAYER_STATUS_PREV) {
            this.videoId = this.playlistManager.prevItem();
        } else if (currentMediaPlayerControlStatus == MEDIA_PLAYER_STATUS_PLAY) {
            // Do nothing
        } else if (currentMediaPlayerControlStatus == MEDIA_PLAYER_STATUS_NEXT) {
            this.videoId = this.playlistManager.nextItem();
        } else if (currentMediaPlayerControlStatus == MEDIA_PLAYER_STATUS_SHUFFLE) {
            this.videoId = this.playlistManager.shuffleItem();
        } else if (currentMediaPlayerControlStatus == MEDIA_PLAYER_STATUS_REPEAT) {
            this.videoId = this.playlistManager.repeatItem();
        }

        this.updateVideoInfo();
    }

    private void updateVideoInfo() {
        this.mediaPlayerHandler.post(new Runnable() {

            @Override
            public void run() {
                youtubeWeb.clearCache(true);
                youtubeWeb.reload();

                String index = Integer.toString(playlistManager.getCurrentIndex() + 1);
                mediaInfoText.setText("曲目: " + index + " - " + playlistManager.getCurrentItem().getSnippet().getTitle());

                rootView.blockPlayAndPause(true);
            }
        });
    }

    private void blockPlayAndPauseBtn(final boolean isBlocked) {
        this.mediaPlayerHandler.post(new Runnable() {

            @Override
            public void run() {
                rootView.blockPlayAndPause(isBlocked);
            }
        });
    }

    private String formatTime(int millis) {
        int seconds = millis;
        int minutes = seconds / 60;
        int hours = minutes / 60;

        return (hours == 0 ? "--:" : hours + ":") + String.format("%02d:%02d", minutes % 60, seconds % 60);
    }

    private void removeDisplayCurrentTimeRunnable() {
        this.youtubeWeb.removeCallbacks(displayCurrentTimeRunnable);
    }

    private void showNotification() {
        StringBuilder sb = new StringBuilder();
        String index = Integer.toString(playlistManager.getCurrentIndex() + 1);
        sb.append("曲目: ").append(index).append(" - ").append(playlistManager.getCurrentItem().getSnippet().getTitle());

        NotificationCompat.Builder nfBuilder = new NotificationCompat.Builder(this);
        nfBuilder.setSmallIcon(R.drawable.ic_clef_note);
        nfBuilder.setContentTitle("Cadenza");
        nfBuilder.setContentText(sb.toString());

        Intent resultIntent = new Intent(this, SplashActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SplashActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        nfBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, nfBuilder.build());
        startForeground(1, nfBuilder.build());
    }

    /**
     * Inner Classes
     */
    private class JavaScriptInterface {
        private static final int JS_PLAYER_CALLBACK_UNSTARTED_STATUS = -1;
        private static final int JS_PLAYER_CALLBACK_ENDED_STATUS = 10;
        private static final int JS_PLAYER_CALLBACK_PLAYING_STATUS = 11;
        private static final int JS_PLAYER_CALLBACK_PAUSED_STATUS = 12;
        private static final int JS_PLAYER_CALLBACK_BUFFERING_STATUS = 13;
        private static final int JS_PLAYER_CALLBACK_CUED_STATUS = 15;

        private Context mContext;
        private int seekBarDurationInfo;

        JavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showCurrentPlayId(String videoId, String listId) {
            logUtil.i("current videoId: " + videoId);
            logUtil.i("current listId: " + listId);
        }

        @JavascriptInterface
        public void showCurrentPlayerStatus(final String status) {
            logUtil.i("current status: " + status);

            int passedStatus = Integer.valueOf(status);
            blockPlayAndPauseBtn(true);

            switch (passedStatus) {
                case JS_PLAYER_CALLBACK_UNSTARTED_STATUS:
                    removeDisplayCurrentTimeRunnable();
                    break;

                case JS_PLAYER_CALLBACK_ENDED_STATUS:
                    if (isShuffleMode) {
                        if (isRepeatOneMode) {
                            updateVideoId(MEDIA_PLAYER_STATUS_REPEAT);
                        } else {
                            updateVideoId(MEDIA_PLAYER_STATUS_SHUFFLE);
                        }
                    } else {
                        if (isRepeatOneMode) {
                            updateVideoId(MEDIA_PLAYER_STATUS_REPEAT);
                        } else {
                            updateVideoId(MEDIA_PLAYER_STATUS_NEXT);
                        }
                    }

                    removeDisplayCurrentTimeRunnable();
                    break;

                case JS_PLAYER_CALLBACK_PLAYING_STATUS:
                    mediaPlayerHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            blockPlayAndPauseBtn(false);
                            rootView.showPlayBtn(true);
                        }
                    });
                    break;

                case JS_PLAYER_CALLBACK_PAUSED_STATUS:
                    mediaPlayerHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            blockPlayAndPauseBtn(false);
                            rootView.showPlayBtn(false);
                        }
                    });

                    removeDisplayCurrentTimeRunnable();
                    break;

                case JS_PLAYER_CALLBACK_BUFFERING_STATUS:
                    removeDisplayCurrentTimeRunnable();
                    break;

                case JS_PLAYER_CALLBACK_CUED_STATUS:
                    removeDisplayCurrentTimeRunnable();
                    break;
            }
        }

        @JavascriptInterface
        public void showDuration(int duration) {
            logUtil.i("duration: " + duration);
            this.seekBarDurationInfo = duration;
            executeVideoUpdateDuration(duration);
        }

        @JavascriptInterface
        public void showCurrentTime(int currentTime, int duration) {
            logUtil.i("currentTime: " + currentTime);
            executeVideoUpdateCurrentTime(currentTime, duration);
        }

        @JavascriptInterface
        public void showError(String error) {
            logUtil.i("current error: " + error);
            executeVideoPlay();
        }

        /**
         * Public Method Area: For calling JS Media Player
         */
        public void executeJSMediaPlayerLoadVideo() {
            youtubeWeb.loadUrl("javascript:init('" + videoId + "', '" + playListId + "')");
        }

        public void executeJSMediaPlayerPlay() {
            youtubeWeb.loadUrl("javascript:playJSPlayerVideo()");
        }

        public void executeJSMediaPlayerPause() {
            youtubeWeb.loadUrl("javascript:pauseJSPlayerVideo()");
        }

        public int getDuration() {
            return this.seekBarDurationInfo;
        }
    }

    private Runnable displayCurrentTimeRunnable = new Runnable() {

        @Override
        public void run() {
            youtubeWeb.loadUrl("javascript:passPlayerCurrentTime()");
        }
    };

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        private int finalProgress = 0;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.finalProgress = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if (!isSeekBarTouch) {
                isSeekBarTouch = true;
                youtubeWeb.removeCallbacks(displayCurrentTimeRunnable);
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            final long playedLength = (javaScriptInterface.getDuration() * this.finalProgress) / 100;

            logUtil.i("OnSeekBarChangeListener::duration - " + javaScriptInterface.getDuration());
            logUtil.i("OnSeekBarChangeListenerlength::playedLength - " + playedLength);

            executeVideoSeekTo((int) playedLength);
        }
    };
}
