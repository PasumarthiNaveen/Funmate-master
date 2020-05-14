package com.xitij.tiktuk.POJO.Video.Request.Following;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("page")
    @Expose
    private Integer page;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    @Override
    public String toString() {
        return "Data{" +
                "userId='" + userId + '\'' +
                ", page=" + page +
                '}';
    }
}