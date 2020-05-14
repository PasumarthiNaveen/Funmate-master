package com.xitij.tiktuk.POJO.LogIn.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("device_id")
    @Expose
    private String deviceId;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("fbid")
    @Expose
    private String fbid;
    @SerializedName("logintype")
    @Expose
    private String logintype;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("platform")
    @Expose
    private String platform;
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("email")
    @Expose
    private String email;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFbid() {
        return fbid;
    }

    public void setFbid(String fbid) {
        this.fbid = fbid;
    }

    public String getLogintype() {
        return logintype;
    }

    public void setLogintype(String logintype) {
        this.logintype = logintype;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}