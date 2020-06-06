package com.example.cafejabi.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cafejabi.R;
import com.example.cafejabi.objects.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JoinActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "JoinActivity";

    private LinearLayout linearLayout_join_email;
    private EditText editText_email, editText_password, editText_password2, editText_nickname;
    private Spinner spinner_age;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ProgressDialog progressDialog;

    private SharedPreferences loginPreferences;

    private String method, nickname, job;
    private int age;
    private List<String> style;
    private boolean nicknameCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        init();
    }

    private void init(){

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginPreferences = getSharedPreferences("login", MODE_PRIVATE);
        method = loginPreferences.getString("method", "");

        linearLayout_join_email = findViewById(R.id.linearLayout_join_email);

        editText_email = findViewById(R.id.editText_join_email);
        editText_password = findViewById(R.id.editText_join_password);
        editText_password2 = findViewById(R.id.editText_join_password2);

        if (mAuth.getCurrentUser() == null){
                mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Log.d(TAG, "Sign in Anonymously : Success");
                    }else
                        Log.e(TAG, "sign in Anonymously : Failure", task.getException());
                }
            });
        }

        //로그인 방법에 따른 layout 제시
        if(method.equals("Email")){
            linearLayout_join_email.setVisibility(View.VISIBLE);
        }else{
            linearLayout_join_email.setVisibility(View.GONE);

            editText_email.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
        }

        editText_nickname = findViewById(R.id.editText_join_nickname);
        nicknameCheck = false;

        //나이(spinner)
        spinner_age = findViewById(R.id.spinner_join_age);
        setSpinner(spinner_age);
        spinner_age.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                age = (int)spinner_age.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //직업(RadioButton)
        ((RadioGroup)findViewById(R.id.rg_join_jobs)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_join_student:
                        job = "학생";
                        break;
                    case R.id.rb_join_worker:
                        job = "직장인";
                        break;
                    case R.id.rb_join_none:
                        job = "무직";
                        break;
                }
                Log.e(TAG, job);
            }
        });

        //목적
        style = new ArrayList<>();
        findViewById(R.id.checkBox_join_study).setOnClickListener(this);
        findViewById(R.id.checkBox_join_talk).setOnClickListener(this);
        findViewById(R.id.checkbox_join_mood).setOnClickListener(this);

        //프로그래스바
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("잠시 기다려주세요");

        findViewById(R.id.button_join_check_nickname).setOnClickListener(this);
        findViewById(R.id.button_join_finish).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.button_join_finish:
                join();
                break;

            case R.id.button_join_check_nickname:
                checkNickname();;
                break;

            case R.id.checkBox_join_study:
                if(((CheckBox)v).isChecked() && !style.contains("공부"))
                    style.add("공부");
                else if(!((CheckBox)v).isChecked())
                    style.remove("공부");
                Log.e(TAG, style.toString());
                break;

            case R.id.checkBox_join_talk:
                if(((CheckBox)v).isChecked() && !style.contains("대화"))
                    style.add("대화");
                else if(!((CheckBox)v).isChecked())
                    style.remove("대화");
                Log.e(TAG, style.toString());
                break;

            case R.id.checkbox_join_mood:
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
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

    private void join(){
        if (mAuth.getCurrentUser().isAnonymous())
            mAuth.getCurrentUser().delete();

        final String email = editText_email.getText().toString();
        String password = editText_password.getText().toString();
        String password2 = editText_password2.getText().toString();

        if(email.isEmpty())
            Toast.makeText(this, "이메일 주소를 입력해주세요!",
                    Toast.LENGTH_SHORT).show();
        else if(!nicknameCheck)
            Toast.makeText(this, "닉네임을 중복 확인해주세요!",
                    Toast.LENGTH_SHORT).show();
        else if(method.equals("Email") && !password.equals(password2))
            Toast.makeText(this, "패스워드를 확인해주세요!",
                    Toast.LENGTH_SHORT).show();
        else if(method.equals("Email") && password.length() < 6)
            Toast.makeText(this, "패스워드는 6자 이상이어야합니다!",
                    Toast.LENGTH_SHORT).show();
        else if(age == 0)
            Toast.makeText(this, "출생년도를 입력해주세요!",
                    Toast.LENGTH_SHORT).show();
        else if(job == null)
            Toast.makeText(this, "직업을 입력해주세요!",
                    Toast.LENGTH_SHORT).show();
        else if(style.isEmpty())
            Toast.makeText(this, "카페 이용 목적을 입력해주세요!",
                    Toast.LENGTH_SHORT).show();
        else {
            progressDialog.show();

            if(method.equals("Email")){
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String uid = user.getUid();
                                    UserInfo userInfo = new UserInfo(uid, email, nickname, age, job, style);

                                    addUserInDb(userInfo, uid);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(JoinActivity.this, "Authentication failed."+task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            else if(mAuth != null){
                FirebaseUser user = mAuth.getCurrentUser();
                String uid = user.getUid();
                UserInfo userInfo = new UserInfo(uid, email, nickname, age, job, style);
                addUserInDb(userInfo, uid);
            }
        }
    }

    private void setSpinner(Spinner spinner){
        List<Integer> ageList = new ArrayList<>();
        int year = 2016;
        for (int i = 0; i < 100; i++) {
            ageList.add(year--);
        }
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, ageList);

        spinner.setAdapter(spinnerAdapter);
    }

    private void addUserInDb(UserInfo userInfo, String uid){
        db.collection("users").document(uid)
                .set(userInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "adding user information to db : success");

                        progressDialog.dismiss();

                        AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                        builder.setTitle("회원 가입").setMessage("회원 가입 성공!");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(method.equals("Email")){
                                    mAuth.signOut();
                                    goActivity(LoginActivity.class);
                                }else
                                    goActivity(MainActivity.class);
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "adding user information to db : fail", e);
                        progressDialog.dismiss();

                        AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                        builder.setTitle("회원 가입").setMessage("오류 : 사용자 정보를 추가하지 못했습니다.");
                        builder.setPositiveButton("OK", null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
    }

    private void goActivity(Class c){
        Intent intent = new Intent(JoinActivity.this, c);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

//    @Override
//    public void onBackPressed(){
//        goActivity(LoginActivity.class);
//    }
}
