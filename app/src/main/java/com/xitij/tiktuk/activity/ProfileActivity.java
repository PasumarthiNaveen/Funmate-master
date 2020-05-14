package com.xitij.tiktuk.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

import com.xitij.tiktuk.FourChamp;
import com.xitij.tiktuk.Fragment.FollowingFragment;
import com.xitij.tiktuk.Fragment.LogInBSFragment;
import com.xitij.tiktuk.Fragment.ProfileFragment;
import com.xitij.tiktuk.Fragment.UserFragment;
import com.xitij.tiktuk.Fragment.VideoPostFragment;
import com.xitij.tiktuk.Listner.OnFollowerListener;
import com.xitij.tiktuk.Listner.OnPeopleListener;
import com.xitij.tiktuk.Listner.OnVideoListener;
import com.xitij.tiktuk.POJO.People;
import com.xitij.tiktuk.POJO.Video.Video;
import com.xitij.tiktuk.R;

import java.util.List;

public class ProfileActivity extends AppCompatActivity implements OnVideoListener, OnFollowerListener, OnPeopleListener, FragmentManager.OnBackStackChangedListener {

    private static final int LOG_IN = 819;
    private static final int AD_DISPLAY_FREQUENCY = 10;
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private LogInBSFragment logInBSFragment;
    private boolean isLoggedFromAnotherTab = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (isUserLoggedIn()) {
            ProfileFragment userFragment = new ProfileFragment();
            Bundle bundle = new Bundle();
            userFragment.setArguments(bundle);
            userFragment.setOnVideoListener(this);
            userFragment.setOnFollowerListener(this);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.add(R.id.frag_profile_activty, userFragment,"ProfileFragment").commit();

            getSupportFragmentManager().addOnBackStackChangedListener(this);

            isLoggedFromAnotherTab = false;
        }
        else {
            logInBSFragment = new LogInBSFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("isProfileActivity",true);
            logInBSFragment.setArguments(bundle);
            logInBSFragment.show(getSupportFragmentManager(), logInBSFragment.getTag());
        }

    }

    @Override
    public void onVideoSelected(int position, List<Video> videos) {
        int index = position + ((position + (position/(AD_DISPLAY_FREQUENCY - 1))) / AD_DISPLAY_FREQUENCY);
        VideoPostFragment fragment = new VideoPostFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position",index);
        fragment.setArguments(bundle);
        fragment.setVideos(videos);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        ft.addToBackStack("ProfileFragment");
        ft.add(R.id.frag_profile_activty, fragment, "VideoPostFragment").commit();

        changeTabColor(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu,menu);
        return true;
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
        ft.addToBackStack("ProfileFragment");
        ft.add(R.id.frag_profile_activty, fragment, "FollowingFragment").commit();

        changeTabColor(false);
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
        ft.addToBackStack("FollowingFragment");
        ft.add(R.id.frag_profile_activty, fragment, "UserFragment").commit();

        changeTabColor(false);
    }

    private void changeTabColor(boolean transparent) {
        Intent intent = new Intent("change_color_of_tab");
        intent.putExtra("transparent",transparent);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onBackStackChanged() {
        FragmentManager fm = getSupportFragmentManager();

        if (fm != null) {
            int backStackCount = fm.getBackStackEntryCount();
            if (backStackCount == 0) {
                changeTabColor(false);
            }
        }
    }

    private boolean isUserLoggedIn() {
        boolean logIn = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isLoggedIn",false);

        return logIn;
    }

    private void showLogInFragment() {
        LogInBSFragment logInBSFragment = new LogInBSFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isProfileActivity",true);
        logInBSFragment.setArguments(bundle);
        logInBSFragment.show(getSupportFragmentManager(), logInBSFragment.getTag());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If user logged successfully then dismiss BSF and show his/her Profile
        if (requestCode == LOG_IN) {
            if (resultCode == RESULT_OK) {

                if (data != null) {
                    boolean isLogInSuccessful = data.getBooleanExtra("isLogInSuccessful",false);
                    PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isLoggedIn",isLogInSuccessful).apply();
                    Log.d(TAG, "onActivityResult() returned: " + isLogInSuccessful);
                    if (isLogInSuccessful) {
                        ProfileFragment userFragment = new ProfileFragment();
                        Bundle bundle = new Bundle();
                        userFragment.setArguments(bundle);
                        userFragment.setOnVideoListener(this);
                        userFragment.setOnFollowerListener(this);

                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.add(R.id.frag_profile_activty, userFragment,"ProfileFragment").commitAllowingStateLoss();

                        getSupportFragmentManager().addOnBackStackChangedListener(this);

                        logInBSFragment.dismissAllowingStateLoss();
                    }
                }

            }
        }

        try {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data);
                Log.d("Activity", "ON RESULT CALLED");
            }
        } catch (Exception e) {
            Log.d("ERROR", e.toString());
        }

    }

    @Override
    public void onBackPressed() {

        FragmentManager fm = getSupportFragmentManager();
        if (fm != null) {
            int backStackCount = fm.getBackStackEntryCount();
            if (backStackCount > 0) {
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

        // if tab is paused by tabactivity -> this condition will be called
        if (!isResumed) {
            //Clear all fragment when it is resumed.
            FragmentManager fm = getSupportFragmentManager();
            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    fm.popBackStack();
                }
            }
            Log.i("TAG","count"+fm.getBackStackEntryCount());
            //Show Login Fragment if user still not logged in -> On Resume
            if (!isUserLoggedIn()) {
                showLogInFragment();
            }
            //Show Profile if user logged from other tabs in -> On Resume
            if (isUserLoggedIn() && isLoggedFromAnotherTab){
                ProfileFragment userFragment = new ProfileFragment();
                Bundle bundle = new Bundle();
                userFragment.setArguments(bundle);
                userFragment.setOnVideoListener(this);
                userFragment.setOnFollowerListener(this);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.add(R.id.frag_profile_activty, userFragment,"ProfileFragment").commit();

                getSupportFragmentManager().addOnBackStackChangedListener(this);

                isLoggedFromAnotherTab = false;

            }
        }

        //if whole app is resumed then it will executes from here!
        FourChamp.setIsParentResumed(false);

        Log.i("TAG","count: "+isResumed);

        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }
}