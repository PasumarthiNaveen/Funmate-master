package com.xitij.tiktuk.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xitij.tiktuk.R;
import com.xitij.tiktuk.activity.LogInActivity;
import com.xitij.tiktuk.activity.LoginWithFacebook;
import com.xitij.tiktuk.activity.PrivacyPolicy;
import com.xitij.tiktuk.activity.SignUpActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;
import com.xitij.tiktuk.API.ApiClient;
import com.xitij.tiktuk.API.ApiInterface;
import com.xitij.tiktuk.POJO.LogIn.Request.Data;
import com.xitij.tiktuk.POJO.LogIn.Request.LogInRequest;
import com.xitij.tiktuk.POJO.LogIn.Request.Request;
import com.xitij.tiktuk.POJO.LogIn.Response.LogIn;

import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogInBSFragment extends BottomSheetDialogFragment {

    private static final String TAG = LogInBSFragment.class.getSimpleName();
    private boolean isProfileActivity;

    private static final int LOG_IN = 819;
    FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 234;
    GoogleSignInClient mGoogleSignInClient;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bs_login, container, false);

        if (getArguments() != null) {
            isProfileActivity = getArguments().getBoolean("isProfileActivity", false);
        }

        //FacebookSdk.sdkInitialize(getActivity());

        //callbackManager = CallbackManager.Factory.create();

        //LoginManager.getInstance().logOut();

        //first we intialized the FirebaseAuth object
        mAuth = FirebaseAuth.getInstance();

        //Then we need a GoogleSignInOptions object
        //And we need to build it as below
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Then we will get the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        initView(view);

        return view;
    }

    private void initView(View view) {
        printKeyHash();
        //Terms and Conditions;
        TextView tvTerms = view.findViewById(R.id.tv_login_terms);

        SpannableString ss = new SpannableString(getResources().getString(R.string.terms_and_conditions));
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        ss.setSpan(boldSpan, 48, 66, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(getContext(), PrivacyPolicy.class));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(span, 48, 66, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvTerms.setText(ss);
        tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
        tvTerms.setHighlightColor(Color.TRANSPARENT);
        //--------------****************------------------//

        TextView tvNeedAC = view.findViewById(R.id.tv_need_account);

        String text = "<font color=#000000>You need a </font><font color=#FF1744>TikTuk</font><font color=#000000> account to continue</font>";
        tvNeedAC.setText(Html.fromHtml(text));

        Button btnSignUp = view.findViewById(R.id.btn_bs_signup);
        ImageView facebook_btn = view.findViewById(R.id.facebook_btn);
        ImageView google_btn = view.findViewById(R.id.google_btn);
        //LoginButton login_button = view.findViewById(R.id.login_button);
        Button btnAlredyAc = view.findViewById(R.id.btn_already_account);

        facebook_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                //Loginwith_FB();
                if (!isProfileActivity) {
                    dismiss();
                }
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), LoginWithFacebook.class);
                    intent.putExtra("isfb",true);
                    getActivity().startActivityForResult(intent, LOG_IN);
                }
                //startActivity(new Intent(getContext(), LoginWithFacebook.class));
            }
        });

        google_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isProfileActivity) {
                    dismiss();
                }
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), LoginWithFacebook.class);
                    intent.putExtra("isfb",false);
                    getActivity().startActivityForResult(intent, LOG_IN);
                }
            }
        });

        text = "<font color=#FFFFFF>Already have account?   </font><font color=#FFD600>LogIn</font>";
        btnAlredyAc.setText(Html.fromHtml(text));

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isProfileActivity) {
                    dismiss();
                }
                if (getActivity() != null) {
                    getActivity().startActivityForResult(new Intent(getActivity(), SignUpActivity.class), LOG_IN);
                }
            }
        });
        btnAlredyAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isProfileActivity) {
                    dismiss();
                }
                if (getActivity() != null) {
                    getActivity().startActivityForResult(new Intent(getActivity(), LogInActivity.class), LOG_IN);
                }
            }
        });

        /*final LoginButton loginButton = view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email","user_birthday","user_gender");
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
        });*/

    }

    private void getUserProfile(AccessToken currentAccessToken) {

        Log.i(TAG, "getUserProfile: "+currentAccessToken);

        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            //You can fetch user info like this…
                            //object.getJSONObject(“picture”).
                            //getJSONObject(“data”).getString(“url”);
                            //object.getString(“name”);
                            //object.getString(“email”));
                            //object.getString(“id”));

                            Log.w(LogInBSFragment.class.getSimpleName(), "onCompleted: " + object.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    // Bottom two function are related to Fb implimentation


    //facebook implimentation
    public void Loginwith_FB() {

        LoginManager.getInstance()
                .logInWithReadPermissions(getActivity(),
                        Arrays.asList("public_profile", "email"));

        // initialze the facebook sdk and request to facebook for login


        /*LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                Log.d("resp_token", loginResult.getAccessToken() + "");
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(getActivity(), "Login Cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("resp", "" + error.toString());
                Toast.makeText(getActivity(), "Login Error" + error.toString(), Toast.LENGTH_SHORT).show();
            }

        });*/

    }

    public void printKeyHash()  {
        try {
            PackageInfo info = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName() , PackageManager.GET_SIGNATURES);
            for(Signature signature:info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("keyhash" , Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        // if user is login then this method will call and
        // facebook will return us a token which will user for get the info of user
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        Log.d("resp_token",token.getToken()+"");
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final String id = Profile.getCurrentProfile().getId();
                            GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject user, GraphResponse graphResponse) {

                                    Log.d("resp",user.toString());
                                    //after get the info of user we will pass to function which will store the info in our server
                                    Call_Api_For_Signup(""+id,""+user.optString("first_name")
                                            ,""+user.optString("last_name"),
                                            "https://graph.facebook.com/"+id+"/picture?width=500&width=500",
                                            "facebook");

                                }
                            });

                            // here is the request to facebook sdk for which type of info we have required
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "last_name,first_name,email");
                            request.setParameters(parameters);
                            request.executeAsync();
                        } else {

                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                Log.v("Task",""+new Gson().toJson(task));
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.v("Account",""+new Gson().toJson(account));

                //authenticating with firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Google ID", "firebaseAuthWithGoogle:" + acct.getId());

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Success", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Toast.makeText(getActivity(), "User Signed In", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("AAAAA", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }


    public void Call_Api_For_Signup(String id,
                                    String f_name,
                                    String l_name,
                                    String picture,
                                    String singnup_type) {

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

                        Toast toast = Toast.makeText(getActivity(), "Log in Successful!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();

                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("isLoggedIn",true).apply();
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putInt("id",id).apply();
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("token",token).apply();
                        String[] profilePic = response.body().getData().getProfilepic().split("/");
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("profilePic",profilePic[profilePic.length-1]).apply();

                        dismiss();
                    }
                    else {
                        Toast toast = Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                }
                else{
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("isLoggedIn",false).apply();
                    try {
                        Log.e("AAAAAA", "onResponse: error: "+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<LogIn> call, Throwable t) {
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("isLoggedIn",false).apply();

            }
        });
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (isProfileActivity) {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        }
        super.onCancel(dialog);
    }

}