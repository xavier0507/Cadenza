package xy.hippocampus.cadenza.controller.activity.sample;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.activity.common.base.BaseActivity;
import xy.hippocampus.cadenza.controller.adapter.base.BaseRVAdapter;
import xy.hippocampus.cadenza.controller.adapter.base.IOnClickedListener;
import xy.hippocampus.cadenza.controller.adapter.recycler.FirebaseItemSampleRVAdapter;
import xy.hippocampus.cadenza.model.bean.MainListItemInfo;

/**
 * Created by Xavier Yin on 2017/7/19.
 */

public class FirebaseSampleActivity extends BaseActivity implements IOnClickedListener<MainListItemInfo> {
    private RecyclerView firebaseRV;
    private BaseRVAdapter firebaseAdapter;
    private List<MainListItemInfo> mainListItemInfos;

    @Override
    protected int layoutResourceId() {
        return R.layout.activity_sample_firebase;
    }

    @Override
    protected void preProcess() {
        super.preProcess();

        this.firebaseAdapter = new FirebaseItemSampleRVAdapter(this, this);
        this.mainListItemInfos = new ArrayList<>();
    }

    @Override
    protected void findView() {
        super.findView();

        this.firebaseRV = (RecyclerView) this.findViewById(R.id.rv_firebase);
        this.firebaseAdapter.setList(this.mainListItemInfos);
        this.firebaseRV.setAdapter(this.firebaseAdapter);
    }

    @Override
    protected void assignViewSettings() {
        super.assignViewSettings();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        this.firebaseRV.setLayoutManager(gridLayoutManager);
    }

    @Override
    protected void addEvents() {
        super.addEvents();
    }

    @Override
    protected void postProcess() {
        super.postProcess();

        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("mainList");
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseAdapter.clearList();
                firebaseAdapter.removeAllViews();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    MainListItemInfo mainListItemInfo = new MainListItemInfo();
                    mainListItemInfo.setTitle(ds.child("title").getValue().toString());
                    mainListItemInfo.setSubTitle(ds.child("subTitle").getValue().toString());
                    mainListItemInfo.setUrl(ds.child("url").getValue().toString());
                    mainListItemInfo.setPlaylistId(ds.child("playlistId").getValue().toString());
                    mainListItemInfos.add(mainListItemInfo);

                    logUtil.i("onDataChange: " + mainListItemInfo.getTitle());
                }

                firebaseAdapter.setList(mainListItemInfos);
                firebaseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logUtil.i("onCancelled");
            }
        });
    }

    @Override
    public void onAdapterClicked(RecyclerView.ViewHolder viewHolder, MainListItemInfo param, int position) {
        Toast.makeText(this, String.valueOf(position), Toast.LENGTH_LONG).show();
    }
}
