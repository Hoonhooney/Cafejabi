package com.example.cafejabi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class CafeRegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CafeRegisterActivity";
    public static StringBuilder sb;

    private EditText editText_cafe_name, editText_business_id, editText_cafe_address;

    private String searchApiClientId, searchApiClientSecret;
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
        editText_cafe_address = findViewById(R.id.editText_cafe_address);

        //API key 가져오기
        try{
            ApplicationInfo ai = getPackageManager()
                    .getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            searchApiClientId = ai.metaData.getString("com.naver.search.CLIENT_ID");
            searchApiClientSecret = ai.metaData.getString("com.naver.search.CLIENT_SECRET");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        findViewById(R.id.button_check_cafe_address).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_check_cafe_address:
                searchLocation();
        }
    }

    private void searchLocation(){
        Log.e(TAG, "searchLocation()");
        String text = null;
        int display = 2;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult()");

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //데이터 받기
                Log.e(TAG, data.getStringExtra("SELECTED_CAFE_NAME")+"");
                editText_cafe_name.setText(data.getStringExtra("SELECTED_CAFE_NAME"));
                editText_cafe_address.setText(data.getStringExtra("SELECTED_CAFE_ADDRESS"));
            }
        }
    }
}
