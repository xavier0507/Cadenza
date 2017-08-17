package xy.hippocampus.cadenza.controller.service.helper;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.support.v7.app.NotificationCompat;

import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.manager.PlaylistManager;

/**
 * Created by Xavier Yin on 2017/8/14.
 */

public class NotificationHelper {
    private static final int NOTIFICATION_ID = 1;

    private PlaylistManager playlistManager = PlaylistManager.getInstance();

    public void showNotification(Service service, boolean isUpdate) {
        String subTitle = this.playlistManager.getCurrentComposer();
        String index = Integer.toString(playlistManager.getCurrentIndex() + 1);
        String currentTitle = service.getString(R.string.notification_title);
        String currentInfo = service.getString(R.string.notification_info, subTitle, index, playlistManager.getCurrentItem().getSnippet().getTitle());

        NotificationManager nfManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder nfBuilder = new NotificationCompat.Builder(service);
        nfBuilder.setSmallIcon(R.drawable.ic_clef_note)
                .setTicker(currentTitle)
                .setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(currentTitle)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(currentInfo))
//                .setContentIntent(resultPendingIntent)
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_logo))
                .setContentText(currentInfo);
        nfManager.notify(NOTIFICATION_ID, nfBuilder.build());

        if (!isUpdate) {
            service.startForeground(NOTIFICATION_ID, nfBuilder.build());
        }
    }

//    public void showBigViewNotification(Service service, boolean isUpdate) {
//        String index = Integer.toString(playlistManager.getCurrentIndex() + 1);
//        String currentTitle = service.getString(R.string.notification_title);
//        String currentInfo = service.getString(R.string.notification_info, index, playlistManager.getCurrentItem().getSnippet().getTitle());
//
//        Intent dismissIntent = new Intent();
//        dismissIntent.setAction("");
//        PendingIntent piDismiss = PendingIntent.getService(service, 0, dismissIntent, 0);
//
//        Intent snoozeIntent = new Intent();
//        snoozeIntent.setAction("");
//        PendingIntent piSnooze = PendingIntent.getService(service, 0, snoozeIntent, 0);
//
//        PendingIntent resultPendingIntent;
//        Intent resultIntent = new Intent(service, SplashActivity.class);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(service);
//        stackBuilder.addParentStack(SplashActivity.class);
//        stackBuilder.addNextIntent(resultIntent);
//        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        RemoteViews contentView = new RemoteViews(service.getPackageName(), R.layout.notification_player_big_view);
//        contentView.setImageViewResource(R.id.img_current_music, R.drawable.ic_clef_note);
//        contentView.setTextViewText(R.id.text_play_list_info, "古典主義");
//        contentView.setTextViewText(R.id.text_music_info, currentInfo);
//
//        NotificationCompat.Builder nfBuilder = new NotificationCompat.Builder(service);
//        nfBuilder.setSmallIcon(R.drawable.ic_clef_note);
////        nfBuilder.setContentIntent(resultPendingIntent);
////        nfBuilder.setTicker(currentTitle);
////        nfBuilder.setWhen(0);
////        nfBuilder.setAutoCancel(false);
////        nfBuilder.setContentTitle(currentTitle);
////        nfBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(currentInfo));
////        nfBuilder.setContentIntent(resultPendingIntent);
////        nfBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
////        nfBuilder.setCustomBigContentView(contentView);
//        nfBuilder.setStyle(new NotificationCompat.DecoratedMediaCustomViewStyle());
//        nfBuilder.setCustomContentView(contentView);
//
//        NotificationManager nfManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
//        nfManager.notify(NOTIFICATION_ID, nfBuilder.build());
//
//        if (!isUpdate) {
//            service.startForeground(NOTIFICATION_ID, nfBuilder.build());
//        }
//    }
}
