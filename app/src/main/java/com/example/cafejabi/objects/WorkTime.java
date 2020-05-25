package com.example.cafejabi.objects;

public class WorkTime {
    private boolean open, working24h;
    private String dayOfWeek;
    private String openAt;
    private String closedAt;

    public WorkTime(String dayOfWeek){
        this.open = false;
        this.working24h = false;
        this.dayOfWeek = dayOfWeek;
        this.openAt = 7+":"+0;
        this.closedAt = 23+":"+0;
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

    public void setOpenAt(int openHour, int openMin) {
        this.openAt = openHour+":"+openMin;
    }

    public String getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(int closedHour, int closedMin) {
        this.closedAt = closedHour+":"+closedMin;
    }
}
