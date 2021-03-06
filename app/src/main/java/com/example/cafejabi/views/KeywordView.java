package com.example.cafejabi.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cafejabi.R;
import com.example.cafejabi.objects.Keyword;

//카페 정보에서 보이는 KeywordView
public class KeywordView extends LinearLayout implements View.OnClickListener{
    private Keyword keyword;
    private LinearLayout linearLayout_keyword;
    private boolean clickable = true;

    public KeywordView(Context context, Keyword keyword){
        super(context, (AttributeSet)null);
        this.keyword = keyword;
        init();
    }

    public void init() {

        LayoutInflater.from(getContext()).inflate(R.layout.item_keyword, this, true);

        linearLayout_keyword = findViewById(R.id.linearLayout_item_keyword);
        TextView textView_keyword = findViewById(R.id.textView_item_keyword);
        textView_keyword.setText(keyword.getName());
        setButton(keyword.isChosen());

        linearLayout_keyword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(clickable){
            setButton(!keyword.isChosen());
        }
    }

    public Keyword getKeyword(){
        return this.keyword;
    }

    public void setButton(boolean chosen){
        keyword.setChosen(chosen);
        if (chosen){
            linearLayout_keyword.setBackground(getResources().getDrawable(R.drawable.keyword_chosen));
        }else{
            linearLayout_keyword.setBackground(getResources().getDrawable(R.drawable.keyword_default));
        }
    }

    public void setClick(boolean b){
        clickable = b;
    }
}
