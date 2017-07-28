package xy.hippocampus.cadenza.view.theme;

import xy.hippocampus.cadenza.controller.activity.common.HomeActivity;

/**
 * Created by Xavier Yin on 2017/7/28.
 */

public class ThemeSettings {
    private HomeActivity activity;

    public ThemeSettings(HomeActivity activity) {
        this.activity = activity;
    }

    public HomeActivity getActivity() {
        return activity;
    }
}
