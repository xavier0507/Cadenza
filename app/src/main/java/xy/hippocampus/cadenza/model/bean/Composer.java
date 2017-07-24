package xy.hippocampus.cadenza.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import xy.hippocampus.cadenza.model.bean.base.BaseBeanWithParcelable;

/**
 * Created by Xavier Yin on 5/31/17.
 */

public class Composer extends BaseBeanWithParcelable {
    private int drawableId;
    private String originalName;
    private String translationName;
    private String composerPhotoURL;
    private List<String> playList;

    private Composer(Parcel in) {
        this.drawableId = in.readInt();
        this.originalName = in.readString();
        this.translationName = in.readString();
        this.playList = new ArrayList<>();
        in.readList(playList, null);
    }

    public Composer() {
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getTranslationName() {
        return translationName;
    }

    public void setTranslationName(String translationName) {
        this.translationName = translationName;
    }

    public String getComposerPhotoURL() {
        return composerPhotoURL;
    }

    public void setComposerPhotoURL(String composerPhotoURL) {
        this.composerPhotoURL = composerPhotoURL;
    }

    public List<String> getPlayList() {
        return playList;
    }

    public void setPlayList(List<String> playList) {
        this.playList = playList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.drawableId);
        dest.writeString(this.originalName);
        dest.writeString(this.translationName);
        dest.writeString(this.composerPhotoURL);
        dest.writeList(this.playList);
    }

    public static final Parcelable.Creator<Composer> CREATOR = new Parcelable.Creator<Composer>() {

        @Override
        public Composer createFromParcel(Parcel source) {
            return new Composer(source);
        }

        @Override
        public Composer[] newArray(int size) {
            return new Composer[size];
        }
    };
}
