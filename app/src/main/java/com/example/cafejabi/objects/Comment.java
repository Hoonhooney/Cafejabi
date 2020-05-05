package com.example.cafejabi.objects;

import java.util.Date;

public class Comment {
    private String cid;
    private String uid;        //유저 이름
    private String comment;     //평가 댓글
    private float score;          //평가점수
    private Date update_time;   //코멘트 업데이트 시간

    public Comment() { }

    public Comment(String cid, String uid, String comment, float score, Date update_time)
    {
        this.cid=cid;      //카페데이터에서 이름 꺼내온다.
        this.uid=uid;       //유저데이터에서 이름 꺼내온다.
        this.comment = comment;
        this.score=score;
        this.update_time=update_time;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getScore() {
        return score;
    }

    public Date getUpdate_time(){
        return update_time;
    }
    public void setUpdate_time(Date update_time) {
        this.update_time=update_time;
    }
}