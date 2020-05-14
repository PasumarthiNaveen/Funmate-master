package com.xitij.tiktuk.POJO.Username.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UsernameRequest {

    @SerializedName("service")
    @Expose
    private String service;
    @SerializedName("request")
    @Expose
    private Request request;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

}
