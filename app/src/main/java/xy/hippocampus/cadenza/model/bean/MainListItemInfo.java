package xy.hippocampus.cadenza.model.bean;

import xy.hippocampus.cadenza.model.bean.base.BaseBeanWithSerializable;

/**
 * Created by Xavier Yin on 2017/7/13.
 */

public class MainListItemInfo extends BaseBeanWithSerializable {
    private String title;
    private String subTitle;
    private String url;
    private String playlistId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }
}
