package com.example.cafejabi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.widget.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.CompassView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback , View.OnClickListener {

    private final String TAG = "MainActivity";

    private static final int ACCESS_LOCATION_PERMISSION_REQUEST_CODE = 100;

    private Context mContext = MainActivity.this;

    private FusedLocationSource locationSource;
    private Location lastLocation;

    private SharedPreferences locationPreferences, loginPreferences;

    private ViewGroup searchLayout;   //사이드 나왔을때 클릭방지할 영역
    private ViewGroup viewLayout;   //전체 감싸는 영역
    private ViewGroup sideLayout;   //사이드바만 감싸는 영역

    private EditText editText_search;

    private Boolean isMenuShow = false;
    private Boolean isExitFlag = false;
    private Boolean isLoggedIn = false;

    private FirebaseAuth mAuth;     //Firebase 인증 여부 확인용
    private FirebaseUser currentUser;   //로그인 사용자

    private IntroActivity adapter;    // 사용자, 사장님 페이지
    private ViewPager viewPager;

    private List<Cafe> cafeList;

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
        isLoggedIn = currentUser != null;
        addSideMenu();

        // 초기 페이지 보이기
//        viewPager = (ViewPager) findViewById(R.id.view);
//        adapter = new IntroActivity(this);
//        viewPager.setAdapter((PagerAdapter) adapter);

    }

    private void init(){
        findViewById(R.id.button_menu).setOnClickListener(this);
        findViewById(R.id.button_search_cafe).setOnClickListener(this);

        searchLayout = findViewById(R.id.linearLayout_search);
        viewLayout = findViewById(R.id.fl_silde);
        sideLayout = findViewById(R.id.view_sildemenu);

        editText_search = findViewById(R.id.editText_search_cafe);

        loginPreferences = getSharedPreferences("login", MODE_PRIVATE);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
    }

//    사이드메뉴 추가
    private void addSideMenu(){

        SideMenuView sideMenu = new SideMenuView(mContext, isLoggedIn);

        sideLayout.addView(sideMenu);

        viewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        //사이드메뉴 버튼 클릭 리스너
        sideMenu.setEventListener(new SideMenuView.EventListener() {
            //메뉴 닫기
            @Override
            public void btnCancel() {
                Log.e(TAG, "btnCancel");
                closeMenu();
            }

            //로그인
            @Override
            public void btnGoLogin() {
                Log.e(TAG, "btnGoLogin");

                startActivity(new Intent(mContext, LoginActivity.class));
                finish();
            }

            //회원 정보 수정
            @Override
            public void btnGoEdit() {
                Log.e(TAG, "btnGoEdit");
            }

            //로그아웃
            @Override
            public void btnLogout() {
                mAuth.signOut();

                String method = loginPreferences.getString("method", null);
                if(method != null && method.equals("Facebook"))
                    LoginManager.getInstance().logOut();

                SharedPreferences.Editor editor = loginPreferences.edit();
                editor.clear();
                editor.apply();

                startActivity(new Intent(mContext, LoginActivity.class));
                finish();
            }

            @Override
            public void btnGoRegisterCafe() {
                startActivity(new Intent(mContext, CafeRegisterActivity.class));
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

        String url = "nmap://search?query="+editText_search.getText()+"&appname=com.example.cafejabi";

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addCategory(Intent.CATEGORY_BROWSABLE);

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list == null || list.isEmpty()) {
            this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.nhn.android.nmap")));
        } else {
            this.startActivity(intent);
        }
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

        //symbol 크기 조정
        naverMap.setSymbolScale(0.7f);

        //카페 마킹하기
        drawCafes(naverMap);

        // GPS 버튼 활성화
        locationSource = new FusedLocationSource(this, ACCESS_LOCATION_PERMISSION_REQUEST_CODE);
        naverMap.setLocationSource(locationSource);
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);

        //나침반 활성화
        CompassView compassView = findViewById(R.id.compassView);
        compassView.setMap(naverMap);

        // 최근 위치로 이동
        locationPreferences = getSharedPreferences("LatestLocation", MODE_PRIVATE);
        double lat = (double)locationPreferences.getFloat("Latitude", 37.2788f);
        double lon = (double)locationPreferences.getFloat("Longitude", 127.0437f);

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

    private void drawCafes(final NaverMap map){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("cafes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Cafe cafe = document.toObject(Cafe.class);
                        Log.d(TAG, cafe.getCafe_name()+"");
                        Marker marker = new Marker();
                        marker.setPosition(new LatLng(cafe.getLocate_x(), cafe.getLocate_y()));
                        marker.setOnClickListener(new Overlay.OnClickListener() {
                            @Override
                            public boolean onClick(@NonNull Overlay overlay) {
                                return false;
                            }
                        });
                        marker.setMap(map);
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
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
        locationPreferences = getSharedPreferences("LatestLocation", MODE_PRIVATE);
        SharedPreferences.Editor editor = locationPreferences.edit();
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
