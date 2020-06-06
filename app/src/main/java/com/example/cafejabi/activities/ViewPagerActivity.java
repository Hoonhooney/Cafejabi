package com.example.cafejabi.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.cafejabi.fragments.FirstFragment;
import com.example.cafejabi.R;
import com.example.cafejabi.fragments.SecondFragment;
import com.example.cafejabi.fragments.ThirdFragment;

import me.relex.circleindicator.CircleIndicator;


public class ViewPagerActivity extends AppCompatActivity {

    private ViewPager mViewpager;
    FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        mViewpager = (ViewPager) findViewById(R.id.vpPager);

        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewpager.setAdapter(adapter);

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mViewpager);

    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static final int NUM_ITEMS = 3;

        MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FirstFragment();
                case 1:
                    return new SecondFragment();
                case 2:
                    return new ThirdFragment();
                default:
                    return null;
            }
        }

    }
}
