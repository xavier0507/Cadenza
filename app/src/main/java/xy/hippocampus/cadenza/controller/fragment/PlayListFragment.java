package xy.hippocampus.cadenza.controller.fragment;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.youtube.model.PlaylistItem;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.adapter.base.BaseRVAdapter;
import xy.hippocampus.cadenza.controller.adapter.base.IOnClickedListener;
import xy.hippocampus.cadenza.controller.adapter.recycler.PlaylistItemRVAdapter;
import xy.hippocampus.cadenza.controller.fragment.base.BaseFragment;
import xy.hippocampus.cadenza.controller.manager.FragmentStackManager;
import xy.hippocampus.cadenza.controller.manager.GoogleAccountManager;
import xy.hippocampus.cadenza.controller.manager.PlaylistManager;
import xy.hippocampus.cadenza.controller.ytapi.base.IAsyncTaskActCallback;
import xy.hippocampus.cadenza.controller.ytapi.playlist.facade.PlaylistApi;
import xy.hippocampus.cadenza.model.bean.MainListItemInfo;
import xy.hippocampus.cadenza.model.constant.IntentExtra;
import xy.hippocampus.cadenza.view.SquareImageView;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;
import static xy.hippocampus.cadenza.controller.manager.GoogleAccountManager.REQUEST_AUTHORIZATION;
import static xy.hippocampus.cadenza.model.constant.Constants.FRAG_MEDIA_PLAYER_PANEL_TAG;
import static xy.hippocampus.cadenza.model.constant.Constants.FRAG_PLAYLIST_TAG;
import static xy.hippocampus.cadenza.model.constant.IntentExtra.INTENT_EXTRA_COMPOSER_INFO;

/**
 * Created by Xavier Yin on 2017/7/4.
 */

