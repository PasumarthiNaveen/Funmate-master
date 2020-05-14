package com.xitij.tiktuk.POJO.Banner.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BannerResponse {

    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("banner_path")
    @Expose
    private String bannerPath;
    @SerializedName("contest_open")
    @Expose
    private ContestOpen contestOpen;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getBannerPath() {
        return bannerPath;
    }

    public void setBannerPath(String bannerPath) {
        this.bannerPath = bannerPath;
    }

    public ContestOpen getContestOpen() {
        return contestOpen;
    }

    public void setContestOpen(ContestOpen contestOpen) {
        this.contestOpen = contestOpen;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

}
