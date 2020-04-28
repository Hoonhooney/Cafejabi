package com.example.cafejabi;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class KeywordAdapter extends BaseAdapter {
    private List<Keyword> keywords = new ArrayList<>();

    public KeywordAdapter(List<Keyword> keywords){
        this.keywords = keywords;
    }

    @Override
    public int getCount() {
        return keywords.size();
    }

    @Override
    public Object getItem(int i) {
        return keywords.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_keyword, viewGroup, false);
        }

        final Keyword keyword = keywords.get(i);

        final LinearLayout linearLayout_keyword = view.findViewById(R.id.linearLayout_item_keyword);
        TextView textView_keyword = view.findViewById(R.id.textView_item_keyword);
        textView_keyword.setText(keyword.getName());
        if(keyword.isChosen()){
            linearLayout_keyword.setBackground(context.getResources().getDrawable(R.drawable.keyword_chosen));
        }else{
            linearLayout_keyword.setBackground(context.getResources().getDrawable(R.drawable.keyword_default));
        }

        linearLayout_keyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(keyword.isChosen()){
                    keyword.setChosen(false);
                    linearLayout_keyword.setBackground(context.getResources().getDrawable(R.drawable.keyword_default));
                }else{
                    keyword.setChosen(true);
                    linearLayout_keyword.setBackground(context.getResources().getDrawable(R.drawable.keyword_chosen));
                }
            }
        });

        return view;
    }

    public void addItem(Keyword keyword){
        keywords.add(keyword);
    }
}
