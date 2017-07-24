package xy.hippocampus.cadenza.controller.adapter.base;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Xavier Yin on 2017/7/6.
 */

public interface IOnClickedListener<T> {
    void onAdapterClicked(RecyclerView.ViewHolder viewHolder, T param, int position);
}
