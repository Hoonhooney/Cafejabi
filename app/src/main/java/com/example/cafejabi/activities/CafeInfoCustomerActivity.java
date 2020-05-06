package com.example.cafejabi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.cafejabi.CommentAdapter;
import com.example.cafejabi.R;
import com.example.cafejabi.objects.Cafe;
import com.example.cafejabi.objects.Comment;
import com.example.cafejabi.objects.Keyword;
import com.example.cafejabi.views.KeywordView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apmem.tools.layouts.FlowLayout;

import java.util.Date;

import static android.view.View.GONE;

public class CafeInfoCustomerActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "CafeInfoCusActivity";
    private String cafeId;

    private Cafe cafe;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private TextView textView_cafeName, textView_cafeAddress, textView_cafeStatus, textView_cafeInfo,
    textView_workingTime, textView_grade, textView_comment_rating;

    private RatingBar ratingBar_cafe, ratingBar_comment;
    private float grade = 1.0f;
    private EditText editText_comment;

    private FlowLayout flowLayout_keywords;
    private LinearLayout linearLayout_addComment;

    private ListView listView_comments;
    private CommentAdapter commentAdapter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_info_customer);

        init();
    }

    private void init(){

        textView_cafeName = findViewById(R.id.textView_info_customer_cafename);
        textView_cafeAddress = findViewById(R.id.textView_info_customer_cafeaddress);
        textView_cafeStatus = findViewById(R.id.textView_info_customer_cafestatus);
        textView_cafeInfo = findViewById(R.id.textView_info_customer_cafeinfo);
        textView_workingTime = findViewById(R.id.textView_info_customer_workingtime);
        textView_grade = findViewById(R.id.textView_info_customer_grade);
        textView_comment_rating = findViewById(R.id.textView_info_customer_comment_rating);

        editText_comment = findViewById(R.id.editText_info_customer_comment);

        flowLayout_keywords = findViewById(R.id.flowLayout_info_customer_keywords);

        listView_comments = findViewById(R.id.listView_info_customer_comments);

        ratingBar_cafe = findViewById(R.id.ratingBar_info_customer_average);
        ratingBar_comment = findViewById(R.id.ratingBar_set_grade);
        linearLayout_addComment = findViewById(R.id.linearLayout_info_customer_addcomment);

        //프로그래스바
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("잠시 기다려주세요");

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //카페 정보 가져오기
        commentAdapter = new CommentAdapter(this);
        listView_comments.setAdapter(commentAdapter);

        getCafeInfo();

        findViewById(R.id.button_info_customer_showcomment).setOnClickListener(this);
        findViewById(R.id.button_info_customer_addcomment).setOnClickListener(this);

        ratingBar_comment.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                grade = v;
                textView_comment_rating.setText(v+"");
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){

            case R.id.button_info_customer_showcomment:
                if(linearLayout_addComment.getVisibility() == GONE)
                    linearLayout_addComment.setVisibility(View.VISIBLE);
                else
                    linearLayout_addComment.setVisibility(GONE);
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
                    textView_cafeName.setText(cafe.getCafe_name());
                    textView_cafeAddress.setText(cafe.getAddress());
                    textView_cafeStatus.setText(cafe.getTable()+"");
                    textView_cafeInfo.setText(cafe.getCafe_info());
                    if(cafe.isIs24Working())
                        textView_workingTime.setText("24시간 영업");
                    else
                        textView_workingTime.setText(cafe.getOpen_time()+"시 ~ "+cafe.getClose_time()+"시");
                    textView_grade.setText(String.format("%.2f", cafe.getGrade_cafe())+"/5");
                    ratingBar_cafe.setRating(cafe.getGrade_cafe());

                    if(cafe.getKeywords() != null){
                        for(String str : cafe.getKeywords()){
                            Keyword keyword = new Keyword(str);
                            keyword.setChosen(true);
                            KeywordView keywordView = new KeywordView(CafeInfoCustomerActivity.this, keyword);
                            keywordView.setClick(false);
                            flowLayout_keywords.addView(keywordView);
                        }
                    }

                    db.collection("comments").whereEqualTo("cid", cafeId).orderBy("update_time")
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            Log.d(TAG, "getting comment data : success");

                            for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                                Comment comment = document.toObject(Comment.class);
                                commentAdapter.addItem(comment);
                                setListViewHeightBasedOnItems(listView_comments);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "getting comment data : failure", e);
                        }
                    });

                    progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "getting cafe info : failed", e);
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(CafeInfoCustomerActivity.this);
                builder.setTitle("오류").setMessage("카페 정보를 불러오는데 실패했습니다.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void addComment(){
        if(mAuth.getCurrentUser() != null){
            final Comment comment = new Comment(cafeId, mAuth.getUid(), editText_comment.getText().toString(),
                    grade, new Date(System.currentTimeMillis()));

            db.collection("comments").add(comment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(TAG, "add comment to db : success");

                    commentAdapter.addItem(comment);
                    setListViewHeightBasedOnItems(listView_comments);
                    commentAdapter.notifyDataSetChanged();

                    //카페 평점 업데이트
                    final float averageRating = commentAdapter.getAverage();
                    db.collection("cafes").document(cafeId).update("grade_cafe", averageRating)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "update cafe grade : success");

                                    textView_grade.setText(String.format("%.2f", averageRating));
                                    ratingBar_cafe.setRating(averageRating);

                                    ratingBar_comment.clearFocus();
                                    editText_comment.setText("");
                                    linearLayout_addComment.setVisibility(GONE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "update cafe grade : failure", e);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "add comment to db : failure", e);
                }
            });
        } else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("로그인 필요").setMessage("댓글을 쓰려면 로그인이 필요합니다.\n로그인하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(CafeInfoCustomerActivity.this, LoginActivity.class));
                    finish();
                }
            }).setNegativeButton("아니오", null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        CommentAdapter listAdapter = (CommentAdapter) listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 500 * (listView.getResources().getDisplayMetrics().density);
                item.measure(View.MeasureSpec.makeMeasureSpec((int) px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);
            // Get padding
            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
            listView.setLayoutParams(params);
            listView.requestLayout();
            //setDynamicHeight(listView);
            return true;

        } else {
            return false;
        }
    }
}
