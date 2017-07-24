package xy.hippocampus.cadenza.controller.activity.sample;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.activity.common.base.BaseActivity;

/**
 * Created by Xavier Yin on 6/11/17.
 */

public class YoutubeThumbnailSampleActivity extends BaseActivity {
    private YouTubeThumbnailView youTubeThumbnailView;

    @Override
    protected int layoutResourceId() {
        return R.layout.activity_sample_youtube_thumbnail;
    }

    @Override
    protected void preProcess() {
        super.preProcess();
    }

    @Override
    protected void findView() {
        super.findView();
        this.youTubeThumbnailView = (YouTubeThumbnailView) this.findViewById(R.id.yttv_play_list_thumbnail);
        this.youTubeThumbnailView.initialize("AIzaSyCK_3DeJCbSwq2G8CnZLHGdvaeMr1Rwoio", new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {
                youTubeThumbnailLoader.setVideo("L4ePL7NKKNk");
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                    @Override
                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                        youTubeThumbnailLoader.release();
                    }

                    @Override
                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }

    @Override
    protected void assignViewSettings() {
        super.assignViewSettings();
    }

    @Override
    protected void addEvents() {
        super.addEvents();
    }

    @Override
    protected void postProcess() {
        super.postProcess();
    }
}
