package com.xitij.tiktuk.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.xitij.tiktuk.API.ApiClient;
import com.xitij.tiktuk.API.ApiInterface;
import com.xitij.tiktuk.Fragment.LogInBSFragment;
import com.xitij.tiktuk.NotificationService;
import com.xitij.tiktuk.POJO.LogIn.Response.LogIn;
import com.xitij.tiktuk.POJO.LogIn.Request.Data;
import com.xitij.tiktuk.POJO.LogIn.Request.LogInRequest;
import com.xitij.tiktuk.POJO.LogIn.Request.Request;
import com.xitij.tiktuk.R;
import com.xitij.tiktuk.Utils.PrefManager;
import com.xitij.tiktuk.View.LoadingView;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LogInActivity.class.getSimpleName();
    private EditText et_username;
    private EditText et_pass;
    private LoadingView loadingView;

    private AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();

    }

    private void initViews() {

        //Toolbar
        Toolbar toolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(R.string.log_in_title);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }

        TextView tvForgetPasss = findViewById(R.id.tv_forget_passowrd);

        SpannableString ss = new SpannableString(getResources().getString(R.string.forget_password));
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        ss.setSpan(boldSpan,17,27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(LogInActivity.this,ForgetPasswordActivity.class));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(span,17,27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvForgetPasss.setText(ss);
        tvForgetPasss.setMovementMethod(LinkMovementMethod.getInstance());
        tvForgetPasss.setHighlightColor(Color.TRANSPARENT);

        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);

        et_username = findViewById(R.id.et_username);
        et_pass = findViewById(R.id.et_password);

        loadingView = findViewById(R.id.loader_login);


        /*
         * Facebook Ads :- show in bottom
         */
        adView = new AdView(this, getString(R.string.facebook_ads_medium_rectangle), AdSize.RECTANGLE_HEIGHT_250);
        //AdSettings.addTestDevice("32a00db4-6a1b-4782-86f4-d041098b4316");

        // Find the Ad Container
        LinearLayout adContainer = findViewById(R.id.linlay_banner_ads);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        //todo undo for ads
        //adView.loadAd();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login)
        {
            loadingView.setVisibility(View.VISIBLE);

            String username = et_username.getText().toString();
            String password = et_pass.getText().toString();


            LogInRequest service = new LogInRequest();
            Request request = new Request();

            String token = NotificationService.getToken(this);
            if (token.equals("null")) {
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                NotificationService.saveToken(LogInActivity.this,task.getResult().getToken());
                            }
                        }
                    }
                });

                token = NotificationService.getToken(this);
            }

            Data data = new Data();
            data.setDeviceId(token);
            data.setUsername(username);
            data.setFbid("");
            data.setLogintype("username");
            data.setPassword(password);
            data.setPlatform("android");
            data.setType("customer");

            request.setData(data);

            service.setService("login");
            service.setRequest(request);

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<LogIn> logInCall = apiInterface.getLogIn(service);
            logInCall.enqueue(new Callback<LogIn>() {
                @Override
                public void onResponse(Call<LogIn> call, Response<LogIn> response) {
                    Log.i(TAG, "onResponse: "+response.code());
                    if (response.isSuccessful()){
                        if (response.body().getSuccess() == 1)
                        {

                            int id = Integer.parseInt(response.body().getData().getUserId());
                            String token = response.body().getToken();

                            Toast toast = Toast.makeText(LogInActivity.this, "Log in Successful!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();

                            PreferenceManager.getDefaultSharedPreferences(LogInActivity.this).edit().putBoolean("isLoggedIn",true).apply();
                            PreferenceManager.getDefaultSharedPreferences(LogInActivity.this).edit().putInt("id",id).apply();
                            PreferenceManager.getDefaultSharedPreferences(LogInActivity.this).edit().putString("token",token).apply();
                            String[] profilePic = response.body().getData().getProfilepic().split("/");
                            PreferenceManager.getDefaultSharedPreferences(LogInActivity.this).edit().putString("profilePic",profilePic[profilePic.length-1]).apply();

                            PrefManager.setFullName(LogInActivity.this,response.body().getData().getFirstName()+" "+response.body().getData().getLastName());

                            PrefManager.setReferralCode(LogInActivity.this,response.body().getData().getReferenceCode());

                            Intent intent = new Intent(LogInActivity.this, LogInBSFragment.class);
                            intent.putExtra("isLogInSuccessful",true);
                            setResult(RESULT_OK,intent);
                            finish();

                        }
                        else {
                            Toast toast = Toast.makeText(LogInActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }
                    }
                    else{
                        PreferenceManager.getDefaultSharedPreferences(LogInActivity.this).edit().putBoolean("isLoggedIn",false).apply();
                        try {
                            Log.e(TAG, "onResponse: error: "+response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    loadingView.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<LogIn> call, Throwable t) {
                    PreferenceManager.getDefaultSharedPreferences(LogInActivity.this).edit().putBoolean("isLoggedIn",false).apply();
                    loadingView.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            PreferenceManager.getDefaultSharedPreferences(LogInActivity.this).edit().putBoolean("isLoggedIn",false).apply();
            Intent intent = new Intent(LogInActivity.this, LogInBSFragment.class);
            intent.putExtra("isLogInSuccessful",false);
            setResult(RESULT_OK,intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
