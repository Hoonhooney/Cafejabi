package com.example.cafejabi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.util.FusedLocationSource;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int ACCESS_LOCATION_PERMISSION_REQUEST_CODE = 100;
    private FusedLocationSource locationSource;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        지도 보이기
        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

//    지도가 로딩된 후 작동
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        //최소, 최대 줌 설정
        naverMap.setMinZoom(12.0);
        naverMap.setMaxZoom(20.0);

        // GPS 버튼 활성화
        locationSource = new FusedLocationSource(this, ACCESS_LOCATION_PERMISSION_REQUEST_CODE);
        naverMap.setLocationSource(locationSource);
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);

        // 최신 위치로 이동
        preferences = getSharedPreferences("LatestLocation", MODE_PRIVATE);
        double lat = (double)preferences.getFloat("Latitude", 37.2788f);
        double lon = (double)preferences.getFloat("Longitude", 127.0437f);

        naverMap.setCameraPosition(new CameraPosition(new LatLng(lat, lon), 17, 20, 0));
    }

//    위치 서비스 권한
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case ACCESS_LOCATION_PERMISSION_REQUEST_CODE:
                locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults);
                return;
        }
    }

//    종료할 때
    @Override
    public void onPause(){
        super.onPause();

        // 현위치 좌표 저장하기

        Location lastLocation = locationSource.getLastLocation();

        preferences = getSharedPreferences("LatestLocation", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if(lastLocation != null){
            editor.putFloat("Latitude", (float)lastLocation.getLatitude());
            editor.putFloat("Longitude", (float)lastLocation.getLongitude());
        }
        editor.apply();
    }
}
