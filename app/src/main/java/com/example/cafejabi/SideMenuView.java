package com.example.cafejabi;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.view.View;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.jar.Attributes;

public class SideMenuView extends RelativeLayout implements View.OnClickListener {

    public EventListener listener;

    private LinearLayout linearLayout_login, linearLayout_logout;

    private ListView listView_menu;

    public boolean isloggedin;

    public void setEventListener(EventListener l){
        listener = l;
    }

    public interface EventListener{
        void btnCancel();
        void btnGoLogin();
        void btnGoEdit();
    }

    public SideMenuView(Context context){
        this(context, null);
        init();
    }

    public SideMenuView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.menu_side, this, true);

        linearLayout_login = findViewById(R.id.linearLayout_menu_login);
        linearLayout_logout = findViewById(R.id.linearLayout_menu_logout);

        //로그아웃 상태, 로그인 상태 메뉴 다르게 보이기
        if(!isloggedin){
            linearLayout_login.setVisibility(GONE);
            linearLayout_logout.setVisibility(VISIBLE);

            findViewById(R.id.button_go_login).setOnClickListener(this);
        }else{
            linearLayout_login.setVisibility(VISIBLE);
            linearLayout_logout.setVisibility(GONE);

            findViewById(R.id.button_go_edit).setOnClickListener(this);
        }

        //메뉴 ListView
        findViewById(R.id.button_menu_back).setOnClickListener(this);

        listView_menu = findViewById(R.id.listView_menu);

        final List<String> list_menu = new ArrayList<>();
        list_menu.add("자주 가는 카페");
        list_menu.add("찜 카페");
        list_menu.add("설정");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                R.layout.item_menu, list_menu);

        listView_menu.setAdapter(adapter);

        listView_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (list_menu.get(position)){

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

            case R.id.button_go_edit:
                listener.btnGoEdit();
                break;

            default:
                break;
        }
    }
}
