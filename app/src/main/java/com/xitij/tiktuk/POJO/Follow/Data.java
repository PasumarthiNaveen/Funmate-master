package com.xitij.tiktuk.POJO.Follow;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("fuser_id")
    @Expose
    private String fuserId;
    @SerializedName("type")
    @Expose
    private String type;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFuserId() {
        return fuserId;
    }

    public void setFuserId(String fuserId) {
        this.fuserId = fuserId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
