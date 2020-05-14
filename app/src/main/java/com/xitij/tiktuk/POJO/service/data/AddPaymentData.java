package com.xitij.tiktuk.POJO.service.data;

import com.xitij.tiktuk.POJO.service.Data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddPaymentData extends Data{

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("request_to")
    @Expose
    private String requestTo;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("point")
    @Expose
    private String point;
    @SerializedName("status")
    @Expose
    private String status;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRequestTo() {
        return requestTo;
    }

    public void setRequestTo(String requestTo) {
        this.requestTo = requestTo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}