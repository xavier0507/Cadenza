package xy.hippocampus.cadenza.controller.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.Random;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.fragment.base.BaseFragment;
import xy.hippocampus.cadenza.controller.manager.FragmentStackManager;
import xy.hippocampus.cadenza.controller.manager.PlaylistManager;
import xy.hippocampus.cadenza.controller.manager.PrefsManager;
import xy.hippocampus.cadenza.model.constant.IntentExtra;
import xy.hippocampus.cadenza.view.MediaPlayerPanel;

import static xy.hippocampus.cadenza.model.constant.Constants.API_KEY;
import static xy.hippocampus.cadenza.model.constant.Constants.FRAG_MEDIA_PLAYER_PANEL_TAG;
import static xy.hippocampus.cadenza.model.constant.Constants.FRAG_YOUTUBE_TAG;

/**
 * Created by Xavier Yin on 2017/6/28.
 */

public class MediaPlayerPanelFragment extends BaseFragment implements MediaPlayerPanel.PlayerCallback, View.OnClickListener, YouTubePlayer.OnInitializedListener {
    private PlaylistManager playlistManager = PlaylistManager.getInstance();
    private PrefsManager prefsManager = PrefsManager.getInstance(this.getActivity());
    private YouTubePlayerSupportFragment youTubePlayerFragment;

    private MediaPlayerPanel mediaPlayerPanel;
    private ImageButton scaleButton;
    private ImageButton shareButton;
    private ImageButton closeButton;
    private TextView videoTitleText;
    private ImageButton shuffleButton;
    private ImageButton shuffleCloseButton;
    private ImageButton prevButton;
    private ImageButton playButton;
    private ImageButton pauseButton;
    private ImageButton nextButton;
    private ImageButton repeatOneButton;
    private ImageButton recycleButton;

    private TextView playTimeTextView;
    private TextView playTotalTimeTextView;
    private SeekBar seekBar;

    private YouTubePlayer player;
    private Handler playTimeTextHandler;
    private Handler seekTimeHandler;

    private String videoId;
    private int currentPlayTime;
    private boolean isBackground;
    private boolean isTouched;
    private boolean isShowRepeatOne = true;
    private boolean isShowShuffle = true;

    @Override
    protected int layoutResourceId() {
        return R.layout.fragment_media_player_panel;
    }

    @Override
    protected void preProcess(View root) {
        super.preProcess(root);

        this.initArgs();
        this.checkStatus();
        this.initYoutube();
    }

    @Override
    protected void findView(View root) {
        super.findView(root);

        this.mediaPlayerPanel = (MediaPlayerPanel) root.findViewById(R.id.media_player_panel);
        this.scaleButton = (ImageButton) root.findViewById(R.id.media_player_panel_btn_scale);
        this.shareButton = (ImageButton) root.findViewById(R.id.media_player_panel_btn_share);
        this.closeButton = (ImageButton) root.findViewById(R.id.media_player_panel_btn_close);
        this.videoTitleText = (TextView) root.findViewById(R.id.media_player_panel_title_text);
        this.shuffleButton = (ImageButton) root.findViewById(R.id.media_player_panel_btn_shuffle);
        this.shuffleCloseButton = (ImageButton) root.findViewById(R.id.media_player_panel_btn_shuffle_close);
        this.prevButton = (ImageButton) root.findViewById(R.id.media_player_panel_btn_prev);
        this.pauseButton = (ImageButton) root.findViewById(R.id.media_player_panel_btn_pause);
        this.playButton = (ImageButton) root.findViewById(R.id.media_player_panel_btn_play);
        this.nextButton = (ImageButton) root.findViewById(R.id.media_player_panel_btn_next);
        this.repeatOneButton = (ImageButton) root.findViewById(R.id.media_player_panel_btn_repeat_one);
        this.recycleButton = (ImageButton) root.findViewById(R.id.media_player_panel_btn_recycle);

        this.playTimeTextView = (TextView) root.findViewById(R.id.media_player_panel_play_time_text);
        this.playTotalTimeTextView = (TextView) root.findViewById(R.id.media_player_panel_play_total_time_text);
        this.seekBar = (SeekBar) root.findViewById(R.id.media_player_panel_seekbar);
    }

