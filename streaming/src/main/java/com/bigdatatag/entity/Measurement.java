package com.bigdatatag.entity;

import java.io.Serializable;

/**
 * Created by safak on 6/8/17.
 */
public class Measurement implements Serializable {
    private static final long serialVersionUID = -2983675472826134176L;

    private String CapturedTime;
    private Double Latitude;
    private Double Longitude;
    private Double Value;
    private String Unit;
    private String LocationName;
    private String DeviceID;
    private String MD5Sum;
    private Double Height;
    private String Surface;
    private String Radiation;
    private String UploadedTime;
    private String LoaderID;

    public String getCapturedTime() {
        return CapturedTime;
    }

    public void setCapturedTime(String capturedTime) {
        CapturedTime = capturedTime;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public Double getValue() {
        return Value;
    }

    public void setValue(Double value) {
        Value = value;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        LocationName = locationName;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }

    public String getMD5Sum() {
        return MD5Sum;
    }

    public void setMD5Sum(String MD5Sum) {
        this.MD5Sum = MD5Sum;
    }

    public Double getHeight() {
        return Height;
    }

    public void setHeight(Double height) {
        Height = height;
    }

    public String getSurface() {
        return Surface;
    }

    public void setSurface(String surface) {
        Surface = surface;
    }

    public String getRadiation() {
        return Radiation;
    }

    public void setRadiation(String radiation) {
        Radiation = radiation;
    }

    public String getUploadedTime() {
        return UploadedTime;
    }

    public void setUploadedTime(String uploadedTime) {
        UploadedTime = uploadedTime;
    }

    public String getLoaderID() {
        return LoaderID;
    }

    public void setLoaderID(String loaderID) {
        LoaderID = loaderID;
    }

    @Override
    public String toString() {
        return "Measurement{"+getCapturedTime() +"}";
    }
}

