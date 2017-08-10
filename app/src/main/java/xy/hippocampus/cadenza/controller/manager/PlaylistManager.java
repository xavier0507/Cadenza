package xy.hippocampus.cadenza.controller.manager;

import com.google.api.services.youtube.model.PlaylistItem;

import java.util.List;
import java.util.Random;

import xy.hippocampus.cadenza.util.LogUtil;

/**
 * Created by Xavier Yin on 2017/7/14.
 */

public class PlaylistManager {
    private static LogUtil logUtil;
    private static PlaylistManager instance;
    private static List<PlaylistItem> itemList;
    private static int currentPlayingItemIndex;

    static {
        logUtil = LogUtil.getInstance(FragmentStackManager.class);
        instance = null;
        itemList = null;
        currentPlayingItemIndex = 0;
    }

    public static synchronized final PlaylistManager getInstance() {
        if (instance == null) {
            instance = new PlaylistManager();
        }

        return instance;
    }

    public String prevItem() {
        if (this.isItemListEmpty()) {
            throw new NullPointerException("請檢查播放列表是否為空。");
        }

        if (currentPlayingItemIndex > 0 && currentPlayingItemIndex < itemList.size()) {
            this.setCurrentPlayingItemIndex(--currentPlayingItemIndex);
        } else {
            this.setCurrentPlayingItemIndex(itemList.size() - 1);
        }

        logUtil.i("PlaylistManager::prevItem() - currentPlayingItemIndex: " + currentPlayingItemIndex);

        return itemList.get(currentPlayingItemIndex).getContentDetails().getVideoId();
    }

    public String nextItem() {
        if (this.isItemListEmpty()) {
            throw new NullPointerException("請檢查播放列表是否為空。");
        }

        if (currentPlayingItemIndex >= 0 && currentPlayingItemIndex < itemList.size() - 1) {
            this.setCurrentPlayingItemIndex(++currentPlayingItemIndex);
        } else {
            this.setCurrentPlayingItemIndex(0);
        }

        logUtil.i("PlaylistManager::nextItem() - currentPlayingItemIndex: " + currentPlayingItemIndex);

        return itemList.get(currentPlayingItemIndex).getContentDetails().getVideoId();
    }

    public String shuffleItem() {
        if (this.isItemListEmpty()) {
            throw new NullPointerException("請檢查播放列表是否為空。");
        }

        Random random = new Random();
        int randomPlayIndex = random.nextInt(this.getItemListSize());
        this.setCurrentPlayingItemIndex(randomPlayIndex);

        return itemList.get(currentPlayingItemIndex).getContentDetails().getVideoId();
    }

    public String repeatItem() {
        if (this.isItemListEmpty()) {
            throw new NullPointerException("請檢查播放列表是否為空。");
        }

        return itemList.get(currentPlayingItemIndex).getContentDetails().getVideoId();
    }

    public void setItemList(List<PlaylistItem> playlistItems) {
        if (playlistItems == null) {
            throw new IllegalArgumentException("playlistItems為空物件");
        }

        itemList = playlistItems;
    }

    public void setCurrentPlayingItemIndex(int playingItemIndex) {
        currentPlayingItemIndex = playingItemIndex;

        logUtil.i("PlaylistManager::setCurrentPlayingItemIndex() - currentPlayingItemIndex: " + playingItemIndex);
    }

    public PlaylistItem getCurrentItem() {
        return itemList.get(currentPlayingItemIndex);
    }

    public int getCurrentIndex() {
        return currentPlayingItemIndex;
    }

    public int getItemListSize() {
        return itemList.size();
    }

    private boolean isItemListEmpty() {
        return (itemList == null || itemList.size() == 0) ? true : false;
    }

    private void printContents() {
        for (PlaylistItem playlistItem : itemList) {
            logUtil.i("PlaylistManager::printContents() - videoId: " + playlistItem.getContentDetails().getVideoId());
            logUtil.i("PlaylistManager::printContents() - channelTitle: " + playlistItem.getSnippet().getChannelTitle());
        }
    }
}
