package com.example.cafejabi.objects;

public class WorkTime {
    private boolean open, working24h;
    private String dayOfWeek;
    private String openAt;
    private String closeAt;

    public WorkTime(String dayOfWeek, int openHour, int openMin, int closeHour, int closeMin){
        this.open = false;
        this.working24h = false;
        this.dayOfWeek = dayOfWeek;
        this.openAt = openHour+":"+openMin;
        this.closeAt = closeHour+":"+closeMin;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isWorking24h() {
        return working24h;
    }

    public void setWorking24h(boolean working24h) {
        this.working24h = working24h;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getOpenAt() {
        return openAt;
    }

    public void setOpenAt(String openAt) {
        this.openAt = openAt;
    }

    public String getCloseAt() {
        return closeAt;
    }

    public void setCloseAt(String closeAt) {
        this.closeAt = closeAt;
    }
}
