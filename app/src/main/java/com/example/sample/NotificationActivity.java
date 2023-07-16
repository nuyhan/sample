package com.example.sample;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.sample.ProductItem;
import java.util.ArrayList;

public class NotificationActivity {
    private static final String CHANNEL_NAME = "채널 이름";
    private NotificationManager notificationManager;
    private Context context;

    public NotificationActivity(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void sendNotification(ArrayList<ProductItem> items, String timeBeforeExpiration) {
        if (items.isEmpty()) {
            return;
        }

        String channelId = "channel_" ;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.enableVibration(true);
            channel.setDescription("알림 채널 설명");
            notificationManager.createNotificationChannel(channel);
        }

        for (ProductItem item : items) {
            String notificationMessage = "상품: " + item.getName() + "\n유통 기한: " + item.getExpiryDate() + "\n알림 시점: " + timeBeforeExpiration;

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("유통 기한 알림")
                    .setContentText(notificationMessage)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            notificationManager.notify(item.getNotificationId(), builder.build());
        }
    }
}
