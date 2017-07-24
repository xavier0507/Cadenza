package xy.hippocampus.cadenza.controller.adapter.recycler;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.adapter.base.BaseRVAdapter;
import xy.hippocampus.cadenza.controller.adapter.base.IOnClickedListener;
import xy.hippocampus.cadenza.model.bean.MainListItemInfo;
import xy.hippocampus.cadenza.view.SquareImageView;

/**
 * Created by Xavier Yin on 5/27/17.
 */

public class MainListItemRVAdapter extends BaseRVAdapter<MainListItemInfo> {
    public static final int TYPE_CATEGORY = 0;
    public static final int TYPE_COMPOSER_INFO = 1;

    public MainListItemRVAdapter(Activity activity, List<MainListItemInfo> composerList, IOnClickedListener onClickedListener) {
        super(activity, composerList, onClickedListener);
    }

    public MainListItemRVAdapter(Activity activity, IOnClickedListener<MainListItemInfo> onClickedListener) {
        super(activity, onClickedListener);
    }

    @Override
    protected int createLayoutId(int viewType) {
        switch (viewType) {
            case TYPE_CATEGORY:
                return R.layout.item_category_text;

            default:
                return R.layout.item_main_list_comtent;
        }
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolder(View inflatedView, int viewType) {
        switch (viewType) {
            case TYPE_CATEGORY:
                return new ItemCategoryVH(inflatedView);

            default:
                return new ItemComposerVH(inflatedView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final MainListItemInfo mainListItemInfo = this.list.get(position);
        holder.setIsRecyclable(false);

        switch (this.getItemViewType(position)) {
            case TYPE_CATEGORY:
                final ItemCategoryVH itemCategoryVH = (ItemCategoryVH) holder;

                itemCategoryVH.categoryText.setText(mainListItemInfo.getTitle());
                break;

            case TYPE_COMPOSER_INFO:
                final ItemComposerVH itemComposerVH = (ItemComposerVH) holder;

                itemComposerVH.titleText.setText(mainListItemInfo.getTitle());
                itemComposerVH.subTitleText.setText(mainListItemInfo.getSubTitle());
                itemComposerVH.rootViewGroup.setTag(mainListItemInfo);

                if (!TextUtils.isEmpty(mainListItemInfo.getUrl())) {
                    Picasso.with(itemComposerVH.urlRIV.getContext())
                            .load(mainListItemInfo.getUrl())
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .centerCrop()
                            .resize(150, 150)
                            .into(itemComposerVH.urlRIV);
                } else {
                    Picasso.with(itemComposerVH.urlRIV.getContext())
                            .load(R.drawable.bg_oops)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .centerCrop()
                            .resize(150, 150)
                            .into(itemComposerVH.urlRIV);
                }

                startAnimation(false, itemComposerVH.itemView, position);
                ViewCompat.setTransitionName(itemComposerVH.urlRIV, String.valueOf(position) + "_photo");
                itemComposerVH.rootViewGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickedListener.onAdapterClicked(itemComposerVH, mainListItemInfo, position);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == 5 || position == 12 || position == 17) {
            return TYPE_CATEGORY;
        }

        return TYPE_COMPOSER_INFO;
    }

    public class ItemComposerVH extends RecyclerView.ViewHolder {
        public ViewGroup rootViewGroup;
        public SquareImageView urlRIV;
        public TextView titleText;
        public TextView subTitleText;

        public ItemComposerVH(View itemView) {
            super(itemView);

            this.rootViewGroup = (ViewGroup) itemView.findViewById(R.id.layout_mainlist_item_root);
            this.urlRIV = (SquareImageView) itemView.findViewById(R.id.rv_url);
            this.titleText = (TextView) itemView.findViewById(R.id.text_title);
            this.subTitleText = (TextView) itemView.findViewById(R.id.text_subtitle);
        }
    }

    public class ItemCategoryVH extends RecyclerView.ViewHolder {
        public TextView categoryText;

        public ItemCategoryVH(View itemView) {
            super(itemView);
            this.categoryText = (TextView) itemView.findViewById(R.id.text_category);
        }
    }

    public static class ItemOffsetDecoration extends RecyclerView.ItemDecoration {
        private int offset;

        public ItemOffsetDecoration(int offset) {
            this.offset = offset;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDraw(c, parent, state);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);

            if (position >= 1 && position <= 4
                    || position >= 6 && position <= 11
                    || position >= 13 && position <= 16
                    || position >= 18) {
                outRect.left = offset;
                outRect.right = offset;
                outRect.bottom = offset;
            }
        }
    }
}
