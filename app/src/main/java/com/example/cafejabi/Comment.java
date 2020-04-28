package com.example.cafejabi;

public class Comment {
    private Cafe cafe;
    private UserInfo user;        //유저 이름
    private String comment;     //평가 댓글
    private int score;          //평가점수


    public Comment() { }

    public Comment(Cafe cafe, UserInfo user, String comment, int score)
    {
        this.cafe=cafe;      //카페데이터에서 이름 꺼내온다.
        this.user=user;       //유저데이터에서 이름 꺼내온다.
        this.score=score;
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
    

}