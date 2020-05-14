package com.xitij.tiktuk.activity;

import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.xitij.tiktuk.API.ApiClient;
import com.xitij.tiktuk.API.ApiInterface;
import com.xitij.tiktuk.POJO.ForgetPassword.Request.ForgetPasswordRequest;
import com.xitij.tiktuk.POJO.ForgetPassword.Request.Request;
import com.xitij.tiktuk.POJO.ForgetPassword.Response.ForgetPasswordResponse;
import com.xitij.tiktuk.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity {
    private EditText etEmail;
    private TextInputLayout ilEmail;
    private FrameLayout loader;

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        Toolbar toolbar = findViewById(R.id.toolbar_forget_password);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_back));
        }

        initView();
    }

    private void initView() {
        etEmail = findViewById(R.id.et_forget_mail);
        etEmail.addTextChangedListener(new MyTextWatcher(etEmail));

        ilEmail = findViewById(R.id.il_forget_mail);

        Button btnSendEmail = findViewById(R.id.btn_send_mail);
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });

        loader = findViewById(R.id.forget_loader);

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
        adView.loadAd();

    }

    private void submitForm() {
        if (!validateEmail())
            return;
        loader.setVisibility(View.VISIBLE);
        callForgetPassswordAPI();
    }

    private void callForgetPassswordAPI() {
        String email = etEmail.getText().toString();
        ForgetPasswordRequest forgetPasswordRequest = new ForgetPasswordRequest();

        Request request = new Request();
        request.setValue(email);

        forgetPasswordRequest.setRequest(request);
        forgetPasswordRequest.setService("forgotpassword");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ForgetPasswordResponse> forgetPasswordResponseCall = apiInterface.getForgetPassword(forgetPasswordRequest);
        forgetPasswordResponseCall.enqueue(new Callback<ForgetPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgetPasswordResponse> call, Response<ForgetPasswordResponse> response) {
                Log.i("TAG", "onResponse: response "+response.code());
                if (response.isSuccessful()){
                    if (response.body().getSuccess() == 1){
                        Toast toast = Toast.makeText(ForgetPasswordActivity.this, "E-mail sent successfully.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                    else {
                        Toast toast = Toast.makeText(ForgetPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                }
                loader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ForgetPasswordResponse> call, Throwable t) {
                Log.i("TAG", "onResponse: response "+t.getMessage());
                loader.setVisibility(View.GONE);
            }
        });
    }

    private boolean validateEmail() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            ilEmail.setError(getString(R.string.err_msg_email));
            requestFocus(etEmail);
            return false;
        } else {
            ilEmail.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {
        private View view;
        MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (view.getId())
            {
                case R.id.et_forget_mail:
                    validateEmail();
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
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
