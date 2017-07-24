package xy.hippocampus.cadenza.controller.adapter.recycler;

import android.app.Activity;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.adapter.base.BaseRVAdapter;
import xy.hippocampus.cadenza.controller.adapter.base.IOnClickedListener;
import xy.hippocampus.cadenza.model.bean.MainListItemInfo;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

/**
 * Created by Xavier Yin on 5/27/17.
 */

public class ComposerSampleRVAdapter extends BaseRVAdapter<MainListItemInfo> {

    public ComposerSampleRVAdapter(Activity activity, List<MainListItemInfo> composerList, IOnClickedListener onClickedListener) {
        super(activity, composerList, onClickedListener);
    }

    @Override
    protected int createLayoutId(int viewType) {
        return R.layout.item_sample_composer_card;
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolder(View inflatedView, final int position) {
        return new ItemComposerVH(inflatedView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ItemComposerVH itemComposerVH = (ItemComposerVH) holder;
        final MainListItemInfo composer = this.list.get(position);

        itemComposerVH.composerOriginalNameText.setText(composer.getTitle());
        itemComposerVH.composerTranslationNameText.setText(composer.getSubTitle());
        itemComposerVH.composerItemRootViewGroup.setTag(composer);

        Picasso.with(itemComposerVH.composerPhotoIV.getContext())
                .load(composer.getUrl())
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(NO_CACHE, NO_STORE)
                .into(itemComposerVH.composerPhotoIV);

        ViewCompat.setTransitionName(itemComposerVH.composerPhotoIV, String.valueOf(position) + "_photo");
        itemComposerVH.composerItemRootViewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickedListener.onAdapterClicked(itemComposerVH, composer, position);
            }
        });
    }

    public class ItemComposerVH extends RecyclerView.ViewHolder {
        public ViewGroup composerItemRootViewGroup;
        public ImageView composerPhotoIV;
        public TextView composerOriginalNameText;
        public TextView composerTranslationNameText;

        public ItemComposerVH(View itemView) {
            super(itemView);
            this.composerItemRootViewGroup = (ViewGroup) itemView.findViewById(R.id.layout_mainlist_item_root);
            this.composerPhotoIV = (ImageView) itemView.findViewById(R.id.rv_url);
            this.composerOriginalNameText = (TextView) itemView.findViewById(R.id.text_title);
            this.composerTranslationNameText = (TextView) itemView.findViewById(R.id.text_subtitle);
        }
    }
}
