package com.xitij.tiktuk.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.xitij.tiktuk.API.ApiClient;
import com.xitij.tiktuk.API.ApiInterface;
import com.xitij.tiktuk.API.ImageProgressRequestBody;
import com.xitij.tiktuk.API.ImageUploadListener;
import com.xitij.tiktuk.Fragment.ProfileFragment;
import com.xitij.tiktuk.POJO.Auth;
import com.xitij.tiktuk.POJO.Upload.Multipart.UploadImageResponse;
import com.xitij.tiktuk.POJO.User.UpdateProfile.Request.Data;
import com.xitij.tiktuk.POJO.User.UpdateProfile.Request.Request;
import com.xitij.tiktuk.POJO.User.UpdateProfile.Request.UpdateProfileRequest;
import com.xitij.tiktuk.POJO.User.UpdateProfile.UpdateProfileResponse;
import com.xitij.tiktuk.POJO.Username.Request.UsernameRequest;
import com.xitij.tiktuk.POJO.Username.UsernameResponse;
import com.xitij.tiktuk.R;
import com.xitij.tiktuk.Transformation.CircleTransform;
import com.xitij.tiktuk.View.LoadingView;

import java.io.File;
import java.io.IOException;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity implements ImageUploadListener {
    private static final String TAG = UpdateProfileActivity.class.getSimpleName();
    private Uri resultUri;
    private EditText etUserName,etFirstName,etLastName,etBio,etInsta,etYoutube;
    private TextInputLayout ilUserName,ilFirstName;
    private LoadingView loadingView,uloadingView;

    private ProgressBar progressBar;
    private TextView tvProgress;
    private boolean isUsername = true;
    private String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        Toolbar toolbar = findViewById(R.id.update_profile_toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();


        ImageView imageView = findViewById(R.id.iv_upload_photo);

        String profilePic = getIntent().getStringExtra("profilePic");
        String profilePath = getIntent().getStringExtra("profilePath");


        if (profilePic.isEmpty()) {
            Picasso.get().load(R.drawable.blank_profile).transform(new CircleTransform()).into(imageView);
        }
        else {
            Picasso.get().load(profilePath+profilePic).transform(new CircleTransform()).resize(200,200).into(imageView);
        }

        ImageView ivLayer = findViewById(R.id.iv_layer);
        ivLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setRequestedSize(200,200, CropImageView.RequestSizeOptions.RESIZE_FIT)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setAspectRatio(1,1)
                        .start(UpdateProfileActivity.this);
            }
        });

    }

    private void initViews() {

        progressBar = findViewById(R.id.update_circularProgressbar);
        progressBar.setProgress(0);   // Main Progress
        progressBar.setSecondaryProgress(100); // Secondary Progress
        progressBar.setMax(100); // Maximum Progress

        tvProgress = findViewById(R.id.tv_update_progress);
        tvProgress.setText("0 %");

        loadingView = findViewById(R.id.update_profile_loader);
        uloadingView = findViewById(R.id.loader_update_username);

        String fname = getIntent().getStringExtra("firstName");
        String lname = getIntent().getStringExtra("lastName");
        String bio = getIntent().getStringExtra("bio");
        String username = getIntent().getStringExtra("username");
        String insta = getIntent().getStringExtra("insta");
        String youtube = getIntent().getStringExtra("youtube");
        String email = getIntent().getStringExtra("email");

        this.username = username;

        EditText etEmail = findViewById(R.id.et_update_email);
        etEmail.setEnabled(false);
        etEmail.setText(email);

        ilFirstName = findViewById(R.id.il_update_first_name);
        ilUserName = findViewById(R.id.il_update_user_name);
        etUserName = findViewById(R.id.et_update_user_name);
        etUserName.setText(username);
        etUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateUsername();
                }
            }
        });

        etFirstName = findViewById(R.id.et_update_first_name);
        etLastName = findViewById(R.id.et_update_lasst_name);
        etBio = findViewById(R.id.et_update_bio);
        etInsta = findViewById(R.id.et_update_insta);
        etYoutube = findViewById(R.id.et_update_youtube);
        etInsta.setText(insta);
        etYoutube.setText(youtube);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(250);
        etBio.setFilters(filterArray);
        etBio.addTextChangedListener(new MaxLineWatcher());

        etFirstName.setText(fname);
        etLastName.setText(lname);
        if (bio.isEmpty()) {
            etBio.setText("");
            etBio.setHint("No bio yet");
        }
        else {
            etBio.setText(bio);
        }

        isUsername = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();

                setNewProfilePic();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void setNewProfilePic() {
        ImageView imageView = findViewById(R.id.iv_upload_photo);
        Picasso.get().load(resultUri).transform(new CircleTransform()).into(imageView);
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

    private boolean validateUsername() {
        String userName = etUserName.getText().toString();

        if (!userName.isEmpty()) {
            if (userName.equals(this.username)) {
                return true;
            }
            else {
                uloadingView.setVisibility(View.VISIBLE);
                getUserNameAPI(userName);
            }
        }
        else {
            ilUserName.setErrorEnabled(true);
            ilUserName.setError(getString(R.string.enter_username));
        }

        return isUsername;

    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_profile_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.done) {
            if (resultUri != null) {
                uploadPhoto(resultUri);
            }
            else {
                updateProfile();
            }
        }
        if (item.getItemId() == android.R.id.home) {
            if (resultUri==null) {
                finish();
            }
            else {
                showAlertDialog();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadPhoto(Uri resultUri) {
        if (!validateFirstName()) {
            return;
        }
        if (!validateUsername()) {
            return;
        }

        final RelativeLayout relativeLayout = findViewById(R.id.rlUpdateProgress);
        relativeLayout.setVisibility(View.VISIBLE);

        File imageFile = new File(resultUri.getPath());

        ImageProgressRequestBody requestBody = new ImageProgressRequestBody(imageFile,this);

        MultipartBody.Part imageBody = MultipartBody.Part.createFormData("image",imageFile.getName(),requestBody);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<UploadImageResponse> responseCall = apiInterface.uploadProfilePhoto(imageBody);
        responseCall.enqueue(new Callback<UploadImageResponse>() {
            @Override
            public void onResponse(Call<UploadImageResponse> call, Response<UploadImageResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        relativeLayout.setVisibility(View.GONE);
                        updateProfile(response.body().getData());
                    }
                }
            }

            @Override
            public void onFailure(Call<UploadImageResponse> call, Throwable t) {

            }
        });
    }

    private void updateProfileAPI(String username, String fname, String lname, String bio, String insta, String youtube, String imageData) {

        loadingView.setVisibility(View.VISIBLE);

        UpdateProfileRequest profileRequest = new UpdateProfileRequest();

        int id = PreferenceManager.getDefaultSharedPreferences(this).getInt("id",-111);
        String token = PreferenceManager.getDefaultSharedPreferences(this).getString("token","null");

        if (id == -111 || token.equals("null")) {
            return;
        }

        Auth auth = new Auth();
        auth.setId(id);
        auth.setToken(token);

        Data data = new Data();
        data.setId(""+id);
        data.setUsername(username);
        data.setFirstName(fname);
        data.setLastName(lname);
        data.setBio(bio);
        data.setInstaUrl(insta);
        data.setYoutubeUrl(youtube);

        if (imageData != null) {
            data.setProfilepic(imageData);
        }

        Request request = new Request();
        request.setData(data);

        profileRequest.setAuth(auth);
        profileRequest.setRequest(request);
        profileRequest.setService("updateUserProfile");

        Log.d(TAG, "updateProfileAPI() returned: " + profileRequest.toString());
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<UpdateProfileResponse> responseCall = apiInterface.updateProfile(profileRequest);
        responseCall.enqueue(new Callback<UpdateProfileResponse>() {
            @Override
            public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
                loadingView.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {

                        Intent intent = new Intent(getApplicationContext(), ProfileFragment.class);
                        intent.putExtra("username",response.body().getData().getUsername());
                        intent.putExtra("fname",response.body().getData().getFirstName());
                        intent.putExtra("lname",response.body().getData().getLastName());
                        intent.putExtra("bio",response.body().getData().getBio());
                        intent.putExtra("profilePath",response.body().getProfilePic());
                        intent.putExtra("profilePic",response.body().getData().getProfilepic());
                        setResult(RESULT_OK,intent);
                        finish();

                    }
                    else {
                        Log.e(TAG,response.body().getMessage());
                    }
                }
                else {
                    try {
                        Log.e(TAG, "onResponse: "+response.code()+ " "+ response.errorBody().string() );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                loadingView.setVisibility(View.GONE);
            }
        });

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
                uloadingView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<UsernameResponse> call, Throwable t) {
                uloadingView.setVisibility(View.GONE);
            }
        });
    }

    private void updateProfileAPI(String username, String fname, String lname, String bio, String instaUrl, String youtubrUrl) {
        updateProfileAPI(username,fname,lname,bio,instaUrl, youtubrUrl,null);
    }

    @Override
    public void onBackPressed() {
        if (resultUri==null) {
            super.onBackPressed();
        }
        else {
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.app_name));
        builder.setMessage("Do you want save changes ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadPhoto(resultUri);
            }
        });
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void updateProfile() {
        updateProfile(null);
    }

    private void updateProfile(String imageData) {

        String username = etUserName.getText().toString();
        String fname = etFirstName.getText().toString().trim();
        String lname = etLastName.getText().toString().trim();
        String bio = etBio.getText().toString().replace("\n","\\n");

        String instaUrl = etInsta.getText().toString().trim();
        String youtubrUrl = etYoutube.getText().toString().trim();

        Log.i(TAG, "updateProfile: "+bio);
        if (imageData != null) {
            updateProfileAPI(username, fname, lname, bio, instaUrl, youtubrUrl, imageData);
        }
        else {
            if (!validateFirstName()) {
                return;
            }
            if (!validateUsername()) {
                return;
            }
            updateProfileAPI(username, fname, lname, bio, instaUrl, youtubrUrl);
        }
    }

    @Override
    public void onImageProgressUpdate(int percentage) {
        progressBar.setProgress(percentage);
        tvProgress.setText(percentage+" %");
    }

    @Override
    public void onIUploadError() {

    }

    @Override
    public void onIUploadFinish() {
        progressBar.setProgress(100);
        tvProgress.setText(R.string.done);
    }

    @Override
    public void onIUploadStart() {

    }

    private class MaxLineWatcher implements TextWatcher {
        TextView textView = findViewById(R.id.limit_bio);
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //textView.setText(getString(R.string.limit_bio_length,3-etBio.getLineCount(),120-s.length()));
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            textView.setText(getString(R.string.limit_bio_length,3-etBio.getLineCount(),120-s.length()));

            /*if (etBio.getLineCount() == 1){
                textView.setText(getString(R.string.limit_bio_length,3-etBio.getLineCount(),120-s.length()));
            }
            else if (etBio.getLineCount() == 2) {

                String[] line = etBio.getText().toString().split("\n");
                textView.setText(getString(R.string.limit_bio_length,3-etBio.getLineCount(),80-line[line.length-1].length()));
            }
            else if (etBio.getLineCount() == 3) {
                String[] line = s.toString().split("\n");
                textView.setText(getString(R.string.limit_bio_length,3-etBio.getLineCount(),40-line[line.length-1].length()));
            }
            else {
                textView.setText(getString(R.string.limit_bio_length,3-etBio.getLineCount(),120-s.length()));
            }*/
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}