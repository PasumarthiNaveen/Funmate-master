package com.xitij.tiktuk.POJO.service.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.xitij.tiktuk.POJO.service.Data;

public class VideoCountData extends Data {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("cont_id")
    @Expose
    private String contId;
    @SerializedName("video_id")
    @Expose
    private String videoId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContId() {
        return contId;
    }

    public void setContId(String contId) {
        this.contId = contId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

}
