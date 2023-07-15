package com.example.sample;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.example.sample.ProductItem;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;


public class NotificationActivity {
    private static final String CHANNEL_ID = "channel_id";
    private static final String CHANNEL_NAME = "Channel Name";
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.enableVibration(true);
            channel.setDescription("Notification Channel Description");
            notificationManager.createNotificationChannel(channel);
        }

        for (ProductItem item : items) {
            String notificationMessage = "상품: " + item.getName() + "\n유통 기한: " + item.getExpiryDate() + "\n알림 시점: " + timeBeforeExpiration;

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("유통 기한 알림")
                    .setContentText(notificationMessage)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            notificationManager.notify(item.getNotificationId(), builder.build());
        }
    }


}
