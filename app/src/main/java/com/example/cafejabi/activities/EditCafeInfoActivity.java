package com.example.cafejabi.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cafejabi.R;
import com.example.cafejabi.objects.Cafe;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;




public class EditCafeInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "EditCafeInfoActivity";
    private Context mContext = EditCafeInfoActivity.this;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Cafe cafe;


    private EditText editText_cafe_description;
    private RadioButton rb_table_1, rb_table_2, rb_table_3, rb_table_4, rb_table_5,
            rb_gap_15min, rb_gap_30min, rb_gap_60min, rb_gap_120min;



    private List<RadioButton> rbList_alarm, rbList_table;


    private CheckBox checkbox_full_time, checkbox_alarm_same_work_time;


    private List<CheckBox> cbList;

    private ProgressDialog progressDialog;

    private SharedPreferences loginPreferences;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cafe_info);

        init();
    }

    private void init(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editText_cafe_description = findViewById(R.id.editText_cafe_description);//카페소개

        rb_table_1 = findViewById(R.id.rb_table_1);
        rb_table_2 = findViewById(R.id.rb_table_2);
        rb_table_3 = findViewById(R.id.rb_table_3);
        rb_table_4 = findViewById(R.id.rb_table_4);
        rb_table_5 = findViewById(R.id.rb_table_5);

        rb_gap_15min = findViewById(R.id.rb_gap_15min);
        rb_gap_30min = findViewById(R.id.rb_gap_30min);
        rb_gap_60min = findViewById(R.id.rb_gap_60min);
        rb_gap_120min = findViewById(R.id.rb_gap_120min);

        rbList_table = new ArrayList<>();//좌석 현황
        rbList_table.add(rb_table_1);
        rbList_table.add(rb_table_2);
        rbList_table.add(rb_table_3);
        rbList_table.add(rb_table_4);
        rbList_table.add(rb_table_5);

        rbList_alarm = new ArrayList<>();//푸쉬알림
        rbList_alarm.add(rb_gap_15min);
        rbList_alarm.add(rb_gap_30min);
        rbList_alarm.add(rb_gap_60min);
        rbList_alarm.add(rb_gap_120min);



    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.button_edit_check_nickname:
               // checkNickname();
                break;

            case R.id.button_edit_finish:
               // editUserInfo();
                break;


            case R.id.checkbox_24working:
                if(((CheckBox)v).isChecked()){//체크를 한 경우 open_time, close_time 어떻게 설정?(24시간 영업인 경우)

                }

                //체크 한 경우 24시간 영업
                //체크 안한 경우 progressbar로 설정

                break;


            case R.id.checkBox_alarm_same_working_time:

                //체크 한 경우 푸쉬알림시간과 영업시간 동일
                //체크 안한 경우 알람 시간 24시간 계속                        -->>>>> 굳이 넣어야 하는지 확인(무조건 영업시간과 동일로 하는게 깔끔할것같은 느낌)

                break;
        }
    }


    private void editCafeInfo(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("카페 정보 수정");



//        db.collection("cafe").document(cafe.getUid()).update("cafe_info", editText_cafe_description, "table", rbList_table, "open_time", open_time, "close_time", close_time
//        "allowAlarm", rbList_alarm, ).addOnSuccessListener(new OnSuccessListener<Void>() {



    }









    private void goActivity(Class c){
        Intent intent = new Intent(mContext, c);
        startActivity(intent);
        finish();
    }





    @Override
    public void onBackPressed(){ goActivity(MainActivity.class);
    }
}