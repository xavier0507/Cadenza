package xy.hippocampus.cadenza.util;

import android.app.ProgressDialog;
import android.content.Context;

import xy.hippocampus.cadenza.controller.activity.app.CadenzaApplication;

/**
 * Created by Xavier Yin on 6/10/17.
 */

public class ViewUtil {

    public static int getStatusBarHeight() {
        Context context = CadenzaApplication.getCadenzaContext();

        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static ProgressDialog createProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        return progressDialog;
    }
}
