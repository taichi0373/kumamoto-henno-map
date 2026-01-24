package com.example.back.dto;

public class RouteRequest {
    private String startLocation;
    private String endLocation;
    private String transportMode;
    private String timeSelect;
    private String date;
    private String time;
    private boolean arriveBy;

    public RouteRequest() {}

    public RouteRequest(String startLocation, String endLocation, String transportMode, String timeSelect, String date, String time, boolean arriveBy) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.transportMode = transportMode;
        this.timeSelect = timeSelect;
        this.date = date;
        this.time = time;
        this.arriveBy = arriveBy;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    public String getTimeSelect() {
        return timeSelect;
    }

    public void setTimeSelect(String timeSelect) {
        this.timeSelect = timeSelect;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isArriveBy() {
        return arriveBy;
    }

    public void setArriveBy(boolean arriveBy) {
        this.arriveBy = arriveBy;
    }

    @Override
    public String toString() {
        return "RouteRequest{" +
                "startLocation='" + startLocation + '\'' +
                ", endLocation='" + endLocation + '\'' +
                ", transportMode='" + transportMode + '\'' +
                ", timeSelect='" + timeSelect + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", arriveBy=" + arriveBy +
                '}';
    }
}
