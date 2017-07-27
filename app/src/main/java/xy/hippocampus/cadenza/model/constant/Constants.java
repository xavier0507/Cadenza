package xy.hippocampus.cadenza.model.constant;

import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import xy.hippocampus.cadenza.BuildConfig;
import xy.hippocampus.cadenza.controller.fragment.AboutFragment;
import xy.hippocampus.cadenza.controller.fragment.ComposerListFragment;
import xy.hippocampus.cadenza.controller.fragment.MainListFragment;
import xy.hippocampus.cadenza.controller.fragment.MediaPlayerPanelFragment;
import xy.hippocampus.cadenza.controller.fragment.PlayListFragment;

/**
 * Created by Xavier Yin on 2017/7/19.
 */

public class Constants {

    // Fragment Tags
    public static final String FRAG_MAIN_LIST_TAG = MainListFragment.class.getSimpleName();
    public static final String FRAG_COMPOSER_LIST_TAG = ComposerListFragment.class.getSimpleName();
    public static final String FRAG_PLAYLIST_TAG = PlayListFragment.class.getSimpleName();
    public static final String FRAG_MEDIA_PLAYER_PANEL_TAG = MediaPlayerPanelFragment.class.getSimpleName();
    public static final String FRAG_YOUTUBE_TAG = YouTubePlayerSupportFragment.class.getSimpleName();
    public static final String FRAG_ABOUT_TAG = AboutFragment.class.getSimpleName();

    // Data
    public static final String API_KEY = BuildConfig.API_KEY;
}
