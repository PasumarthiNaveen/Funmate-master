package com.xitij.tiktuk.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xitij.tiktuk.API.ApiClient;
import com.xitij.tiktuk.API.ApiInterface;
import com.xitij.tiktuk.POJO.service.Auth;
import com.xitij.tiktuk.POJO.service.Request;
import com.xitij.tiktuk.POJO.service.Service;
import com.xitij.tiktuk.POJO.service.data.UserPointData;
import com.xitij.tiktuk.POJO.userpoints.UserPointsResponse;
import com.xitij.tiktuk.R;
import com.xitij.tiktuk.Utils.PrefManager;
import com.xitij.tiktuk.View.LoadingView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPointsActivity extends AppCompatActivity {

    private LoadingView loadingView;

    private TextView tvUpload,tvWatch,tvShare,tvTotalPoints,tvReferral;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_points);

        loadingView = findViewById(R.id.loader_user_points);

        tvTotalPoints = findViewById(R.id.tv_total_points);
        tvWatch = findViewById(R.id.tv_watch);
        tvUpload = findViewById(R.id.tv_upload);
        tvShare = findViewById(R.id.tv_share);
        tvReferral = findViewById(R.id.tv_referral);

        tvTotalPoints.setText("0");
        tvWatch.setText("0");
        tvUpload.setText("0");
        tvShare.setText("0");
        tvReferral.setText("0");

        getPointsAPI();


        TextView tvUserReferralCode = findViewById(R.id.tv_referral_code);
        tvUserReferralCode.setText(PrefManager.getReferralCode(this));
        tvUserReferralCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getString(R.string.app_name), PrefManager.getReferralCode(UserPointsActivity.this));
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(UserPointsActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void getPointsAPI() {

        loadingView.setVisibility(View.VISIBLE);

        Service service = new Service();

        final int id = PreferenceManager.getDefaultSharedPreferences(this).getInt("id",-111);
        String token = PreferenceManager.getDefaultSharedPreferences(this).getString("token","null");

        if (id == -111 || token.equals("null")) {
            return;
        }

        Auth auth = new Auth();
        auth.setId(id);
        auth.setToken(token);

        Request request = new Request();

        UserPointData data = new UserPointData();
        data.setUserId(""+id);

        request.setData(data);

        service.setAuth(auth);
        service.setRequest(request);
        service.setService("getPoint");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<UserPointsResponse> call = apiInterface.getUserPoints(service);
        call.enqueue(new Callback<UserPointsResponse>() {
            @Override
            public void onResponse(Call<UserPointsResponse> call, Response<UserPointsResponse> response) {
                loadingView.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {

                        if (response.body().getPoint() != null) {
                            updateChart(response.body());
                        }

                    } else {
                        Toast.makeText(UserPointsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserPointsResponse> call, Throwable t) {
                loadingView.setVisibility(View.GONE);
                Toast.makeText(UserPointsActivity.this, "failed! Try again", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void updateChart(UserPointsResponse response) {

        tvTotalPoints.setText(""+response.getPoint());

        tvWatch.setText(""+response.getViewPoint());
        tvUpload.setText(""+response.getUplodePoint());
        tvShare.setText(""+response.getSharePoint());
        tvReferral.setText(""+response.getRefPoint());


    }
}
