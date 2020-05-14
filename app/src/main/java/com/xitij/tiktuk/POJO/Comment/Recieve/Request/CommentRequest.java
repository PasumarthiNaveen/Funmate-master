package com.xitij.tiktuk.POJO.Comment.Recieve.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.xitij.tiktuk.POJO.Auth;

public class CommentRequest {

    @SerializedName("auth")
    @Expose
    private Auth auth;
    @SerializedName("request")
    @Expose
    private Request request;
    @SerializedName("service")
    @Expose
    private String service;

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

}