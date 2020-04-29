package com.example.cafejabi.objects;

public class Keyword {
    private String name;
    private boolean chosen;

    public Keyword(String name){
        this.name = name;
        chosen = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChosen() {
        return chosen;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }
}
