package com.example.cafejabi.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.cafejabi.R;
import com.example.cafejabi.activities.CafeInfoCustomerActivity;
import com.example.cafejabi.objects.Cafe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CafeAdapter extends BaseAdapter {
    private Context mContext;
    private List<Cafe> cafeList;

    private TextView textView_cafe_name, textView_cafe_address, textView_cafe_status;

    public CafeAdapter(Context context){
        this.mContext = context;
        this.cafeList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return cafeList.size();
    }

    @Override
    public Object getItem(int position) {
        return cafeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void addItem(Cafe cafe){
        cafeList.add(0, cafe);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final Cafe cafe = cafeList.get(position);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_cafe, parent, false);
        }

        textView_cafe_name = view.findViewById(R.id.textView_item_cafe_name);
        textView_cafe_address = view.findViewById(R.id.textView_item_cafe_address);
        textView_cafe_status = view.findViewById(R.id.textView_item_cafe_status);

        if(cafe != null){
            textView_cafe_name.setText(cafe.getCafe_name());
            textView_cafe_address.setText(cafe.getAddress());

            textView_cafe_status.setText(cafe.getTable()+"");

            int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

            if (!cafe.isIs24Working() && (currentHour < cafe.getOpen_time() || currentHour >= cafe.getClose_time())){
                textView_cafe_status.setText("닫힘");
            }else if(!cafe.isAllowAlarm()) {
                textView_cafe_status.setVisibility(View.INVISIBLE);
            }else{
                switch(cafe.getTable()){
                    case 0:
                        textView_cafe_status.setText("매우 한산");
                        textView_cafe_status.setTextColor(mContext.getColor(R.color.colorTable0));
                        break;
                    case 1:
                        textView_cafe_status.setText("한산");
                        textView_cafe_status.setTextColor(mContext.getColor(R.color.colorTable1));
                        break;
                    case 2:
                        textView_cafe_status.setText("보통");
                        textView_cafe_status.setTextColor(mContext.getColor(R.color.colorTable2));
                        break;
                    case 3:
                        textView_cafe_status.setText("많음");
                        textView_cafe_status.setTextColor(mContext.getColor(R.color.colorTable3));
                        break;
                    case 4:
                        textView_cafe_status.setText("꽉 참");
                        textView_cafe_status.setTextColor(mContext.getColor(R.color.colorTable4));
                        break;
                }
            }
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CafeInfoCustomerActivity.class);
                intent.putExtra("CafeId", cafe.getCid());
                mContext.startActivity(intent);
            }
        });

        return view;
    }
}
