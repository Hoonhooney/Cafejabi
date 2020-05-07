package com.example.cafejabi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.cafejabi.objects.Comment;
import com.example.cafejabi.objects.UserInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends BaseAdapter {
    private Context mContext;
    private List<Comment> commentList;

    private TextView textView_comment, textView_nickname, textView_created_at, textView_rating;
    private RatingBar ratingBar_rating;

    public CommentAdapter(Context context){
        this.mContext = context;
        this.commentList = new ArrayList<>();
    }

    public float getAverage(){
        float sum = 0.0f;
        for(Comment comment : commentList){
            sum += comment.getScore();
        }

        return sum / (float)commentList.size();
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void addItem(Comment comment){
        commentList.add(0, comment);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Comment comment = commentList.get(position);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_comment, parent, false);
        }

        ratingBar_rating = view.findViewById(R.id.ratingBar_item_comment);
        textView_rating = view.findViewById(R.id.textView_item_comment_rating);
        textView_comment = view.findViewById(R.id.textView_item_comment_comment);
        textView_nickname = view.findViewById(R.id.textView_item_comment_nickname);
        textView_created_at = view.findViewById(R.id.textView_item_comment_created_at);

        ratingBar_rating.setRating(comment.getScore());
        textView_rating.setText(comment.getScore()+"/5");

        textView_comment.setText(comment.getComment());

        textView_nickname.setText(comment.getUser_nickname());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일");
        textView_created_at.setText(format.format(comment.getUpdate_time()));

        return view;
    }
}
