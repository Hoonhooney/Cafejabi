package com.example.cafejabi.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cafejabi.R;
import com.example.cafejabi.objects.Keyword;

public class KeywordView extends LinearLayout {
    private Keyword keyword;

    public KeywordView(Context context, Keyword keyword){
        super(context, (AttributeSet)null);
        this.keyword = keyword;
        init();
    }

    public void init() {

        LayoutInflater.from(getContext()).inflate(R.layout.item_keyword, this, true);

        final LinearLayout linearLayout_keyword = findViewById(R.id.linearLayout_item_keyword);
        TextView textView_keyword = findViewById(R.id.textView_item_keyword);
        textView_keyword.setText(keyword.getName());
        if(keyword.isChosen()){
            linearLayout_keyword.setBackground(getResources().getDrawable(R.drawable.keyword_chosen));
        }else{
            linearLayout_keyword.setBackground(getResources().getDrawable(R.drawable.keyword_default));
        }

        linearLayout_keyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(keyword.isChosen()){
                    keyword.setChosen(false);
                    linearLayout_keyword.setBackground(getResources().getDrawable(R.drawable.keyword_default));
                }else{
                    keyword.setChosen(true);
                    linearLayout_keyword.setBackground(getResources().getDrawable(R.drawable.keyword_chosen));
                }
            }
        });
    }
}
