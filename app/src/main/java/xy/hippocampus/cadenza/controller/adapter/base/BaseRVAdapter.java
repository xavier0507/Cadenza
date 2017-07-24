package xy.hippocampus.cadenza.controller.adapter.base;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import java.util.List;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.util.LogUtil;

/**
 * Created by Xavier Yin on 5/23/17.
 */

public abstract class BaseRVAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected LogUtil logUtil = LogUtil.getInstance(BaseRVAdapter.class);

    protected Activity activity;
    protected RecyclerView recyclerView;
    protected List<T> list;
    protected IOnClickedListener<T> onClickedListener;

    private int lastPosition = -1;

    public BaseRVAdapter(Activity activity, List<T> list, IOnClickedListener<T> onClickedListener) {
        this.activity = activity;
        this.list = list;
        this.onClickedListener = onClickedListener;
    }

    public BaseRVAdapter(Activity activity, IOnClickedListener<T> onClickedListener) {
        this(activity, null, onClickedListener);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = this.createInflatedViewWithLayoutId(parent, viewType);
        return this.createViewHolder(inflatedView, viewType);
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public void clearList() {
        this.list.clear();
    }

    public void removeAllViews() {
        this.recyclerView.getLayoutManager().removeAllViews();
    }

    protected View createInflatedViewWithLayoutId(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View inflatedView = layoutInflater.inflate(this.createLayoutId(viewType), parent, false);
        return inflatedView;
    }

    protected boolean isResourceNull() {
        return (this.list == null || this.onClickedListener == null) ? true : false;
    }

    protected void startAnimation(final boolean isCrossedAnimation, final View view, final int position) {
        if (isCrossedAnimation) {
            if (position > this.lastPosition) {
                Animation animation;
                if (position % 2 == 0) {
                    animation = AnimationUtils.loadAnimation(activity, android.R.anim.slide_in_left);
                } else {
                    animation = AnimationUtils.loadAnimation(activity, R.anim.slide_in_right);
                }
                animation.setInterpolator(new DecelerateInterpolator());

                view.startAnimation(animation);
                lastPosition = position;
            }
        } else {
            if (position > this.lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(activity, android.R.anim.slide_in_left);
                view.startAnimation(animation);
                lastPosition = position;
            }
        }
    }

    abstract protected int createLayoutId(int viewType);

    abstract protected RecyclerView.ViewHolder createViewHolder(View inflatedView, int viewType);
}
