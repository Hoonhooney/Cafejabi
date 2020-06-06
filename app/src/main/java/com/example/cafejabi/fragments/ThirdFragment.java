package com.example.cafejabi.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cafejabi.R;

import static android.content.Context.MODE_PRIVATE;


public class ThirdFragment extends Fragment {

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third, container, false);

        view.findViewById(R.id.button_end_viewpageractivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //다음 접속부터 해당 액티비티 안보이게
                SharedPreferences loginPreferences = getContext().getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = loginPreferences.edit();
                editor.putBoolean("firstTime", false);
                editor.apply();

                getActivity().finish();
            }
        });
        return view;
    }
}
