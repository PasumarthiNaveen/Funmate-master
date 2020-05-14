package com.xitij.tiktuk.Fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import com.xitij.tiktuk.Listner.OnVideoListener;
import com.xitij.tiktuk.POJO.Auth;
import com.xitij.tiktuk.POJO.Follow.Data;
import com.xitij.tiktuk.POJO.Follow.DoFollowRequest;
import com.xitij.tiktuk.POJO.Follow.DoFollowResponse;
import com.xitij.tiktuk.POJO.User.Request.Request;
import com.xitij.tiktuk.POJO.User.Request.UserRequest;
import com.xitij.tiktuk.POJO.User.UserResponse;
import com.xitij.tiktuk.POJO.Video.Video;
import com.xitij.tiktuk.R;
import com.xitij.tiktuk.Transformation.CircleTransform;
import com.xitij.tiktuk.Utils.PreciseCount;
import com.xitij.tiktuk.View.LoadingView;

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

public class UserFragment extends Fragment {
    private static final String TAG = UserFragment.class.getSimpleName();
    private UserPostAdapter adapter;
    private List<Video> postVideos,badgeVideos,videos;

    private TextView tvTitle;
    private Button btnFollow;

    private ImageView ivUser;

    private LoadingView loadingView;

    private Boolean isFollowing;

    private OnVideoListener onVideoListener;
    private OnFollowerListener onFollowerListener;

    private int fanCount = 0;

    private String profileUrl = "";

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
        View view = inflater.inflate(R.layout.fragment_user,container,false);

