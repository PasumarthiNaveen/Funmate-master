package com.xitij.tiktuk.POJO.Following;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.xitij.tiktuk.POJO.People;

import java.util.List;

public class FollowingResponse {

    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<People> people = null;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<People> getPeople() {
        return people;
    }

    public void setPeople(List<People> people) {
        this.people = people;
    }

}
