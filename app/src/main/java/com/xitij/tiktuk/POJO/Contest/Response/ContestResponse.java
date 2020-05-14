package com.xitij.tiktuk.POJO.Contest.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContestResponse {

    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("contest_path")
    @Expose
    private String contestPath;
    @SerializedName("data")
    @Expose
    private List<Contest> data = null;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getContestPath() {
        return contestPath;
    }

    public void setContestPath(String contestPath) {
        this.contestPath = contestPath;
    }

    public List<Contest> getData() {
        return data;
    }

    public void setData(List<Contest> data) {
        this.data = data;
    }

}
