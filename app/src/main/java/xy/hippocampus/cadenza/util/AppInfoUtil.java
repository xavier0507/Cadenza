package xy.hippocampus.cadenza.util;

/**
 * Created by Xavier Yin on 2017/7/29.
 */

public class AppInfoUtil {
    private static int VERSION_NUMBER_SIZE = 3;
    private static String REG_VERSION_PATTERN = "[0-9]*";

    public static boolean isLastVersionOnLocalSide(String localVersion, String remoteVersion) {
        String[] localVersionArray = localVersion.split("\\.");
        String[] remoteVersionArray = remoteVersion.split("\\.");



        if (isCurrentVersionLength(localVersion, remoteVersion) &&
                isCurrentVersionFormat(localVersion, remoteVersion)) {
            for (int i = 0; i < localVersionArray.length; i++) {
                LogUtil.getInstance(AppInfoUtil.class).i("local version: " + localVersionArray[i]);
                LogUtil.getInstance(AppInfoUtil.class).i("remote version: " + remoteVersionArray[i]);

                if (Integer.parseInt(localVersionArray[0]) > Integer.parseInt(remoteVersionArray[0])) {
                    break;
                } else if (Integer.parseInt(localVersionArray[i]) < Integer.parseInt(remoteVersionArray[i])) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    private static boolean isCurrentVersionLength(String localVersion, String remoteVersion) {
        String[] localVersionArray = localVersion.split("\\.");
        String[] remoteVersionArray = remoteVersion.split("\\.");

        if (localVersionArray.length != VERSION_NUMBER_SIZE || remoteVersionArray.length != VERSION_NUMBER_SIZE) {
            return false;
        }

        return true;
    }

    private static boolean isCurrentVersionFormat(String localVersion, String remoteVersion) {
        String[] localVersionArray = localVersion.split("\\.");
        String[] remoteVersionArray = remoteVersion.split("\\.");

        for (int i = 0; i < localVersionArray.length; i++) {
            if (!localVersionArray[i].matches(REG_VERSION_PATTERN)) {
                return false;
            }

            if (!remoteVersionArray[i].matches(REG_VERSION_PATTERN)) {
                return false;
            }
        }

        return true;
    }
}
