package com.example.cafejabi.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cafejabi.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SelectLocationPopupActivity extends Activity {
    private static final String TAG = "LocationPopupActivity";

    private ListView listView_cafe_locations;
    private CafeListAdapter adapter;
    private String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_select_location_popup);

        Intent intent = getIntent();
        data = intent.getStringExtra("LOCATION_DATA");

        listView_cafe_locations = findViewById(R.id.listView_select_cafe_locations);
        adapter = new CafeListAdapter();
        listView_cafe_locations.setAdapter(adapter);

        setDataToList();

        listView_cafe_locations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "onItemClick()");
                SimpleCafeInfo selectedCafe = (SimpleCafeInfo) adapter.getItem(i);
                Intent intent = new Intent();
                intent.putExtra("SELECTED_CAFE_NAME", selectedCafe.getCafeName());
                intent.putExtra("SELECTED_CAFE_ADDRESS", selectedCafe.getCafeAddress());
                intent.putExtra("SELECTED_CAFE_MAPX", selectedCafe.getMapx());
                intent.putExtra("SELECTED_CAFE_MAPY", selectedCafe.getMapy());
                setResult(RESULT_OK, intent);

                finish();
            }
        });

        if(adapter.isEmpty()){
            findViewById(R.id.linearLayout_alert_empty_list).setVisibility(View.VISIBLE);
            findViewById(R.id.button_alert_empty_list).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

//    JSON Data를 ListView로 보여주기
    private void setDataToList(){
        Log.e(TAG, "setDataToList()");

        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = new JSONArray(jsonObject.getString("items"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Log.d(TAG, object.toString());
                String title = object.getString("title").replace("<b>", "").replace("</b>","");
                String address = object.getString("address");
                int mapx = object.getInt("mapx");
                int mapy = object.getInt("mapy");
                Log.d(TAG, mapx+", "+mapy);

                adapter.addItem(new SimpleCafeInfo(title, address, mapx, mapy));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static class CafeListAdapter extends BaseAdapter{
        private ArrayList<SimpleCafeInfo> cafeList = new ArrayList<>();
        CafeListAdapter(){}

        @Override
        public int getCount() {
            return cafeList.size();
        }

        @Override
        public Object getItem(int i) {
            return cafeList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Context context = viewGroup.getContext();

            if(view == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_locations, viewGroup, false);
            }

            TextView textView_cafeName = view.findViewById(R.id.textView_item_locations_cafe_name);
            TextView textView_cafeAddress = view.findViewById(R.id.textView_item_locations_cafe_address);

            SimpleCafeInfo cafe = cafeList.get(i);

            textView_cafeName.setText(cafe.getCafeName());
            textView_cafeAddress.setText(cafe.getCafeAddress());

            return view;
        }

        void addItem(SimpleCafeInfo cafe){
            cafeList.add(cafe);
        }
    }

    public static class SimpleCafeInfo{
        private String cafeName;
        private String cafeAddress;
        private int mapx, mapy;

        SimpleCafeInfo(String cafeName, String cafeAddress, int mapx, int mapy) {
            this.cafeName = cafeName;
            this.cafeAddress = cafeAddress;
            this.mapx = mapx;
            this.mapy = mapy;
        }

        String getCafeName() {
            return cafeName;
        }

        String getCafeAddress() {
            return cafeAddress;
        }

        int getMapx() {
            return mapx;
        }

        int getMapy() {
            return mapy;
        }
    }
}
