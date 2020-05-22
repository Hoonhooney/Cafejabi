package com.example.cafejabi.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.example.cafejabi.R;
import com.example.cafejabi.objects.WorkTime;

public class WorkTimeSettingView extends LinearLayout implements View.OnClickListener{
    private static final String TAG = "WorkTimeSettingView";
    private Context mContext = getContext();
    private WorkTime wt;

    private LinearLayout linearLayout_wt, linearLayout_wt_set;
    private CheckBox checkBox_day, checkBox_working24h;

    public WorkTimeSettingView(Context context, WorkTime wt) {
        this(context, (AttributeSet) null);
        this.wt = wt;
        init();
    }

    public WorkTimeSettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.item_wt_setting, this, true);

        linearLayout_wt = findViewById(R.id.linearLayout_wt);
        linearLayout_wt_set = findViewById(R.id.linearLayout_wt_set);

        checkBox_day = findViewById(R.id.checkBox_wt);
        checkBox_day.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                wt.setOpen(b);
                if (b){
                    linearLayout_wt.setVisibility(VISIBLE);
                } else{
                    linearLayout_wt.setVisibility(GONE);
                }
            }
        });

        checkBox_working24h = findViewById(R.id.checkBox_wt_24h);
        checkBox_working24h.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                wt.setWorking24h(b);
                if (b){
                    linearLayout_wt_set.setVisibility(VISIBLE);
                } else{
                    linearLayout_wt_set.setVisibility(GONE);
                }
            }
        });

        findViewById(R.id.editText_wt_open).setOnClickListener(this);
        findViewById(R.id.editText_wt_closed).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.editText_wt_open:
                break;
            case R.id.editText_wt_closed:
                break;
        }
    }
}
