package com.xitij.tiktuk.POJO.Comment.Recieve.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("vid")
    @Expose
    private String vid;
    @SerializedName("page")
    @Expose
    private Integer page;

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

}