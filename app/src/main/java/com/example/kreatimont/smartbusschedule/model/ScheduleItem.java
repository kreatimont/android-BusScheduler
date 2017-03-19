package com.example.kreatimont.smartbusschedule.model;


import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ScheduleItem extends RealmObject {

    @PrimaryKey
    private int id;

    private int busId;

    @SerializedName("from_date")
    private Date fromDate;
    @SerializedName("to_date")
    private Date toDate;

    @SerializedName("from_time")
    private String fromTime;
    @SerializedName("to_time")
    private String toTime;

    @SerializedName("from_info")
    private String fromInfo;
    @SerializedName("to_info")
    private String toInfo;

    @SerializedName("info")
    private String info;
    @SerializedName("price")
    private int price;

    @SerializedName("from_city")
    private ScheduleCity fromCity;
    @SerializedName("to_city")
    private ScheduleCity toCity;


    public ScheduleItem() {}

    public ScheduleCity getFromCity() {
        return fromCity;
    }

    public void setFromCity(ScheduleCity fromCity) {
        this.fromCity = fromCity;
    }

    public ScheduleCity getToCity() {
        return toCity;
    }

    public void setToCity(ScheduleCity toCity) {
        this.toCity = toCity;
    }

    public ScheduleItem(int id, int busId, Date fromDate, Date toDate, String fromTime, String toTime, String fromInfo, String toInfo, String info, int price, ScheduleCity fromCity, ScheduleCity toCity) {
        this.id = id;
        this.busId = busId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.fromInfo = fromInfo;
        this.toInfo = toInfo;
        this.info = info;
        this.price = price;
        this.fromCity = fromCity;
        this.toCity = toCity;

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

    @Override
    public String toString() {
        return "[id: " + this.id + "," +
                " from_date: " + this.fromDate + "," +
                " to_date: " + this.toDate + "," +
                " from_time: " + this.fromTime + "," +
                " to_time: " + this.toTime+ "," +
                " from_info: " + this.fromInfo + "," +
                " to_info: " + this.toInfo+ "," +
                " from_city: " + this.fromCity.toString() + "," +
                " to_city: " + this.toCity.toString() + "," +
                " bus_id: " + this.busId + "," +
                " info: " + this.info + "," +
                " price: " + this.price+ "]";
    }
}
