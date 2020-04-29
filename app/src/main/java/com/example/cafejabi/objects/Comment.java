package com.example.cafejabi.objects;

public class Comment {
    private Cafe cafe;
    private UserInfo user;        //유저 이름
    private String comment;     //평가 댓글
    private int score;          //평가점수
    private long update_time;   //코멘트 업데이트 시간

    public Comment() { }

    public Comment(Cafe cafe, UserInfo user, String comment, int score, long update_time)
    {
        this.cafe=cafe;      //카페데이터에서 이름 꺼내온다.
        this.user=user;       //유저데이터에서 이름 꺼내온다.
        this.score=score;
        this.update_time=update_time;
    }

    public Cafe getCafe() {
        return cafe;
    }

    public void setCafe(Cafe cafe) {
        this.cafe = cafe;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    public long getUpdate_time(){
        return update_time;
    }
    public void setUpdate_time(long update_time) {
        this.update_time=update_time;
    }
}