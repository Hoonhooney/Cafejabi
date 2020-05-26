package com.example.cafejabi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.cafejabi.objects.Cafe;
import com.example.cafejabi.objects.Keyword;
import com.example.cafejabi.R;
import com.example.cafejabi.objects.WorkTime;
import com.example.cafejabi.views.KeywordView;
import com.example.cafejabi.views.WorkTimeSettingView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.Tm128;

import org.apmem.tools.layouts.FlowLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.bendik.simplerangeview.SimpleRangeView;

public class CafeRegisterActivity extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = "CafeRegisterActivity";
    public static StringBuilder sb;

    private EditText editText_cafe_name, editText_business_id, editText_cafe_description;
    private TextView textView_cafe_address;
    private CheckBox checkBox_is24Working, checkBox_alarm_same_work_time;
    private boolean isAllowedWithAlarm, is24Working, isSameAlarmWithWorkTime;
    private RadioGroup radioGroup_allow_service, radioGroup_set_gap;
//    private SimpleRangeView rangeSeekBar_set_working_time, rangeSeekBar_set_alarm_time;
    private LinearLayout linearLayout_allow_service;
    private FlowLayout flowLayout_keywords;

    private List<Keyword> keywords;
    private List<String> keywords_selected;

    private List<WorkTime> workTimes = new ArrayList<>();

    private String searchApiClientId, searchApiClientSecret;
    private int gap;
    private double locate_x, locate_y;

    private FirebaseAuth mAuth;
    private String uid;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_register);

        init();
    }

    public void init(){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        editText_cafe_name = findViewById(R.id.editText_cafe_name);
        editText_business_id = findViewById(R.id.editText_business_id);
        editText_cafe_description = findViewById(R.id.editText_cafe_description);

        textView_cafe_address = findViewById(R.id.textView_search_cafe_address);

//        checkBox_is24Working = findViewById(R.id.checkbox_24working);
//        checkBox_alarm_same_work_time = findViewById(R.id.checkBox_alarm_same_working_time);

        radioGroup_allow_service = findViewById(R.id.radioGroup_allow_seat_info);
        radioGroup_set_gap = findViewById(R.id.radioGroup_set_alarm_gap);

//        rangeSeekBar_set_alarm_time = findViewById(R.id.rangeSeekBar_set_alarm_time);

        linearLayout_allow_service = findViewById(R.id.linearLayout_allow_seat_info);

//        checkBox_is24Working.setOnCheckedChangeListener(this);

        radioGroup_allow_service.setOnCheckedChangeListener(this);

        radioGroup_set_gap.setOnCheckedChangeListener(this);

        checkBox_alarm_same_work_time.setOnCheckedChangeListener(this);

        LinearLayout linearLayout_wt = findViewById(R.id.linearLayout_cafe_register_wt);
        String[] days = getResources().getStringArray(R.array.day);
        for (String day : days){
            workTimes.add(new WorkTime(day));
        }
        for (int i = 0; i < workTimes.size(); i++) {
            linearLayout_wt.addView(new WorkTimeSettingView(this, workTimes.get(i)));
        }

        //키워드 등록
        keywords = new ArrayList<>();
        keywords_selected = new ArrayList<>();
        String[] arr_keyword = getResources().getStringArray(R.array.keywords);
        for(String str : arr_keyword){
            keywords.add(new Keyword(str));
        }

        flowLayout_keywords = findViewById(R.id.flowLayout_keywords);
        for(Keyword keyword : keywords){
            Log.e(TAG, "Keyword : "+keyword.getName());
            KeywordView keywordView = new KeywordView(this, keyword);
            flowLayout_keywords.addView(keywordView);
        }

        //사용자 정보 불러오기
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        db = FirebaseFirestore.getInstance();

        //API key 가져오기
        try{
            ApplicationInfo ai = getPackageManager()
                    .getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            searchApiClientId = ai.metaData.getString("com.naver.search.CLIENT_ID");
            searchApiClientSecret = ai.metaData.getString("com.naver.search.CLIENT_SECRET");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //카페 검색 버튼 기능 활성화
        findViewById(R.id.button_check_cafe_address).setOnClickListener(this);
        findViewById(R.id.button_register_cafe).setOnClickListener(this);
    }

//    Button onClickListener
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_check_cafe_address:
                searchLocation();
                break;
            case R.id.button_register_cafe:
                registerCafe();
                break;
        }
    }

