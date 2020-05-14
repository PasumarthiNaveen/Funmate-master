package com.xitij.tiktuk.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.xitij.tiktuk.FourChamp;
import com.xitij.tiktuk.Fragment.ContestFragment;
import com.xitij.tiktuk.Fragment.ContestVideoFragment;
import com.xitij.tiktuk.Listner.OnContestListener;
import com.xitij.tiktuk.POJO.Contest.Response.Contest;
import com.xitij.tiktuk.R;

public class ContestActivity extends AppCompatActivity implements OnContestListener {

    //private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest);

        /*interstitialAd = new InterstitialAd(this,getString(R.string.facebook_ads_interestial));
        interstitialAd.loadAd();
        AdSettings.addTestDevice("3d4047c6-375d-4189-ae15-9c221f6fb6e0");*/

        ContestFragment contestFragment = new ContestFragment();
        contestFragment.setOnContestListener(this);


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.add(R.id.frag_contest_activty, contestFragment,"ContestFragment").commit();

    }

    @Override
    public void onContestSelected(Contest contest) {

        /*if (interstitialAd.isAdLoaded()) {
            interstitialAd.show();
            interstitialAd.setAdListener(new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {

                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    showContestVideoFragment(contest);
                }

                @Override
                public void onError(Ad ad, AdError adError) {

                }

                @Override
                public void onAdLoaded(Ad ad) {

                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            });
        } else {
            showContestVideoFragment(contest);
        }*/
        showContestVideoFragment(contest);
    }

    private void showContestVideoFragment(Contest contest) {
        ContestVideoFragment fragment = new ContestVideoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id",contest.getId());
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        ft.addToBackStack("ContestVideoFragment");
        ft.add(R.id.frag_contest_activty, fragment).commit();

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

        if (!isResumed) {
            FragmentManager fm = getSupportFragmentManager();
            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            Log.i("TAG","count"+fm.getBackStackEntryCount());
        }

        FourChamp.setIsParentResumed(false);

        Log.i("TAG","count: "+isResumed);

        super.onResume();
    }

}
