package xy.hippocampus.cadenza.controller.adapter.recycler;

import android.app.Activity;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.adapter.base.BaseRVAdapter;
import xy.hippocampus.cadenza.controller.adapter.base.IOnClickedListener;
import xy.hippocampus.cadenza.model.bean.MainListItemInfo;
import xy.hippocampus.cadenza.view.SquareImageView;

/**
 * Created by Xavier Yin on 2017/7/19.
 */

public class FirebaseItemSampleRVAdapter extends BaseRVAdapter<MainListItemInfo> {

    public FirebaseItemSampleRVAdapter(Activity activity, IOnClickedListener<MainListItemInfo> onClickedListener) {
        super(activity, onClickedListener);
    }

    @Override
    protected int createLayoutId(int viewType) {
        return R.layout.item_main_list_comtent;
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolder(View inflatedView, int viewType) {
        return new FirebaseItemVH(inflatedView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final FirebaseItemVH itemComposerVH = (FirebaseItemVH) holder;
        final MainListItemInfo mainListItemInfo = this.list.get(position);

        if (!TextUtils.isEmpty(mainListItemInfo.getTitle())) {
            itemComposerVH.titleText.setText(mainListItemInfo.getTitle());
        }

        if (!TextUtils.isEmpty(mainListItemInfo.getSubTitle())) {
            itemComposerVH.subTitleText.setText(mainListItemInfo.getSubTitle());
        }

        if (!TextUtils.isEmpty(mainListItemInfo.getUrl())) {
            Picasso.with(itemComposerVH.urlRIV.getContext())
                    .load(mainListItemInfo.getUrl())
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .centerCrop()
                    .resize(150, 150)
                    .into(itemComposerVH.urlRIV);
            ViewCompat.setTransitionName(itemComposerVH.urlRIV, String.valueOf(position) + "_photo");
        }

        itemComposerVH.rootViewGroup.setTag(mainListItemInfo);
        itemComposerVH.rootViewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickedListener.onAdapterClicked(itemComposerVH, mainListItemInfo, position);
            }
        });
    }

    public class FirebaseItemVH extends RecyclerView.ViewHolder {
        public ViewGroup rootViewGroup;
        public SquareImageView urlRIV;
        public TextView titleText;
        public TextView subTitleText;

        public FirebaseItemVH(View itemView) {
            super(itemView);

            this.rootViewGroup = (ViewGroup) itemView.findViewById(R.id.layout_mainlist_item_root);
            this.urlRIV = (SquareImageView) itemView.findViewById(R.id.rv_url);
            this.titleText = (TextView) itemView.findViewById(R.id.text_title);
            this.subTitleText = (TextView) itemView.findViewById(R.id.text_subtitle);
        }
    }
}
