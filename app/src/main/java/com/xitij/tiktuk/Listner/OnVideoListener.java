package com.xitij.tiktuk.Listner;

import com.xitij.tiktuk.POJO.Video.Video;

import java.util.List;

public interface OnVideoListener {
    void onVideoSelected(int position, List<Video> videos);
}