//    CheckBox onCheckedChangeListener
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch(compoundButton.getId()){

//            case R.id.checkbox_24working:
//                is24Working = b;
//                if(b){
//                    rangeSeekBar_set_working_time.setVisibility(View.GONE);
//                }else{
//                    rangeSeekBar_set_working_time.setVisibility(View.VISIBLE);
//                }
//                break;

//            case R.id.checkBox_alarm_same_working_time:
//                isAllowedWithAlarm = b;
//                if(b){
//                    rangeSeekBar_set_alarm_time.setVisibility(View.GONE);
//                }else{
//                    rangeSeekBar_set_alarm_time.setVisibility(View.VISIBLE);
//                }
//                break;
        }
    }

//    RadioGroup onCheckedChangeListener
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch(radioGroup.getId()){
            case R.id.radioGroup_allow_seat_info:
                if(i == R.id.rb_allow_seat_info){
                    isAllowedWithAlarm = true;
                    linearLayout_allow_service.setVisibility(View.VISIBLE);
                }else{
                    isAllowedWithAlarm = false;
                    linearLayout_allow_service.setVisibility(View.GONE);
                }
                break;

            case R.id.radioGroup_set_alarm_gap:
                switch (i){
                    case R.id.rb_gap_15min:
                        gap = 15;
                        break;
                    case R.id.rb_gap_30min:
                        gap = 30;
                        break;
                    case R.id.rb_gap_60min:
                        gap = 60;
                        break;
                    case R.id.rb_gap_120min:
                        gap = 90;
                        break;
                }
                break;
        }
    }

//    지역 검색으로 카페 설정하기
    private void searchLocation(){
        Log.e(TAG, "searchLocation()");
        String text = null;
//        int display = 2;

        try {
            text = URLEncoder.encode(editText_cafe_name.getText().toString(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String apiURL = "https://openapi.naver.com/v1/search/local?query=" + text;

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", searchApiClientId);
        requestHeaders.put("X-Naver-Client-Secret", searchApiClientSecret);
        String responseBody = get(apiURL, requestHeaders);

        Log.d(TAG, responseBody);

        Intent intent = new Intent(this, SelectLocationPopupActivity.class);
        intent.putExtra("LOCATION_DATA",responseBody);
        startActivityForResult(intent, 1);
    }

//    카페 검색 결과 json으로 가져오기
    private String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : "+apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결을 실패했습니다. : "+apiUrl, e);
        }
    }

//    검색 결과 읽어오기
    private String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

//    카페 등록하기
    private void registerCafe(){
        String cafeName = editText_cafe_name.getText().toString();
        String cafeAddress = textView_cafe_address.getText().toString();
        String description = editText_cafe_description.getText().toString();

        String cid = db.collection("cafes").document().getId();

        for(Keyword keyword : keywords){
            if(keyword.isChosen())
                keywords_selected.add(keyword.getName());
        }

        Cafe cafe = new Cafe(cid, uid, cafeName, cafeAddress,
                locate_x, locate_y, 0, isAllowedWithAlarm, keywords_selected);

//        if(!is24Working){
//        }
        if(isAllowedWithAlarm){
            cafe.setAlarm_gap(gap);
        }

        cafe.setCafe_info(description);
        cafe.setWorkTimes(workTimes);

        addCafeInDb(cafe);
    }

    private void addCafeInDb(final Cafe cafe){
        db.collection("cafes").document(cafe.getCid()).set(cafe).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "Success : add cafe data into db");

                addCafeInUserInfo(cafe);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Fail : add cafe data into db", e);

                AlertDialog.Builder builder = new AlertDialog.Builder(CafeRegisterActivity.this);
                builder.setTitle("카페 등록").setMessage("카페를 등록하는데 실패했습니다.")
                        .setPositiveButton("OK", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void addCafeInUserInfo(final Cafe cafe){
        final AlertDialog.Builder builder = new AlertDialog.Builder(CafeRegisterActivity.this);
        builder.setTitle("카페 등록");

        db.collection("users").document(uid).update("managingCafe", cafe)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "Success: update userInfo");

                builder.setMessage("카페 등록을 완료했습니다!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(CafeRegisterActivity.this, MainActivity.class));
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Fail: update userInfo", e);

                builder.setMessage("카페 등록을 실패했습니다!").setPositiveButton("OK", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

//    다른 Activity에서의 정보 받기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult()");

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //데이터 받기
                Log.e(TAG, data.getStringExtra("SELECTED_CAFE_NAME")+"");
                editText_cafe_name.setText(data.getStringExtra("SELECTED_CAFE_NAME"));
                textView_cafe_address.setText(data.getStringExtra("SELECTED_CAFE_ADDRESS"));

                Tm128 tm128 = new Tm128(data.getIntExtra("SELECTED_CAFE_MAPX", 0), data.getIntExtra("SELECTED_CAFE_MAPY", 0));
                LatLng latLng = tm128.toLatLng();
                locate_x = latLng.latitude;
                locate_y = latLng.longitude;
            }
        }
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
