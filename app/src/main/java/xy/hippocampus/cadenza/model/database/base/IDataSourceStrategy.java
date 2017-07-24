package xy.hippocampus.cadenza.model.database.base;

import java.util.List;

import xy.hippocampus.cadenza.model.bean.MainListItemInfo;

/**
 * Created by Xavier Yin on 2017/7/20.
 */

public interface IDataSourceStrategy {
    List<MainListItemInfo> retrieveData();
}
