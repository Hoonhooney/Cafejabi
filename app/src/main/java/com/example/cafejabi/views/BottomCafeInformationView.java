package com.example.cafejabi.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.cafejabi.R;
import com.example.cafejabi.objects.Cafe;
import com.example.cafejabi.objects.UserInfo;
import com.example.cafejabi.objects.Keyword;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.apmem.tools.layouts.FlowLayout;

public class BottomCafeInformationView extends RelativeLayout implements View.OnClickListener {
    private static final String TAG = "BottomInfoView";

    public EventListener listener;

    public Cafe cafe;

    public UserInfo userinfo;

    private TextView textView_cafe_name, textView_comments, textView_last_updated_time;
    private RatingBar ratingBar_cafe_grade;
    private FlowLayout flowLayout_keywords;

    private ImageView liking_cafe_list_checkbox;
    private boolean isChecked;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public void setEventListener(EventListener l){
        listener = l;
    }



    public interface EventListener{
        void btnCancel();
        void btnGoCafeInfo();
    }

    public BottomCafeInformationView(Context context, Cafe cafe){
        this(context, (AttributeSet) null);
        this.cafe = cafe;
        init();
    }

    public BottomCafeInformationView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.info_bottom, this, true);

        textView_cafe_name = findViewById(R.id.textView_info_cafename);
        textView_last_updated_time = findViewById(R.id.textView_info_last_updated_time);
        textView_comments = findViewById(R.id.textView_info_bottom_comments);
        ratingBar_cafe_grade = findViewById(R.id.ratingBar_info_bottom);
        flowLayout_keywords = findViewById(R.id.flowLayout_info_keywords);
        liking_cafe_list_checkbox = findViewById(R.id.liking_cafe_list_checkbox);               //따라해서 추가함 정확한 의미 모름


        textView_cafe_name.setText(cafe.getCafe_name());
        textView_last_updated_time.setText("마지막 업데이트 시간 : "+cafe.getTable_update_time());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        db.collection("users").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userinfo = documentSnapshot.toObject(UserInfo.class);

                if (userinfo != null){
                    for(int i=0;i<userinfo.getLikingCafeList().size();i++){
                        if(userinfo.getLikingCafeList().get(i).equals(cafe.getCid())){      //해당 유저의 찜 리스트 카페들의 이름을 차례로 검사
                            liking_cafe_list_checkbox.setBackground(getResources().getDrawable(R.drawable.heart_check));
                            isChecked = true;                                               //같은 경우 체크가 되어있는 상태로 만든다. (초기 상태) , 사용자가 체크박스 누르는 경우도 만들어야 함
                        }
                    }
                }
            }
        });

        liking_cafe_list_checkbox.setOnClickListener(this);

        ratingBar_cafe_grade.setRating(cafe.getGrade_cafe());

        db.collection("comments").whereEqualTo("cid", cafe.getCid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    textView_comments.setText(String.format("%.2f", cafe.getGrade_cafe()) + " ("+task.getResult().size()+")");
                }
            }
        });

        if(cafe.getKeywords() != null){
            for(String str_keyword : cafe.getKeywords()){
                Keyword keyword = new Keyword(str_keyword);
                keyword.setChosen(true);
                KeywordView keywordView = new KeywordView(getContext(), keyword);
                keywordView.setClick(false);
                flowLayout_keywords.addView(keywordView);
            }
        }



        findViewById(R.id.linearLayout_bottom_info).setOnClickListener(this);


    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
//            case R.id.button_menu_back:
//                listener.btnCancel();
//                break;

            case R.id.linearLayout_bottom_info:
                Log.e(TAG, "goCafeInfo()");
                listener.btnGoCafeInfo();
                break;

            case R.id.liking_cafe_list_checkbox:
                if(!isChecked){
                    userinfo.getLikingCafeList().add(cafe.getCid());
                    liking_cafe_list_checkbox.setBackground(getResources().getDrawable(R.drawable.heart_check));
                    isChecked = true;
                }else{
                    userinfo.getLikingCafeList().remove(cafe.getCid());
                    liking_cafe_list_checkbox.setBackground(getResources().getDrawable(R.drawable.heart_noncheck));
                    isChecked = false;
                }

                db.collection("users").document(userinfo.getUid())
                        .update("likingCafeList", userinfo.getLikingCafeList()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "update likingCafeList : success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "update likingCafeList : failure", e);
                    }
                });
                break;

            default:
                break;
        }
    }
}
