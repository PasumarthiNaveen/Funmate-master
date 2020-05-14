package com.xitij.tiktuk.View;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.vollink.jiaozivideoplayer.JZDataSource;
import com.vollink.jiaozivideoplayer.Jzvd;
import com.vollink.jiaozivideoplayer.JzvdStd;
import com.xitij.tiktuk.R;

public class VideoPlayer extends JzvdStd {

    public RelativeLayout rlPic;

    public VideoPlayer(Context context) {
        super(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setUp(JZDataSource jzDataSource, int screen) {
        super.setUp(jzDataSource, screen);
    }

    @Override
    public void setUp(String url, String title, int screen) {
        super.setUp(url, title, screen);
        JzvdStd.ACTION_BAR_EXIST = false;
        JzvdStd.TOOL_BAR_EXIST = false;
    }

    @Override
    public void init(Context context) {
        super.init(context);
        rlPic = findViewById(R.id.rl_blur_iv);
    }

    public void setBackgroundBlur(Drawable drawable) {
        if (drawable != null) {
            rlPic.setBackground(drawable);
        }
        else {
            rlPic.setBackgroundColor(Color.BLACK);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_video;
    }

    @Override
    public void onStatePreparing() {
        super.onStatePreparing();
        fullscreenButton.setVisibility(GONE);
        progressBar.setVisibility(GONE);
        bottomProgressBar.setVisibility(GONE);
        currentTimeTextView.setVisibility(GONE);
        totalTimeTextView.setVisibility(GONE);
    }

    @Override
    public void onStatePrepared() {
        fullscreenButton.setVisibility(GONE);
        progressBar.setVisibility(GONE);
        bottomProgressBar.setVisibility(GONE);
        currentTimeTextView.setVisibility(GONE);
        totalTimeTextView.setVisibility(GONE);
    }

    @Override
    public void onStateNormal() {
        super.onStateNormal();
        fullscreenButton.setVisibility(GONE);
        progressBar.setVisibility(GONE);
        bottomProgressBar.setVisibility(GONE);
        currentTimeTextView.setVisibility(GONE);
        totalTimeTextView.setVisibility(GONE);
    }

    @Override
    public void onStateError() {
        super.onStateError();
        fullscreenButton.setVisibility(GONE);
        progressBar.setVisibility(GONE);
        bottomProgressBar.setVisibility(GONE);
        currentTimeTextView.setVisibility(GONE);
        totalTimeTextView.setVisibility(GONE);
    }

    @Override
    public void onStatePause() {
        super.onStatePause();
        fullscreenButton.setVisibility(GONE);
        progressBar.setVisibility(GONE);
        bottomProgressBar.setVisibility(GONE);
        videoCurrentTime.setVisibility(GONE);
        currentTimeTextView.setVisibility(GONE);
        totalTimeTextView.setVisibility(GONE);
    }

    @Override
    public void onStatePlaying() {
        super.onStatePlaying();
        fullscreenButton.setVisibility(GONE);
        progressBar.setVisibility(INVISIBLE);
        bottomProgressBar.setVisibility(INVISIBLE);
        currentTimeTextView.setVisibility(GONE);
        totalTimeTextView.setVisibility(GONE);
    }

    @Override
    public void onStateAutoComplete() {
        super.onStateAutoComplete();
        fullscreenButton.setVisibility(GONE);
        progressBar.setVisibility(GONE);
        bottomProgressBar.setVisibility(GONE);
        currentTimeTextView.setVisibility(GONE);
        totalTimeTextView.setVisibility(GONE);
        startButton.performClick();
    }

    @Override
    public void setProgressAndText(int progress, long position, long duration) {
        progressBar.setVisibility(GONE);
    }

    @Override
    public void setBufferProgress(int bufferProgress) {
        progressBar.setVisibility(GONE);
    }

    @Override
    public void onClickUiToggle() {
        if (bottomContainer.getVisibility() != View.VISIBLE) {
            setSystemTimeAndBattery();
            clarity.setText(jzDataSource.getCurrentKey().toString());
        }
        if (currentState == CURRENT_STATE_PREPARING) {
            changeUiToPreparing();
            if (bottomContainer.getVisibility() == View.VISIBLE) {
            } else {
                setSystemTimeAndBattery();
            }
        } else if (currentState == CURRENT_STATE_PLAYING) {
            startButton.performClick();
        } else if (currentState == CURRENT_STATE_PAUSE) {
            startButton.performClick();
        }
    }

    @Override
    public void dissmissControlView() {
        if (currentState != CURRENT_STATE_NORMAL
                && currentState != CURRENT_STATE_ERROR
                && currentState != CURRENT_STATE_AUTO_COMPLETE) {
            post(new Runnable() {
                @Override
                public void run() {
                    bottomContainer.setVisibility(View.INVISIBLE);
                    topContainer.setVisibility(View.INVISIBLE);
                    startButton.setVisibility(View.INVISIBLE);
                    if (clarityPopWindow != null) {
                        clarityPopWindow.dismiss();
                    }
                }
            });
        }
    }

    /*public static void changeState(int state) {
        currentState = state;
    }*/

}