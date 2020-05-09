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
import com.example.cafejabi.objects.Comment;
import com.example.cafejabi.objects.UserInfo;
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

public class EditUserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "EditUserInfoActivity";
    private Context mContext = EditUserInfoActivity.this;

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

    private String nickname, new_nickname, job;
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

        nicknameCheck = false;

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

        //프로그래스바
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("잠시 기다려주세요");
        progressDialog.show();

        //UserInfo 가져오기
        db.collection("users").document(Objects.requireNonNull(mAuth.getUid()))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, "getting UserInfo : success");
                user = documentSnapshot.toObject(UserInfo.class);

                progressDialog.dismiss();

                if(user == null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("오류").setMessage("사용자 정보를 불러오는데 실패했습니다,")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    goActivity(MainActivity.class);
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
                    cb_study.setOnClickListener(EditUserInfoActivity.this);
                    cb_rest.setOnClickListener(EditUserInfoActivity.this);
                    cb_mood.setOnClickListener(EditUserInfoActivity.this);
                }
            }
        });

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
            if(rb.getText().toString().equals(user.getJob())){
                rb.setChecked(true);
                job = user.getJob();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.button_edit_check_nickname:
                checkNickname();
                break;

            case R.id.button_edit_finish:
                editUserInfo();
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("닉네임 중복 확인");

        if(editText_nickname.getText().toString().equals(nickname)){
            builder.setMessage("기존 닉네임과 동일합니다.\n그대로 사용하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    nicknameCheck = true;
                    editText_nickname.setFocusable(false);
                }
            }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    editText_nickname.setFocusable(true);
                }
            });

            builder.create().show();
        }else{
            new_nickname = editText_nickname.getText().toString();
            progressDialog.show();

            db.collection("users").whereEqualTo("nickname", new_nickname)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            Log.d(TAG, "checking nickname success");
                            progressDialog.dismiss();

                            if(!new_nickname.isEmpty() && queryDocumentSnapshots.getDocuments().isEmpty()){
                                builder.setMessage("사용 가능한 닉네임입니다!");
                                nicknameCheck = true;
                            }else if(new_nickname.isEmpty()){
                                builder.setMessage("닉네임을 입력해주세요!");
                                nicknameCheck = false;
                            }else{
                                builder.setMessage("이미 다른 사용자가 사용 중인 닉네임입니다.");
                                nicknameCheck = false;
                            }

                            builder.setPositiveButton("OK", null);
                            builder.create().show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "checking nickname failed", e);
                            progressDialog.dismiss();
                            builder.setMessage("데이터 검색 오류");
                            builder.setPositiveButton("OK", null);
                            builder.create().show();
                        }
                    });
        }
    }

    private void editUserInfo(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("사용자 정보 수정");

        if(editText_nickname.getText().toString().equals(nickname) || nicknameCheck) {
            progressDialog.show();
            db.collection("users").document(user.getUid())
                    .update("nickname", new_nickname == null ? nickname : new_nickname,
                            "style", style, "job", job).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "edit userInfo : success");

                    if(new_nickname != null){
                        db.collection("comments").whereEqualTo("user_nickname", nickname)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for(QueryDocumentSnapshot document : task.getResult()){
                                        Comment comment = document.toObject(Comment.class);
                                        db.collection("comments").document(comment.getId()).update("user_nickname", new_nickname)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "edit comments : success");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "edit comments : failure", e);
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }

                    progressDialog.dismiss();
                    Toast.makeText(EditUserInfoActivity.this, "사용자 정보 수정 완료", Toast.LENGTH_SHORT).show();
                    goActivity(MainActivity.class);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "edit userInfo : failure", e);
                    builder.setMessage("사용자 정보 수정을 실패하였습니다.").setPositiveButton("OK", null);
                    builder.create().show();
                }
            });
        }else{
            builder.setMessage("닉네임 중복확인이 필요합니다.").setPositiveButton("OK", null);
            builder.create().show();
    }
    }

    private void goActivity(Class c){
        Intent intent = new Intent(mContext, c);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){
        goActivity(MainActivity.class);
    }
}