    @Override
    protected void assignViewSettings(View root) {
        super.assignViewSettings(root);

        this.setMediaPlayerPanelCallBack();
        this.showCurrentVideoTitle();
        this.showRepeatOneStatus();
        this.showShuffleStatus();
    }

    @Override
    protected void addEvents(View root) {
        super.addEvents(root);

        this.scaleButton.setOnClickListener(this);
        this.shareButton.setOnClickListener(this);
        this.closeButton.setOnClickListener(this);
        this.shuffleButton.setOnClickListener(this);
        this.shuffleCloseButton.setOnClickListener(this);
        this.prevButton.setOnClickListener(this);
        this.playButton.setOnClickListener(this);
        this.pauseButton.setOnClickListener(this);
        this.nextButton.setOnClickListener(this);
        this.repeatOneButton.setOnClickListener(this);
        this.recycleButton.setOnClickListener(this);

        this.seekBar.setOnSeekBarChangeListener(this.seekBarChangeListener);
    }

    @Override
    protected void postProcess(View root) {
        super.postProcess(root);

        this.playTimeTextHandler = new Handler();
        this.seekTimeHandler = new Handler();
    }

    @Override
    public void onResume() {
        try {
            super.onResume();

            if (!FragmentStackManager.getInstance().includesKey(FRAG_MEDIA_PLAYER_PANEL_TAG)) {
                FragmentStackManager.getInstance().pushFragment(FRAG_MEDIA_PLAYER_PANEL_TAG, this);
            }

            if (!isBackground) {
                this.isBackground = false;
            } else {
                if (this.mediaPlayerPanel.isCanHide()) {
                    this.mediaPlayerPanel.enlargePlayerView();
                }
            }
        } catch (Exception e) {
            this.youTubePlayerFragment.initialize(API_KEY, this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (!isBackground) {
            this.isBackground = true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        this.logUtil.i("Media Player onDestroyView");

        if (this.playTimeTextHandler != null) {
            this.playTimeTextHandler.removeCallbacks(this.displayCurrentTimeRunnable);
        }

        if (this.seekTimeHandler != null) {
            this.seekTimeHandler.removeCallbacks(this.seekProgressRunnable);
        }

        if (this.player != null) {
            this.player.release();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        this.logUtil.i("onInitializationSuccess");

        if (player == null) {
            return;
        }

        this.player = player;
        this.displayCurrentTime();

        if (!wasRestored) {
            this.player.cueVideo(this.videoId);
        }

        this.logUtil.i("wasRestored: " + wasRestored);

        this.player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        this.player.setPlaybackEventListener(this.playbackEventListener);
        this.player.setPlayerStateChangeListener(this.playerStateChangeListener);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
        String errorMessage = error.toString();
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();

        this.logUtil.i("errorMessage:" + errorMessage);
    }

    @Override
    public void onPlayerViewHide() {
        this.logUtil.i("Media Player onPlayerViewHide");

        if (FragmentStackManager.getInstance().includesKey(FRAG_MEDIA_PLAYER_PANEL_TAG)) {
            this.getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            FragmentStackManager.getInstance().popFragment(FRAG_MEDIA_PLAYER_PANEL_TAG);
        }
    }

    @Override
    public void onPlayerClick() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.media_player_panel_btn_scale:
                this.mediaPlayerPanel.narrowPlayerView();
                break;

            case R.id.media_player_panel_btn_share:
                break;

            case R.id.media_player_panel_btn_close:
                this.onPlayerViewHide();
                break;

            case R.id.media_player_panel_btn_shuffle:
                this.setShowShuffleStatus(false);
                this.showShuffleStatus();
                Toast.makeText(this.getActivity(), "開啟隨機播放", Toast.LENGTH_LONG).show();
                break;

            case R.id.media_player_panel_btn_shuffle_close:
                this.setShowShuffleStatus(true);
                this.showShuffleStatus();
                Toast.makeText(this.getActivity(), "關閉隨機播放", Toast.LENGTH_LONG).show();
                break;

            case R.id.media_player_panel_btn_prev:
                if (!this.isShowShuffle) {
                    if (player != null) {
                        Random random = new Random();
                        int randomPlayIndex = random.nextInt(playlistManager.getItemListSize());
                        playlistManager.setCurrentPlayingItemIndex(randomPlayIndex);
                        videoId = playlistManager.getCurrentItem().getContentDetails().getVideoId();
                        player.loadVideo(videoId);
                        player.play();

                        showCurrentVideoTitle();
                    }
                } else {
                    if (this.player != null) {
                        this.videoId = this.playlistManager.prevItem();

                        this.logUtil.i("current video id: " + this.videoId);

                        this.showCurrentVideoTitle();
                        this.player.loadVideo(this.videoId);
                        this.player.play();
                    }
                }
                break;

            case R.id.media_player_panel_btn_play:
                try {
                    if (this.player != null && !this.player.isPlaying()) {
                        if (this.isBackground) {
                            this.player.loadVideo(this.videoId);
                        } else {
                            this.player.play();
                        }
                    }
                } catch (Exception e) {
                    this.youTubePlayerFragment.initialize(API_KEY, MediaPlayerPanelFragment.this);
                }
                break;

            case R.id.media_player_panel_btn_pause:
                if (this.player != null && this.player.isPlaying()) {
                    this.player.pause();
                }
                break;

            case R.id.media_player_panel_btn_next:
                if (!this.isShowShuffle) {
                    if (player != null) {
                        Random random = new Random();
                        int randomPlayIndex = random.nextInt(playlistManager.getItemListSize());
                        playlistManager.setCurrentPlayingItemIndex(randomPlayIndex);
                        videoId = playlistManager.getCurrentItem().getContentDetails().getVideoId();
                        player.loadVideo(videoId);
                        player.play();

                        showCurrentVideoTitle();
                    }
                } else {
                    if (this.player != null) {
                        this.videoId = this.playlistManager.nextItem();

                        this.logUtil.i("current video id: " + this.videoId);

                        this.showCurrentVideoTitle();
                        this.player.loadVideo(this.videoId);
                        this.player.play();
                    }
                }
                break;

            case R.id.media_player_panel_btn_repeat_one:
                this.setShowRepeatOneStatus(false);
                this.showRepeatOneStatus();
                Toast.makeText(this.getActivity(), "開啟單曲播放", Toast.LENGTH_LONG).show();
                break;

            case R.id.media_player_panel_btn_recycle:
                this.setShowRepeatOneStatus(true);
                this.showRepeatOneStatus();
                Toast.makeText(this.getActivity(), "開啟循環播放", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void initArgs() {
        if (this.getArguments() != null) {
            Bundle bundle = this.getArguments();
            this.videoId = bundle.getString(IntentExtra.INTENT_EXTRA_PLAYLIST_VIDEO_ID);

            this.logUtil.i("original videoId: " + this.videoId);
        }
    }

    private void checkStatus() {
        this.isShowRepeatOne = this.prefsManager.acquireIsRepeatOneStatus();
        this.isShowShuffle = this.prefsManager.acquireIsShuffleStatus();
    }

    private void initYoutube() {
        this.arrangeYoutubeView();
        this.arrangeYoutubeAPIKey();
    }

    private void arrangeYoutubeView() {
        FragmentManager fragmentManager = this.getChildFragmentManager();
        FragmentTransaction transaction = this.getChildFragmentManager().beginTransaction();

        this.youTubePlayerFragment = (YouTubePlayerSupportFragment) fragmentManager.findFragmentByTag(FRAG_YOUTUBE_TAG);
        if (this.youTubePlayerFragment == null) {
            this.youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
            transaction.replace(R.id.youtube_layout, youTubePlayerFragment).addToBackStack(null).commit();
        }
    }

    private void arrangeYoutubeAPIKey() {
        this.youTubePlayerFragment.initialize(API_KEY, this);
    }

    private void displayTotalTime() {
        String formattedTotalTime = formatTime(this.player.getDurationMillis());
        this.playTotalTimeTextView.setText(formattedTotalTime);
    }

    private void displayCurrentTime() {
        if (this.player == null) {
            return;
        }

        try {
            String formattedTime = formatTime(this.player.getDurationMillis() - this.player.getCurrentTimeMillis());
            this.playTimeTextView.setText(formattedTime);
        } catch (IllegalStateException e) {
            this.logUtil.e("displayCurrentTime exception: " + e.getMessage());

            this.playTimeTextHandler.removeCallbacks(this.displayCurrentTimeRunnable);
        }
    }

    private void seekProgress() {
        if (this.player == null) {
            return;
        }

        try {
            float progressRatio = ((float) this.player.getCurrentTimeMillis() / (float) this.player.getDurationMillis()) * 100;
            this.seekBar.setProgress((int) progressRatio);
        } catch (IllegalStateException e) {
            this.logUtil.e("seekProgress exception: " + e.getMessage());

            this.seekTimeHandler.removeCallbacks(this.seekProgressRunnable);
        }
    }

    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;

        return (hours == 0 ? "--:" : hours + ":") + String.format("%02d:%02d", minutes % 60, seconds % 60);
    }

    private void showPlayingPauseStatus(boolean isShowPause) {
        if (isShowPause) {
            this.playButton.setVisibility(View.GONE);
            this.pauseButton.setVisibility(View.VISIBLE);
        } else {
            this.playButton.setVisibility(View.VISIBLE);
            this.pauseButton.setVisibility(View.GONE);
        }
    }
    private void setMediaPlayerPanelCallBack() {
        this.mediaPlayerPanel.setPlayerCallback(this);
    }

    private void setShowRepeatOneStatus(boolean isShowRepeatOne) {
        this.prefsManager.putIsRepeatOneStatus(isShowRepeatOne);
        this.isShowRepeatOne = this.prefsManager.acquireIsRepeatOneStatus();
    }

    private void showRepeatOneStatus() {
        if (this.isShowRepeatOne) {
            this.repeatOneButton.setVisibility(View.VISIBLE);
            this.recycleButton.setVisibility(View.GONE);
        } else {
            this.repeatOneButton.setVisibility(View.GONE);
            this.recycleButton.setVisibility(View.VISIBLE);
        }
    }

    private void setShowShuffleStatus(boolean isShowShuffle) {
        this.prefsManager.putIsShuffleStatus(isShowShuffle);
        this.isShowShuffle = this.prefsManager.acquireIsShuffleStatus();
    }

    private void showShuffleStatus() {
        if (this.isShowShuffle) {
            this.shuffleButton.setVisibility(View.VISIBLE);
            this.shuffleCloseButton.setVisibility(View.GONE);
        } else {
            this.shuffleButton.setVisibility(View.GONE);
            this.shuffleCloseButton.setVisibility(View.VISIBLE);
        }
    }

    private void showCurrentVideoTitle() {
        this.videoTitleText.setText(this.playlistManager.getCurrentItem().getSnippet().getTitle());
    }

    /**
     * Inner Classes
     */
    private Runnable displayCurrentTimeRunnable = new Runnable() {

        @Override
        public void run() {
            displayCurrentTime();
            playTimeTextHandler.postDelayed(this, 100);
        }
    };

    private Runnable seekProgressRunnable = new Runnable() {

        @Override
        public void run() {
            seekProgress();
            seekTimeHandler.postDelayed(this, 100);
        }
    };

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {

        @Override
        public void onBuffering(boolean arg0) {
            logUtil.i("Video onBuffering!");
        }

        @Override
        public void onPaused() {
            logUtil.i("Video onPaused!");

            currentPlayTime = player.getCurrentTimeMillis();

            playTimeTextHandler.removeCallbacks(displayCurrentTimeRunnable);
            seekTimeHandler.removeCallbacks(seekProgressRunnable);

            showPlayingPauseStatus(false);
        }

        @Override
        public void onPlaying() {
            logUtil.i("Video onPlaying!");

            playTimeTextHandler.postDelayed(displayCurrentTimeRunnable, 100);
            seekTimeHandler.postDelayed(seekProgressRunnable, 100);

            displayTotalTime();
            displayCurrentTime();
            seekProgress();

            showPlayingPauseStatus(true);
        }

        @Override
        public void onSeekTo(int arg0) {
            logUtil.i("Video onSeekTo!");

            playTimeTextHandler.postDelayed(displayCurrentTimeRunnable, 100);
        }

        @Override
        public void onStopped() {
            logUtil.i("Video Stopped!");

            playTimeTextHandler.removeCallbacks(displayCurrentTimeRunnable);
            seekTimeHandler.removeCallbacks(seekProgressRunnable);
        }
    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {

        @Override
        public void onAdStarted() {
            logUtil.i("State onAdStarted!");
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
            logUtil.i("State onError!");
            arrangeYoutubeAPIKey();
        }

        @Override
        public void onLoaded(String arg0) {
            logUtil.i("State onLoaded!");

            if (player != null && !player.isPlaying()) {
                player.play();
            }

            if (isBackground && player != null && !player.isPlaying()) {
                player.seekToMillis(currentPlayTime);
                player.play();
                isBackground = false;

                logUtil.i("current time after resume: " + currentPlayTime);
            }
        }

        @Override
        public void onLoading() {
            logUtil.i("State onLoading!");
        }

        @Override
        public void onVideoEnded() {
            logUtil.i("State onVideoEnded!");

            if (!isShowShuffle) {
                if (isShowRepeatOne) {
                    if (player != null) {
                        Random random = new Random();
                        int randomPlayIndex = random.nextInt(playlistManager.getItemListSize());
                        playlistManager.setCurrentPlayingItemIndex(randomPlayIndex);
                        videoId = playlistManager.getCurrentItem().getContentDetails().getVideoId();
                        player.loadVideo(videoId);
                        player.play();

                        showCurrentVideoTitle();
                    }
                } else {
                    if (player != null) {
                        videoId = playlistManager.getCurrentItem().getContentDetails().getVideoId();
                        player.loadVideo(videoId);
                        player.play();

                        showCurrentVideoTitle();
                    }
                }
            } else {
                if (isShowRepeatOne) {
                    if (player != null) {
                        videoId = playlistManager.nextItem();
                        player.loadVideo(videoId);
                        player.play();

                        showCurrentVideoTitle();
                    }
                } else {
                    if (player != null) {
                        videoId = playlistManager.getCurrentItem().getContentDetails().getVideoId();
                        player.loadVideo(videoId);
                        player.play();

                        showCurrentVideoTitle();
                    }
                }
            }
        }

        @Override
        public void onVideoStarted() {
            logUtil.i("State onVideoStarted!");

            showPlayingPauseStatus(true);
            displayCurrentTime();
        }
    };

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        private int finalProgress = 0;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.finalProgress = progress;
            logUtil.i("onProgressChanged - final progress: " + this.finalProgress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            logUtil.i("onStartTrackingTouch");

            if (!isTouched) {
                isTouched = true;
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            logUtil.i("onStopTrackingTouch");

            try {
                if (player != null && isTouched) {
                    long lengthPlayed = (player.getDurationMillis() * this.finalProgress) / 100;
                    player.seekToMillis((int) lengthPlayed);
                    isTouched = false;
                }
            } catch (IllegalStateException e) {
                youTubePlayerFragment.initialize(API_KEY, MediaPlayerPanelFragment.this);
            }
        }
    };
}
