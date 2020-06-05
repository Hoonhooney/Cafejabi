package com.example.cafejabi.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cafejabi.R;
import com.example.cafejabi.objects.Cafe;


import com.example.cafejabi.objects.Keyword;
import com.example.cafejabi.objects.WorkTime;
import com.example.cafejabi.views.KeywordView;
import com.example.cafejabi.views.WorkTimeSettingView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apmem.tools.layouts.FlowLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;




public class EditCafeInfoActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private final String TAG = "EditCafeInfoActivity";
    private Context mContext = EditCafeInfoActivity.this;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Cafe cafe;

    private String cid, description;

    private int table;

    private TextView textView_latest_updated_time;
    private EditText editText_cafe_name, editText_cafe_description;
    private LinearLayout linearLayout_cafe_wt, linearLayout_cafe_wt_everyday;
    private FlowLayout flowLayout_keywords;
    private Switch switch_alarm_on;
    private RadioButton rb_table_0, rb_table_1, rb_table_2, rb_table_3, rb_table_4,
            rb_gap_15min, rb_gap_30min, rb_gap_60min, rb_gap_120min;
    private CheckBox checkBox_wt_everyday;


    private List<String> keywords = new ArrayList<>();
    private List<KeywordView> kwvs = new ArrayList<>();
    private List<WorkTime> workTimes = new ArrayList<>();
    private WorkTime wt_everyday;

    private int density, gap;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cafe_info);

        init();
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        textView_latest_updated_time = findViewById(R.id.textView_edit_last_updated_time);

        rb_table_0 = findViewById(R.id.rb_edit_table_0);
        rb_table_1 = findViewById(R.id.rb_edit_table_1);
        rb_table_2 = findViewById(R.id.rb_edit_table_2);
        rb_table_3 = findViewById(R.id.rb_edit_table_3);
        rb_table_4 = findViewById(R.id.rb_edit_table_4);

        rb_gap_15min = findViewById(R.id.rb_edit_gap_15min);
        rb_gap_30min = findViewById(R.id.rb_edit_gap_30min);
        rb_gap_60min = findViewById(R.id.rb_edit_gap_60min);
        rb_gap_120min = findViewById(R.id.rb_edit_gap_120min);

        checkBox_wt_everyday = findViewById(R.id.checkbox_cafe_edit_wt_everyday);
        checkBox_wt_everyday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    linearLayout_cafe_wt.setVisibility(View.GONE);
                    linearLayout_cafe_wt_everyday.setVisibility(View.VISIBLE);
                }else{
                    linearLayout_cafe_wt.setVisibility(View.VISIBLE);
                    linearLayout_cafe_wt_everyday.setVisibility(View.GONE);
                }
            }
        });

        editText_cafe_name = findViewById(R.id.editText_edit_cafe_name);
        editText_cafe_description = findViewById(R.id.editText_edit_cafe_description);

        flowLayout_keywords = findViewById(R.id.flowLayout_edit_keywords);
        String[] basicKeywords = getResources().getStringArray(R.array.keywords);
        for (String kw : basicKeywords){
            KeywordView kwv = new KeywordView(mContext, new Keyword(kw));
            kwvs.add(kwv);
            flowLayout_keywords.addView(kwv);
        }
        linearLayout_cafe_wt = findViewById(R.id.linearLayout_cafe_edit_wt);
        linearLayout_cafe_wt_everyday = findViewById(R.id.linearLayout_cafe_edit_wt_everyday);

        switch_alarm_on = findViewById(R.id.switch_alarm_setting);

        //프로그래스바
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("잠시 기다려주세요");

        setCafe();

        ((RadioGroup)findViewById(R.id.radioGroup_edit_table)).setOnCheckedChangeListener(this);
        ((RadioGroup)findViewById(R.id.radioGroup_edit_alarm_gap)).setOnCheckedChangeListener(this);

        findViewById(R.id.button_edit_cafe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCafeInfo();
            }
        });
    }

