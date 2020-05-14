package com.xitij.tiktuk.POJO.Video;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.xitij.tiktuk.POJO.Comment.Recieve.Response.Comment;

import java.util.List;

public class Video {

    @SerializedName("vid")
    @Expose
    private String vid;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("cont_id")
    @Expose
    private String contId;
    @SerializedName("video_url")
    @Expose
    private String videoUrl;
    @SerializedName("thumb_url")
    @Expose
    private String thumbUrl;
    @SerializedName("total_likes")
    @Expose
    private String totalLikes;
    @SerializedName("total_share")
    @Expose
    private String totalShare;
    @SerializedName("is_win")
    @Expose
    private String isWin;
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
    @SerializedName("contest_title")
    @Expose
    private String contestTitle;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("profilepic")
    @Expose
    private String profilepic;
    @SerializedName("total_comment")
    @Expose
    private String totalComment;
    @SerializedName("comment")
    @Expose
    private List<Comment> comment = null;

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(String totalLikes) {
        this.totalLikes = totalLikes;
    }

    public String getTotalShare() {
        return totalShare;
    }

    public void setTotalShare(String totalShare) {
        this.totalShare = totalShare;
    }

    public String getIsWin() {
        return isWin;
    }

    public void setIsWin(String isWin) {
        this.isWin = isWin;
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

    public String getContestTitle() {
        return contestTitle;
    }

    public void setContestTitle(String contestTitle) {
        this.contestTitle = contestTitle;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getTotalComment() {
        return totalComment;
    }

    public void setTotalComment(String totalComment) {
        this.totalComment = totalComment;
    }

    public List<Comment> getComment() {
        return comment;
    }

    public void setComment(List<Comment> comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Video{" +
                "vid='" + vid + '\'' +
                ", userId='" + userId + '\'' +
                ", contId='" + contId + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", thumbUrl='" + thumbUrl + '\'' +
                ", totalLikes='" + totalLikes + '\'' +
                ", totalShare='" + totalShare + '\'' +
                ", isWin='" + isWin + '\'' +
                ", isDeleted='" + isDeleted + '\'' +
                ", creationDatetime='" + creationDatetime + '\'' +
                ", modificationDatetime='" + modificationDatetime + '\'' +
                ", deletionDatetime='" + deletionDatetime + '\'' +
                ", profilepic='" + profilepic + '\'' +
                ", totalComment='" + totalComment + '\'' +
                ", comment=" + comment +
                '}';
    }
}