public class PlayListFragment extends BaseFragment implements
        AppBarLayout.OnOffsetChangedListener,
        SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener,
        IOnClickedListener<PlaylistItem> {
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private PlaylistManager playlistManager = PlaylistManager.getInstance();

    private boolean isTheTitleVisible;
    private boolean isTheTitleContainerVisible = true;

    protected Toolbar toolbar;

    private AppBarLayout appBarLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FrameLayout titleFrameLayout;
    private LinearLayout titleContainerLayout;

    private ImageView backImageView;
    private TextView titleText;

    private SquareImageView composerPhotoBackgroundRIV;
    private SquareImageView composerPhotoRIV;

    private SquareImageView composerCircle;
    private ViewGroup itemsLinearLayout;
    private TextView originalName;
    private TextView translationName;
    private ProgressDialog progressDialog;

    private MainListItemInfo mainListItemInfo;
    private PlaylistApi playlistApi;

    private List<PlaylistItem> currentPlaylistItems;

    @Override
    protected void onAuthSuccess() {
        super.onAuthSuccess();

        this.logUtil.i("PlayListFragment RESULT_OK");

        this.processAdapterData();
        this.isCheckingAccountStatus = false;
    }

    @Override
    protected int layoutResourceId() {
        return R.layout.fragment_play_list;
    }

    @Override
    protected void preProcess(View root) {
        super.preProcess(root);

        if (this.getArguments() != null) {
            this.logUtil.i("acquireComposerInfo");
            this.mainListItemInfo = (MainListItemInfo) this.getArguments().getSerializable(INTENT_EXTRA_COMPOSER_INFO);
        }

        this.playlistApi = PlaylistApi.getInstance(this.getActivity(), GoogleAccountManager.getInstance(this.getActivity()).getCredential());
    }

    @Override
    protected void findView(View root) {
        super.findView(root);

        this.swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh);
        this.toolbar = (Toolbar) root.findViewById(R.id.main_toolbar);
        this.appBarLayout = (AppBarLayout) root.findViewById(R.id.main_appbar);
        this.titleFrameLayout = (FrameLayout) root.findViewById(R.id.main_framelayout_title);
        this.titleContainerLayout = (LinearLayout) root.findViewById(R.id.main_linearlayout_title);
        this.backImageView = (ImageView) root.findViewById(R.id.main_imageview_back);
        this.titleText = (TextView) root.findViewById(R.id.main_textview_title);
        this.originalName = (TextView) root.findViewById(R.id.text_title);
        this.translationName = (TextView) root.findViewById(R.id.text_subtitle);
        this.composerCircle = (SquareImageView) root.findViewById(R.id.rectangle_composer);
        this.progressDialog = new ProgressDialog(this.getContext());

        this.composerPhotoBackgroundRIV = (SquareImageView) root.findViewById(R.id.main_imageview_placeholder);
        this.composerPhotoRIV = (SquareImageView) root.findViewById(R.id.rectangle_composer);
        this.itemsLinearLayout = (ViewGroup) root.findViewById(R.id.linearlayout_items);
    }

    @Override
    protected void assignViewSettings(View root) {
        super.assignViewSettings(root);

        this.backImageView.setVisibility(View.INVISIBLE);
        this.titleText.setText(this.mainListItemInfo.getTitle());

        this.originalName.setText(this.mainListItemInfo.getTitle());
        this.translationName.setText(this.mainListItemInfo.getSubTitle());
        this.progressDialog.setMessage(this.getString(R.string.progress_dialog_default_message));

        this.toolbar.setBackgroundColor(this.primaryColor);
        this.titleFrameLayout.setBackgroundColor(this.primaryColor);
        this.composerPhotoBackgroundRIV.setBackgroundColor(this.primaryColor);

        this.swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#" + Integer.toHexString(this.accentColor)));
        this.swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.parseColor("#" + Integer.toHexString(this.primaryColor)));

        Picasso.with(this.getActivity())
                .load(this.mainListItemInfo.getUrl())
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .centerCrop()
                .resize(180, 180)
                .memoryPolicy(NO_CACHE, NO_STORE)
                .into(this.composerPhotoBackgroundRIV);
        Picasso.with(this.getActivity())
                .load(this.mainListItemInfo.getUrl())
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .centerCrop()
                .resize(150, 150)
                .memoryPolicy(NO_CACHE, NO_STORE)
                .into(this.composerPhotoRIV);

        this.startAlphaAnimation(this.titleText, 0, View.INVISIBLE);
    }

    @Override
    protected void addEvents(View root) {
        super.addEvents(root);

        this.appBarLayout.addOnOffsetChangedListener(this);
        this.swipeRefreshLayout.setOnRefreshListener(this);
        this.backImageView.setOnClickListener(this);
    }

    @Override
    protected void postProcess(View root) {
        super.postProcess(root);

        this.processAdapterData();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.logUtil.i("Playlist onResume!");

        if (!FragmentStackManager.getInstance().includesKey(FRAG_PLAYLIST_TAG)) {
            FragmentStackManager.getInstance().pushFragment(FRAG_PLAYLIST_TAG, this);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        this.logUtil.i("Playlist::onOffsetChanged() - offset:" + offset);

        if (offset == 0) {
            this.swipeRefreshLayout.setEnabled(true);
        } else if (Math.abs(offset) > 0) {
            this.swipeRefreshLayout.setEnabled(false);
        }

        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        this.handleAlphaOnTitle(percentage);
        this.handleToolbarTitleVisibility(percentage);
    }

    @Override
    public void onRefresh() {
        this.processAdapterData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_imageview_back:
                if (this != null && FragmentStackManager.getInstance().includesKey(FRAG_PLAYLIST_TAG)) {
                    this.getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                    FragmentStackManager.getInstance().popFragment(FRAG_PLAYLIST_TAG);
                }
                break;
        }
    }

    @Override
    public void onAdapterClicked(RecyclerView.ViewHolder viewHolder, PlaylistItem param, int position) {
        this.logUtil.i("current playing position: " + position);

        if (this.currentPlaylistItems != null && this.currentPlaylistItems.size() > 0) {
            playlistManager.setItemList(this.currentPlaylistItems);
            playlistManager.setCurrentPlayingItemIndex(position);
        }

        FragmentManager fragmentManager = this.getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment = new MediaPlayerPanelFragment();
        FragmentStackManager.getInstance().pushFragment(FRAG_MEDIA_PLAYER_PANEL_TAG, fragment);

        Bundle bundle = new Bundle();
        bundle.putString(IntentExtra.INTENT_EXTRA_PLAYLIST_VIDEO_ID, param.getContentDetails().getVideoId());
        fragment.setArguments(bundle);
        fragmentTransaction
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.frag_media_player, fragment, FRAG_MEDIA_PLAYER_PANEL_TAG)
                .commit();
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (this.isTheTitleContainerVisible) {
                this.startAlphaAnimation(this.titleContainerLayout, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                this.startAlphaAnimation(this.composerCircle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                this.isTheTitleContainerVisible = false;
            }
        } else {
            if (!isTheTitleContainerVisible) {
                this.startAlphaAnimation(this.titleContainerLayout, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                this.startAlphaAnimation(this.composerCircle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                this.isTheTitleContainerVisible = true;
            }
        }
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (!this.isTheTitleVisible) {
                this.startAlphaAnimation(this.titleText, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                this.startAlphaAnimation(this.backImageView, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                this.isTheTitleVisible = true;
            }
        } else {
            if (this.isTheTitleVisible) {
                this.startAlphaAnimation(this.titleText, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                this.startAlphaAnimation(this.backImageView, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                this.isTheTitleVisible = false;
            }
        }
    }

    private void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE) ? new AlphaAnimation(0f, 1f) : new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    private void processAdapterData() {
        IAsyncTaskActCallback playlistItemActCallback = new IAsyncTaskActCallback<Void, Void, List<PlaylistItem>>() {

            @Override
            public void onActPreExecute() {
                if (!swipeRefreshLayout.isRefreshing()) {
                    getProgressCallback().notifyProgressAppear(true);
                }
            }

            @Override
            public void onActProgressUpdate(Void... y) {

            }

            @Override
            public void onActPostExecute(List<PlaylistItem> playlistItems) {
                if (getActivity() != null) {
                    BaseRVAdapter<PlaylistItem> playlistItemBaseRVAdapter = new PlaylistItemRVAdapter(getActivity(), playlistItems, PlayListFragment.this);

                    RecyclerView mediaInfoRV = new RecyclerView(getActivity());
                    mediaInfoRV.setAdapter(playlistItemBaseRVAdapter);
                    mediaInfoRV.setLayoutManager(new LinearLayoutManager(getActivity()));

                    currentPlaylistItems = playlistItems;

                    itemsLinearLayout.removeAllViews();
                    itemsLinearLayout.addView(mediaInfoRV);
                }

                getProgressCallback().notifyProgressDisappear();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onActCancelFinished() {
                getProgressCallback().notifyProgressDisappear();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onActCancelled(Exception lastError) {
                if (lastError != null && lastError instanceof UserRecoverableAuthIOException) {
                    if (!isCheckingAccountStatus) {
                        isCheckingAccountStatus = true;
                        startActivityForResult(((UserRecoverableAuthIOException) lastError).getIntent(), REQUEST_AUTHORIZATION);
                    }
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        };
        this.playlistApi.requestPlaylistItemApi(this.mainListItemInfo.getPlaylistId(), playlistItemActCallback);
    }

    public static PlayListFragment newInstance() {
        return new PlayListFragment();
    }
}
