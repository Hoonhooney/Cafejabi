package com.example.cafejabi.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

public class Alarm {
    private static final int MINUTE = 1000*60;
    private Context mContext;
    private PendingIntent sender;
    private SharedPreferences alarmPreferences;
    private AlarmManager manager;

    public Alarm(Context context){
        mContext = context;

        Intent intent = new Intent(context, AlarmReceiver.class);
        sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmPreferences = context.getSharedPreferences("alarm", Context.MODE_PRIVATE);
    }

    public void set(){
        Log.d("Alarm", "set()");

        long latestUpdated = alarmPreferences.getLong("updatedAt", System.currentTimeMillis());
        int gap = alarmPreferences.getInt("gap", 60)*MINUTE;
        boolean on = alarmPreferences.getBoolean("on", false);

        if (on){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, latestUpdated+gap, sender);
            }else
                manager.setExact(AlarmManager.RTC_WAKEUP, latestUpdated+gap, sender);
        }
    }

    public void off(){
        Log.d("Alarm", "off()");

        SharedPreferences.Editor editor = alarmPreferences.edit();
        editor.putBoolean("on", false);
        editor.apply();

        if (manager != null)
            manager.cancel(sender);
    }
}
