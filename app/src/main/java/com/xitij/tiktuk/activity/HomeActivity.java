package com.xitij.tiktuk.activity;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vollink.jiaozivideoplayer.Jzvd;
import com.xitij.tiktuk.Fragment.FollowingVideoFragment;
import com.xitij.tiktuk.Fragment.LiveVideoFragment;
import com.xitij.tiktuk.Fragment.PopularVideoFragment;
import com.xitij.tiktuk.Fragment.SharedVideoFragment;
import com.xitij.tiktuk.R;

public class HomeActivity extends AppCompatActivity implements TabLayout.BaseOnTabSelectedListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private boolean isShared = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        isShared = getIntent().getBooleanExtra("shared",false);
        if (isShared) {
            Log.i(TAG, "onCreate: shared true");

            Bundle bundle = new Bundle();
            bundle.putString("video",getIntent().getStringExtra("video"));

            SharedVideoFragment fragment = new SharedVideoFragment();
            fragment.setArguments(bundle);

            replaceFragment(fragment);

        }
        else {
            Log.i(TAG, "onCreate: shared false");
            initView();
        }

    }

    private void initView() {
        TabLayout tlVideo = findViewById(R.id.tl_video_type);

        tlVideo.addOnTabSelectedListener(this);
        tlVideo.setTabMode(TabLayout.MODE_SCROLLABLE);
        tlVideo.addTab(tlVideo.newTab().setText("Popular"), true);
//        tlVideo.addTab(tlVideo.newTab().setText("Live\nContest"),true);
        tlVideo.addTab(tlVideo.newTab().setText("Following"));


        //Add divider in between tabs..
        /*LinearLayout linearLayout = (LinearLayout) tlVideo.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.DKGRAY);
        drawable.setSize(1, 10);
        linearLayout.setDividerPadding(10);
        linearLayout.setDividerDrawable(drawable);*/
        for (int i = 0; i < tlVideo.getTabCount(); i++) {
            TabLayout.Tab tab = tlVideo.getTabAt(i);
            LinearLayout relativeLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.tab_background, tlVideo, false);

            if (i==2) {
                View view = relativeLayout.findViewById(R.id.tabDivider);
                view.setVisibility(View.GONE);
            }

            TextView tabTextView = relativeLayout.findViewById(R.id.tab_title);
            tabTextView.setText(tab.getText());
            tab.setCustomView(relativeLayout);

        }

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        setCurrentTabFragment(tab.getPosition());
        Jzvd.releaseAllVideos();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void setCurrentTabFragment(int position) {
        switch (position)
        {
            case 0 :
                replaceFragment(new PopularVideoFragment());
                break;
            case 1:
                replaceFragment(new LiveVideoFragment());
                break;
            case 2:
                replaceFragment(new FollowingVideoFragment());
                break;
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.linlayFragments, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        if (isShared) {
            initView();
            isShared = false;
        }
        else {
            super.onBackPressed();
        }
    }
}
