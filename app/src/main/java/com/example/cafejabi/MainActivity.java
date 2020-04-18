package com.example.cafejabi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.CompassView;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback , View.OnClickListener {

    private final String TAG = "MainActivity";

    private static final int ACCESS_LOCATION_PERMISSION_REQUEST_CODE = 100;

    private Context mContext = MainActivity.this;

    private FusedLocationSource locationSource;
    private Location lastLocation;

    private SharedPreferences preferences;

    private ViewGroup searchLayout;   //사이드 나왔을때 클릭방지할 영역
    private ViewGroup viewLayout;   //전체 감싸는 영역
    private ViewGroup sideLayout;   //사이드바만 감싸는 영역

    private EditText editText_search;

    private Boolean isMenuShow = false;
    private Boolean isExitFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        //지도 보이기
        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //사이드메뉴 활성화
        addSideMenu();
    }

    private void init(){
        findViewById(R.id.button_menu).setOnClickListener(this);
        findViewById(R.id.button_search_cafe).setOnClickListener(this);

        searchLayout = findViewById(R.id.linearLayout_search);
        viewLayout = findViewById(R.id.fl_silde);
        sideLayout = findViewById(R.id.view_sildemenu);

        editText_search = findViewById(R.id.editText_search_cafe);
    }

//    사이드메뉴 추가
    private void addSideMenu(){

        SideMenuView sideMenu = new SideMenuView(mContext);
        sideLayout.addView(sideMenu);

        viewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        sideMenu.setEventListener(new SideMenuView.EventListener() {
            @Override
            public void btnCancel() {
                Log.e(TAG, "btnCancel");
                closeMenu();
            }
        });
    }

    public void closeMenu(){
        Log.e(TAG, "closeMenu()");

        isMenuShow = false;
        Animation slide = AnimationUtils.loadAnimation(mContext, R.anim.sidemenu_hidden);
        sideLayout.startAnimation(slide);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewLayout.setVisibility(View.GONE);
                viewLayout.setEnabled(false);
                searchLayout.setVisibility(View.VISIBLE);
            }
        }, 450);
    }

    public void showMenu(){
        Log.e(TAG, "showMenu()");

        isMenuShow = true;
        Animation slide = AnimationUtils.loadAnimation(this, R.anim.sidemenu_show);
        sideLayout.startAnimation(slide);
        viewLayout.setVisibility(View.VISIBLE);
        viewLayout.setEnabled(true);
        searchLayout.setVisibility(View.GONE);
    }

//    카페 검색
    public void searchCafe(){
        Log.e(TAG, "searchCafe()");
    }

//    onClick 이벤트
    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.button_menu :
                showMenu();
                break;

            case R.id.button_search_cafe:
                searchCafe();
                break;
        }
    }

//    지도가 로딩된 후 작동
    @Override
    public void onMapReady(@NonNull final NaverMap naverMap) {
        Log.e(TAG, "onMapReady()");

        //최소, 최대 줌 설정
        naverMap.setMinZoom(12.0);
        naverMap.setMaxZoom(20.0);

        // GPS 버튼 활성화
        locationSource = new FusedLocationSource(this, ACCESS_LOCATION_PERMISSION_REQUEST_CODE);
        naverMap.setLocationSource(locationSource);
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);

        //나침반 활성화
        CompassView compassView = findViewById(R.id.compassView);
        compassView.setMap(naverMap);

        // 최근 위치로 이동
        preferences = getSharedPreferences("LatestLocation", MODE_PRIVATE);
        double lat = (double)preferences.getFloat("Latitude", 37.2788f);
        double lon = (double)preferences.getFloat("Longitude", 127.0437f);

        naverMap.setCameraPosition(new CameraPosition(new LatLng(lat, lon), 17, 0, 0));

        // 카메라 움직일 때마다 작동
        lastLocation = new Location("Provider");
        naverMap.addOnCameraChangeListener(new NaverMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(int i, boolean b) {
                // 최근 위치 정보 갱신
                lastLocation.setLatitude(naverMap.getCameraPosition().target.latitude);
                lastLocation.setLongitude(naverMap.getCameraPosition().target.longitude);
            }
        });
    }

//    위치 서비스 권한
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "onRequestPermissionsResult()");

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
        preferences = getSharedPreferences("LatestLocation", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if(lastLocation != null){
            editor.putFloat("Latitude", (float)lastLocation.getLatitude());
            editor.putFloat("Longitude", (float)lastLocation.getLongitude());
        }
        editor.apply();
    }

//    back 버튼 두 번 누르면 종료
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {

        if(isMenuShow){
            closeMenu();
        }else{

            if(isExitFlag){
                finish();
            } else {

                isExitFlag = true;
                Toast.makeText(this, "뒤로가기를 한 번 더 누르면 앱이 종료됩니다.",  Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExitFlag = false;
                    }
                }, 2000);
            }
        }
    }
}
