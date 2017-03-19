package com.example.kreatimont.smartbusschedule.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ScheduleCity extends RealmObject {

    @PrimaryKey
    private int id;
    private int highlight;
    private String name;

    public ScheduleCity() {}

    public ScheduleCity(int highlight, int id, String name) {
        this.highlight = highlight;
        this.id = id;
        this.name = name;
    }

    public int getHighlight() {
        return highlight;
    }

    public void setHighlight(int highlight) {
        this.highlight = highlight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "[id: " + this.id + "," +
                " name: " + this.name+ "," +
                " highlight: " + this.highlight + "]";
    }
}
