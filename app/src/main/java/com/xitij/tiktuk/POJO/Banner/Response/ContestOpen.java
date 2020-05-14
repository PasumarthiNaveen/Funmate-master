package com.xitij.tiktuk.POJO.Banner.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContestOpen {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("contest_title")
    @Expose
    private String contestTitle;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("open_close")
    @Expose
    private String openClose;
    @SerializedName("open_date")
    @Expose
    private String openDate;
    @SerializedName("close_date")
    @Expose
    private String closeDate;
    @SerializedName("pay_type")
    @Expose
    private String payType;
    @SerializedName("pay_amount")
    @Expose
    private String payAmount;
    @SerializedName("point")
    @Expose
    private String point;
    @SerializedName("is_deleted")
    @Expose
    private String isDeleted;
    @SerializedName("creation_datetime")
    @Expose
    private String creationDatetime;
    @SerializedName("modification_datetime")
    @Expose
    private String modificationDatetime;
    @SerializedName("deletion_datetime")
    @Expose
    private String deletionDatetime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContestTitle() {
        return contestTitle;
    }

    public void setContestTitle(String contestTitle) {
        this.contestTitle = contestTitle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOpenClose() {
        return openClose;
    }

    public void setOpenClose(String openClose) {
        this.openClose = openClose;
    }

    public String getOpenDate() {
        return openDate;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public String getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(String closeDate) {
        this.closeDate = closeDate;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getCreationDatetime() {
        return creationDatetime;
    }

    public void setCreationDatetime(String creationDatetime) {
        this.creationDatetime = creationDatetime;
    }

    public String getModificationDatetime() {
        return modificationDatetime;
    }

    public void setModificationDatetime(String modificationDatetime) {
        this.modificationDatetime = modificationDatetime;
    }

    public String getDeletionDatetime() {
        return deletionDatetime;
    }

    public void setDeletionDatetime(String deletionDatetime) {
        this.deletionDatetime = deletionDatetime;
    }

}
