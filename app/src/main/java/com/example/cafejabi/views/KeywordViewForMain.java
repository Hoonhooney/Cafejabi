package com.example.cafejabi.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cafejabi.R;
import com.example.cafejabi.objects.Keyword;

//MainActivity에서 보이는 KeywordView
public class KeywordViewForMain extends LinearLayout implements View.OnClickListener {
    private Keyword keyword;
    private LinearLayout linearLayout_keyword;
    private boolean clickable = true;

    private EventListener listener;

    public void setEventListener(EventListener l) {
        listener = l;
    }

    public interface EventListener {
        void showMarkers(KeywordViewForMain keywordView);
    }

    public KeywordViewForMain(Context context, Keyword keyword){
        super(context, (AttributeSet)null);
        this.keyword = keyword;
        init();
    }

    public void init() {

        LayoutInflater.from(getContext()).inflate(R.layout.item_keyword, this, true);

        linearLayout_keyword = findViewById(R.id.linearLayout_item_keyword);
        TextView textView_keyword = findViewById(R.id.textView_item_keyword);
        textView_keyword.setText(keyword.getName());
        if(keyword.isChosen()){
            linearLayout_keyword.setBackground(getResources().getDrawable(R.drawable.keyword_chosen));
        }else{
            linearLayout_keyword.setBackground(getResources().getDrawable(R.drawable.keyword_default));
        }

        linearLayout_keyword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(clickable){
            if(keyword.isChosen()){
                keyword.setChosen(false);
                linearLayout_keyword.setBackground(getResources().getDrawable(R.drawable.keyword_default));
            }else{
                keyword.setChosen(true);
                linearLayout_keyword.setBackground(getResources().getDrawable(R.drawable.keyword_chosen));
            }

            listener.showMarkers(this);
        }
    }

    public void setClick(boolean b){
        clickable = b;
    }

    public Keyword getKeyword(){
        return keyword;
    }
}
