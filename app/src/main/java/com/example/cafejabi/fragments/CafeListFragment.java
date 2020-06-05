package com.example.cafejabi.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cafejabi.adapters.CafeAdapter;
import com.example.cafejabi.R;
import com.example.cafejabi.objects.Cafe;
import com.example.cafejabi.objects.UserInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CafeListFragment extends Fragment {
    private static final String TAG = "CafeListFragment";
    private static final int CODE_VISITED_CAFE = 1, CODE_LIKING_CAFE = 2;

    private TextView textView_cafe_list_empty;
    private ListView listView_cafe_list;
    private List<String> cafeList;

    private CafeAdapter cafeAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private int code;

    public CafeListFragment(int code) {
        this.code = code;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cafeList = new ArrayList<>();
        cafeAdapter = new CafeAdapter(getContext());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        db.collection("users").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, "get userInfo : success");

                UserInfo user = documentSnapshot.toObject(UserInfo.class);

                assert user != null;
                if(code == CODE_VISITED_CAFE){
                    cafeList = user.getVisitedCafeList();
                }else if(code == CODE_LIKING_CAFE){
                    cafeList = user.getLikingCafeList();
                }

                for(String cid : cafeList){
                    db.collection("cafes").document(cid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Cafe cafe = documentSnapshot.toObject(Cafe.class);
                            cafeAdapter.addItem(cafe);
                            cafeAdapter.notifyDataSetChanged();
                        }
                    });
                }

                if(cafeList.isEmpty())
                    textView_cafe_list_empty.setVisibility(View.VISIBLE);
                else
                    textView_cafe_list_empty.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "get userInfo : failure", e);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_cafe_list, container, false);

        textView_cafe_list_empty = view.findViewById(R.id.textView_cafe_list_empty);
        listView_cafe_list = view.findViewById(R.id.listView_cafe_list);

        listView_cafe_list.setAdapter(cafeAdapter);

        return view;
    }
}
