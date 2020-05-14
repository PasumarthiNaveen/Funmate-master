package com.xitij.tiktuk.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.xitij.tiktuk.FourChamp;
import com.xitij.tiktuk.Fragment.FollowingFragment;
import com.xitij.tiktuk.Fragment.SearchFragment;
import com.xitij.tiktuk.Fragment.UserFragment;
import com.xitij.tiktuk.Fragment.VideoPostFragment;
import com.xitij.tiktuk.Listner.OnFollowerListener;
import com.xitij.tiktuk.Listner.OnPeopleListener;
import com.xitij.tiktuk.Listner.OnVideoListener;
import com.xitij.tiktuk.POJO.People;
import com.xitij.tiktuk.POJO.Video.Video;
import com.xitij.tiktuk.R;

import java.util.List;

public class SearchActivity extends AppCompatActivity implements OnPeopleListener, OnFollowerListener, OnVideoListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SearchFragment fragment = new SearchFragment();
        fragment.setOnSearchFragmentListener(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.add(R.id.frag_search_activty, fragment,"SearchFragment").commit();

    }

    @Override
    public void onListClicked(People people) {
        UserFragment fragment = new UserFragment();
        fragment.setOnFollowerListener(this);
        fragment.setOnVideoListener(this);
        Bundle bundle = new Bundle();
        bundle.putString("userId",people.getUserId());
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        ft.addToBackStack("SearchFragment");
        ft.add(R.id.frag_search_activty, fragment, "UserFragment").commit();

        changeTabColor(false);
    }

    @Override
    public void onFollowerSelected(String userId) {
        FollowingFragment fragment = new FollowingFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userId",userId);
        fragment.setArguments(bundle);
        fragment.setOnPeopleListener(this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        ft.addToBackStack("SearchFragment");
        ft.add(R.id.frag_search_activty, fragment, "FollowingFragment").commit();

        changeTabColor(false);
    }

    @Override
    public void onVideoSelected(int position, List<Video> videos) {
        VideoPostFragment fragment = new VideoPostFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position",position);
        fragment.setArguments(bundle);
        fragment.setVideos(videos);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        ft.addToBackStack("SearchFragment");
        ft.add(R.id.frag_search_activty, fragment, "VideoPostFragment").commit();

        changeTabColor(true);
    }

    private void changeTabColor(boolean transparent) {
        Intent intent = new Intent("change_color_of_tab");
        intent.putExtra("transparent",transparent);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm != null) {
            int backStackCount = fm.getBackStackEntryCount();
            if (backStackCount>0) {
                String tag = fm.getBackStackEntryAt(backStackCount-1).getName();
                if (tag != null && !tag.equals("VideoPostFragment")) {
                    changeTabColor(false);
                }
                super.onBackPressed();
            }
            else {
                this.getParent().onBackPressed();
            }
        }
    }

    @Override
    protected void onResume() {

        boolean isResumed = FourChamp.isIsParentResumed();

        if (!isResumed && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FragmentManager fm = getSupportFragmentManager();
            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStackImmediate();
            }
            Log.i("TAG","count"+fm.getBackStackEntryCount());

        }

        FourChamp.setIsParentResumed(false);

        Log.i("TAG","count: "+isResumed);

        super.onResume();

    }


}
