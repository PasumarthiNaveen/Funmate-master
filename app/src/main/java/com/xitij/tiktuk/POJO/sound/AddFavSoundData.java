package com.xitij.tiktuk.POJO.sound;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.xitij.tiktuk.POJO.service.Data;

public class AddFavSoundData extends Data {

    @SerializedName("fb_id")
    @Expose
    private String fbId;
    @SerializedName("sound_id")
    @Expose
    private String soundId;

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getSoundId() {
        return soundId;
    }

    public void setSoundId(String soundId) {
        this.soundId = soundId;
    }

}
