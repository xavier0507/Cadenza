package xy.hippocampus.cadenza.model.database.mainlist;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import xy.hippocampus.cadenza.model.bean.ErrorMessage;
import xy.hippocampus.cadenza.model.bean.MainListItemInfo;
import xy.hippocampus.cadenza.model.database.base.IDataSourceStrategy;
import xy.hippocampus.cadenza.util.LogUtil;

/**
 * Created by Xavier Yin on 2017/7/20.
 */

public class FirebaseDataSourceImp implements IDataSourceStrategy {
    private static final LogUtil logUtil = LogUtil.getInstance(FirebaseDataSourceImp.class);

    private IValueAddListener valueAddListener;
    private List<MainListItemInfo> mainListItemInfos;

    public FirebaseDataSourceImp(IValueAddListener valueAddListener) {
        this.valueAddListener = valueAddListener;
        this.mainListItemInfos = new ArrayList<>();
    }

    @Override
    public List<MainListItemInfo> retrieveData() {
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("mainList");
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mainListItemInfos.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    MainListItemInfo mainListItemInfo = new MainListItemInfo();
                    mainListItemInfo.setTitle(ds.child("title").getValue().toString());
                    mainListItemInfo.setSubTitle(ds.child("subTitle").getValue().toString());
                    mainListItemInfo.setUrl(ds.child("url").getValue().toString());
                    mainListItemInfo.setPlaylistId(ds.child("playlistId").getValue().toString());
                    mainListItemInfos.add(mainListItemInfo);

                    logUtil.i("subTitle: " + mainListItemInfo.getSubTitle());
                }

                valueAddListener.onDataChange(mainListItemInfos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ErrorMessage errorMessage = new ErrorMessage();
                errorMessage.setErrorCode(databaseError.getCode());
                errorMessage.setErrorMessage(databaseError.getMessage());
                errorMessage.setErrorDetails(databaseError.getDetails());
                errorMessage.setToException(databaseError.toException());

                valueAddListener.onCancelled(errorMessage);
            }
        });

        return null;
    }

    public interface IValueAddListener {
        void onDataChange(List<MainListItemInfo> mainListItemInfos);

        void onCancelled(ErrorMessage databaseError);
    }
}
