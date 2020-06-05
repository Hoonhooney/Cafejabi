package com.example.cafejabi.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.cafejabi.R;
import com.example.cafejabi.objects.Cafe;
import com.example.cafejabi.objects.UserInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SideMenuView extends RelativeLayout implements View.OnClickListener {
    private static final String TAG = "SideMenuView";

    public EventListener listener;

    private LinearLayout linearLayout_login, linearLayout_logout, linearLayout_myCafe;
    private TextView textView_username, textView_userstyle, textView_myCafe;
    private Switch switch_myCafe;
    private ListView listView_menu;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserInfo userInfo;


    public boolean isloggedin;

    public void setEventListener(EventListener l){
        listener = l;
    }

    public interface EventListener{
        void btnCancel();
        void btnGoLogin();
        void btnGoEdit(String cid);
        void btnGoEditUser();
        void btnLogout();
        void btnGoRegisterCafe();
        void btnGoCafeList(int code);
        void btnGoSettings();
    }

    public SideMenuView(Context context, boolean isloggedin){
        this(context, null);
        this.isloggedin = isloggedin;
        init();
    }



    public SideMenuView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.menu_side, this, true);

        linearLayout_login = findViewById(R.id.linearLayout_menu_login);
        linearLayout_logout = findViewById(R.id.linearLayout_menu_logout);
        linearLayout_myCafe = findViewById(R.id.linearLayout_side_my_cafe);

        textView_myCafe = findViewById(R.id.textView_side_my_cafe);
        switch_myCafe = findViewById(R.id.switch_side_cafe_onoff);

        //로그아웃 상태, 로그인 상태 메뉴 다르게 보이기
        final List<String> list_menu = new ArrayList<>();



        if(!isloggedin){
            linearLayout_login.setVisibility(GONE);
            linearLayout_logout.setVisibility(VISIBLE);

            findViewById(R.id.button_go_login).setOnClickListener(this);
        }else{
            //로그인한 상태에 사용자 닉네임, 스타일 보이기

            linearLayout_login.setVisibility(VISIBLE);
            linearLayout_logout.setVisibility(GONE);
            list_menu.add("로그아웃");
            list_menu.add("방문한 카페");
            list_menu.add("찜 카페");



            textView_username = findViewById(R.id.textView_menu_userName);
            textView_userstyle = findViewById(R.id.textView_menu_userStyle);


            assert user != null;
            DocumentReference ref = db.collection("users").document(user.getUid());
            ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    userInfo = documentSnapshot.toObject(UserInfo.class);
                    assert userInfo != null;
                    textView_username.setText(userInfo.getNickname());
                    textView_userstyle.setText(userInfo.getStyle().toString());

                    if(userInfo.getManagingCafe() !=null){
                        linearLayout_myCafe.setVisibility(VISIBLE);
                        db.collection("cafes").document(userInfo.getManagingCafe()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                final Cafe myCafe = documentSnapshot.toObject(Cafe.class);
                                if (myCafe != null){
                                    Log.d(TAG, "get my cafe : success");
                                    textView_myCafe.setText(myCafe.getCafe_name());

                                    linearLayout_myCafe.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            listener.btnGoEdit(myCafe.getCid());
                                        }
                                    });

                                    switch_myCafe.setChecked(myCafe.isOpen());

                                    switch_myCafe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            db.collection("cafes").document(myCafe.getCid()).update("open", isChecked).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "cafe open update : Success");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e(TAG, "cafe open update : Failure", e);
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "get my cafe : failure", e);
                            }
                        });
                    }
                    else{
                        list_menu.add("카페 등록하기");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "failed to get userInfo", e);
                }
            });

            findViewById(R.id.button_go_edit_user).setOnClickListener(this);


        }
        list_menu.add("설정");

        //메뉴 ListView
        findViewById(R.id.button_menu_back).setOnClickListener(this);

        listView_menu = findViewById(R.id.listView_menu);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                R.layout.item_menu, list_menu);

        listView_menu.setAdapter(adapter);

        listView_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (list_menu.get(position)){
                    case "로그아웃":
                        listener.btnLogout();
                        break;
                    case "카페 등록하기":
                        listener.btnGoRegisterCafe();
                        break;
                    case "방문한 카페":
                        listener.btnGoCafeList(1);
                        break;
                    case "찜 카페":
                        listener.btnGoCafeList(2);
                        break;
                    case "설정":
                        listener.btnGoSettings();
                }
            }
        });
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_menu_back:
                listener.btnCancel();
                break;

            case R.id.button_go_login:
                listener.btnGoLogin();
                break;

            case R.id.button_go_edit_user:
                listener.btnGoEditUser();

            default:
                break;
        }
    }
}
