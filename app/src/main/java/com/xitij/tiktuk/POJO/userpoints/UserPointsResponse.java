package com.xitij.tiktuk.POJO.userpoints;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserPointsResponse {

    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("point")
    @Expose
    private Integer point;
    @SerializedName("view_point")
    @Expose
    private String viewPoint;
    @SerializedName("uplode_point")
    @Expose
    private String uplodePoint;
    @SerializedName("share_point")
    @Expose
    private String sharePoint;
    @SerializedName("ref_point")
    @Expose
    private String refPoint;
    @SerializedName("cnv_point")
    @Expose
    private String cnvPoint;
    @SerializedName("cnv_amount")
    @Expose
    private String cnvAmount;

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

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public String getViewPoint() {
        return viewPoint;
    }

    public void setViewPoint(String viewPoint) {
        this.viewPoint = viewPoint;
    }

    public String getUplodePoint() {
        return uplodePoint;
    }

    public void setUplodePoint(String uplodePoint) {
        this.uplodePoint = uplodePoint;
    }

    public String getSharePoint() {
        return sharePoint;
    }

    public void setSharePoint(String sharePoint) {
        this.sharePoint = sharePoint;
    }

    public String getRefPoint() {
        return refPoint;
    }

    public void setRefPoint(String refPoint) {
        this.refPoint = refPoint;
    }

    public String getCnvPoint() {
        return cnvPoint;
    }

    public void setCnvPoint(String cnvPoint) {
        this.cnvPoint = cnvPoint;
    }

    public String getCnvAmount() {
        return cnvAmount;
    }

    public void setCnvAmount(String cnvAmount) {
        this.cnvAmount = cnvAmount;
    }

}