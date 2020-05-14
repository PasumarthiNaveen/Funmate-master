package com.xitij.tiktuk.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.xitij.tiktuk.API.ApiClient;
import com.xitij.tiktuk.API.ApiInterface;
import com.xitij.tiktuk.Adapter.BannerAdapter;
import com.xitij.tiktuk.FourChamp;
import com.xitij.tiktuk.NotificationService;
import com.xitij.tiktuk.POJO.Banner.Banner;
import com.xitij.tiktuk.POJO.Banner.Request.BannerRequest;
import com.xitij.tiktuk.POJO.Banner.Request.Data;
import com.xitij.tiktuk.POJO.Banner.Request.Request;
import com.xitij.tiktuk.POJO.Banner.Response.BannerResponse;
import com.xitij.tiktuk.R;
import com.xitij.tiktuk.View.LoadingView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BannerActivity extends AppCompatActivity {

    private static final String TAG = BannerActivity.class.getSimpleName();
    private List<Banner> bannerList;
    private BannerAdapter bannerAdapter;
    private LoadingView loadingView;
    private FrameLayout btnSkip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);


        /*String userToken = NotificationService.getToken(this);
        if (userToken.equals("null")) {
            Log.i(TAG, "onCreate: Firebase token not received yet!");
        }
        else {
            Log.d(TAG, "onCreate() returned: " + userToken);
        }*/

        /*FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed", task.getException());
                }
                else {
                    // Get new Instance ID token
                    String token = task.getResult().getToken();

                    // Log and toast
                    String msg = getString(R.string.msg_token_fmt, token);
                    Log.d(TAG, msg);
                }
            }
        });*/

    }

    private void initView() {
        ViewPager viewPager = findViewById(R.id.vp_banner);

        TabLayout tabLayout = findViewById(R.id.tl_banner);

        bannerList = new ArrayList<>();
        bannerAdapter = new BannerAdapter(bannerList);
        viewPager.setAdapter(bannerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        loadingView = findViewById(R.id.loader_banner);
        btnSkip = findViewById(R.id.btn_skip);
        getBannerAPI();
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BannerActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        TextView tvRetry = findViewById(R.id.tv_retry);
        tvRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout rlRetry = findViewById(R.id.rl_retry);
                rlRetry.setVisibility(View.GONE);
                getBannerAPI();
            }
        });
    }

    private void getBannerAPI() {

        loadingView.setVisibility(View.VISIBLE);

        String token = NotificationService.getToken(this);
        if (token.equals("null")) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            NotificationService.saveToken(BannerActivity.this,task.getResult().getToken());
                        }
                    }
                }
            });

            token = NotificationService.getToken(this);
        }

        Log.i(TAG, "getBannerAPI: "+token);

        BannerRequest bannerRequest = new BannerRequest();

        Request request = new Request();

        Data data = new Data();
        data.setDeviceId(token);
        data.setPlatform("android");

        request.setData(data);

        bannerRequest.setRequest(request);
        bannerRequest.setService("getBanner");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<BannerResponse> responseCall = apiInterface.getBanner(bannerRequest);
        responseCall.enqueue(new Callback<BannerResponse>() {
            @Override
            public void onResponse(Call<BannerResponse> call, Response<BannerResponse> response) {
                btnSkip.setVisibility(View.VISIBLE);
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        String[] images = response.body().getData().getImage().split(",");

                        for (String image : images) {
                            Banner banner = new Banner(response.body().getBannerPath(), image);
                            bannerList.add(banner);
                        }

                        bannerAdapter.notifyDataSetChanged();

                        PreferenceManager.getDefaultSharedPreferences(BannerActivity.this).edit().putString("contest_id",response.body().getContestOpen().getId()).apply();

                        //PrefManager.setPaidContest(BannerActivity.this,response.body().getContestOpen().getPayType().equals("1"));

                        //PrefManager.setPayAmount(BannerActivity.this,response.body().getContestOpen().getPoint());

                        getDataFromBranch();

                        boolean isLiveVideo = getIntent().getBooleanExtra("live_noti",false);
                        if (isLiveVideo) {
                            String data = getIntent().getStringExtra("data");

                            Intent intent = new Intent(BannerActivity.this,MainActivity.class);
                            intent.putExtra("video",data);
                            intent.putExtra("shared",true);
                            startActivity(intent);
                            finish();

                        }

                    }
                }
                loadingView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<BannerResponse> call, Throwable t) {
                loadingView.setVisibility(View.GONE);
                RelativeLayout rlRetry = findViewById(R.id.rl_retry);
                rlRetry.setBackgroundColor(Color.parseColor("#F50057"));
                rlRetry.setVisibility(View.VISIBLE);
                btnSkip.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Branch.getAutoInstance(this);

        if (FourChamp.isIsParentResumed()) {
            getDataFromBranch();
        }

    }

    @Override
    public void onNewIntent(Intent intent) {
        intent.putExtra("isShared",true);
        super.onNewIntent(intent);
        this.setIntent(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //resume app from app launcher and prevent if live notification or shared video is there
        boolean isLiveVideo = getIntent().getBooleanExtra("live_noti",false);
        boolean isShared = getIntent().getBooleanExtra("isShared",false);
        Log.i(TAG, "onCreate: "+isLiveVideo+isShared);
        if (!isLiveVideo && !isShared) {
            if (!isTaskRoot()) {
                finish();
                return;
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RelativeLayout relativeLayout = findViewById(R.id.rl_banner);
                relativeLayout.setBackgroundColor(Color.WHITE);
                RelativeLayout splash = findViewById(R.id.rl_splash);
                splash.setVisibility(View.GONE);
                initView();
            }
        },3000);

    }


    private void getDataFromBranch() {

        Branch branch = Branch.getInstance();

        // Branch init
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    // params are the deep linked params associated with the link that the user clicked -> was re-directed to this app
                    // params will be empty if no data found
                    // ... insert custom logic here ...
                    Log.i("BRANCH SDK", referringParams.toString());

                    try {
                        if (referringParams.has("data")) {

                            String data = referringParams.getString("data");
                            JSONObject object = new JSONObject(data);

                            Intent intent = new Intent(BannerActivity.this,MainActivity.class);

                            if (object.has("isProfile")) {
                                intent.putExtra("isProfile",true);
                                intent.putExtra("id",object.getString("id"));
                            }
                            else {
                                intent.putExtra("video",referringParams.getString("data"));
                                intent.putExtra("shared",true);
                            }

                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.i("BRANCH SDK", error.getMessage());
                }
            }
        }, BannerActivity.this.getIntent().getData(), BannerActivity.this);

    }

}
