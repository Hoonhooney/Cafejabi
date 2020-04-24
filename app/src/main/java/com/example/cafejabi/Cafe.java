package com.example.cafejabi;

public class Cafe {
    private String cid;
    private double locate_x;
    private double locate_y;
    private int total_table;
    private int table;
    private long update_time;
    private String cafe_info;
    private int open_time;
    private int close_time;
    private long update_time_alarm;

    public Cafe() {
    }

    public Cafe(String cid, double locate_x, double locate_y, int total_table, int open_time, int close_time, long update_time_alarm) {
    this.cid = cid;
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

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
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
