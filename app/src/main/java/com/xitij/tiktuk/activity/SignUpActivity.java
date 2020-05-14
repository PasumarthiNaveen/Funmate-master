package com.xitij.tiktuk.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xitij.tiktuk.API.ApiClient;
import com.xitij.tiktuk.API.ApiInterface;
import com.xitij.tiktuk.Fragment.LogInBSFragment;
import com.xitij.tiktuk.NotificationService;
import com.xitij.tiktuk.POJO.SignUp.Request.Data;
import com.xitij.tiktuk.POJO.SignUp.Request.Request;
import com.xitij.tiktuk.POJO.SignUp.Request.SignUpRequest;
import com.xitij.tiktuk.POJO.SignUp.Response.SignUpResponse;
import com.xitij.tiktuk.POJO.Username.Request.UsernameRequest;
import com.xitij.tiktuk.POJO.Username.UsernameResponse;
import com.xitij.tiktuk.R;
import com.xitij.tiktuk.Utils.PrefManager;
import com.xitij.tiktuk.View.LoadingView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = SignUpActivity.class.getSimpleName();
    private EditText etFirstName,etLastName,etEmail,etPassword,etPINCode,etMobileNumber,etUserName,etReferalCode;
    private TextInputLayout ilFirstName,ilLastName,ilEmail,ilPassword,ilPINCode,ilMobileNumber,ilUserName;
    private FrameLayout loader;
    private LoadingView loadingView;

    private boolean isUsername = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViews();
    }

    private void initViews() {

        //Toolbar
        Toolbar toolbar = findViewById(R.id.signup_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar()!=null)
        {
            getSupportActionBar().setTitle(getString(R.string.sign_up_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }

        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etEmail = findViewById(R.id.et_signup_mail);
        etPassword = findViewById(R.id.et_signup_pass);
        etMobileNumber = findViewById(R.id.et_mobile);
        etPINCode = findViewById(R.id.et_pincode);
        etUserName = findViewById(R.id.et_signup_username);
        etReferalCode = findViewById(R.id.et_referal);

        ilFirstName = findViewById(R.id.il_first_name);
        ilLastName = findViewById(R.id.il_last_name);
        ilEmail = findViewById(R.id.il_signup_mail);
        ilPassword = findViewById(R.id.il_signup_pass);
        ilMobileNumber = findViewById(R.id.il_mobile);
        ilPINCode = findViewById(R.id.il_pincode);
        ilUserName = findViewById(R.id.il_signup_username);

        etFirstName.addTextChangedListener(new MyTextWatcher(etFirstName));
        etLastName.addTextChangedListener(new MyTextWatcher(etLastName));
        etEmail.addTextChangedListener(new MyTextWatcher(etEmail));
        etPassword.addTextChangedListener(new MyTextWatcher(etPassword));
        etMobileNumber.addTextChangedListener(new MyTextWatcher(etMobileNumber));
        etPINCode.addTextChangedListener(new MyTextWatcher(etPINCode));
        etUserName.addTextChangedListener(new MyTextWatcher(etUserName));
        isUsername = true;

        etUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateUsername();
                }
            }
        });

        Button btnSignUp = findViewById(R.id.btn_signup);
        btnSignUp.setOnClickListener(this);

        loader = findViewById(R.id.signup_loader);
        loadingView = findViewById(R.id.loader_username);
        loadingView.setVisibility(View.GONE);

        SpannableString ss = new SpannableString(getResources().getString(R.string.terms_and_conditions));
        TextView tvTerms = findViewById(R.id.tvTermsNcondition);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        ss.setSpan(boldSpan,48,66,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(SignUpActivity.this,PrivacyPolicy.class));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(span,48,66, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvTerms.setText(ss);
        tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
        tvTerms.setHighlightColor(Color.TRANSPARENT);


    }

    @Override
    public void onClick(View v) {
        submitForm();
    }

    /**
     * Validating form
     */
    private void submitForm() {
        if (!validateFirstName()) {
            return;
        }

        if (!validateLastName()){
            return;
        }

        if (!validatePinCode()){
            return;
        }

        if (!validateMobileNumber()){
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validateUsername()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        loader.setVisibility(View.VISIBLE);
        getSignUpAPI();
    }

    private void getSignUpAPI() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString();
        String username = etUserName.getText().toString();
        String password = etPassword.getText().toString();
        String pinCode = etPINCode.getText().toString();
        String mobileNumber = etMobileNumber.getText().toString();
        String referal = etReferalCode.getText().toString().trim();


        SignUpRequest service = new SignUpRequest();

        Request request = new Request();


        String deviceId = NotificationService.getToken(this);

        Data data = new Data();
        data.setType("customer");
        data.setFirstName(firstName);
        data.setLastName(lastName);
        data.setUsername(username);
        data.setEmail(email);
        data.setPassword(password);
        data.setPincode(pinCode);
        data.setMobile(mobileNumber);
        data.setPlatform("android");
        data.setDeviceId(deviceId);
        data.setLat("2.17");
        data.setLng("3.501");

        if (!referal.isEmpty()) {
            data.setReferenceCode(referal);
        }

        request.setData(data);

        service.setService("signup");
        service.setRequest(request);


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<SignUpResponse> signUpCall = apiInterface.getSignUp(service);
        signUpCall.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getSuccess() == 1){

                        int id = response.body().getUserId();
                        String token = response.body().getToken();

                        Toast.makeText(SignUpActivity.this, "SignUp success.", Toast.LENGTH_SHORT).show();

                        PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this).edit().putBoolean("isLoggedIn",true).apply();
                        PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this).edit().putInt("id",id).apply();
                        PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this).edit().putString("token",token).apply();

                        String[] profilePic = response.body().getData().getProfilepic().split("/");
                        PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this).edit().putString("profilePic",profilePic[profilePic.length-1]).apply();

                        PrefManager.setFullName(SignUpActivity.this,response.body().getData().getFirstName()+" "+response.body().getData().getLastName());

                        PrefManager.setReferralCode(SignUpActivity.this,response.body().getData().getReferenceCode());

                        Intent intent = new Intent(SignUpActivity.this, LogInBSFragment.class);
                        intent.putExtra("isLogInSuccessful",true);
                        setResult(RESULT_OK,intent);
                        finish();

                    }
                    else if (response.body().getSuccess() == 3) {
                        ilEmail.setError(getString(R.string.err_existing_email));
                        requestFocus(etEmail);
                        PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this).edit().putBoolean("isLoggedIn",false).apply();
                    }
                    else if (response.body().getSuccess() == 2) {
                        ilUserName.setErrorEnabled(true);
                        ilUserName.setError(getString(R.string.existing_user));
                        isUsername = false;
                        requestFocus(etUserName);
                        PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this).edit().putBoolean("isLoggedIn",false).apply();
                    }
                    else {
                        Toast.makeText(SignUpActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this).edit().putBoolean("isLoggedIn",false).apply();
                        Log.e(TAG, "onResponse: Error success=0");
                    }
                    Log.e(TAG, "onResponse: Error success= "+response.body().getSuccess()+" "+response.body().getMessage());
                }
                loader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this).edit().putBoolean("isLoggedIn",false).apply();
                loader.setVisibility(View.GONE);
            }
        });
    }

    private boolean validateFirstName(){
        if (etFirstName.getText().toString().trim().isEmpty()){
            ilFirstName.setError(getString(R.string.err_first_name));
            requestFocus(etFirstName);
            return false;
        }
        else {
            ilFirstName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLastName() {
        if (etLastName.getText().toString().trim().isEmpty()){
            ilLastName.setError(getString(R.string.err_last_name));
            requestFocus(etLastName);
            return false;
        }
        else {
            ilLastName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePinCode() {
        String pass = etPassword.getText().toString().trim();
        String pin = etPINCode.getText().toString().trim();
        if (pin.trim().isEmpty() || pin.length() <= 5){
            ilPINCode.setError(getString(R.string.err_msg_password));
            requestFocus(etPINCode);
            return false;
        }
        else if (!pin.equals(pass)) {
            ilPINCode.setError(getString(R.string.err_cnf_msg_password));
            requestFocus(etPINCode);
            return false;
        }
        else {
            ilPINCode.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateMobileNumber() {
        String phone = etMobileNumber.getText().toString();
        if (phone.trim().isEmpty() || phone.length() != 10){
            ilMobileNumber.setError(getString(R.string.err_mobile_no));
            requestFocus(etMobileNumber);
            return false;
        }
        else {
            ilMobileNumber.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if (etPassword.getText().toString().trim().isEmpty() || etPassword.getText().length() <= 5) {
            ilPassword.setError(getString(R.string.err_msg_password));
            requestFocus(etPassword);
            return false;
        } else {
            ilPassword.setErrorEnabled(false);
        }

        return true;
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

    private boolean validateUsername() {
        String userName = etUserName.getText().toString().trim();
        String regex = "^(?=.{3,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
        if (!userName.isEmpty() && !userName.matches(regex)) {
            ilUserName.setErrorEnabled(true);
            ilUserName.setError(getString(R.string.valid_username));
            return false;
        }

        if (!userName.isEmpty()) {
            loadingView.setVisibility(View.VISIBLE);
            getUserNameAPI(userName);
        }
        else {
            ilUserName.setErrorEnabled(true);
            ilUserName.setError(getString(R.string.enter_username));
        }

        return isUsername;

    }

    private void getUserNameAPI(String userName) {

        UsernameRequest usernameRequest = new UsernameRequest();

        com.xitij.tiktuk.POJO.Username.Request.Data data = new com.xitij.tiktuk.POJO.Username.Request.Data();
        data.setUsername(userName);
        data.setType("username");

        com.xitij.tiktuk.POJO.Username.Request.Request request = new com.xitij.tiktuk.POJO.Username.Request.Request();
        request.setData(data);

        usernameRequest.setRequest(request);
        usernameRequest.setService("checkexistuser");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<UsernameResponse> responseCall = apiInterface.getUsername(usernameRequest);
        responseCall.enqueue(new Callback<UsernameResponse>() {
            @Override
            public void onResponse(Call<UsernameResponse> call, Response<UsernameResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        ilUserName.setErrorEnabled(true);
                        ilUserName.setError(getString(R.string.existing_user));
                        requestFocus(etUserName);
                        isUsername = false;
                    }
                    else {
                        ilUserName.setErrorEnabled(false);
                        isUsername = true;
                    }
                }
                loadingView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<UsernameResponse> call, Throwable t) {
                loadingView.setVisibility(View.GONE);
            }
        });
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

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.et_first_name:
                    validateFirstName();
                    break;
                case R.id.et_last_name:
                    validateLastName();
                    break;
                case R.id.et_signup_mail:
                    validateEmail();
                    break;
                case R.id.et_signup_pass:
                    validatePassword();
                    break;
                case R.id.et_mobile:
                    validateMobileNumber();
                    break;
                case R.id.et_pincode:
                    validatePinCode();
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isLoggedIn",false).apply();
            Intent intent = new Intent(this, LogInBSFragment.class);
            intent.putExtra("isLogInSuccessful",false);
            setResult(RESULT_OK,intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}