package com.xitij.tiktuk.activity;

import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.xitij.tiktuk.API.ApiClient;
import com.xitij.tiktuk.API.ApiInterface;
import com.xitij.tiktuk.FourChamp;
import com.xitij.tiktuk.POJO.Upload.Multipart.UploadVideoResponse;
import com.xitij.tiktuk.POJO.payment.PaymentResponse;
import com.xitij.tiktuk.POJO.service.Auth;
import com.xitij.tiktuk.POJO.service.Request;
import com.xitij.tiktuk.POJO.service.Service;
import com.xitij.tiktuk.POJO.service.data.PaymentData;
import com.xitij.tiktuk.R;
import com.xitij.tiktuk.Utils.PrefManager;
import com.xitij.tiktuk.View.LoadingView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = TestActivity.class.getSimpleName();

    private LoadingView loadingView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        String data = getIntent().getStringExtra("data");

        Gson gson = new Gson();

        final UploadVideoResponse response = gson.fromJson(data,UploadVideoResponse.class);

        ImageView imageView = findViewById(R.id.iv_uploaded_thumb);
        Picasso.get().load(FourChamp.VIDEO_BASE_URL+response.getThumbData()).into(imageView);

        loadingView = findViewById(R.id.loader_payment);

        final String videoUrl = PrefManager.getTempVUrl(this);
        final String thmubUrl = PrefManager.getTempTUrl(this);

        Button btnPay = findViewById(R.id.btn_procced_points);
        btnPay.setText(getString(R.string.pay_d_points,PrefManager.getPayAmount(this)));
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPaymentSuccessAPI(videoUrl,thmubUrl);
            }
        });


    }



    /*@Override
    public void onPaymentSuccess(String razorpayPaymentID, com.razorpay.PaymentData paymentData) {
        Log.i(TAG, "onPaymentSuccess: "+razorpayPaymentID);


    }

    @Override
    public void onPaymentError(int i, String s, com.razorpay.PaymentData paymentData) {
        loadingView.setVisibility(View.GONE);
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.putExtra("isSuccess",false);
        setResult(RESULT_OK,intent);
        finish();
    }*/


    private void getPaymentSuccessAPI(final String videoUrl, final String thmubUrl) {

        loadingView.setVisibility(View.VISIBLE);

        Service service = new Service();

        int id = PreferenceManager.getDefaultSharedPreferences(this).getInt("id",-311);
        String token = PreferenceManager.getDefaultSharedPreferences(this).getString("token","null");

        String contestId = PreferenceManager.getDefaultSharedPreferences(TestActivity.this).getString("contest_id","1");

        Auth auth = new Auth();
        auth.setId(id);
        auth.setToken(token);

        PaymentData data = new PaymentData();
        data.setAmount(PrefManager.getPayAmount(this));
        data.setUserId(String.valueOf(id));
        data.setCurrency("INR");
        data.setVideoUrl(videoUrl);
        data.setThumbUrl(thmubUrl);
        data.setContId(contestId);

        Request request = new Request();
        request.setData(data);

        service.setAuth(auth);
        service.setRequest(request);
        service.setService("addpayment");

        final Gson gson = new Gson();

        Log.i(TAG, "getPaymentSuccessAPI: request: "+gson.toJson(service));

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<PaymentResponse> call = apiInterface.getPaymentService(service);
        call.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                loadingView.setVisibility(View.GONE);
                if (response.isSuccessful()) {

                    Log.i(TAG, "onResponse: ");

                    if (response.body().getSuccess() == 1) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(TestActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                loadingView.setVisibility(View.GONE);
                Toast.makeText(TestActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
