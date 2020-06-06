package com.example.cafejabi.alarm;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.cafejabi.R;
import com.example.cafejabi.activities.MainActivity;
import com.example.cafejabi.objects.Cafe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences alarmPreferences;
    boolean open = false;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("Alarm", "AlarmReceiver");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        alarmPreferences = context.getSharedPreferences("alarm", Context.MODE_PRIVATE);
        boolean alarm_on = alarmPreferences.getBoolean("on", false);

        FirebaseUser user = mAuth.getCurrentUser();
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (alarm_on && user != null && !user.isAnonymous() && 7 < currentHour && currentHour < 23){
            db.collection("cafes").whereEqualTo("uid", user.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        Cafe myCafe = Objects.requireNonNull(task.getResult()).getDocuments().get(0).toObject(Cafe.class);

                        open = myCafe != null && myCafe.isOpen();

                        if (open){

                            Intent toIntent = new Intent(context, MainActivity.class);
                            toIntent.putExtra("cid", myCafe.getCid());
                            toIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, toIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "cafejabi")
                                    .setSmallIcon(R.mipmap.ic_launcher) //알람 아이콘
                                    .setContentTitle("카페잡이")  //알람 제목
                                    .setContentText("현재 카페의 자리 정보를 이용자들에게 알려주세요!") //알람 내용
                                    .setContentIntent(pendingIntent)  //탭하면 카페 정보 업데이트 페이지로 이동
                                    .setAutoCancel(true)    //탭하면 알림 자동 삭제
                                    .setLights(context.getResources().getColor(R.color.colorAccent), 300, 100)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT); //알람 중요도

                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                            notificationManager.notify(0, builder.build()); //알람 생성
                        }
                    }
                }
            });
        }
    }
}
