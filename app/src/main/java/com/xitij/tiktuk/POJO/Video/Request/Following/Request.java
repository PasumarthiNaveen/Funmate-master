package com.xitij.tiktuk.POJO.Video.Request.Following;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Request {
    @SerializedName("data")
    @Expose
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Request{" +
                "data=" + data.toString() +
                '}';
    }
}
