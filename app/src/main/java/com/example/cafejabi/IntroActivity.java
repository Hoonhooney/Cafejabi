package com.example.cafejabi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class IntroActivity extends PagerAdapter {

    private int[] images = {R.drawable.page1, R.drawable.page2, R.drawable.page3 };
    private LayoutInflater inflater;
    private Context context;



    public IntroActivity(Context context)
    {
        this.context = context;
    }


    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return false;
//        return view == ((LinearLayout) object);
    }

    public Object instantiateItem(ViewGroup container, int position)
    {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.activity_intro, container, false);
        ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
//        TextView textView = (TextView) v.findViewById(R.id.textView);
        imageView.setImageResource(images[position]);
        container.addView(v);
        return v;
    }

    public void destroyitem(ViewGroup container, int position, Object object)
    {
        container.invalidate();
    }
}
