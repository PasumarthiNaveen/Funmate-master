package com.xitij.tiktuk.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xitij.tiktuk.API.ApiClient;
import com.xitij.tiktuk.API.ApiInterface;
import com.xitij.tiktuk.Adapter.UserPostAdapter;
import com.xitij.tiktuk.ItemDecoration.GridSpacingItemDecoration;
import com.xitij.tiktuk.Listner.OnFollowerListener;
import com.xitij.tiktuk.Listner.OnPostRemoveCallback;
import com.xitij.tiktuk.Listner.OnPostRemoveListener;
import com.xitij.tiktuk.Listner.OnVideoListener;
import com.xitij.tiktuk.POJO.Auth;
import com.xitij.tiktuk.POJO.User.Request.Request;
import com.xitij.tiktuk.POJO.User.Request.UserRequest;
import com.xitij.tiktuk.POJO.User.UserResponse;
import com.xitij.tiktuk.POJO.Video.DeleteVideo.Data;
import com.xitij.tiktuk.POJO.Video.DeleteVideo.DeleteVideoRequest;
import com.xitij.tiktuk.POJO.Video.DeleteVideo.DeleteVideoResponse;
import com.xitij.tiktuk.POJO.Video.Video;
import com.xitij.tiktuk.R;
import com.xitij.tiktuk.Transformation.CircleTransform;
import com.xitij.tiktuk.Utils.PreciseCount;
import com.xitij.tiktuk.Utils.PrefManager;
import com.xitij.tiktuk.View.LoadingView;
import com.xitij.tiktuk.activity.BannerActivity;
import com.xitij.tiktuk.activity.ChangePasswordActivity;
import com.xitij.tiktuk.activity.PointsActivity;
import com.xitij.tiktuk.activity.PrivacyPolicy;
import com.xitij.tiktuk.activity.UpdateProfileActivity;
import com.xitij.tiktuk.activity.UserPointsActivity;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment implements OnPostRemoveListener {
    private static final int UPDATE_REQUEST = 400;
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private SwipeRefreshLayout refreshLayout;

    private UserPostAdapter adapter;

    private List<Video> postVideos,badgeVideos,videos;

    private TextView tvTitle;
    private TextView tvBio;
    private TextView tvUsername;

    private ImageView ivUser;

    private LoadingView loadingView;

    private String profileUrl = "";

    private OnVideoListener onVideoListener;
    private OnFollowerListener onFollowerListener;
    private OnPostRemoveCallback onPostRemoveCallback;

    private boolean isPostVideoSelected = true;

    public void setOnVideoListener(OnVideoListener onVideoListener) {
        this.onVideoListener = onVideoListener;
    }

    public void setOnFollowerListener(OnFollowerListener onFollowerListener) {
        this.onFollowerListener = onFollowerListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        Toolbar toolbar = view.findViewById(R.id.user_toolbar);
        tvTitle = toolbar.findViewById(R.id.tv_user_title);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        Button btnShareProfile = toolbar.findViewById(R.id.ownProfileShare);
        btnShareProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!profileUrl.isEmpty()) {
                    String user_id = getArguments().getString("userId");
                    initBranch(getContext(),profileUrl,user_id);
                }
            }
        });

        Button btnUserPoints = toolbar.findViewById(R.id.btn_user_points);
        btnUserPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PointsActivity.class));
            }
        });

        initView(view);

        getUserAPI(view);

        return view;
    }

    private void getUserAPI(final View view) {
        loadingView.setVisibility(View.VISIBLE);

        UserRequest userRequest = new UserRequest();

        final int id = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("id",-111);
        String token = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("token","null");

        if (id == -111 || token.equals("null")) {
            return;
        }

        Auth auth = new Auth();
        auth.setId(id);
        auth.setToken(token);

        Request request = new Request();
        request.setUserId(""+id);
        request.setId(id);

        userRequest.setAuth(auth);
        userRequest.setRequest(request);
        userRequest.setService("get_user_fullprofile");

        Log.w("Request",userRequest.toString());
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<UserResponse> userResponseCall = apiInterface.getUserProfile(userRequest);
        userResponseCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                Log.w(TAG, "onResponse: "+response.code()+" "+response.toString()+ " "+response.errorBody()+ " "+ response.headers());
                if (response.isSuccessful()){

                    PrefManager.setReferralCode(getContext(),response.body().getData().getReferenceCode());

                    final String profilePath = response.body().getData().getProfilePath();
                    final String profilePic = response.body().getData().getProfilepic();

                    Log.e(TAG, "onResponse: "+profilePath+profilePic);

                    if (profilePic.isEmpty()) {
                        Picasso.get().load(R.drawable.blank_profile).transform(new CircleTransform()).into(ivUser);
                    }
                    else {
                        Picasso.get().load(profilePath+profilePic).transform(new CircleTransform()).resize(200,200).into(ivUser);
                    }

                    profileUrl = profilePath+profilePic;

                    final String username = response.body().getData().getUsername();
                    final String fname = response.body().getData().getFirstName();
                    final String lname = response.body().getData().getLastName();
                    final String bio = response.body().getData().getBio();
                    final String youtubeUrl = response.body().getData().getYoutubeUrl();
                    final String instaUrl = response.body().getData().getInstaUrl();
                    final String email = response.body().getData().getEmail();
                    ImageView iv_user_edit = view.findViewById(R.id.iv_user_edit);
                    iv_user_edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent in = new Intent(getActivity(), UpdateProfileActivity.class);
                            in.putExtra("profilePath",profilePath);
                            in.putExtra("profilePic",profilePic);
                            in.putExtra("firstName",fname);
                            in.putExtra("lastName",lname);
                            in.putExtra("username",username);
                            in.putExtra("bio",bio);
                            in.putExtra("insta",instaUrl);
                            in.putExtra("youtube",youtubeUrl);
                            in.putExtra("email",email);
                            startActivityForResult(in,UPDATE_REQUEST);

                        }
                    });

                    tvTitle.setText(fname+" "+lname);
                    String fans = "<span><big><b>"+ PreciseCount.from(response.body().getData().getFollowerCount())+"</b></big><br><small><small>Fans</small></small></span>";
                    String following = "<span><big><b>"+PreciseCount.from(response.body().getData().getFollowingCount())+"</b></big><br><small><small>Following</small></small></span>";

                    TextView tvFans,tvFollowing;
                    tvFans = view.findViewById(R.id.tvfans);
                    tvFollowing = view.findViewById(R.id.tvfollowing);


                    final String user_id = response.body().getData().getUserId();
                    tvUsername.setText(getString(R.string.user_name,username));
                    tvFans.setText(Html.fromHtml(fans));
                    tvFollowing.setText(Html.fromHtml(following));
                    if (bio.isEmpty()) {
                        tvBio.setText(R.string.no_bio_yet);
                    }
                    else {
                        tvBio.setText(bio);
                    }

                    postVideos.clear();
                    postVideos.addAll(response.body().getData().getAllvideos());
                    badgeVideos.clear();
                    badgeVideos.addAll(response.body().getData().getWinvideos());
                    videos.clear();
                    if (isPostVideoSelected()) {
                        videos.addAll(postVideos);
                    }
                    else {
                        videos.addAll(badgeVideos);
                    }
                    adapter.notifyDataSetChanged();

                    tvFollowing.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onFollowerListener.onFollowerSelected(user_id);
                        }
                    });
                }
                loadingView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                //Log.e("ProfileFragment",t.getMessage());
                loadingView.setVisibility(View.GONE);
            }
        });

    }

    private void initView(View view) {

        refreshLayout = view.findViewById(R.id.srl_profile);

        loadingView = view.findViewById(R.id.loader_profile);
        loadingView.setVisibility(View.GONE);

        tvBio = view.findViewById(R.id.tvBio);
        tvUsername = view.findViewById(R.id.tv_user_id);
        tvUsername.setEnabled(PrefManager.isUserNameEditable(getContext()));

        ivUser = view.findViewById(R.id.iv_user);

        final LinearLayout btnPosts,btnBadges;
        btnPosts = view.findViewById(R.id.btnPosts);
        btnBadges = view.findViewById(R.id.btnBadges);

        btnPosts.setActivated(true);
        btnPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!btnPosts.isActivated()){
                    btnBadges.setActivated(false);
                    btnPosts.setActivated(true);
                    setPostVideoSelected(true);
                    videos.clear();
                    videos.addAll(postVideos);
                    adapter.clearAds();
                    adapter.setLongClickable(true);
                    adapter.setChecked(false);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        btnBadges.setActivated(false);
        btnBadges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!btnBadges.isActivated()){
                    btnBadges.setActivated(true);
                    btnPosts.setActivated(false);
                    setPostVideoSelected(false);
                    videos.clear();
                    videos.addAll(badgeVideos);
                    adapter.clearAds();
                    adapter.setChecked(false);
                    adapter.setLongClickable(false);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        RecyclerView rvPosts;

        rvPosts = view.findViewById(R.id.rvPosts);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),3, GridLayoutManager.VERTICAL,false);
        layoutManager.setItemPrefetchEnabled(true);
        rvPosts.setLayoutManager(layoutManager);
        rvPosts.addItemDecoration(new GridSpacingItemDecoration(3, 20, true));
        postVideos = new ArrayList<>();
        badgeVideos = new ArrayList<>();
        videos = new ArrayList<>();
        adapter = new UserPostAdapter(getContext(), videos);
        adapter.setVideoListener(onVideoListener);
        adapter.setOnPostRemoveListener(this);
        adapter.setLongClickable(true);
        rvPosts.setAdapter(adapter);

        final View fView = view;

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserAPI(fView);
                refreshLayout.setRefreshing(false);
            }
        });

    }

    private void getLogOutAPI() {
        if (getContext() != null) {
            try {
                OutputStreamWriter stream = new OutputStreamWriter(getContext().openFileOutput("like.txt",Context.MODE_PRIVATE));
                stream.write("");
                stream.flush();
                stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().clear().apply();
            getContext().getSharedPreferences("_",MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(getContext(), BannerActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_change_password) {
            startActivity(new Intent(getContext(), ChangePasswordActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.menu_privacy_policy) {
            startActivity(new Intent(getContext(), PrivacyPolicy.class));
            return true;
        }
        if (item.getItemId() == R.id.menu_Logout) {
            getLogOutAPI();
            return true;
        }
        if (item.getItemId() == R.id.menu_wallet) {
            startActivity(new Intent(getContext(), UserPointsActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.menu_rate_us) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.xitij.tiktuk")));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.xitij.tiktuk")));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UPDATE_REQUEST) {
            if (resultCode == RESULT_OK) {
                //String profilePic = data.getStringExtra("profilePic");
                String username = data.getStringExtra("username");
                String fname = data.getStringExtra("fname");
                String lname = data.getStringExtra("lname");
                String bio = data.getStringExtra("bio");
                String profilePic = data.getStringExtra("profilePic");
                String profilePath = data.getStringExtra("profilePath");

                tvTitle.setText(fname+" "+lname);
                tvBio.setText(bio);
                tvUsername.setText(getString(R.string.user_name,username));

                if (!profilePic.isEmpty()) {
                    profileUrl = profilePath+profilePic;
                    Picasso.get().load(profilePath+profilePic).transform(new CircleTransform()).into(ivUser);
                }



                Log.i(TAG, "onActivityResult: fname"+fname);
                Log.i(TAG, "onActivityResult: bio"+bio);
            }
        }
    }

    private void initBranch(Context context, String url, String id) {

        //todo add video url and image url too in json!

        String json = "{\"id\":\""+id+"\",\"isProfile\":false}";
        String title = "TikTuk profile";

        Log.i("ShareJson","Json Object: "+json);
        BranchUniversalObject buo = new BranchUniversalObject()
                .setCanonicalIdentifier("content/12345")
                .setTitle("TikTuk")
                .setContentImageUrl(url)
                .setContentDescription("Check out my TikTuk profile")
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setContentMetadata(new ContentMetadata().addCustomMetadata("data",json));

        LinkProperties lp = new LinkProperties()
                .setFeature("sharing")
                .setCampaign("Content launch")
                .setStage("User")
                .addControlParameter("$desktop_url", "http://tiktukreward.ga/playstore.php")
                .addControlParameter("custom", "data")
                .addControlParameter("custom_random", Long.toString(Calendar.getInstance().getTimeInMillis()));

        ShareSheetStyle ss = new ShareSheetStyle(context, title, title)
                .setCopyUrlStyle(ContextCompat.getDrawable(context, android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
                .setMoreOptionStyle(ContextCompat.getDrawable(context, android.R.drawable.ic_menu_more), "Show more")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK_MESSENGER)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FLICKR)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.WHATS_APP)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.GMAIL)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.GOOGLE_DOC)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.HANGOUT)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.INSTAGRAM)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.PINTEREST)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.SNAPCHAT)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.TWITTER)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.WECHAT)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
                .setAsFullWidthStyle(true)
                .setSharingTitle("Share With");

        buo.showShareSheet((Activity) context, lp,  ss,  new Branch.BranchLinkShareListener() {
            @Override
            public void onShareLinkDialogLaunched() {
            }
            @Override
            public void onShareLinkDialogDismissed() {
            }
            @Override
            public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error) {
                Log.i("Branch", "onLinkShareResponse: SharingLink: "+sharedLink);
            }
            @Override
            public void onChannelSelected(String channelName) {

            }
        });
    }

    @Override
    public void onPostRemoved(int position, OnPostRemoveCallback onPostRemoveCallback) {

        this.onPostRemoveCallback = onPostRemoveCallback;
        showConfirmDialog(position);

    }

    private void showConfirmDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.app_name);
        builder.setMessage("Confirm delete?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getDeletePostAPI(position);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onPostRemoveCallback.onFail();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);

    }

    private void getDeletePostAPI(final int position) {

        loadingView.setVisibility(View.VISIBLE);

        DeleteVideoRequest deleteVideoRequest = new DeleteVideoRequest();

        com.xitij.tiktuk.POJO.Video.DeleteVideo.Request request = new com.xitij.tiktuk.POJO.Video.DeleteVideo.Request();

        Data data = new Data();
        data.setVid(postVideos.get(position).getVid());

        request.setData(data);

        final int id = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("id",-111);
        String token = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("token","null");

        if (id == -111 || token.equals("null")) {
            this.onPostRemoveCallback.onFail();
            return;
        }

        Auth auth = new Auth();
        auth.setId(id);
        auth.setToken(token);

        deleteVideoRequest.setAuth(auth);
        deleteVideoRequest.setRequest(request);
        deleteVideoRequest.setService("deletevideo");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<DeleteVideoResponse> call = apiInterface.deleteVideo(deleteVideoRequest);
        call.enqueue(new Callback<DeleteVideoResponse>() {
            @Override
            public void onResponse(Call<DeleteVideoResponse> call, Response<DeleteVideoResponse> response) {
                Log.i(TAG, "onResponse: "+response.code());
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        ProfileFragment.this.onPostRemoveCallback.onSuccess(position);
                    }
                    else {
                        Log.i(TAG, "onResponse: "+response.body().getMessage());
                        ProfileFragment.this.onPostRemoveCallback.onFail();
                    }
                }
                loadingView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<DeleteVideoResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: "+t.getMessage());
                ProfileFragment.this.onPostRemoveCallback.onFail();
                loadingView.setVisibility(View.GONE);
            }
        });

    }

    public boolean isPostVideoSelected() {
        return isPostVideoSelected;
    }

    public void setPostVideoSelected(boolean postVideoSelected) {
        isPostVideoSelected = postVideoSelected;
    }
}
