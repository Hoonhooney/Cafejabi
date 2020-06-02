package com.example.cafejabi.views;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.cafejabi.R;
import com.example.cafejabi.activities.MainActivity;
import com.example.cafejabi.objects.WorkTime;

public class WorkTimeSettingView extends LinearLayout implements View.OnClickListener{
    private static final String TAG = "WorkTimeSettingView";
    private Context mContext = getContext();
    private WorkTime wt;

    private LinearLayout linearLayout_wt, linearLayout_wt_set;
    private CheckBox checkBox_day, checkBox_working24h;
    private EditText editText_open, editText_closed;
    private TextView textView_day_off;

    private int openH = 7, openM = 0, closedH = 23, closedM = 0;

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
        textView_day_off = findViewById(R.id.textView_cafe_day_off);

        checkBox_day = findViewById(R.id.checkBox_wt);
        checkBox_day.setChecked(wt.isOpen());
        setLayout(wt.isOpen());

        checkBox_day.setText(wt.getDayOfWeek());
        checkBox_day.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                wt.setOpen(b);
                setLayout(b);
            }
        });

        checkBox_working24h = findViewById(R.id.checkBox_wt_24h);
        checkBox_working24h.setChecked(wt.isWorking24h());
        if (wt.isWorking24h())
            linearLayout_wt_set.setVisibility(GONE);
        checkBox_working24h.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                wt.setWorking24h(b);
                if (b){
                    linearLayout_wt_set.setVisibility(GONE);
                } else{
                    linearLayout_wt_set.setVisibility(VISIBLE);
                }
            }
        });

        editText_open = findViewById(R.id.editText_wt_open);
        editText_open.setText(wt.getOpenAt());
        editText_closed = findViewById(R.id.editText_wt_closed);
        editText_closed.setText(wt.getClosedAt());
        editText_open.setFocusable(false);
        editText_closed.setFocusable(false);
        editText_open.setOnClickListener(this);
        editText_closed.setOnClickListener(this);
    }

    public void setLayout(boolean open){
        if (open){
            linearLayout_wt.setVisibility(VISIBLE);
            textView_day_off.setVisibility(GONE);
            checkBox_day.setChecked(true);
        }
        else{
            linearLayout_wt.setVisibility(GONE);
            textView_day_off.setVisibility(VISIBLE);
            checkBox_day.setChecked(false);
        }
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()){
            case R.id.editText_wt_open:
                TimePickerDialog picker_open = new TimePickerDialog(mContext, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                openH = sHour;
                                openM = sMinute;
                                wt.setOpenAt(sHour, sMinute);
                                ((EditText)view).setText(wt.getOpenAt());
                            }
                        }, openH, openM, false);
                picker_open.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                picker_open.show();
                break;
            case R.id.editText_wt_closed:
                TimePickerDialog picker_closed = new TimePickerDialog(mContext, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                closedH = sHour;
                                closedM = sMinute;
                                wt.setClosedAt(sHour, sMinute);
                                ((EditText)view).setText(wt.getClosedAt());
                            }
                        }, closedH, closedM, false);
                picker_closed.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                picker_closed.show();
                break;
        }
    }
}
