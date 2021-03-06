package com.example.cafejabi.objects;

import java.util.Date;

public class Comment {
    private String id;
    private String cid;
    private String user_nickname;        //유저 이름
    private String comment;     //평가 댓글
    private float score;          //평가점수
    private Date update_time;   //코멘트 업데이트 시간

    public Comment() { }

    public Comment(String id, String cid, String user_nickname, String comment, float score, Date update_time)
    {
        this.id = id;
        this.cid=cid;      //카페데이터에서 이름 꺼내온다.
        this.user_nickname = user_nickname;       //유저데이터에서 이름 꺼내온다.
        this.comment = comment;
        this.score=score;
        this.update_time=update_time;
    }

    public String getId(){return id;}

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
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