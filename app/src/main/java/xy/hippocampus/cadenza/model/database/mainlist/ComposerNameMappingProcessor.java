package xy.hippocampus.cadenza.model.database.mainlist;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.activity.app.CadenzaApplication;
import xy.hippocampus.cadenza.util.LogUtil;

/**
 * Created by Xavier Yin on 5/31/17.
 */
public class ComposerNameMappingProcessor {
    private static LogUtil logUtil = LogUtil.getInstance(ComposerNameMappingProcessor.class);

    public static final int ORIGINAL_NAME = 0;
    public static final int TRANSLATION_NAME = 1;
    public static final int COMPOSER_PHOTO_URL = 2;
    public static final int COMPOSER_PLAYLIST_INDEX = 3;

    private static final ComposerNameMappingProcessor instance = new ComposerNameMappingProcessor();

    private Map<Integer, List<String>> composerSortedNameMap = new TreeMap<>();

    private ComposerNameMappingProcessor() {
        this.initComposerSortedName();
    }

    public static ComposerNameMappingProcessor getInstance() {
        return instance;
    }

    public Map<Integer, List<String>> getComposerSortedNameMap() {
        return this.composerSortedNameMap;
    }

    public int getComposerSortedNameMapSize() {
        return this.composerSortedNameMap.size();
    }

    public String retrieveOriginalComposerNameWithIndex(int index) {
        if (this.composerSortedNameMap == null) {
            Resources resources = CadenzaApplication.getCadenzaResources();
            throw new RuntimeException(resources.getString(R.string.exception_no_composer_original_name));
        }

        return this.composerSortedNameMap.get(index).get(ORIGINAL_NAME);
    }

    public String retrieveTranslationComposerNameWithIndex(int index) {
        if (this.composerSortedNameMap == null) {
            Resources resources = CadenzaApplication.getCadenzaResources();
            throw new IllegalStateException(resources.getString(R.string.exception_no_composer_translation_name));
        }

        return this.composerSortedNameMap.get(index).get(TRANSLATION_NAME);
    }

    public String retrievePlaylistWithOriginalName(int index) {
        if (this.composerSortedNameMap == null) {
            Resources resources = CadenzaApplication.getCadenzaResources();
            throw new IllegalStateException(resources.getString(R.string.exception_no_composer_play_list));
        }

        return this.composerSortedNameMap.get(index).get(COMPOSER_PLAYLIST_INDEX);
    }

    public String retrieveComposerPhotoWithIndex(int index) {
        if (this.composerSortedNameMap == null) {
            Resources resources = CadenzaApplication.getCadenzaResources();
            throw new IllegalStateException(resources.getString(R.string.exception_no_composer_photo));
        }

        return this.composerSortedNameMap.get(index).get(COMPOSER_PHOTO_URL);
    }

    private void initComposerSortedName() {
        Resources resources = CadenzaApplication.getCadenzaResources();
        List<String> composerList = new ArrayList<>(Arrays.asList(resources.getStringArray(R.array.main_list)));

        logUtil.i("composerList size: " + composerList.size());

        for (int i = 0; i < composerList.size(); i++) {
            String composerName = composerList.get(i);
            String originalName = composerName.split("，")[ORIGINAL_NAME];
            String translationName = composerName.split("，")[TRANSLATION_NAME];
            String composerPhotoURL = composerName.split("，")[COMPOSER_PHOTO_URL];
            String works = composerName.split("，")[COMPOSER_PLAYLIST_INDEX];

            logUtil.i("originalName from composerList: " + originalName);
            logUtil.i("translationName from composerList: " + translationName);
            logUtil.i("composerPhotoURL from composerList: " + composerPhotoURL);
            logUtil.i("works from composerList: " + works);

            List<String> composerNameParts = new ArrayList<>();
            composerNameParts.add(originalName);
            composerNameParts.add(translationName);
            composerNameParts.add(composerPhotoURL);
            composerNameParts.add(works);
            composerSortedNameMap.put(i, composerNameParts);

            logUtil.i("originalName from treeMap: " + composerSortedNameMap.get(i).get(ORIGINAL_NAME));
            logUtil.i("translationName from treeMap: " + composerSortedNameMap.get(i).get(TRANSLATION_NAME));
            logUtil.i("composer photo url from treeMap: " + composerSortedNameMap.get(i).get(COMPOSER_PHOTO_URL));
            logUtil.i("works id from treeMap: " + composerSortedNameMap.get(i).get(COMPOSER_PLAYLIST_INDEX));
        }
    }
}
