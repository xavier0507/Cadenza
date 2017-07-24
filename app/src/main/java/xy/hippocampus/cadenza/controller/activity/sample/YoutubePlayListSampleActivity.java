package xy.hippocampus.cadenza.controller.activity.sample;

import android.app.ProgressDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.api.services.youtube.model.PlaylistItem;

import java.util.List;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.activity.common.base.BaseEPMIActivity;
import xy.hippocampus.cadenza.controller.adapter.base.BaseRVAdapter;
import xy.hippocampus.cadenza.controller.ytapi.base.IAsyncTaskActCallback;
import xy.hippocampus.cadenza.controller.ytapi.playlist.facade.PlaylistApi;

/**
 * Created by Xavier Yin on 6/14/17.
 */

public class YoutubePlayListSampleActivity extends BaseEPMIActivity {
    private RecyclerView mediaInfoRV;
    private BaseRVAdapter<PlaylistItem> playlistItemRVAdapter;
    private ProgressDialog progressDialog;
    private PlaylistApi playlistApi;

    @Override
    protected int layoutResourceId() {
        return R.layout.activity_sample_youtube_play_list;
    }

    @Override
    protected void preProcess() {
        super.preProcess();
        this.playlistApi = PlaylistApi.getInstance(this, this.googleAccountManager.getCredential());
    }

    @Override
    protected void findView() {
        super.findView();

        this.mediaInfoRV = (RecyclerView) this.findViewById(R.id.rv_media_info_list);
        this.progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void assignViewSettings() {
        super.assignViewSettings();

        this.mediaInfoRV.setLayoutManager(new LinearLayoutManager(this));
        this.progressDialog.setMessage("處理中...");
    }

    @Override
    protected void addEvents() {
        super.addEvents();
    }

    @Override
    protected void postProcess() {
        super.postProcess();
//        this.playlistApi.requestPlaylistItemApi(this.playlistItemActCallback);
    }

    private IAsyncTaskActCallback playlistItemActCallback = new IAsyncTaskActCallback<Void, Void, List<PlaylistItem>>() {

        @Override
        public void onActPreExecute() {
            progressDialog.show();
        }

        @Override
        public void onActProgressUpdate(Void... y) {
            // Do nothing
        }

        @Override
        public void onActPostExecute(List<PlaylistItem> playlistItems) {
            progressDialog.hide();
//            playlistItemRVAdapter = new PlaylistItemRVAdapter(YoutubePlayListSampleActivity.this, playlistItems);
            mediaInfoRV.setAdapter(playlistItemRVAdapter);
        }

        @Override
        public void onActCancelled(Exception lastError) {
            progressDialog.hide();
        }

        @Override
        public void onActCancelFinished() {

        }
    };

//    private class MakeRequestTask extends AsyncTask<Void, Void, List<PlaylistItem>> {
//        private com.google.api.services.youtube.YouTube service = null;
//        private Exception lastError = null;
//
//        MakeRequestTask(GoogleAccountCredential credential) {
//            HttpTransport transport = AndroidHttp.newCompatibleTransport();
//            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
//
//            this.service = new com.google.api.services.youtube.YouTube.Builder(transport, jsonFactory, credential)
//                    .setApplicationName("YouTube Data API Android Quickstart")
//                    .build();
//        }
//
//        @Override
//        protected void onPreExecute() {
//            progressDialog.show();
//        }
//
//        @Override
//        protected List<PlaylistItem> doInBackground(Void... params) {
//            try {
//                return getDataFromApi();
//            } catch (Exception e) {
//                this.lastError = e;
//                this.cancel(true);
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(List<PlaylistItem> output) {
//            progressDialog.hide();
//
//            if (output == null || output.size() == 0) {
//                Toast.makeText(YoutubePlayListSampleActivity.this, "無結果", Toast.LENGTH_LONG).show();
//            } else {
//                playlistItemRVAdapter = new PlaylistItemRVAdapter(YoutubePlayListSampleActivity.this, output);
//                mediaInfoRV.setAdapter(playlistItemRVAdapter);
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            progressDialog.hide();
//
//            if (lastError != null) {
//                if (lastError instanceof GooglePlayServicesAvailabilityIOException) {
//                    googleAccountManager.showGooglePlayServicesAvailabilityErrorDialog(((GooglePlayServicesAvailabilityIOException) lastError).getConnectionStatusCode());
//                } else if (lastError instanceof UserRecoverableAuthIOException) {
//                    startActivityForResult(((UserRecoverableAuthIOException) lastError).getIntent(), REQUEST_AUTHORIZATION);
//                } else {
//                    Toast.makeText(YoutubePlayListSampleActivity.this, "The following error occurred:\n" + lastError.getCause(), Toast.LENGTH_LONG).show();
//                }
//            } else {
//                Toast.makeText(YoutubePlayListSampleActivity.this, "Request cancelled.", Toast.LENGTH_LONG).show();
//            }
//        }
//
//        private List<PlaylistItem> getDataFromApi() throws IOException {
//            HashMap<String, String> parameters = new HashMap<>();
//            parameters.put("part", "snippet,contentDetails");
//            parameters.put("maxResults", "25");
//            parameters.put("playlistId", "PLOEZfHSao19VduitdH5zXo5Nptx6EdPf0");
//
//            YouTube.PlaylistItems.List playListRequest = this.service.playlistItems().list(parameters.get("part").toString());
//
//            if (parameters.containsKey("maxResults")) {
//                playListRequest.setMaxResults(Long.parseLong(parameters.get("maxResults").toString()));
//            }
//
//            if (parameters.containsKey("playlistId") && parameters.get("playlistId") != "") {
//                playListRequest.setPlaylistId(parameters.get("playlistId").toString());
//            }
//
//            PlaylistItemListResponse response = playListRequest.execute();
//            List<PlaylistItem> results = response.getItems();
//
//            return results;
//        }
//    }
}
