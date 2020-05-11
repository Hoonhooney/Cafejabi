package com.example.cafejabi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.example.cafejabi.R;
import com.example.cafejabi.fragments.CafeListFragment;
import com.google.android.material.tabs.TabLayout;

public class CafeListActivity extends AppCompatActivity {
    private static final String TAG = "CafeListActivity";
    private static final int CODE_VISITED_CAFE = 1, CODE_LIKING_CAFE = 2;

    private TabLayout tabLayout_cafeList;
    private ViewPager viewPager_cafeList;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_list);

        init();
    }

    private void init(){
        tabLayout_cafeList = findViewById(R.id.tabLayout_cafe_list);

        viewPager_cafeList = findViewById(R.id.viewPager_cafe_list);

        tabLayout_cafeList.addTab(tabLayout_cafeList.newTab());
        tabLayout_cafeList.addTab(tabLayout_cafeList.newTab());

        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout_cafeList.getTabCount());
        viewPager_cafeList.setAdapter(pagerAdapter);

        tabLayout_cafeList.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager_cafeList.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout_cafeList.setupWithViewPager(viewPager_cafeList);

        viewPager_cafeList.setCurrentItem(getIntent().getIntExtra("CODE", 1)-1);
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int _numOfTabs;

        public PagerAdapter(FragmentManager fm, int numOfTabs) {
            super(fm);
            this._numOfTabs = numOfTabs;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return position == 0 ? "방문한 카페" : "찜 카페";
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    CafeListFragment tab1 = new CafeListFragment(CODE_VISITED_CAFE);
                    return tab1;
                case 1:
                    CafeListFragment tab2 = new CafeListFragment(CODE_LIKING_CAFE);
                    return tab2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return _numOfTabs;
        }
    }
}
