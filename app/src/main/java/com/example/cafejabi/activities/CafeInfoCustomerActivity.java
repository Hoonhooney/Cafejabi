package com.example.cafejabi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.cafejabi.R;
import com.example.cafejabi.objects.Cafe;
import com.example.cafejabi.objects.Comment;
import com.example.cafejabi.objects.Keyword;
import com.example.cafejabi.objects.UserInfo;
import com.example.cafejabi.views.KeywordView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.apmem.tools.layouts.FlowLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CafeInfoCustomerActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "CafeInfoCusActivity";
    private String cafeId;

    private UserInfo user;
    private Cafe cafe;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private TextView textview_cafeName, textView_cafeAddress, textView_cafeStatus, textView_cafeInfo,
    textView_workingTime, textView_grade;

    private RatingBar ratingBar_comment;
    private float grade = 1.0f;
    private EditText editText_comment;

    private FlowLayout flowLayout_keywords;
    private LinearLayout linearLayout_addComment;

    private ListView listView_comments;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_info_customer);

        init();
    }

    private void init(){

        textview_cafeName = findViewById(R.id.textView_info_customer_cafename);
        textView_cafeAddress = findViewById(R.id.textView_info_customer_cafeaddress);
        textView_cafeStatus = findViewById(R.id.textView_info_customer_cafestatus);
        textView_cafeInfo = findViewById(R.id.textView_info_customer_cafeinfo);
        textView_workingTime = findViewById(R.id.textView_info_customer_workingtime);
        textView_grade = findViewById(R.id.textView_info_customer_grade);

        editText_comment = findViewById(R.id.editText_info_customer_comment);

        flowLayout_keywords = findViewById(R.id.flowLayout_info_customer_keywords);

        listView_comments = findViewById(R.id.listView_info_customer_comments);

        ratingBar_comment = findViewById(R.id.ratingBar_set_grade);
        linearLayout_addComment = findViewById(R.id.linearLayout_info_customer_addcomment);

        //프로그래스바
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("잠시 기다려주세요");

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //카페 정보 가져오기
        getCafeInfo();

        findViewById(R.id.button_info_customer_showcomment).setOnClickListener(this);
        findViewById(R.id.button_info_customer_addcomment).setOnClickListener(this);

        ratingBar_comment.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                grade = v;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){

            case R.id.button_info_customer_showcomment:
                if(linearLayout_addComment.getVisibility() == View.GONE)
                    linearLayout_addComment.setVisibility(View.VISIBLE);
                else
                    linearLayout_addComment.setVisibility(View.GONE);
                break;

            case R.id.button_info_customer_addcomment:
                addComment();
                break;
        }
    }

    private void getCafeInfo(){
        cafeId = getIntent().getStringExtra("CafeId");

        progressDialog.show();
        db.collection("cafes").document(cafeId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                cafe = documentSnapshot.toObject(Cafe.class);

                if(cafe != null){
                    textview_cafeName.setText(cafe.getCafe_name());
                    textView_cafeAddress.setText(cafe.getAddress());
                    textView_cafeStatus.setText(cafe.getTable()+"");
                    textView_cafeInfo.setText(cafe.getCafe_info());
                    if(cafe.isIs24Working())
                        textView_workingTime.setText("24시간 영업");
                    else
                        textView_workingTime.setText(cafe.getOpen_time()+"시 ~ "+cafe.getClose_time()+"시");
                    textView_grade.setText(cafe.getGrade_cafe()+"");

                    if(cafe.getKeywords() != null){
                        for(String str : cafe.getKeywords()){
                            Keyword keyword = new Keyword(str);
                            keyword.setChosen(true);
                            KeywordView keywordView = new KeywordView(CafeInfoCustomerActivity.this, keyword);
                            keywordView.setClick(false);
                            flowLayout_keywords.addView(keywordView);
                        }
                    }

                    progressDialog.dismiss();
                }
            }
        });
    }

    private void addComment(){
        Comment comment = new Comment(cafeId, mAuth.getUid(), editText_comment.getText().toString(),
                grade, new Date(System.currentTimeMillis()));

        List<Comment> commentList = new ArrayList<>();
        if(cafe.getComment_list() != null)
            commentList = cafe.getComment_list();
        commentList.add(comment);

        db.collection("cafes").document(cafeId).update("comment_list", commentList)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Succeeded to add comment");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to add comment", e);
            }
        });
    }
}