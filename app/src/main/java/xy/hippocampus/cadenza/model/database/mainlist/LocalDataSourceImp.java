package xy.hippocampus.cadenza.model.database.mainlist;

import java.util.ArrayList;
import java.util.List;

import xy.hippocampus.cadenza.model.bean.MainListItemInfo;
import xy.hippocampus.cadenza.model.database.base.IDataSourceStrategy;

/**
 * Created by Xavier Yin on 2017/7/20.
 */

public class LocalDataSourceImp implements IDataSourceStrategy {
    private static final ComposerNameMappingProcessor COMPOSER_NAME_MAPPING_PROCESSOR = ComposerNameMappingProcessor.getInstance();

    @Override
    public List<MainListItemInfo> retrieveData() {
        List<MainListItemInfo> mainListItemInfos = new ArrayList<>();
        mainListItemInfos.clear();
        for (int i = 0; i < COMPOSER_NAME_MAPPING_PROCESSOR.getComposerSortedNameMapSize(); i++) {
            MainListItemInfo mainListItemInfo = new MainListItemInfo();
            mainListItemInfo.setTitle(COMPOSER_NAME_MAPPING_PROCESSOR.retrieveOriginalComposerNameWithIndex(i));
            mainListItemInfo.setSubTitle(COMPOSER_NAME_MAPPING_PROCESSOR.retrieveTranslationComposerNameWithIndex(i));
            mainListItemInfo.setUrl(COMPOSER_NAME_MAPPING_PROCESSOR.retrieveComposerPhotoWithIndex(i));
            mainListItemInfo.setPlaylistId(COMPOSER_NAME_MAPPING_PROCESSOR.retrievePlaylistWithOriginalName(i));
            mainListItemInfos.add(mainListItemInfo);
        }

        return mainListItemInfos;
    }
}
