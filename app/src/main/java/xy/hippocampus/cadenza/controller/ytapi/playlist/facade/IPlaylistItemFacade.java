package xy.hippocampus.cadenza.controller.ytapi.playlist.facade;

import xy.hippocampus.cadenza.controller.ytapi.base.IAsyncTaskActCallback;

/**
 * Created by Xavier Yin on 16/06/2017.
 */

public interface IPlaylistItemFacade {
    void requestPlaylistItemApi(String playlistId, IAsyncTaskActCallback actCallback);

    void cancelPlaylistItemApi();
}
