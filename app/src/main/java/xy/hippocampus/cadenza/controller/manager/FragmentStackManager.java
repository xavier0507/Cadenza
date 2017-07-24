package xy.hippocampus.cadenza.controller.manager;

import android.support.v4.app.Fragment;

import java.util.Collection;
import java.util.LinkedHashMap;

import xy.hippocampus.cadenza.util.LogUtil;

/**
 * Created by Xavier Yin on 2017/7/6.
 */

public class FragmentStackManager {
    private static final LogUtil logUtil = LogUtil.getInstance(FragmentStackManager.class);
    private static FragmentStackManager instance = null;
    private static LinkedHashMap<String, Fragment> fragmentStorage = new LinkedHashMap<>();

    private FragmentStackManager() {
    }

    public static synchronized final FragmentStackManager getInstance() {
        if (instance == null) {
            instance = new FragmentStackManager();
        }

        return instance;
    }

    public void pushFragment(String fragmentTag, Fragment fragment) {
        if (fragmentStorage.containsKey(fragmentTag)) {
            logUtil.i("contains!");
            updateContent(fragmentTag, fragment);
        } else {
            logUtil.i("not contains!");
            putContent(fragmentTag, fragment);
        }

        for (String key : fragmentStorage.keySet()) {
            logUtil.i("stack order: " + key);
        }
    }

    public void popFragment(String fragmentTag) {
        this.removeContent(fragmentTag);
    }

    public int getSize() {
        return fragmentStorage.size();
    }

    public Fragment getContent(String key) {
        return fragmentStorage.get(key);
    }

    public boolean includesKey(String key) {
        return fragmentStorage.containsKey(key);
    }

    public Collection<Fragment> getValues() {
        return fragmentStorage.values();
    }

    public void clearAll() {
        fragmentStorage.clear();
    }

    private void updateContent(String fragmentTag, Fragment fragment) {
        fragmentStorage.remove(fragmentTag);
        fragmentStorage.put(fragmentTag, fragment);
    }

    private void putContent(String fragmentTag, Fragment fragment) {
        fragmentStorage.put(fragmentTag, fragment);
    }

    private void removeContent(String fragmentTag) {
        if (fragmentStorage.containsKey(fragmentTag)) {
            fragmentStorage.remove(fragmentTag);
        }
    }
}
