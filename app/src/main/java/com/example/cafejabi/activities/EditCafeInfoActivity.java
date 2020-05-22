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

    private String description;

    private int table;

    private EditText editText_cafe_description;
    private RadioButton rb_table_1, rb_table_2, rb_table_3, rb_table_4, rb_table_5,
            rb_gap_15min, rb_gap_30min, rb_gap_60min, rb_gap_120min;


    private List<String> keyword;

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

    private void init() {
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

        keyword = new ArrayList<>();

        //Cafe 가져오고 싶은데 계속 error 뜸
        db.collection("cafes").document(Objects.requireNonNull(mAuth.getUid()))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, "getting Cafe : success");
                cafe = documentSnapshot.toObject(Cafe.class);

                progressDialog.dismiss();

                if (cafe == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("오류").setMessage("카페 정보를 불러오는데 실패했습니다,")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    goActivity(MainActivity.class);
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    //기존 카페 정보 세팅
                    setCafe();

                    //좌석(RadioButton)
                    ((RadioGroup) findViewById(R.id.radioGroup_set_table)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            switch (checkedId) {
                                case R.id.rb_table_1:
                                    table = 0;
                                    break;
                                case R.id.rb_table_2:
                                    table = 1;
                                    break;
                                case R.id.rb_table_3:
                                    table = 2;
                                    break;
                                case R.id.rb_table_4:
                                    table = 3;
                                    break;
                                case R.id.rb_table_5:
                                    table = 4;
                                    break;
                            }
                            Log.e(TAG, table+"");//왜 오류인지 모름
                        }
                    });

                    //목적
                    //cb_study.setOnClickListener(EditCafeInfoActivityTestMode.this);
                    //이런 식으로 모두 써야함


                }
            }
        });

        findViewById(R.id.button_edit_finish).setOnClickListener(this);

    }

    private void setCafe() {
        table = cafe.getTable();


        description = cafe.getCafe_info();
        editText_cafe_description.setText(description);

        keyword = cafe.getKeywords();
        for (String str : keyword) {
            for (CheckBox cb : cbList) {
                if (str.equals(cb.getText().toString()))
                    cb.setChecked(true);
            }
        }

        for (RadioButton rb : rbList_table) {
            if (rb.getText().toString().equals(cafe.getTable())) {
                rb.setChecked(true);
                table = cafe.getTable();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_edit_finish://확인 버튼
                editCafeInfo();
                break;

            case R.id.checkbox_24working:
                if (((CheckBox) v).isChecked()) {//체크를 한 경우 open_time, close_time 어떻게 설정?(24시간 영업인 경우)
                    cafe.setOpen_time(0);
                    cafe.setClose_time(0);
                } else {//24시간 영업이 아닌 경우
                    //progressbar로 설정
//                            cafe.setOpen_time(progressDialog());
//                            cafe.setOpen_time(progressDialog());
                }
                break;


            case R.id.checkBox_alarm_same_working_time:
                if (((CheckBox) v).isChecked()) {//체크를 한 경우 푸쉬 알림시간과 영업 시간이 동일

                } else {
                    //체크 안한 경우 알람 시간 24시간 계속                        -->>>>> 굳이 넣어야 하는지 확인(무조건 영업시간과 동일로 하는게 깔끔할것같은 느낌)
                }
                break;


            //푸쉬 알람 시간 간격 설정 rb_List_alarm 이거를 Cafe.update_time_alarm 에 저장 (Long)

        }
    }


    private void editCafeInfo() {//변경한 정보를 서버에 저장하는 과정
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("카페 정보 수정");
        {//닉네임 중복 확인 함수라 editcafe에는 필요 없음

            progressDialog.show();

            db.collection("cafes").document(cafe.getUid())
                    .update("description", description,
                            "rblist_alarm", rbList_alarm, "table", table, "cbList", cbList).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "edit cafeInfo : success");

                    if (description != null) {
                        db.collection("comments").whereEqualTo("cafe_description", description)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            }
                        });
                    }

                    progressDialog.dismiss();
                    Toast.makeText(EditCafeInfoActivity.this, "카페 정보 수정 완료", Toast.LENGTH_SHORT).show();
                    goActivity(MainActivity.class);//밑에서 정의한 클래스가 왜 에러인지 모르겠으나 해결하면 여기도 해결
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "edit cafeInfo : failure", e);
                    builder.setMessage("카페 정보 수정을 실패하였습니다.").setPositiveButton("OK", null);
                    builder.create().show();
                }
            });

        }

    }

    private void goActivity (Class c){
        Intent intent = new Intent(mContext, c);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed () {
        goActivity(MainActivity.class);
    }
}