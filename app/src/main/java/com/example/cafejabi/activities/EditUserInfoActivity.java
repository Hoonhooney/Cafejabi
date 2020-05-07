package com.example.cafejabi.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cafejabi.R;
import com.example.cafejabi.objects.UserInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditUserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "EditUserInfoActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private UserInfo user;

    private EditText editText_nickname;
    private RadioButton rb_job_student, rb_job_worker, rb_job_none;
    private List<RadioButton> rbList;
    private CheckBox cb_study, cb_rest, cb_mood;
    private List<CheckBox> cbList;

    private ProgressDialog progressDialog;

    private SharedPreferences loginPreferences;

    private String nickname, job;
    private List<String> style;
    private boolean nicknameCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);

        init();
    }

    private void init(){

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //UserInfo 가져오기
        db.collection("user").document(Objects.requireNonNull(mAuth.getUid()))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, "getting UserInfo : success");
                user = documentSnapshot.toObject(UserInfo.class);
            }
        });

        editText_nickname = findViewById(R.id.editText_edit_nickname);

        rb_job_student = findViewById(R.id.rb_edit_student);
        rb_job_worker = findViewById(R.id.rb_edit_worker);
        rb_job_none = findViewById(R.id.rb_edit_none);
        rbList = new ArrayList<>();
        rbList.add(rb_job_student);
        rbList.add(rb_job_worker);
        rbList.add(rb_job_none);

        cb_study = findViewById(R.id.checkBox_edit_study);
        cb_rest = findViewById(R.id.checkBox_edit_rest);
        cb_mood = findViewById(R.id.checkbox_edit_mood);
        cbList = new ArrayList<>();
        cbList.add(cb_study);
        cbList.add(cb_rest);
        cbList.add(cb_mood);
        for(CheckBox cb : cbList)
            cb.setChecked(false);

        style = new ArrayList<>();

        if(user == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("오류").setMessage("사용자 정보를 불러오는데 실패했습니다,")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(EditUserInfoActivity.this, MainActivity.class));
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            //기존 사용자 정보 세팅
            setUserInfo();

            //직업(RadioButton)
            ((RadioGroup)findViewById(R.id.rg_edit_jobs)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId){
                        case R.id.rb_edit_student:
                            job = "학생";
                            break;
                        case R.id.rb_edit_worker:
                            job = "직장인";
                            break;
                        case R.id.rb_edit_none:
                            job = "무직";
                            break;
                    }
                    Log.e(TAG, job);
                }
            });

            //목적
            cb_study.setOnClickListener(this);
            cb_rest.setOnClickListener(this);
            cb_mood.setOnClickListener(this);
        }

        //프로그래스바
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("잠시 기다려주세요");

        findViewById(R.id.button_edit_check_nickname).setOnClickListener(this);
        findViewById(R.id.button_edit_finish).setOnClickListener(this);
    }

    private void setUserInfo(){
        nickname = user.getNickname();
        editText_nickname.setText(nickname);

        style = user.getStyle();
        for(String str : style){
            for(CheckBox cb : cbList){
                if(str.equals(cb.getText().toString()))
                    cb.setChecked(true);
            }
        }

        for(RadioButton rb : rbList){
            if(rb.getText().toString().equals(user.getJob()))
                rb.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.button_edit_check_nickname:
                checkNickname();;
                break;

            case R.id.checkBox_edit_study:
                if(((CheckBox)v).isChecked() && !style.contains("공부"))
                    style.add("공부");
                else if(!((CheckBox)v).isChecked())
                    style.remove("공부");
                Log.e(TAG, style.toString());
                break;

            case R.id.checkBox_edit_rest:
                if(((CheckBox)v).isChecked() && !style.contains("휴식"))
                    style.add("휴식");
                else if(!((CheckBox)v).isChecked())
                    style.remove("휴식");
                Log.e(TAG, style.toString());
                break;

            case R.id.checkbox_edit_mood:
                if(((CheckBox)v).isChecked() && !style.contains("사진"))
                    style.add("사진");
                else if(!((CheckBox)v).isChecked())
                    style.remove("사진");
                Log.e(TAG, style.toString());
                break;
        }
    }

    private void checkNickname(){
        nickname = editText_nickname.getText().toString();
        progressDialog.show();

        db.collection("users").whereEqualTo("nickname", nickname)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d(TAG, "checking nickname success");
                        progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditUserInfoActivity.this);
                        builder.setTitle("닉네임 중복 확인");

                        if(!nickname.isEmpty() && queryDocumentSnapshots.getDocuments().isEmpty()){
                            builder.setMessage("사용 가능한 닉네임입니다!");
                            nicknameCheck = true;
                        }else if(nickname.isEmpty()){
                            builder.setMessage("닉네임을 입력해주세요!");
                            nicknameCheck = false;
                        }else{
                            builder.setMessage("이미 다른 사용자가 사용 중인 닉네임입니다.");
                            nicknameCheck = false;
                        }

                        builder.setPositiveButton("OK", null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "checking nickname failed", e);
                        progressDialog.dismiss();
                    }
                });
    }

    private void goActivity(Class c){
        Intent intent = new Intent(EditUserInfoActivity.this, c);
        startActivity(intent);
        finish();
    }

//    @Override
//    public void onBackPressed(){
//        goActivity(LoginActivity.class);
//    }
}