        Toolbar toolbar = view.findViewById(R.id.user_toolbar);
        tvTitle = toolbar.findViewById(R.id.tv_user_title);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);


        String user_id = getArguments().getString("userId");

        Button btnShareProfile = toolbar.findViewById(R.id.btnShareProfile);
        btnShareProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!profileUrl.isEmpty()) {
                    String user_id = getArguments().getString("userId");
                    initBranch(getContext(),profileUrl,user_id);
                }
            }
        });

        initView(view);

        getUserAPI(view,user_id);

        return view;
    }

    private void getUserAPI(final View view, String user_id) {

        loadingView.setVisibility(View.VISIBLE);

        UserRequest userRequest = new UserRequest();

        int id = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("id",-111);

        String userId;

        if (id != -111) {
            userId = ""+id;
        }
        else {
            userId = user_id;
        }

        Request request = new Request();
        request.setUserId(userId);
        request.setId(Integer.parseInt(user_id));

        userRequest.setRequest(request);
        userRequest.setService("get_user_fullunauthprofile");

        Log.w("Request",userRequest.toString());
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<UserResponse> userResponseCall = apiInterface.getUserProfile(userRequest);
        userResponseCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                Log.w("UserFragment", "onResponse: "+response.code()+" "+response.toString()+ " "+response.errorBody()+ " "+ response.headers());
                if (response.isSuccessful()){

                    if (response.body().getSuccess() == 1) {

                        final String profilePath = response.body().getData().getProfilePath();
                        final String profilePic = response.body().getData().getProfilepic();

                        Log.e("UserActivity", "onResponse: "+profilePath+profilePic);

                        if (profilePic.isEmpty()) {
                            Picasso.get().load(R.drawable.blank_profile).transform(new CircleTransform()).into(ivUser);
                        }
                        else {
                            Picasso.get().load(profilePath+profilePic).transform(new CircleTransform()).resize(200,200).into(ivUser);
                        }

                        profileUrl = profilePath+profilePic;

                        final String userId = response.body().getData().getUserId();
                        String userName = response.body().getData().getUsername();
                        Log.i("onResponse", "onResponse: "+userId);

                        tvTitle.setText(response.body().getData().getFirstName()+" "+response.body().getData().getLastName());
                        int fansCount = Integer.parseInt(response.body().getData().getFollowerCount());
                        fanCount = fansCount;
                        String fans = "<span><big><b>"+fansCount+"</b></big><br><small><small>Fans</small></small></span>";
                        String following = "<span><big><b>"+PreciseCount.from(response.body().getData().getFollowingCount())+"</b></big><br><small><small>Following</small></small></span>";

                        isFollowing = response.body().getData().getIsFollowing();

                        if (isFollowing)
                            btnFollow.setText(getResources().getString(R.string.unfollow));
                        else
                            btnFollow.setText(getResources().getString(R.string.follow));

                        final TextView tvUserID,tvFans,tvFollowing,tvBio;
                        tvUserID = view.findViewById(R.id.tv_user_id);
                        tvFans = view.findViewById(R.id.tvfans);
                        tvFollowing = view.findViewById(R.id.tvfollowing);
                        tvBio = view.findViewById(R.id.tvBio);

                        tvUserID.setText(getString(R.string.user_name,userName));
                        tvFans.setText(Html.fromHtml(fans));
                        tvFollowing.setText(Html.fromHtml(following));
                        String bio = response.body().getData().getBio();
                        if (bio.isEmpty()) {
                            tvBio.setText(R.string.no_bio_yet);
                        }
                        else {
                            tvBio.setText(response.body().getData().getBio());
                        }

                        btnFollow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isUserLoggedIn()) {
                                    btnFollow.setClickable(false);
                                    if (isFollowing) {
                                        doFollowing("UserUnfollow","unfollowing",userId);
                                        btnFollow.setText(getResources().getString(R.string.follow));
                                        isFollowing = !isFollowing;
                                        --fanCount;
                                        String fans = "<span><big><b>"+PreciseCount.from(""+fanCount)+"</b></big><br><small><small>Fans</small></small></span>";
                                        tvFans.setText(Html.fromHtml(fans));
                                    }
                                    else {
                                        doFollowing("Userfollow","following",userId);
                                        btnFollow.setText(getResources().getString(R.string.unfollow));
                                        isFollowing = !isFollowing;
                                        fanCount++;
                                        String fans = "<span><big><b>"+PreciseCount.from(""+fanCount)+"</b></big><br><small><small>Fans</small></small></span>";
                                        tvFans.setText(Html.fromHtml(fans));
                                    }
                                }
                                else {
                                    showLogInFragment();
                                }
                            }
                        });

                        Button btnInsta = view.findViewById(R.id.btnInsta);
                        final String instaUrl = response.body().getData().getInstaUrl();
                        if (instaUrl.isEmpty()) {
                            btnInsta.setVisibility(View.INVISIBLE);
                        }
                        else {
                            btnInsta.setVisibility(View.VISIBLE);
                        }
                        btnInsta.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri uri = Uri.parse("http://instagram.com/"+instaUrl);
                                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                                likeIng.setPackage("com.instagram.android");

                                try {
                                    startActivity(likeIng);
                                } catch (ActivityNotFoundException e) {
                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("http://instagram.com/"+instaUrl)));
                                }
                            }
                        });

                        Button btnYoutube = view.findViewById(R.id.btnYoutube);
                        final String youtubeUrl = response.body().getData().getYoutubeUrl();
                        Log.d("TAG", "onResponse() returned: " + youtubeUrl);
                        if (youtubeUrl.isEmpty()) {
                            btnYoutube.setVisibility(View.INVISIBLE);
                        }
                        else {
                            btnYoutube.setVisibility(View.VISIBLE);
                        }
                        btnYoutube.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(youtubeUrl));
                                startActivity(intent);
                            }
                        });

                        postVideos.clear();
                        postVideos.addAll(response.body().getData().getAllvideos());
                        badgeVideos.clear();
                        badgeVideos.addAll(response.body().getData().getWinvideos());
                        videos.clear();
                        videos.addAll(postVideos);
                        adapter.notifyDataSetChanged();

                        tvFollowing.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onFollowerListener.onFollowerSelected(userId);
                            }
                        });
                    }
                    else {
                        Log.e("UserFragment", "onResponse: "+response.body().getMessage());
                    }
                }
                loadingView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("ProfileFragment",t.getMessage());
                loadingView.setVisibility(View.GONE);
            }
        });

    }

    private void doFollowing(String service, String type, String userId) {
        DoFollowRequest followRequest = new DoFollowRequest();

        Auth auth = new Auth();
        int id = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("id",-111);
        String token = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("token","null");

        if (id == -111 || token.equals("null")) {
            showLogInFragment();
            return;
        }

        auth.setToken(token);
        auth.setId(id);

        Data data = new Data();
        data.setUserId(""+id);
        data.setFuserId(userId);
        data.setType(type);

        com.xitij.tiktuk.POJO.Follow.Request request = new com.xitij.tiktuk.POJO.Follow.Request();
        request.setData(data);

        followRequest.setAuth(auth);
        followRequest.setRequest(request);
        followRequest.setService(service);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<DoFollowResponse> responseCall = apiInterface.doFollow(followRequest);
        responseCall.enqueue(new Callback<DoFollowResponse>() {
            @Override
            public void onResponse(Call<DoFollowResponse> call, Response<DoFollowResponse> response) {
                btnFollow.setClickable(true);
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        Log.d("UserFragment", "onResponse() returned: " + response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<DoFollowResponse> call, Throwable t) {
                btnFollow.setClickable(true);
            }
        });



    }

    private void initView(final View view) {

        final SwipeRefreshLayout refreshLayout = view.findViewById(R.id.srl_user);

        loadingView = view.findViewById(R.id.loader_user);
        loadingView.setVisibility(View.GONE);

        ivUser = view.findViewById(R.id.iv_user);

        final LinearLayout btnPosts,btnBadges;
        btnFollow = view.findViewById(R.id.btnFollow);
        btnFollow.setClickable(true);
        btnPosts = view.findViewById(R.id.btnPosts);
        btnBadges = view.findViewById(R.id.btnBadges);

        btnPosts.setActivated(true);
        btnPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!btnPosts.isActivated()){
                    btnBadges.setActivated(false);
                    btnPosts.setActivated(true);
                    videos.clear();
                    videos.addAll(postVideos);
                    adapter.clearAds();
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
                    videos.clear();
                    videos.addAll(badgeVideos);
                    adapter.clearAds();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        RecyclerView rvPosts;

        rvPosts = view.findViewById(R.id.rvPosts);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),3, GridLayoutManager.VERTICAL,false);
        layoutManager.setInitialPrefetchItemCount(9);
        rvPosts.setLayoutManager(layoutManager);
        rvPosts.addItemDecoration(new GridSpacingItemDecoration(3, 30, true));
        postVideos = new ArrayList<>();
        badgeVideos = new ArrayList<>();
        videos = new ArrayList<>();
        adapter = new UserPostAdapter(getContext(), videos);
        adapter.setVideoListener(onVideoListener);
        rvPosts.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String user_id = getArguments().getString("userId");
                getUserAPI(view,user_id);
                refreshLayout.setRefreshing(false);
            }
        });

    }

    private boolean isUserLoggedIn() {
        boolean logIn = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("isLoggedIn",false);

        return logIn;
    }
    private void showLogInFragment() {
        LogInBSFragment fragment = new LogInBSFragment();
        fragment.show(((AppCompatActivity)getContext()).getSupportFragmentManager(), fragment.getTag());
        //todo handle login fragment activity result
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getActivity() != null) {
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    return true;
                }
                else {
                    getActivity().finish();
                }
            }
        }
        return super.onOptionsItemSelected(item);
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
                .setCampaign("TikTuk contest")
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

}
