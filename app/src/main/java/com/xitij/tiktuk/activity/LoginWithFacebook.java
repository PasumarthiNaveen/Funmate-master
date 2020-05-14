package com.xitij.tiktuk.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.xitij.tiktuk.API.ApiClient;
import com.xitij.tiktuk.API.ApiInterface;
import com.xitij.tiktuk.BuildConfig;
import com.xitij.tiktuk.Fragment.LogInBSFragment;
import com.xitij.tiktuk.NotificationService;
import com.xitij.tiktuk.POJO.LogIn.Request.Data;
import com.xitij.tiktuk.POJO.LogIn.Request.LogInRequest;
import com.xitij.tiktuk.POJO.LogIn.Request.Request;
import com.xitij.tiktuk.POJO.LogIn.Response.LogIn;
import com.xitij.tiktuk.POJO.SignUp.Request.SignUpRequest;
import com.xitij.tiktuk.POJO.SignUp.Response.SignUpResponse;
import com.xitij.tiktuk.R;
import com.xitij.tiktuk.Utils.PrefManager;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginWithFacebook extends AppCompatActivity {

    private static final String TAG = LoginWithFacebook.class.getSimpleName();
    private static final int GOOGLE_SIGN_IN = 80;


    //Google
    private GoogleSignInClient googleSignInClient;


    //facebook
    private CallbackManager callbackManager;
    private AccessToken mAccessToken;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_with_facebook);

        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");;
        dialog.setCancelable(false);

        callbackManager = CallbackManager.Factory.create();

        final LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mAccessToken = loginResult.getAccessToken();
                getUserProfile(mAccessToken);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });





        SignInButton button = findViewById(R.id.signin_google);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        Log.i(TAG, "onComplete: signout");
                    }
                });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
            }
        });


        if (getIntent().getBooleanExtra("isfb",true)){
            loginButton.setVisibility(View.VISIBLE);

        } else {
            button.setVisibility(View.VISIBLE);

        }

    }

    private void getUserProfile(AccessToken currentAccessToken) {

        Log.i(TAG, "getUserProfile: "+currentAccessToken.getToken());

        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        //You can fetch user info like this…
                        //object.getJSONObject(“picture”).
                        //getJSONObject(“data”).getString(“url”);
                        //object.getString(“name”);
                        //object.getString(“email”));
                        //object.getString(“id”));



                        if (object.has("email")) {

                            String id = object.optString("email");

                            Call_Api_For_Signup(""+id,""+object.optString("first_name")
                                    ,""+object.optString("last_name"));

                        } else {

                            Toast.makeText(LoginWithFacebook.this, "Email not found", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginWithFacebook.this, SignUpActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                            startActivity(intent);
                            finish();


                        }

                        Log.w(LoginWithFacebook.class.getSimpleName(), "onCompleted: "+object.toString());


                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,birthday,first_name,gender,last_name,email,picture.width(200)");
        request.setParameters(parameters);
        request.executeAsync();

    }


    public void Call_Api_For_Signup(final String id,
                                    final String f_name,
                                    final String l_name) {

        dialog.show();

       /* Log.v("AAAAAAAAA", "id=> " + id);
        Log.v("AAAAAAAAA", "f_name=> " + f_name);
        Log.v("AAAAAAAAA", "l_name=> " + l_name);
        Log.v("AAAAAAAAA", "picture=> " + picture);
        Log.v("AAAAAAAAA", "singnup_type=> " + singnup_type);*/
        Log.v("AAAAAAAAA", "id=> " + id);
        LogInRequest service = new LogInRequest();
        Request request = new Request();
        Data data = new Data();
        data.setEmail(id);
        request.setData(data);
        service.setService("checkexistemail");
        service.setRequest(request);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<LogIn> logInCall = apiInterface.getLogIn(service);
        Log.v("AAAAAAAAA","service=> "+new Gson().toJson(service));
        Log.v("AAAAAAAAA",""+logInCall.request());
        logInCall.enqueue(new Callback<LogIn>() {
            @Override
            public void onResponse(Call<LogIn> call, Response<LogIn> response) {
                Log.i("AAAAAA", "onResponse: "+response.code());
                if (response.isSuccessful()){
                    if (response.body().getSuccess() == 1)
                    {

                        int id = Integer.parseInt(response.body().getData().getUserId());
                        String token = response.body().getToken();

                        Toast toast = Toast.makeText(LoginWithFacebook.this, "Log in Successful!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();

                        PreferenceManager.getDefaultSharedPreferences(LoginWithFacebook.this).edit().putBoolean("isLoggedIn",true).apply();
                        PreferenceManager.getDefaultSharedPreferences(LoginWithFacebook.this).edit().putInt("id",id).apply();
                        PreferenceManager.getDefaultSharedPreferences(LoginWithFacebook.this).edit().putString("token",token).apply();
                        String[] profilePic = response.body().getData().getProfilepic().split("/");
                        PreferenceManager.getDefaultSharedPreferences(LoginWithFacebook.this).edit().putString("profilePic",profilePic[profilePic.length-1]).apply();

                        PrefManager.setFullName(LoginWithFacebook.this,response.body().getData().getFirstName()+" "+response.body().getData().getLastName());

                        PrefManager.setReferralCode(LoginWithFacebook.this,response.body().getData().getReferenceCode());

                        PrefManager.setUserNameEditable(LoginWithFacebook.this,false);

                        Intent intent = new Intent(LoginWithFacebook.this, LogInBSFragment.class);
                        intent.putExtra("isLogInSuccessful",true);
                        setResult(RESULT_OK,intent);
                        finish();

                    }
                    else {
                        getSignUpAPI(f_name, l_name, id, id, "", "");
                    }
                }
                else{
                    Toast.makeText(LoginWithFacebook.this, "Server Error", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<LogIn> call, Throwable t) {
                PreferenceManager.getDefaultSharedPreferences(LoginWithFacebook.this).edit().putBoolean("isLoggedIn",false).apply();
                dialog.dismiss();

            }
        });
    }

    private void getSignUpAPI(String firstName,
                              String lastName,
                              String email,
                              String username,
                              String password,
                              String mobileNumber) {

        SignUpRequest service = new SignUpRequest();

        com.xitij.tiktuk.POJO.SignUp.Request.Request request = new com.xitij.tiktuk.POJO.SignUp.Request.Request();

        String deviceId = NotificationService.getToken(this);

        com.xitij.tiktuk.POJO.SignUp.Request.Data data = new com.xitij.tiktuk.POJO.SignUp.Request.Data();
        data.setType("customer");
        data.setFirstName(firstName);
        data.setLastName(lastName);
        data.setUsername(username);
        data.setEmail(email);
        data.setPassword(password);
        data.setPincode("");
        data.setMobile(mobileNumber);
        data.setPlatform("android");
        data.setDeviceId(deviceId);
        data.setLat("2.17");
        data.setLng("3.501");

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

                        Toast.makeText(LoginWithFacebook.this, "SignUp success.", Toast.LENGTH_SHORT).show();

                        PreferenceManager.getDefaultSharedPreferences(LoginWithFacebook.this).edit().putBoolean("isLoggedIn",true).apply();
                        PreferenceManager.getDefaultSharedPreferences(LoginWithFacebook.this).edit().putInt("id",id).apply();
                        PreferenceManager.getDefaultSharedPreferences(LoginWithFacebook.this).edit().putString("token",token).apply();

                        String[] profilePic = response.body().getData().getProfilepic().split("/");
                        PreferenceManager.getDefaultSharedPreferences(LoginWithFacebook.this).edit().putString("profilePic",profilePic[profilePic.length-1]).apply();

                        PrefManager.setFullName(LoginWithFacebook.this,response.body().getData().getFirstName()+" "+response.body().getData().getLastName());

                        PrefManager.setReferralCode(LoginWithFacebook.this,response.body().getData().getReferenceCode());

                        PrefManager.setUserNameEditable(LoginWithFacebook.this,false);

                        Intent intent = new Intent(LoginWithFacebook.this, LogInBSFragment.class);
                        intent.putExtra("isLogInSuccessful",true);
                        setResult(RESULT_OK,intent);
                        finish();

                    }
                    else if (response.body().getSuccess() == 3) {
                        ///ilEmail.setError(getString(R.string.err_existing_email));
                        //requestFocus(etEmail);
                        PreferenceManager.getDefaultSharedPreferences(LoginWithFacebook.this).edit().putBoolean("isLoggedIn",false).apply();
                    }
                    else if (response.body().getSuccess() == 2) {
                        //ilUserName.setErrorEnabled(true);
                        //ilUserName.setError(getString(R.string.existing_user));
                        //isUsername = false;
                        //requestFocus(etUserName);
                        PreferenceManager.getDefaultSharedPreferences(LoginWithFacebook.this).edit().putBoolean("isLoggedIn",false).apply();
                    }
                    else {
                        Toast.makeText(LoginWithFacebook.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        PreferenceManager.getDefaultSharedPreferences(LoginWithFacebook.this).edit().putBoolean("isLoggedIn",false).apply();
                        Log.e(TAG, "onResponse: Error success=0");

                    }
                    Log.e(TAG, "onResponse: Error success= "+response.body().getSuccess()+" "+response.body().getMessage());
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                PreferenceManager.getDefaultSharedPreferences(LoginWithFacebook.this).edit().putBoolean("isLoggedIn",false).apply();
                dialog.dismiss();
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }

    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            onLoggedIn(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            e.printStackTrace();
            onLoggedIn(null);
        }
    }

    private void onLoggedIn(GoogleSignInAccount account) {

        if (account != null) {
            Log.d(TAG, "onLoggedIn() returned: " + account.getDisplayName() );
            Log.d(TAG, "onLoggedIn() returned: " + account.getEmail() );
            Log.d(TAG, "onLoggedIn() returned: " + account.getIdToken() );
            Log.d(TAG, "onLoggedIn() returned: " + account.getPhotoUrl() );
            Log.d(TAG, "onLoggedIn() returned: " + account.getId() );

            String fname,lname;
            try {
                String[] name = account.getDisplayName().split(" ");
                fname = name[0];
                lname = name[1];

            } catch (Exception e) {
                fname = account.getDisplayName();
                lname = "";
            }



            Call_Api_For_Signup(account.getEmail(),fname, lname);

        }

        //todo handle Missing Bday and email from fb and google


    }

    private void printHashKey() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    BuildConfig.APPLICATION_ID,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

}
