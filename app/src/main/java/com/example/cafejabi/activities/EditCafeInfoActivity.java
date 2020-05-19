//package com.example.cafejabi.activities;
//
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.Toast;
//
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.cafejabi.R;
//import com.example.cafejabi.objects.Cafe;
//
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;


//
//
//public class EditCafeInfoActivity extends AppCompatActivity implements View.OnClickListener {
//
//    private final String TAG = "EditCafeInfoActivity";
//    private Context mContext = EditCafeInfoActivity.this;
//
//    private FirebaseAuth mAuth;
//    private FirebaseFirestore db;
//
//    private Cafe cafe;
//
//
//    private EditText editText_cafe_description;
//    private RadioButton rb_table_1, rb_table_2, rb_table_3, rb_table_4, rb_table_5,
//            rb_gap_15min, rb_gap_30min, rb_gap_60min, rb_gap_120min;
//
//
//
//    private List<RadioButton> rbList;
//
//
//    private CheckBox checkbox_full_time, checkbox_alarm_same_work_time;
//
//
//    private List<CheckBox> cbList;
//
//    private ProgressDialog progressDialog;
//
//    private SharedPreferences loginPreferences;
//
//    private String nickname, new_nickname, job;
//    private List<String> style;
//    private boolean nicknameCheck;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_user_info);
//
//        init();
//    }
//
//    private void init(){
//
//        mAuth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//
//    }

//
//    @Override
//    public void onClick(View v) {
//        switch(v.getId()){
//
//            case R.id.button_edit_check_nickname:
//               // checkNickname();
//                break;
//
//            case R.id.button_edit_finish:
//               // editUserInfo();
//                break;
//
//            case R.id.checkBox_edit_study:
//                if(((CheckBox)v).isChecked() && !style.contains("공부"))
//                    style.add("공부");
//                else if(!((CheckBox)v).isChecked())
//                    style.remove("공부");
//                Log.e(TAG, style.toString());
//                break;
//
//            case R.id.checkBox_edit_rest:
//                if(((CheckBox)v).isChecked() && !style.contains("휴식"))
//                    style.add("휴식");
//                else if(!((CheckBox)v).isChecked())
//                    style.remove("휴식");
//                Log.e(TAG, style.toString());
//                break;
//
//            case R.id.checkbox_edit_mood:
//                if(((CheckBox)v).isChecked() && !style.contains("사진"))
//                    style.add("사진");
//                else if(!((CheckBox)v).isChecked())
//                    style.remove("사진");
//                Log.e(TAG, style.toString());
//                break;
//        }
//    }

//
//    private void editUserInfo(){
//        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setTitle("사용자 정보 수정");
//
//        if(editText_nickname.getText().toString().equals(nickname) || nicknameCheck) {
//            progressDialog.show();
//            db.collection("users").document(user.getUid())
//                    .update("nickname", new_nickname == null ? nickname : new_nickname,
//                            "style", style, "job", job).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    Log.d(TAG, "edit userInfo : success");
//
//                    if(new_nickname != null){
//                        db.collection("comments").whereEqualTo("user_nickname", nickname)
//                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if(task.isSuccessful()){
//                                    for(QueryDocumentSnapshot document : task.getResult()){
//                                        Comment comment = document.toObject(Comment.class);
//                                        db.collection("comments").document(comment.getId()).update("user_nickname", new_nickname)
//                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void aVoid) {
//                                                        Log.d(TAG, "edit comments : success");
//                                                    }
//                                                }).addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                Log.e(TAG, "edit comments : failure", e);
//                                            }
//                                        });
//                                    }
//                                }
//                            }
//                        });
//                    }
//
//                    progressDialog.dismiss();
//                    Toast.makeText(EditUserInfoActivity.this, "사용자 정보 수정 완료", Toast.LENGTH_SHORT).show();
//                    goActivity(MainActivity.class);
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.e(TAG, "edit userInfo : failure", e);
//                    builder.setMessage("사용자 정보 수정을 실패하였습니다.").setPositiveButton("OK", null);
//                    builder.create().show();
//                }
//            });
//        }else{
//            builder.setMessage("닉네임 중복확인이 필요합니다.").setPositiveButton("OK", null);
//            builder.create().show();
//        }
//    }
//
//    private void goActivity(Class c){
//        Intent intent = new Intent(mContext, c);
//        startActivity(intent);
//        finish();
//    }
//
//    @Override
//    public void onBackPressed(){
//        goActivity(MainActivity.class);
//    }
//}
