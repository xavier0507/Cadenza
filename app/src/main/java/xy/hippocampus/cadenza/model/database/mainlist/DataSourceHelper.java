package xy.hippocampus.cadenza.model.database.mainlist;

import java.util.List;

import xy.hippocampus.cadenza.model.bean.MainListItemInfo;
import xy.hippocampus.cadenza.model.database.base.IDataSourceStrategy;

/**
 * Created by Xavier Yin on 2017/7/20.
 */

public class DataSourceHelper {
    private static IDataSourceStrategy DATA_SOURCE_STRATEGY = new LocalDataSourceImp();

    public static void setDataSourceStrategy(IDataSourceStrategy dataSourceStrategy) {
        DATA_SOURCE_STRATEGY = dataSourceStrategy;
    }

    public static List<MainListItemInfo> retrieveData() {
        return DATA_SOURCE_STRATEGY.retrieveData();
    }
}