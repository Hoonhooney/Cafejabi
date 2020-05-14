package com.example.cafejabi.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cafejabi.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "LoginActivity";

    private CallbackManager mCallbackManager;

    private EditText editText_email, editText_password;

    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;

    private String method = "";
    private SharedPreferences loginPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init(){

        editText_email = findViewById(R.id.editText_email);
        editText_password = findViewById(R.id.editText_password);

        findViewById(R.id.button_login).setOnClickListener(this);
        findViewById(R.id.button_go_join).setOnClickListener(this);
        findViewById(R.id.button_no_login).setOnClickListener(this);
        findViewById(R.id.button_go_reset_password).setOnClickListener(this);

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.button_login_facebook);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });

        mAuth = FirebaseAuth.getInstance();
        loginPreferences = getSharedPreferences("login", MODE_PRIVATE);

        //프로그래스바
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("잠시 기다려주세요");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_login:
                loginEmail();
                break;

            case R.id.button_go_join:
                method = "Email";
                goActivity(JoinActivity.class);
                break;

            case R.id.button_no_login:
                goActivity(MainActivity.class);
                break;

            case R.id.button_go_reset_password:
                startActivity(new Intent(this, ResetPasswordActivity.class));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

//    로그인 과정(이메일)
    private void loginEmail(){
        Log.e(TAG, "loginEmail()");

        String email = editText_email.getText().toString();
        String password = editText_password.getText().toString();

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(LoginActivity.this, "이메일, 패스워드를 입력해주세요!",
                    Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                progressDialog.dismiss();

                                method = "Email";
                                goActivity(MainActivity.class);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "There is no user record corresponding to this identifier",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

//   Facebook login
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            method = "Facebook";
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users").whereEqualTo("uid", user.getUid())
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if(queryDocumentSnapshots.getDocuments().isEmpty()){
                                        goActivity(JoinActivity.class);
                                    }else
                                        goActivity(MainActivity.class);
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void goActivity(Class c){
        Intent intent = new Intent(LoginActivity.this, c);
        SharedPreferences.Editor editor = loginPreferences.edit();
        editor.putString("method", method);
        editor.apply();
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
