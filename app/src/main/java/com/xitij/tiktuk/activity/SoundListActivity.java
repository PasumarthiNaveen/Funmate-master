package com.xitij.tiktuk.activity;

import android.content.res.Resources;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.appcompat.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.xitij.tiktuk.Fragment.DiscoverSoundListFragment;
import com.xitij.tiktuk.Fragment.FavouriteSoundFragment;
import com.xitij.tiktuk.R;
import com.xitij.tiktuk.View.NonSwipeViewPager;

public class SoundListActivity extends AppCompatActivity implements View.OnClickListener{

    protected TabLayout tablayout;

    protected NonSwipeViewPager pager;

    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_list);

        tablayout = (TabLayout) findViewById(R.id.groups_tab);
        pager = findViewById(R.id.viewpager);
        pager.setOffscreenPageLimit(2);
        pager.setPagingEnabled(false);

        // Note that we are passing childFragmentManager, not FragmentManager
        adapter = new ViewPagerAdapter(getResources(), getSupportFragmentManager());
        pager.setAdapter(adapter);
        tablayout.setupWithViewPager(pager);


        findViewById(R.id.Goback).setOnClickListener(this);


    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Goback:
                onBackPressed();
                break;
        }
    }



    class ViewPagerAdapter extends FragmentPagerAdapter {


        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();


        public ViewPagerAdapter(final Resources resources, FragmentManager fm) {

            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            final Fragment result;
            switch (position) {
                case 0:
                    result = new DiscoverSoundListFragment();
                    break;
                case 1:
                    result = new FavouriteSoundFragment();
                    break;
                default:
                    result = null;
                    break;
            }

            return result;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            switch (position) {
                case 0:
                    return "Discover";
                case 1:
                    return "My Favorites";

                default:
                    return null;

            }


        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }


        /**
         * Get the Fragment by position
         *
         * @param position tab position of the fragment
         * @return
         */


        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }


    }

}