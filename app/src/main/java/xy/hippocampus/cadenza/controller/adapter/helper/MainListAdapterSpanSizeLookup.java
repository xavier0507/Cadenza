package xy.hippocampus.cadenza.controller.adapter.helper;

import android.support.v7.widget.GridLayoutManager;

import xy.hippocampus.cadenza.controller.adapter.base.BaseRVAdapter;
import xy.hippocampus.cadenza.model.bean.MainListItemInfo;
import xy.hippocampus.cadenza.util.LogUtil;

/**
 * Created by Xavier Yin on 2017/7/15.
 */

public class MainListAdapterSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
    private LogUtil logUtil = LogUtil.getInstance(this.getClass());

    private BaseRVAdapter<MainListItemInfo> adapter;

    public MainListAdapterSpanSizeLookup(BaseRVAdapter<MainListItemInfo> adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getSpanSize(int position) {
        logUtil.i("lookup size: " + position);

        if (position == 0 || position == 5 || position == 12 || position == 17) {
            return 6;
        }

        if (position >=1 && position <= 4) {
            return 3;
        } else if (position >= 6 && position <= 11) {
            return 2;
        } else if (position >= 13 && position <= 16) {
            return 3;
        } else {
            return 2;
        }
    }
}
