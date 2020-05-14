package com.xitij.tiktuk.POJO.Username;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UsernameResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("success")
    @Expose
    private Integer success;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

}
