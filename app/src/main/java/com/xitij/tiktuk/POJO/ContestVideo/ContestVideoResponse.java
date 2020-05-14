package com.xitij.tiktuk.POJO.ContestVideo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.xitij.tiktuk.POJO.Video.Video;

import java.util.List;

public class ContestVideoResponse {

    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("videos_url")
    @Expose
    private String videosUrl;
    @SerializedName("profile_url")
    @Expose
    private String profileUrl;
    @SerializedName("data")
    @Expose
    private List<Video> data = null;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getVideosUrl() {
        return videosUrl;
    }

    public void setVideosUrl(String videosUrl) {
        this.videosUrl = videosUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public List<Video> getData() {
        return data;
    }

    public void setData(List<Video> data) {
        this.data = data;
    }

}
