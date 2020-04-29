package com.example.cafejabi.objects;

import java.util.List;

public class Cafe {
    private String cid;                  //카페 관리자 유저
    private String cafe_name;            //카페 이름
    private String address;              //카페 주소
    private double locate_x;             //카페 위치 x좌표
    private double locate_y;             //카페 위치 y좌표
    private int total_table;             //카페에 있는 총 테이블 개수
    private int table;                   //남은 테이블 개수 or 카페 수용인원 퍼센트? 단계
    private long table_update_time;            //좌석업데이트를 한 시간
    private String cafe_info;                  //카페 정보
    private boolean is24Working;               //24시간 영업 여부
    private boolean allowAlarm;                //푸쉬알람서비스 여부
    private int open_time;                     //카페 여는 시간
    private int close_time;                    //카페 닫는 시간
    private long update_time_alarm;            //좌석 업데이트 알람 설정 시간
    private List<String> keywords;             //카페 키워드 리스트
    private Comment comment_list;                    //카페에 대한 평가

    public Cafe() {
    }

    public Cafe(String cid, String cafe_name, String address,
                double locate_x, double locate_y, int total_table, boolean is24Working, boolean allowAlarm, List<String> keywords) {
    this.cid = cid;
    this.cafe_name = cafe_name;
    this.address = address;
    this.locate_x = locate_x;
    this.locate_y = locate_y;
    this.total_table = total_table;
    this.is24Working = is24Working;
    this.allowAlarm = allowAlarm;
    this.keywords = keywords;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCafe_name() {
        return cafe_name;
    }

    public void setCafe_name(String cafe_name) {
        this.cafe_name = cafe_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLocate_x() {
        return locate_x;
    }

    public void setLocate_x(double locate_x) {
        this.locate_x = locate_x;
    }

    public double getLocate_y() {
        return locate_y;
    }

    public void setLocate_y(double locate_y) {
        this.locate_y = locate_y;
    }

    public int getTotal_table() {
        return total_table;
    }

    public void setTotal_table(int total_table) {
        this.total_table = total_table;
    }

    public int getTable() {
        return table;
    }

    public void setTable(int table) {
        this.table = table;
    }

    public long getTable_update_time() {
        return table_update_time;
    }

    public boolean isIs24Working() {
        return is24Working;
    }

    public void setIs24Working(boolean is24Working) {
        this.is24Working = is24Working;
    }

    public boolean isAllowAlarm() {
        return allowAlarm;
    }

    public void setAllowAlarm(boolean allowAlarm) {
        this.allowAlarm = allowAlarm;
    }

    public void setTable_update_time(long table_update_time) {
        this.table_update_time = table_update_time;
    }

    public String getCafe_info() {
        return cafe_info;
    }

    public void setCafe_info(String cafe_info) {
        this.cafe_info = cafe_info;
    }

    public int getOpen_time() {
        return open_time;
    }

    public void setOpen_time(int open_time) {
        this.open_time = open_time;
    }

    public int getClose_time() {
        return close_time;
    }

    public void setClose_time(int close_time) {
        this.close_time = close_time;
    }

    public long getUpdate_time_alarm() {
        return update_time_alarm;
    }

    public void setUpdate_time_alarm(long update_time_alarm) {
        this.update_time_alarm = update_time_alarm;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public Comment getComment() {
        return comment_list;
    }

    public void setComment(Comment comment) {
        this.comment_list = comment;
    }
}
