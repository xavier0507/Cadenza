package xy.hippocampus.cadenza.controller.activity.sample;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import xy.hippocampus.cadenza.R;

public class YoutubeSampleActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private static final String API_KEY = "AIzaSyCK_3DeJCbSwq2G8CnZLHGdvaeMr1Rwoio";
    private static final String VIDEO_ID = "QaBrjn3Nn6M";

    private YouTubePlayerView youtubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_sample_youtube);

        this.youtubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
        this.youtubePlayerView.initialize(API_KEY, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (null == youTubePlayer) return;

        if (!b) {
            Log.d("CheckPoint", "CheckPoint !wasRestored");
            youTubePlayer.cueVideo(VIDEO_ID);
        }

        youTubePlayer.setPlayerStateChangeListener(this.playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(this.playbackEventListener);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "onInitializationSuccess!", Toast.LENGTH_SHORT).show();
    }

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onLoading() {
        }

        @Override
        public void onLoaded(String s) {
        }

        @Override
        public void onAdStarted() {
        }

        @Override
        public void onVideoStarted() {
        }

        @Override
        public void onVideoEnded() {
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
        }
    };

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onPlaying() {
        }

        @Override
        public void onPaused() {
        }

        @Override
        public void onStopped() {
        }

        @Override
        public void onBuffering(boolean b) {
        }

        @Override
        public void onSeekTo(int i) {
        }
    };
}
