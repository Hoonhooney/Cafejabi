package com.example.cafejabi.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Cafe {
    private String cid;                  //카페 id
    private String uid;                  //카페 관리자 유저
    private String cafe_name;            //카페 이름
    private String address;              //카페 주소
    private double locate_x;             //카페 위치 x좌표
    private double locate_y;             //카페 위치 y좌표
    private int total_table;             //카페에 있는 총 테이블 개수
    private int table;                   //남은 테이블 개수 or 카페 수용인원 퍼센트? 단계
    private Date table_update_time;            //좌석업데이트를 한 시간
    private String cafe_info;                  //카페 정보
    private boolean allowAlarm;                //푸쉬알람서비스 여부
    private int alarm_gap;            //좌석 업데이트 알람 설정 시간
    private List<String> keywords;             //카페 키워드 리스트
    private float grade_cafe;                               //카페 평점
    private List<WorkTime> workTimes;
    private boolean open;

    public Cafe() {
    }

    public Cafe(String cid, String uid, String cafe_name, String address,
                double locate_x, double locate_y, int total_table, boolean allowAlarm, List<String> keywords) {
    this.cid = cid;
    this.uid = uid;
    this.cafe_name = cafe_name;
    this.address = address;
    this.locate_x = locate_x;
    this.locate_y = locate_y;
    this.total_table = total_table;
    this.allowAlarm = allowAlarm;
    this.keywords = keywords;
    this.workTimes = new ArrayList<>();
    this.open = false;
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

    public Date getTable_update_time() {
        return table_update_time;
    }

    public boolean isAllowAlarm() {
        return allowAlarm;
    }

    public void setAllowAlarm(boolean allowAlarm) {
        this.allowAlarm = allowAlarm;
    }

    public void setTable_update_time(Date table_update_time) {
        this.table_update_time = table_update_time;
    }

    public String getCafe_info() {
        return cafe_info;
    }

    public void setCafe_info(String cafe_info) {
        this.cafe_info = cafe_info;
    }

    public int getAlarm_gap() {
        return alarm_gap;
    }

    public void setAlarm_gap(int alarm_gap) {
        this.alarm_gap = alarm_gap;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public float getGrade_cafe() {
        return grade_cafe;
    }

    public void setGrade_cafe(float grade_cafe) {
        this.grade_cafe = grade_cafe;
    }

    public List<WorkTime> getWorkTimes() {
        return workTimes;
    }

    public void setWorkTimes(List<WorkTime> workTimes) {
        this.workTimes = workTimes;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
