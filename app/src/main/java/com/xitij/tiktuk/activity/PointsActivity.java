package com.xitij.tiktuk.activity;

import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xitij.tiktuk.API.ApiClient;
import com.xitij.tiktuk.API.ApiInterface;
import com.xitij.tiktuk.POJO.paymentdetail.PaymentDetailResponse;
import com.xitij.tiktuk.POJO.service.Auth;
import com.xitij.tiktuk.POJO.service.Request;
import com.xitij.tiktuk.POJO.service.Service;
import com.xitij.tiktuk.POJO.service.data.AddPaymentData;
import com.xitij.tiktuk.POJO.service.data.UserPointData;
import com.xitij.tiktuk.POJO.userpoints.UserPointsResponse;
import com.xitij.tiktuk.R;
import com.xitij.tiktuk.Utils.PreciseCount;
import com.xitij.tiktuk.Utils.PrefManager;
import com.xitij.tiktuk.View.LoadingView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PointsActivity extends AppCompatActivity {

    private static final String TAG = PointsActivity.class.getSimpleName();
    private LoadingView loadingView;
    private EditText etRedeemPoint;
    private TextInputLayout ilPoints, ilRedeemAcId;
    private EditText etPoints;
    private TextView tvPoints;
    private String method = "paypal";

    private TextView tvTransactionSuccess;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);
        initViews();
    }

    private void initViews() {

        loadingView = findViewById(R.id.loader_redeem_points);

        tvTransactionSuccess = findViewById(R.id.tv_transaction_success);

        ilPoints = findViewById(R.id.til_redeem_points);
        ilRedeemAcId = findViewById(R.id.til_redeem_id);

        etRedeemPoint = findViewById(R.id.et_redeem_id);
        etPoints = findViewById(R.id.et_redeem_points);

        ilRedeemAcId.setHint(getString(R.string.hint_paypal_account));

        tvPoints = findViewById(R.id.tv_total_points);
        tvPoints.setText("0.0");

        final ImageView ivPaypal = findViewById(R.id.ck_paypal);
        ivPaypal.setSelected(true);

        final ImageView ivPaytm = findViewById(R.id.ck_paytm);

        ivPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivPaytm.setSelected(false);
                ivPaypal.setSelected(true);

                ilRedeemAcId.setHint(getString(R.string.hint_paypal_account));

                method = "paypal";

            }
        });

        ivPaytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivPaytm.setSelected(true);
                ivPaypal.setSelected(false);

                ilRedeemAcId.setHint(getString(R.string.hint_paytm_no));

                method = "paytm";

            }
        });

        Button btnRedeem = findViewById(R.id.btn_redeem_payment);
        btnRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPaymentAPI();
            }
        });

        getUserPoints();


    }

    private void getUserPoints() {

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
                            //updateUserPoints(response.body().getPoint());
                            float cnvPoint = Float.parseFloat(response.body().getCnvPoint());
                            float cnvAmount = Float.parseFloat(response.body().getCnvAmount());

                            float money = (response.body().getPoint() / cnvPoint) * cnvAmount;

                            tvPoints.setText(""+response.body().getPoint()+"  =  $ "+money);
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<UserPointsResponse> call, Throwable t) {
                loadingView.setVisibility(View.GONE);
            }
        });

    }

    private void getPaymentAPI() {

        loadingView.setVisibility(View.VISIBLE);

        Service service = new Service();

        final int id = PreferenceManager.getDefaultSharedPreferences(this).getInt("id",-111);
        String token = PreferenceManager.getDefaultSharedPreferences(this).getString("token","null");
        String full_name = PrefManager.getFullName(this);

        if (id == -111 || token.equals("null")) {
            return;
        }
        //fixme add user_id in Addpaymentdata

        String points = etPoints.getText().toString().trim();
        String acId = etRedeemPoint.getText().toString().trim();

        if (!isValidPoints()) {
            return;
        }
        if (!isValidAccount()) {
            return;
        }


        Auth auth = new Auth();
        auth.setId(id);
        auth.setToken(token);

        Request request = new Request();

        AddPaymentData data = new AddPaymentData();
        data.setUserId(""+id);
        data.setMethod(this.method);
        data.setName(full_name);
        data.setRequestTo(acId);
        data.setStatus("pending");
        data.setPoint(points);


        request.setData(data);

        service.setRequest(request);
        service.setAuth(auth);
        service.setService("add_payment_detail");

        try {
            tvTransactionSuccess.setText(PreciseCount.from(points));
        } catch (Exception e) {
            tvTransactionSuccess.setText(points);
        }

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<PaymentDetailResponse> call = apiInterface.addPaymentDetail(service);
        call.enqueue(new Callback<PaymentDetailResponse>() {
            @Override
            public void onResponse(Call<PaymentDetailResponse> call, Response<PaymentDetailResponse> response) {
                loadingView.setVisibility(View.GONE);
                if (response.isSuccessful()) {

                    if (response.body().getSuccess() == 1) {

                        tvTransactionSuccess.setVisibility(View.VISIBLE);

                        Toast toast = Toast.makeText(PointsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();

                    } else {

                        Toast toast = Toast.makeText(PointsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();

                    }

                }
            }

            @Override
            public void onFailure(Call<PaymentDetailResponse> call, Throwable t) {
                loadingView.setVisibility(View.GONE);
            }
        });

    }

    /*private void updateUserPoints(String point) {

        final DecimalFormat df = new DecimalFormat("#,##,##0.00");

        ValueAnimator animator = ValueAnimator.ofFloat(0f,Float.valueOf(point));
        animator.setDuration(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                String s = df.format((float)animation.getAnimatedValue());
                String[] x = s.split("\\.");
                s = s.replace("."," ");

                //Log.i(TAG, "onAnimationUpdate: "+s);
                //Log.i(TAG, "onAnimationUpdate: "+ Arrays.toString(x));

                SpannableString ss1=  new SpannableString(s);
                ss1.setSpan(new RelativeSizeSpan(2.5f), 0,x[0].length()+1, 0); // set size
                ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, x[0].length()+1, 0);// set color

                tvPoints.setText(ss1);

            }
        });
        animator.start();

    }*/

    private boolean isValidPoints() {
        if (etPoints.getText().toString().trim().isEmpty()) {
            ilPoints.setError(getString(R.string.err_enter_points));
            ilPoints.setErrorEnabled(true);
            return false;
        } else {
            ilPoints.setErrorEnabled(false);
        }
        return true;
    }

    private boolean isValidAccount() {
        if (etRedeemPoint.getText().toString().trim().isEmpty()) {
            if (method.equals("paypal"))
                ilRedeemAcId.setError(getString(R.string.err_paypay_account));
            if (method.equals("paytm"))
                ilRedeemAcId.setError(getString(R.string.err_paytm_number));
            ilRedeemAcId.setErrorEnabled(true);
            return false;
        } else {
            ilRedeemAcId.setErrorEnabled(false);
        }
        return true;
    }

}
