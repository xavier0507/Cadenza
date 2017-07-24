package xy.hippocampus.cadenza.controller.ytapi.playlist.facade;

import android.app.Activity;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import xy.hippocampus.cadenza.controller.ytapi.base.BaseApi;
import xy.hippocampus.cadenza.controller.ytapi.base.IAsyncTaskActCallback;
import xy.hippocampus.cadenza.controller.ytapi.playlist.PlaylistItemAsyncTask;

/**
 * Created by Xavier Yin on 16/06/2017.
 */

public class PlaylistApi extends BaseApi implements IPlaylistItemFacade {
    private static PlaylistApi instance = null;

    private PlaylistItemAsyncTask playlistItemAsyncTask;

    private PlaylistApi(Activity activity, GoogleAccountCredential credential) {
        super(activity, credential);
    }

    public static synchronized PlaylistApi getInstance(Activity activity, GoogleAccountCredential credential) {
        if (instance == null) {
            instance = new PlaylistApi(activity, credential);
        }

        return instance;
    }

    @Override
    public void requestPlaylistItemApi(String playlistId, IAsyncTaskActCallback actCallback) {
        this.playlistItemAsyncTask = new PlaylistItemAsyncTask(this.activity, actCallback, this.credential, playlistId);
        this.playlistItemAsyncTask.execute();
    }

    @Override
    public void cancelPlaylistItemApi() {
        if (this.playlistItemAsyncTask != null) {
            this.playlistItemAsyncTask.cancel(true);
        }
    }
}
