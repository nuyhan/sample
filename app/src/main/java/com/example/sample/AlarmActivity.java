//package com.example.sample;
//
//import android.util.Log;
//import android.app.NotificationManager;
//import android.content.Context;
//import android.os.Build;
//import android.app.NotificationChannel;
//import android.graphics.Color;
//import androidx.core.app.NotificationCompat;
//import androidx.annotation.NonNull;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.text.SimpleDateFormat;
//import java.text.ParseException;
//import java.util.Date;
//
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//public class AlarmActivity extends FirebaseMessagingService {
//    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//    private static final String TAG = "AlarmActivity";
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        // FCM 토큰 가져오기
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "FCM 등록 토큰 가져오기 실패", task.getException());
//                            return;
//                        }
//
//                        // Get new FCM registration token
//                        String token = task.getResult();
//
//                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d(TAG, msg);
//                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//        if (remoteMessage.getData().size() > 0) {
//            // 데이터 메시지 처리
//            String itemName = remoteMessage.getData().get("itemName");
//            String expiryDate = remoteMessage.getData().get("expiryDate");
//            int quantity = Integer.parseInt(remoteMessage.getData().get("quantity"));
//            String timeBeforeExpiration = remoteMessage.getData().get("timeBeforeExpiration");
//
//            ProductItem item = new ProductItem(itemName, expiryDate, quantity);
//            checkExpiration(item, timeBeforeExpiration);
//        } else if (remoteMessage.getNotification() != null) {
//            // 알림 메시지 처리
//            String title = remoteMessage.getNotification().getTitle();
//            String body = remoteMessage.getNotification().getBody();
//
//            // 알림 생성 및 표시
//            sendNotification(new ProductItem(title, "", 0), body);
//        }
//    }
//
//
//
//    @Override
//    public void onNewToken(@NonNull String token) {
//        super.onNewToken(token);
//
//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // FCM registration token to your app server.
//        sendRegistrationToServer(token);
//    }
//    private void checkExpiration(ProductItem item, String timeBeforeExpiration) {
//        try {
//            Date expiryDate = dateFormat.parse(item.getExpiryDate());
//            long timeDifference = expiryDate.getTime() - System.currentTimeMillis();
//
//            if (timeDifference <= 0) { // 유통기한이 지났을 경우
//                // 유통기한이 지난 경우에 대한 처리 코드 추가
//            } else if (timeDifference <= 86400000 && timeDifference > 0) { // 24시간(86400000밀리초) 이내에 유통기한이 끝나는 상품 체크
//                sendNotification(item, timeBeforeExpiration);
//            } else if (timeDifference <= 259200000 && timeDifference > 0) { // 3일(72시간) 이내에 유통기한이 끝나는 상품 체크
//                sendNotification(item, timeBeforeExpiration);
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void sendNotification(ProductItem item, String timeBeforeExpiration) {
//        String channelId = "channel_id";
//        String channelName = "Channel Name";
//
//        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
//            channel.enableLights(true);
//            channel.setLightColor(Color.GREEN);
//            channel.enableVibration(true);
//            channel.setDescription("알림 채널 설명");
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        String notificationMessage = "상품: " + item.getName() + "\n유통 기한: " + item.getExpiryDate() + "\n알림 시점: " + timeBeforeExpiration;
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.drawable.ic_notification)
//                .setContentTitle("유통 기한 알림")
//                .setContentText(notificationMessage)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setAutoCancel(true);
//
//        notificationManager.notify(0, builder.build());
//    }
//
//    public void showNotification(Context context, String message) {
//        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
//    }
//
//}