//    cid를 이용해 기존 카페 정보 불러오기
    private void setCafe() {
        Intent intent = getIntent();
        cid = intent.getStringExtra("cid");

        if (cid != null){
            db.collection("cafes").document(cid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    cafe = documentSnapshot.toObject(Cafe.class);

                    if (cafe != null){
                        if (cafe.getTable_update_time() != null){
                            long latestUpdatedTime = cafe.getTable_update_time().getTime();
                            long currentTime = System.currentTimeMillis();
                            long gapTime = currentTime - latestUpdatedTime;
                            if (gapTime < 1000*60*60)
                                textView_latest_updated_time.setText(gapTime/(1000*60)+"분 전");
                            else if (gapTime < 1000*60*60*24)
                                textView_latest_updated_time.setText(gapTime/(1000*60*60)+"시간 전");
                            else
                                textView_latest_updated_time.setText(new SimpleDateFormat("yyyy년 MM월 dd일 HHmm").format(cafe.getTable_update_time()));
                        }else{
                            textView_latest_updated_time.setText("없음");
                        }

                        editText_cafe_name.setText(cafe.getCafe_name());
                        editText_cafe_description.setText(cafe.getCafe_info());

                        keywords = cafe.getKeywords();
                        if (keywords != null){
                            for (String kw : keywords){
                                for (KeywordView kwv : kwvs){
                                    if (kw.equals(kwv.getKeyword().getName()))
                                        kwv.setButton(true);
                                }
                            }
                        }

                        workTimes = new ArrayList<>();
                        String[] days = getResources().getStringArray(R.array.day);
                        for (String day : days){
                            workTimes.add(new WorkTime(day));
                        }
                        for (int i = 0; i < workTimes.size(); i++) {
                            linearLayout_cafe_wt.addView(new WorkTimeSettingView(mContext, workTimes.get(i)));
                        }

                        wt_everyday = new WorkTime("매일");
                        WorkTimeSettingView wtView = new WorkTimeSettingView(mContext, wt_everyday);
                        wtView.setLayout(true);
                        linearLayout_cafe_wt_everyday.addView(wtView);

                        List<WorkTime> wts = cafe.getWorkTimes();
                        if (wts != null && !wts.isEmpty()){
                            Log.d(TAG, "workTimes : not null and not empty");
                            if (cafe.getWorkTimes().size() == 1){
                                checkBox_wt_everyday.setChecked(true);
                                linearLayout_cafe_wt_everyday.setVisibility(View.VISIBLE);
                                linearLayout_cafe_wt.setVisibility(View.GONE);
                                wt_everyday = wts.get(0);
                                linearLayout_cafe_wt_everyday.removeAllViews();
                                linearLayout_cafe_wt_everyday.addView(new WorkTimeSettingView(mContext, wt_everyday));
                            }else{
                                workTimes = wts;
                                linearLayout_cafe_wt_everyday.setVisibility(View.GONE);
                                linearLayout_cafe_wt.setVisibility(View.VISIBLE);
                                linearLayout_cafe_wt.removeAllViews();
                                for (WorkTime wt : workTimes){
                                    WorkTimeSettingView wtv = new WorkTimeSettingView(mContext, wt);
                                    linearLayout_cafe_wt.addView(wtv);
                                }
                            }
                        }

                        density = cafe.getTable();
                        switch (cafe.getTable()){
                            case 0:
                                rb_table_0.setChecked(true);
                                break;
                            case 1:
                                rb_table_1.setChecked(true);
                                break;
                            case 2:
                                rb_table_2.setChecked(true);
                                break;
                            case 3:
                                rb_table_3.setChecked(true);
                                break;
                            case 4:
                                rb_table_4.setChecked(true);
                                break;
                        }

                        gap = cafe.getAlarm_gap();
                        if (cafe.isAllowAlarm()){
                            switch (cafe.getAlarm_gap()){
                                case 15:
                                    rb_gap_15min.setChecked(true);
                                    break;
                                case 30:
                                    rb_gap_30min.setChecked(true);
                                    break;
                                case 60:
                                    rb_gap_60min.setChecked(true);
                                    break;
                                case 120:
                                    rb_gap_120min.setChecked(true);
                                    break;
                            }
                        }
                    }
                }
            });
        }
    }


    private void editCafeInfo() {//변경한 정보를 서버에 저장하는 과정
        progressDialog.show();

        Map<String, Object> updatedCafeMap = new HashMap<>();

        if (!editText_cafe_name.getText().toString().equals(cafe.getCafe_name()))
            updatedCafeMap.put("cafe_name", editText_cafe_name.getText().toString());

        if (!editText_cafe_description.getText().toString().equals(cafe.getCafe_info()))
            updatedCafeMap.put("cafe_info", editText_cafe_description.getText().toString());

        List<String> newKeywords = new ArrayList<>();
        for (KeywordView kwv : kwvs){
            if (kwv.getKeyword().isChosen())
                newKeywords.add(kwv.getKeyword().getName());
        }
        updatedCafeMap.put("keywords", newKeywords);

        if (density != cafe.getTable()){
            updatedCafeMap.put("table", density);
            updatedCafeMap.put("table_update_time", new Date());
        }

        if (checkBox_wt_everyday.isChecked()){{
            List<WorkTime> wtList = new ArrayList<>();
            wtList.add(wt_everyday);
            updatedCafeMap.put("workTimes", wtList);
        }
        }else
            updatedCafeMap.put("workTimes", workTimes);

        if (gap != cafe.getAlarm_gap())
            updatedCafeMap.put("alarm_gap", gap);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("카페 정보 업데이트");

        db.collection("cafes").document(cid).update(updatedCafeMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "update cafeInfo : success");
                progressDialog.dismiss();
                builder.setMessage("카페 정보 업데이트 성공!").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.create().show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "update cafe info : failure", e);
                progressDialog.dismiss();
                builder.setMessage("카페 정보 업데이트 실패").setPositiveButton("확인", null);
                builder.create().show();
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch(group.getId()){
            case R.id.radioGroup_edit_table:
                switch(checkedId){
                    case R.id.rb_edit_table_0:
                        density = 0;
                        break;
                    case R.id.rb_edit_table_1:
                        density = 1;
                        break;
                    case R.id.rb_edit_table_2:
                        density = 2;
                        break;
                    case R.id.rb_edit_table_3:
                        density = 3;
                        break;
                    case R.id.rb_edit_table_4:
                        density = 4;
                        break;
                }
                break;

            case R.id.radioGroup_set_alarm_gap:
                switch (checkedId){
                    case R.id.rb_edit_gap_15min:
                        gap = 15;
                        break;
                    case R.id.rb_edit_gap_30min:
                        gap = 30;
                        break;
                    case R.id.rb_edit_gap_60min:
                        gap = 60;
                        break;
                    case R.id.rb_edit_gap_120min:
                        gap = 120;
                        break;
                }
                break;
        }
    }
}