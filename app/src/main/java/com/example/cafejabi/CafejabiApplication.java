package com.example.cafejabi;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

public class CafejabiApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 노티피케이션 채널 생성하기 안드로이드 버전 오레오 이상부터 필요
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("Alarm", "Notification channel create");

            String channelId = "cafejabi";
            CharSequence channelName = "cafejabi";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }
    }

}
