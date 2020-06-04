package com.example.cafejabi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.PointF;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cafejabi.objects.Keyword;
import com.example.cafejabi.views.BottomCafeInformationView;
import com.example.cafejabi.objects.Cafe;
import com.example.cafejabi.R;
import com.example.cafejabi.views.KeywordView;
import com.example.cafejabi.views.KeywordViewForMain;
import com.example.cafejabi.views.SideMenuView;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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
import com.naver.maps.map.overlay.Align;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.CompassView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback , View.OnClickListener, KeywordViewForMain.EventListener {

    private final String TAG = "MainActivity";

    private static final int ACCESS_LOCATION_PERMISSION_REQUEST_CODE = 100;

    private final int markerWidth = 90;
    private final int markerHeight = 135;

    private Context mContext = MainActivity.this;

    private NaverMap map;
    private MapFragment mapFragment;
    private FusedLocationSource locationSource;
    private Location lastLocation;

    private SharedPreferences locationPreferences, loginPreferences;

    private ViewGroup searchLayout;   //사이드 나왔을때 클릭방지할 영역
    private ViewGroup viewLayout;   //전체 감싸는 영역
    private ViewGroup sideLayout;   //사이드바만 감싸는 영역
    private ViewGroup bottomLayout;
    private LinearLayout linearLayout_keywords;

    private AutoCompleteTextView editText_search;

    private Boolean isMenuShow = false;
    private Boolean isInfoShow = false;
    private Boolean isExitFlag = false;
    private Boolean isLoggedIn = false;

    private FirebaseAuth mAuth;     //Firebase 인증 여부 확인용
    private FirebaseUser currentUser;   //로그인 사용자
    private FirebaseFirestore db;   //Firebase database

    private List<String> cafeList = new ArrayList<>();
    private Map<Marker, Integer> markerMap = new HashMap<>();
    private int maxKeywords = 1;
    private List<KeywordViewForMain> keywordViewForMains = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();

        init();

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

        linearLayout_keywords = findViewById(R.id.linearLayout_keywords);
        String[] arr_keyword = getResources().getStringArray(R.array.keywords);
        for (String kw : arr_keyword){
            final KeywordViewForMain keywordView = new KeywordViewForMain(this, new Keyword(kw));
            keywordView.setEventListener(this);
            keywordViewForMains.add(keywordView);
            linearLayout_keywords.addView(keywordView);
        }

        mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        //사이드메뉴 활성화
        isLoggedIn = currentUser != null && !currentUser.isAnonymous();
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
            public void btnGoEditUser() {
                Log.e(TAG, "btnGoEditUser");
                Intent intent = new Intent(mContext, EditUserInfoActivity.class);
                startActivity(intent);
            }

            @Override
            public void btnGoEdit(String cid) {
                Log.e(TAG, "btnGoEdit");
                Intent intent = new Intent(mContext, EditCafeInfoActivity.class);
                intent.putExtra("cid", cid);
                startActivity(intent);
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
            }

            @Override
            public void btnGoSettings(){
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
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

        refreshMap();
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
        if (markerMap.isEmpty())
            drawCafes(naverMap);
        else{
            for (Marker marker : markerMap.keySet())
                if (markerMap.get(marker) == maxKeywords)
                    marker.setMap(naverMap);
        }

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

        naverMap.setCameraPosition(new CameraPosition(new LatLng(lat == 0 ? 37.2788f : lat, lon == 0 ? 127.0437f : lon),
                17, 0, 0));

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
                for(Marker m : markerMap.keySet()){
                    m.setWidth(markerWidth);
                    m.setHeight(markerHeight);
                    m.setCaptionColor(Color.BLACK);
                    m.setCaptionHaloColor(Color.WHITE);
                }
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

        map = naverMap;
    }

    private void drawCafes(final NaverMap map){
        cafeList.clear();
        if (!markerMap.isEmpty()){
            for (Marker marker : markerMap.keySet()){
                marker.setMap(null);
            }
            markerMap.clear();
        }
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

                        if(!cafe.isOpen()){
                            marker.setIcon(OverlayImage.fromResource(R.drawable.pin_closed));
                        } else if(!cafe.isAllowAlarm()){
                            marker.setIcon(OverlayImage.fromResource(R.drawable.pin_unknown));
                        }else{
                            switch(cafe.getTable()){
                                case 0:
                                    marker.setIcon(OverlayImage.fromResource(R.drawable.pin1));
                                    break;
                                case 1:
                                    marker.setIcon(OverlayImage.fromResource(R.drawable.pin2));
                                    break;
                                case 2:
                                    marker.setIcon(OverlayImage.fromResource(R.drawable.pin3));
                                    break;
                                case 3:
                                    marker.setIcon(OverlayImage.fromResource(R.drawable.pin4));
                                    break;
                                case 4:
                                    marker.setIcon(OverlayImage.fromResource(R.drawable.pin5));
                                    break;
                            }
                        }

                        marker.setWidth(markerWidth);
                        marker.setHeight(markerHeight);

                        marker.setCaptionText(cafe.getCafe_name());
                        marker.setCaptionTextSize(16);
                        marker.setCaptionAligns(Align.Top);
                        marker.setCaptionRequestedWidth(200);
                        marker.setCaptionMinZoom(16.0);

                        marker.setHideCollidedSymbols(true);

                        markerMap.put(marker, 1);

                        marker.setOnClickListener(new Overlay.OnClickListener() {
                            @Override
                            public boolean onClick(@NonNull Overlay overlay) {
                                editText_search.setText("");

                                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(cafeLocation)
                                        .animate(CameraAnimation.Easing, 200);
                                map.moveCamera(cameraUpdate);

                                Marker marker = (Marker)overlay;

                                for(Marker m : markerMap.keySet()){
                                    if(marker == m){
                                        m.setWidth(markerWidth/3*4);
                                        m.setHeight(markerHeight/3*4);
                                        m.setCaptionColor(Color.BLUE);
                                        m.setCaptionHaloColor(Color.rgb(200, 255, 200));
                                        m.setZIndex(100);
                                    }
                                    else{
                                        m.setWidth(markerWidth);
                                        m.setHeight(markerHeight);
                                        m.setCaptionColor(Color.BLACK);
                                        m.setCaptionHaloColor(Color.WHITE);
                                        m.setZIndex(1);
                                    }
                                }

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

                    for(Marker m : markerMap.keySet()){
                        if(m.getCaptionText().equals(resultCafe.getCafe_name())){
                            m.setWidth(markerWidth/3*4);
                            m.setHeight(markerHeight/3*4);
                            m.setCaptionColor(Color.BLUE);
                            m.setCaptionHaloColor(Color.rgb(200, 255, 200));
                            m.setZIndex(100);
                        }
                        else{
                            m.setWidth(markerWidth);
                            m.setHeight(markerHeight);
                            m.setCaptionColor(Color.BLACK);
                            m.setCaptionHaloColor(Color.WHITE);
                            m.setZIndex(1);
                        }
                    }
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

//    KeywordView 버튼 카페 Marker 분류 EventListener
    public void showMarkers(final KeywordViewForMain keywordView) {

        db.collection("cafes").whereArrayContains("keywords", keywordView.getKeyword().getName())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    maxKeywords = keywordView.getKeyword().isChosen() ? maxKeywords+1 : maxKeywords-1;

                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        Cafe chosenCafe = documentSnapshot.toObject(Cafe.class);
                        for (Marker marker : markerMap.keySet()){
                            if (marker.getCaptionText().equals(chosenCafe.getCafe_name())){
                                int num_chosen = markerMap.get(marker);
                                markerMap.put(marker, keywordView.getKeyword().isChosen() ? num_chosen+1 : num_chosen-1);
                            }
                        }
                    }

                    int num_markers = 0;
                    Log.d(TAG, "maxKeywords : "+maxKeywords);

                    for (Marker marker : markerMap.keySet()){

                        marker.setWidth(markerWidth);
                        marker.setHeight(markerHeight);
                        marker.setCaptionColor(Color.BLACK);
                        marker.setCaptionHaloColor(Color.WHITE);
                        marker.setZIndex(1);

                        if (markerMap.get(marker) == maxKeywords){
                            marker.setMap(map);
                            num_markers++;
                        }
                        else
                            marker.setMap(null);
                    }

                    if (num_markers == 0){
                        boolean isKeywordOff = true;
                        for (KeywordViewForMain keywordView : keywordViewForMains){
                            if (keywordView.getKeyword().isChosen()){
                                isKeywordOff = false;
                                break;
                            }
                        }
                        if (isKeywordOff) {
                            for(Marker marker : markerMap.keySet())
                                marker.setMap(map);
                        }
                    }

                    bottomLayout.removeAllViews();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

//        startActivity(new Intent(this, ViewPagerActivity.class));
        
        // 초기 페이지 보이기
        if(loginPreferences.getBoolean("firstTime", true))
            startActivity(new Intent(this, ViewPagerActivity.class));
        else{
            //    시작할 때 비회원인 경우 익명 인증하기
            if (currentUser == null){
                mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "Sign in Anonymously : Success");
                            isLoggedIn = false;
                        }else
                            Log.e(TAG, "sign in Anonymously : Failure", task.getException());
                    }
                });
            }

            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        refreshMap();
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

//    지도 보이기
    private void refreshMap(){
        if(map != null){
            drawCafes(map);
        }
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
