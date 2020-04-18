package com.example.cafejabi;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.view.View;

import java.util.EventListener;
import java.util.jar.Attributes;

public class SideMenuView extends RelativeLayout implements View.OnClickListener {

    public EventListener listener;

    public void setEventListener(EventListener l){
        listener = l;
    }

    public interface EventListener{
        void btnCancel();
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

        findViewById(R.id.button_menu_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_menu_back:
                listener.btnCancel();
                break;

            default:
                break;
        }
    }
}
