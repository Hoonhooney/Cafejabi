package com.example.cafejabi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.cafejabi.R;

import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ResetPasswordActivity";

    private EditText editText_email;

    private String email = "";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        editText_email = findViewById(R.id.editText_resetp_email);
        editText_email.setMaxLines(1);

        findViewById(R.id.button_resetp_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = editText_email.getText().toString();
                send();
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

//    비밀번호 변경 요청 계정 이메일로 보내기
    private void send(){
        if(email.isEmpty()){
            Toast.makeText(ResetPasswordActivity.this, "이메일을 입력해주세요!",
                    Toast.LENGTH_SHORT).show();
        }else{
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                builder.setTitle("패스워드 변경");
                                builder.setMessage("해당 이메일로 메일이 발송되었습니다.\n" +
                                        "이메일에 접속해서 패스워드를 수정해주세요!");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }else{
                                Toast.makeText(ResetPasswordActivity.this, "이메일을 정확히 입력해주세요!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
