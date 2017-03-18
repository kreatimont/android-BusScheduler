package com.example.kreatimont.smartbusschedule.model;


import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ScheduleItem extends RealmObject {

    @PrimaryKey
    private int id;

    private int busId;

    private String fromCityString;
    private String toCityString;

    private int fromCityHighlight;
    private int toCityHighlight;

    private Date fromDate;
    private Date toDate;

    private String fromTime;
    private String toTime;

    private String fromInfo;
    private String toInfo;

    private String info;
    private int price;


    public ScheduleItem() {

    }

    public ScheduleItem(int id, int busId, String fromCityString, String toCityString, int fromCityHighlight, int toCityHighlight, Date fromDate, Date toDate, String fromTime, String toTime, String fromInfo, String toInfo, String info, int price) {
        this.id = id;
        this.busId = busId;
        this.fromCityString = fromCityString;
        this.toCityString = toCityString;
        this.fromCityHighlight = fromCityHighlight;
        this.toCityHighlight = toCityHighlight;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.fromInfo = fromInfo;
        this.toInfo = toInfo;
        this.info = info;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public String getFromCityString() {
        return fromCityString;
    }

    public void setFromCityString(String fromCityString) {
        this.fromCityString = fromCityString;
    }

    public String getToCityString() {
        return toCityString;
    }

    public void setToCityString(String toCityString) {
        this.toCityString = toCityString;
    }

    public int getFromCityHighlight() {
        return fromCityHighlight;
    }

    public void setFromCityHighlight(int fromCityHighlight) {
        this.fromCityHighlight = fromCityHighlight;
    }

    public int getToCityHighlight() {
        return toCityHighlight;
    }

    public void setToCityHighlight(int toCityHighlight) {
        this.toCityHighlight = toCityHighlight;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getFromInfo() {
        return fromInfo;
    }

    public void setFromInfo(String fromInfo) {
        this.fromInfo = fromInfo;
    }

    public String getToInfo() {
        return toInfo;
    }

    public void setToInfo(String toInfo) {
        this.toInfo = toInfo;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
