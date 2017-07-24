package xy.hippocampus.cadenza.controller.ytapi.playlist;

import android.app.Activity;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xy.hippocampus.cadenza.controller.ytapi.base.BaseAsyncTask;
import xy.hippocampus.cadenza.controller.ytapi.base.IAsyncTaskActCallback;

/**
 * Created by Xavier Yin on 6/15/17.
 */

public class PlaylistItemAsyncTask extends BaseAsyncTask<Void, Void, List<PlaylistItem>> {
    private String playlistId;

    public PlaylistItemAsyncTask(Activity activity, IAsyncTaskActCallback actCallback, GoogleAccountCredential credential, String playlistId) {
        super(activity, actCallback, credential);
        this.playlistId = playlistId;
    }

    @Override
    protected List<PlaylistItem> getDataFromApi(Void... params) throws IOException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("part", "snippet,contentDetails");
        parameters.put("maxResults", "50");
        parameters.put("playlistId", this.playlistId);

        YouTube.PlaylistItems.List playListRequest = this.service.playlistItems().list(parameters.get("part").toString());

        if (parameters.containsKey("maxResults")) {
            playListRequest.setMaxResults(Long.parseLong(parameters.get("maxResults").toString()));
        }

        if (parameters.containsKey("playlistId") && parameters.get("playlistId") != "") {
            playListRequest.setPlaylistId(parameters.get("playlistId").toString());
        }

        PlaylistItemListResponse response = playListRequest.execute();
        String nextToken = response.getNextPageToken();

        List<PlaylistItem> results = new ArrayList<>();
        results.addAll(response.getItems());

        logutil.i("nextToken" + nextToken);

        while (nextToken != null) {
            response = playListRequest.setPageToken(nextToken).execute();
            results.addAll(response.getItems());
            nextToken = response.getNextPageToken();
        }

        return results;
    }
}
