package com.xitij.tiktuk.activity;

import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xitij.tiktuk.API.ApiClient;
import com.xitij.tiktuk.API.ApiInterface;
import com.xitij.tiktuk.POJO.Auth;
import com.xitij.tiktuk.POJO.ChangePassword.ChangePasswordRequest;
import com.xitij.tiktuk.POJO.ChangePassword.ChangePasswordResponse;
import com.xitij.tiktuk.POJO.ChangePassword.Request;
import com.xitij.tiktuk.R;
import com.xitij.tiktuk.View.InputLayout;
import com.xitij.tiktuk.View.LoadingView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private LoadingView loadingView;
    private EditText etOldPass,etNewPass,etCnfPass;
    private InputLayout ilOldPass,ilNewPass,ilCnfPass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_password);

        Toolbar toolbar = findViewById(R.id.toolbar_change_passsword);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }

        initView();

    }

    private void initView() {

        loadingView = findViewById(R.id.loader_change_passsword);
        loadingView.setVisibility(View.GONE);

        etOldPass = findViewById(R.id.et_cp_old_passord);
        etNewPass = findViewById(R.id.et_cp_new_passord);
        etCnfPass = findViewById(R.id.et_cp_cnf_passord);

        etOldPass.addTextChangedListener(new CPWatcher(etOldPass));

        ilOldPass = findViewById(R.id.il_old_password);
        ilNewPass = findViewById(R.id.il_new_password);
        ilCnfPass = findViewById(R.id.il_cnf_password);

        Button button = findViewById(R.id.btn_change_password);
        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        submitForm();
    }

    private void submitForm() {
        if (!validateOldPassword()) {
            return;
        }
        if (!validateNewPassword()) {
            return;
        }
        if (!validateCnfPassword()) {
            return;
        }

        loadingView.setVisibility(View.VISIBLE);
        getChangePasswordApi();

    }

    private void getChangePasswordApi() {

        String oldPass = etOldPass.getText().toString().trim();
        String newPass = etNewPass.getText().toString().trim();
        String cnfPass = etCnfPass.getText().toString().trim();

        ChangePasswordRequest passwordRequest = new ChangePasswordRequest();

        Auth auth = new Auth();
        int id = PreferenceManager.getDefaultSharedPreferences(this).getInt("id",-111);
        String token = PreferenceManager.getDefaultSharedPreferences(this).getString("token","null");

        if (id == -111 || token.equals("null")) {
            finish();
        }

        auth.setToken(token);
        auth.setId(id);


        final Request request = new Request();
        request.setOldpassword(oldPass);
        request.setNewpassword(newPass);
        request.setConfirmpassword(cnfPass);

        passwordRequest.setAuth(auth);
        passwordRequest.setRequest(request);
        passwordRequest.setService("changepassword");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ChangePasswordResponse> responseCall = apiInterface.getChangePassword(passwordRequest);
        responseCall.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        Toast toast = Toast.makeText(ChangePasswordActivity.this,response.body().getMessage(),Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                        finish();
                    }
                    else if (response.body().getSuccess() == 2) {
                        ilOldPass.setError(getString(R.string.err_msg_crt_password));
                        requestFocus(etOldPass);
                    }
                    else if (response.body().getSuccess() == 3) {
                        ilCnfPass.setError(getString(R.string.err_cnf_msg_password));
                        requestFocus(etCnfPass);
                    }
                    else {
                        Toast.makeText(ChangePasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                loadingView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, getString(R.string.can_not_connect_server), Toast.LENGTH_SHORT).show();
                loadingView.setVisibility(View.GONE);
            }
        });

    }


    private class CPWatcher implements TextWatcher {
        private View view;
        private CPWatcher(View view) {
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
            switch (view.getId()) {

                case R.id.et_cp_old_passord:
                    validateOldPassword();
                    break;
                case R.id.et_cp_new_passord:
                    validateNewPassword();
                    break;
                case R.id.et_cp_cnf_passord:
                    validateCnfPassword();
                    break;

            }
        }
    }

    private boolean validateOldPassword() {
        if (etOldPass.getText().toString().trim().isEmpty()) {
            ilOldPass.setError(getString(R.string.err_msg_password));
            requestFocus(etOldPass);
            return false;
        } else {
            ilOldPass.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateNewPassword() {
        if (etNewPass.getText().toString().trim().isEmpty() || etNewPass.getText().length() <= 5) {
            ilNewPass.setError(getString(R.string.err_msg_password));
            requestFocus(etNewPass);
            return false;
        } else {
            ilNewPass.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateCnfPassword() {

        String pass = etNewPass.getText().toString().trim();
        String cPass = etCnfPass.getText().toString().trim();

        if (cPass.isEmpty() || etCnfPass.getText().length() <= 5) {
            ilCnfPass.setError(getString(R.string.err_msg_password));
            requestFocus(etCnfPass);
            return false;
        }else if (!cPass.equals(pass)) {

            ilCnfPass.setError(getString(R.string.err_cnf_msg_password));
            requestFocus(etCnfPass);
            return false;

        } else {
            ilCnfPass.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
