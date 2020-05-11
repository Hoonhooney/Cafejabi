package com.example.cafejabi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PointF;

import android.content.Context;
import android.content.Intent;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cafejabi.views.BottomCafeInformationView;
import com.example.cafejabi.objects.Cafe;
import com.example.cafejabi.R;
import com.example.cafejabi.views.SideMenuView;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback , View.OnClickListener {

    private final String TAG = "MainActivity";

    private static final int ACCESS_LOCATION_PERMISSION_REQUEST_CODE = 100;

    private Context mContext = MainActivity.this;

    private MapFragment mapFragment;
    private FusedLocationSource locationSource;
    private Location lastLocation;

    private SharedPreferences locationPreferences, loginPreferences;

    private ViewGroup searchLayout;   //사이드 나왔을때 클릭방지할 영역
    private ViewGroup viewLayout;   //전체 감싸는 영역
    private ViewGroup sideLayout;   //사이드바만 감싸는 영역
    private ViewGroup bottomLayout;

    private AutoCompleteTextView editText_search;

    private Boolean isMenuShow = false;
    private Boolean isInfoShow = false;
    private Boolean isExitFlag = false;
    private Boolean isLoggedIn = false;

    private FirebaseAuth mAuth;     //Firebase 인증 여부 확인용
    private FirebaseUser currentUser;   //로그인 사용자
    private FirebaseFirestore db;   //Firebase database

    private List<String> cafeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        // 초기 페이지 보이기
        if(mAuth == null && loginPreferences.getBoolean("firstTime", true))
            startActivity(new Intent(this, ViewPagerActivity.class));
    }

    private void init(){
        findViewById(R.id.button_menu).setOnClickListener(this);
//        findViewById(R.id.button_search_cafe).setOnClickListener(this);

        searchLayout = findViewById(R.id.linearLayout_search);
        viewLayout = findViewById(R.id.fl_silde);
        sideLayout = findViewById(R.id.view_sildemenu);

        bottomLayout = findViewById(R.id.view_bottominfo);

        editText_search = findViewById(R.id.editText_search_cafe);
        editText_search.setSingleLine();
        editText_search.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        loginPreferences = getSharedPreferences("login", MODE_PRIVATE);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();

        //지도 보이기
        mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //사이드메뉴 활성화
        isLoggedIn = currentUser != null;
        addSideMenu();
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
                goActivity(LoginActivity.class);
            }

            //회원 정보 수정
            @Override
            public void btnGoEdit() {
                Log.e(TAG, "btnGoEdit");
                goActivity(EditUserInfoActivity.class);
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

                goActivity(LoginActivity.class);
            }

            @Override
            public void btnGoRegisterCafe() {
                goActivity(CafeRegisterActivity.class);
            }

            @Override
            public void btnGoCafeList(int code) {
                Intent intent = new Intent(mContext, CafeListActivity.class);
                intent.putExtra("CODE", code);
                startActivity(intent);
                finish();
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
        bottomLayout.removeAllViews();

        isMenuShow = true;
        Animation slide = AnimationUtils.loadAnimation(this, R.anim.sidemenu_show);
        sideLayout.startAnimation(slide);
        viewLayout.setVisibility(View.VISIBLE);
        viewLayout.setEnabled(true);
        searchLayout.setVisibility(View.GONE);
    }

//    onClick 이벤트
    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.button_menu :
                showMenu();
                break;

//            case R.id.button_search_cafe:
//                searchCafe();
//                break;
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

        //지도 길게 누르면 bottomLayout 제거
        naverMap.setOnMapLongClickListener(new NaverMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                bottomLayout.removeAllViews();
            }
        });

        //검색 기능
        editText_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    searchCafe(naverMap);
                    hideKeyboard(v);
                }
                return false;
            }
        });
        editText_search.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, cafeList));
        editText_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchCafe(naverMap);
                hideKeyboard(editText_search);
            }
        });

        findViewById(R.id.button_search_cafe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCafe(naverMap);
                hideKeyboard(v);
            }
        });
    }

    private void drawCafes(final NaverMap map){
        cafeList.clear();
        db.collection("cafes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        final Cafe cafe = document.toObject(Cafe.class);
                        cafeList.add(cafe.getCafe_name());
                        Log.d(TAG, cafe.getCafe_name()+"");
                        Marker marker = new Marker();
                        final LatLng cafeLocation = new LatLng(cafe.getLocate_x(), cafe.getLocate_y());
                        marker.setPosition(cafeLocation);
                        marker.setOnClickListener(new Overlay.OnClickListener() {
                            @Override
                            public boolean onClick(@NonNull Overlay overlay) {
                                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(cafeLocation)
                                        .animate(CameraAnimation.Easing, 200);
                                map.moveCamera(cameraUpdate);
                                showCafeInfo(cafe);
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

//    카페 검색
    public void searchCafe(final NaverMap naverMap){
        Log.e(TAG, "searchCafe()");

        db.collection("cafes").whereGreaterThanOrEqualTo("cafe_name", editText_search.getText().toString())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Cafe resultCafe = task.getResult().getDocuments().get(0).toObject(Cafe.class);

                    CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(resultCafe.getLocate_x(), resultCafe.getLocate_y()))
                            .animate(CameraAnimation.Easing, 200);
                    naverMap.moveCamera(cameraUpdate);
                    showCafeInfo(resultCafe);
                }
            }
        });
    }

    private void showCafeInfo(Cafe cafe){
        Log.d(TAG, "showCafeInfo()");

        isInfoShow = true;

        bottomLayout.removeAllViews();
        final BottomCafeInformationView bottomInfoView = new BottomCafeInformationView(mContext, cafe);
        bottomInfoView.setEventListener(new BottomCafeInformationView.EventListener() {
            @Override
            public void btnCancel() {

            }

            @Override
            public void btnGoCafeInfo() {
                Log.e(TAG, "goCafeInfo()");
                Intent intent = new Intent(MainActivity.this, CafeInfoCustomerActivity.class);
                intent.putExtra("CafeId", bottomInfoView.cafe.getCid());
                startActivity(intent);
            }
        });
        bottomInfoView.setClickable(true);
        bottomLayout.addView(bottomInfoView);

        Animation slide = AnimationUtils.loadAnimation(this, R.anim.bottominfo_show);
        bottomLayout.startAnimation(slide);
        bottomLayout.setVisibility(View.VISIBLE);
        sideLayout.setEnabled(true);
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

    private void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

    private void goActivity(Class c){
        Intent intent = new Intent(mContext, c);
        startActivity(intent);
        finish();
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
