package xy.hippocampus.cadenza.controller.adapter.recycler;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.youtube.model.PlaylistItem;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.adapter.base.BaseRVAdapter;
import xy.hippocampus.cadenza.controller.adapter.base.IOnClickedListener;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

/**
 * Created by Xavier Yin on 6/15/17.
 */

public class PlaylistItemRVAdapter extends BaseRVAdapter<PlaylistItem> {

    public PlaylistItemRVAdapter(Activity activity, List<PlaylistItem> list, IOnClickedListener onClickedListener) {
        super(activity, list, onClickedListener);
    }

    @Override
    protected int createLayoutId(int viewType) {
        return R.layout.item_play_list_content;
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolder(View inflatedView, final int position) {
        return new PlaylistItemRVH(inflatedView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final PlaylistItemRVAdapter.PlaylistItemRVH playlistItemRVH = (PlaylistItemRVAdapter.PlaylistItemRVH) holder;
        final PlaylistItem playlistItem = this.list.get(position);

        if (playlistItem.getSnippet().getThumbnails() != null) {
            String photoUrl = playlistItem.getSnippet().getThumbnails().getHigh().getUrl();
            Picasso.with(playlistItemRVH.thumbnailImageView.getContext())
                    .load(photoUrl)
                    .error(R.drawable.bg_oops)
//                    .networkPolicy(NetworkPolicy.NO_STORE)
//                    .memoryPolicy(NO_CACHE, NO_STORE)
                    .resize(120, 80)
                    .centerCrop()
                    .into(playlistItemRVH.thumbnailImageView);
        } else {
            Picasso.with(playlistItemRVH.thumbnailImageView.getContext())
                    .load(R.drawable.bg_oops)
                    .resize(120, 80)
                    .centerCrop()
                    .into(playlistItemRVH.thumbnailImageView);
        }

        playlistItemRVH.playListItemNameText.setTag(position);
        playlistItemRVH.playListItemNameText.setText(this.activity.getString(R.string.hint_video_number, String.valueOf(position + 1), playlistItem.getSnippet().getTitle()));

        this.startAnimation(true, playlistItemRVH.playlistItemRoot, position);
        playlistItemRVH.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onClickedListener.onAdapterClicked(playlistItemRVH, playlistItem, position);
            }
        });
    }

    public class PlaylistItemRVH extends RecyclerView.ViewHolder {
        ViewGroup playlistItemRoot;
        ImageView thumbnailImageView;
        TextView playListItemNameText;

        public PlaylistItemRVH(View itemView) {
            super(itemView);

            this.playlistItemRoot = (ViewGroup) itemView.findViewById(R.id.layout_playlist_item_root);
            this.thumbnailImageView = (ImageView) itemView.findViewById(R.id.img_play_list_thumbnail);
            this.playListItemNameText = (TextView) itemView.findViewById(R.id.text_play_list_item_name);
        }
    }

    public class ResizeImgTransformation implements Transformation {
        private ImageView thumbnailImageView;

        public Transformation setThumbnailImageView(ImageView thumbnailImageView) {
            this.thumbnailImageView = thumbnailImageView;
            return this;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            int imgWidth = this.thumbnailImageView.getWidth();
            int imgHeight = this.thumbnailImageView.getHeight();
            int x = imgWidth / 2;
            int y = imgHeight / 2;
            Bitmap result = Bitmap.createBitmap(source, x, y, imgWidth, imgHeight);

            if (result != source) {
                source.recycle();
            }

            logUtil.i("sourceWidth: " + source.getWidth());
            logUtil.i("sourceHeight: " + source.getHeight());

            logUtil.i("imgWidth: " + imgWidth);
            logUtil.i("imgHeight: " + imgHeight);

            return result;
        }

        @Override
        public String key() {
            return "resizeImg";
        }
    }
}

