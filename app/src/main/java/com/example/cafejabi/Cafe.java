package com.example.cafejabi;

public class Cafe {
    private String cid;                  //카페 관리자 유저
    private String cafe_name;            //카페 이름
    private double locate_x;             //카페 위치 x좌표
    private double locate_y;             //카페 위치 y좌표
    private int total_table;             //카페에 있는 총 테이블 개수
    private int table;                   //남은 테이블 개수 or 카페 수용인원 퍼센트? 단계
    private long table_update_time;            //좌석업데이트를 한 시간
    private String cafe_info;                  //카페 정보
    private int open_time;                     //카페 여는 시간
    private int close_time;                    //카페 닫는 시간
    private long update_time_alarm;            //좌석 업데이트 알람 설정 시간

    public Cafe() {
    }

    public Cafe(String cid, String cafe_name, double locate_x, double locate_y, int total_table, int open_time, int close_time, long update_time_alarm) {
    this.cid = cid;
    this.cafe_name = cafe_name;
    this.locate_x = locate_x;
    this.locate_y = locate_y;
    this.total_table = total_table;
    this.open_time=open_time;
    this.close_time=close_time;
    this.update_time_alarm=update_time_alarm;
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
}
