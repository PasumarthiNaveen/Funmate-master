package com.xitij.tiktuk.Fragment;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.vollink.jiaozivideoplayer.Jzvd;
import com.xitij.tiktuk.API.ApiClient;
import com.xitij.tiktuk.API.ApiInterface;
import com.xitij.tiktuk.Adapter.CommentAdapter;
import com.xitij.tiktuk.POJO.Auth;
import com.xitij.tiktuk.POJO.LikenShare.Data;
import com.xitij.tiktuk.POJO.LikenShare.LikenShareResponse;
import com.xitij.tiktuk.POJO.LikenShare.Request;
import com.xitij.tiktuk.POJO.LikenShare.Requests.AddLikeRequest;
import com.xitij.tiktuk.POJO.LikenShare.Requests.ShareRequest;
import com.xitij.tiktuk.POJO.Video.Video;
import com.xitij.tiktuk.R;
import com.xitij.tiktuk.Transformation.CircleTransform;
import com.xitij.tiktuk.Utils.PreciseCount;
import com.xitij.tiktuk.View.JZExoPlayer;
import com.xitij.tiktuk.View.LikeButton.LikeButton;
import com.xitij.tiktuk.View.VideoPlayer;
import com.xitij.tiktuk.activity.UserActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

public class SharedVideoFragment extends Fragment implements View.OnClickListener,
        LikeButton.OnLikeEventListener,
        CommentBSFragment.OnCommentAddListener,
        CommentAdapter.OnCommentRemoveListener {

    private static final String VIDEO_BASE_URL = "http://3.212.177.112:80/uploads/videos/";
    private static final String PROFIL_URL = "http://3.212.177.112:80/uploads/profile_pic/";
    private String[] ids;

    private VideoPlayer player;
    private Video video;

    private DownloadManager downloadManager;
    private long refid;
    private ArrayList<Long> list = new ArrayList<>();
    private String filename;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shared_video,container,false);

        String video = getArguments().getString("video","null");

        this.ids = readFromFile(getContext()).split(",");

        if (!video.equals("null")) {
            initView(view,video);
        }

        downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        getContext().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        return view;
    }

    private void initView(View view, String json) {

        LinearLayout back = view.findViewById(R.id.share_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        Gson gson = new Gson();

        Type type = new TypeToken<Video>(){}.getType();
        video = gson.fromJson(json,type);

        Jzvd.setMediaInterface(new JZExoPlayer(getContext()));

        player = view.findViewById(R.id.shared_player);

        String url = video.getVideoUrl();
        String title = video.getVid();

        player.setUp(VIDEO_BASE_URL+url,title, Jzvd.SCREEN_WINDOW_NORMAL);
        player.setBufferProgress(0);
        Jzvd.SAVE_PROGRESS = false;

        player.titleTextView.setVisibility(View.GONE);
        player.progressBar.setVisibility(View.GONE);
        player.currentTimeTextView.setVisibility(View.GONE);
        player.startButton.performClick();

        TextView tvComment,tvShare,tvDownload,tvVideUsername,tvVideoContestName;
        ImageView ivUser,ivWin;
        LikeButton likeButton;
        //tvLike = itemView.findViewById(R.id.btnLike);
        tvComment = view.findViewById(R.id.btnComment);
        tvShare = view.findViewById(R.id.btnshare);
        tvDownload = view.findViewById(R.id.btndownload);
        tvVideUsername = view.findViewById(R.id.tv_video_username);
        tvVideoContestName = view.findViewById(R.id.tv_video_contest_name);

        ivUser = view.findViewById(R.id.iv_video_user);
        ivWin = view.findViewById(R.id.iv_win);

        likeButton = view.findViewById(R.id.likeButton);

        tvComment.setOnClickListener(this);
        tvShare.setOnClickListener(this);
        ivUser.setOnClickListener(this);


        Integer likeCount = Integer.parseInt(video.getTotalLikes());

        if (Arrays.asList(ids).contains(video.getVid())) {
            likeButton.setCurrentlyLiked(true);
        }
        else {
            likeButton.setCurrentlyLiked(false);
        }
        likeButton.setLikeIcon(getContext().getResources().getDrawable(R.drawable.ic_like_red));
        likeButton.setUnlikeIcon(getContext().getResources().getDrawable(R.drawable.ic_like));
        likeButton.setCount(likeCount);
        likeButton.setOnLikeEventListener(this);
        likeButton.setPositon(1);

        //myViewHolder.tvLike.setText(videos.get(i).getTotalLike());
        tvComment.setText(PreciseCount.from(video.getTotalComment()));
        tvShare.setText(PreciseCount.from(video.getTotalShare()));
        tvVideUsername.setText(getContext().getString(R.string.user_name,video.getUsername()));
        tvVideoContestName.setText(video.getContestTitle());

        String profileUrl = video.getProfilepic();
        String thumbUrl = video.getThumbUrl();

        int win = Integer.parseInt(video.getIsWin());
        if (win == 1) {
            ivWin.setVisibility(View.VISIBLE);
        }
        else {
            ivWin.setVisibility(View.GONE);
        }

        if (profileUrl.isEmpty()){
            Picasso.get().load(R.drawable.blank_profile).transform(new CircleTransform()).into(ivUser);
        }
        else {
            Picasso.get().load(PROFIL_URL+profileUrl).placeholder(R.drawable.blank_profile).transform(new CircleTransform()).resize(90,90).into(ivUser);
        }

        //Disable own profile on video
        if (isUserLoggedIn()){
            int id = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("id",-111);
            if (id != -111) {
                if (video.getUserId().equals(""+id)) {
                    ivUser.setVisibility(View.INVISIBLE);
                }
            }
        }



        /*
         * Downloading Video
         * */
        final String downloadUrl = VIDEO_BASE_URL+video.getVideoUrl();
        tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse("" + downloadUrl));
                String[] filename = downloadUrl.split("/");
                SharedVideoFragment.this.filename = filename[filename.length-1];
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                request.setAllowedOverRoaming(true);
                request.setTitle(SharedVideoFragment.this.filename);
                request.setVisibleInDownloadsUi(true);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/TikTuk/" + SharedVideoFragment.this.filename);


                refid = downloadManager.enqueue(request);


                Log.e("OUTbtnDownload", "" + refid+ " "+filename[filename.length-1]);

                list.add(refid);
                Toast.makeText(getContext(), "Downloading...", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnComment:
                if (player.currentState == Jzvd.CURRENT_STATE_PLAYING)
                {
                    player.startButton.performClick();
                }
                handleComment();
                break;
            case R.id.btnshare:
                if (player.currentState == Jzvd.CURRENT_STATE_PLAYING)
                {
                    player.startButton.performClick();
                }
                initBranch();
                break;
            case R.id.iv_video_user:
                handleUserProfile();
                break;

        }
    }

    private boolean isUserLoggedIn() {
        return PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("isLoggedIn",false);
    }

    @Override
    public void onLikeClicked(LikeButton androidLikeButton) {

        if (isUserLoggedIn()) {
            int likeCount = androidLikeButton.getCount();
            likeCount = likeCount+1;
            androidLikeButton.setCount(likeCount);
            addLikeAPI();
        }
        else {
            androidLikeButton.performClick();
            showLogInFragment();
        }

    }

    @Override
    public void onUnlikeClicked(LikeButton androidLikeButton) {

        if (isUserLoggedIn()) {

            int likeCount = androidLikeButton.getCount();
            likeCount = likeCount-1;
            androidLikeButton.setCount(likeCount);

            String[] ids = readFromFile(getContext()).split(",");
            List<String> list = new ArrayList<>(Arrays.asList(ids));
            list.remove(video.getVid());
            ids = list.toArray(new String[] {});
            Log.e("Straing", "onUnlikeClicked: "+stringArrayToString(ids));
            writeToFile(stringArrayToString(ids),getContext());

        }

    }

    @Override
    public void onCommentAdded(int position) {
        int count = Integer.parseInt(video.getTotalComment());
        video.setTotalComment(""+(count+1));

        TextView tvComment;
        if (getView() != null) {
            tvComment = getView().findViewById(R.id.btnComment);
            tvComment.setText(PreciseCount.from(video.getTotalComment()));
        }

    }

    @Override
    public void onCommentRemoved(int position) {
        int count = Integer.parseInt(video.getTotalComment());
        video.setTotalComment(""+(count-1));

        TextView tvComment;
        if (getView() != null) {
            tvComment = getView().findViewById(R.id.btnComment);
            tvComment.setText(PreciseCount.from(video.getTotalComment()));
        }
    }

    private void handleComment() {
        if (isUserLoggedIn()){
            showCommentFragment();
        }
        else {
            showLogInFragment();
        }
    }

    private void initBranch() {

        //todo change desktop url

        String vUrl = video.getVideoUrl();
        video.setVideoUrl(vUrl);
        String url = video.getThumbUrl();
        video.setThumbUrl(url);

        Gson gson = new Gson();
        String json = gson.toJson(video);

        String title = video.getContestTitle();

        Log.i("ShareJson","Json Object: "+json);
        BranchUniversalObject buo = new BranchUniversalObject()
                .setCanonicalIdentifier("content/12345")
                .setTitle("TikTuk")
                .setContentImageUrl(url)
                .setContentDescription("Check out this new video")
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setContentMetadata(new ContentMetadata().addCustomMetadata("data",json));

        LinkProperties lp = new LinkProperties()
                .setFeature("sharing")
                .setCampaign(title)
                .setStage("TikTuk user")
                .addControlParameter("$desktop_url", "http://tiktukreward.ga/playstore.php")
                .addControlParameter("custom", "data")
                .addControlParameter("custom_random", Long.toString(Calendar.getInstance().getTimeInMillis()));

        ShareSheetStyle ss = new ShareSheetStyle(getContext(), title, title)
                .setCopyUrlStyle(ContextCompat.getDrawable(getContext(), android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
                .setMoreOptionStyle(ContextCompat.getDrawable(getContext(), android.R.drawable.ic_menu_more), "Show more")
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

        buo.showShareSheet((Activity) getContext(), lp,  ss,  new Branch.BranchLinkShareListener() {
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

                Log.i("Branch", "onChannelSelected: "+channelName);
                getShareAPI();

            }
        });
    }

    private void getShareAPI() {

        ShareRequest shareRequest = new ShareRequest();

        Data data = new Data();
        data.setVideoId(video.getVid());
        data.setType("share");
        data.setCount("1");
        data.setUserId(video.getUserId());

        Request request = new Request();
        request.setData(data);

        shareRequest.setRequest(request);
        shareRequest.setService("SaveShare");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<LikenShareResponse> responseCall = apiInterface.saveShare(shareRequest);
        responseCall.enqueue(new Callback<LikenShareResponse>() {
            @Override
            public void onResponse(Call<LikenShareResponse> call, Response<LikenShareResponse> response) {
                Log.i("VideoAdapter", "onResponse: "+response.code());
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        int share = Integer.parseInt(video.getTotalShare());
                        video.setTotalShare(""+(share+1));
                    }
                }
            }

            @Override
            public void onFailure(Call<LikenShareResponse> call, Throwable t) {

            }
        });

    }

    private void handleUserProfile() {
        Intent intent = new Intent(getContext(), UserActivity.class);
        intent.putExtra("userId",video.getUserId());
        getContext().startActivity(intent);
    }

    private void showCommentFragment() {
        int id = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("id",-111);
        String profilePic = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("profilePic","");

        Bundle bundle = new Bundle();
        bundle.putString("totalComment",video.getTotalComment());
        bundle.putString("vid",video.getVid());
        bundle.putString("user_id",""+id);
        bundle.putString("profilePic",profilePic);
        CommentBSFragment fragment = new CommentBSFragment();
        fragment.setOnCommentAddListener(this,0);
        fragment.setOnCommentRemoveListener(this,0);
        fragment.setArguments(bundle);
        fragment.show(((AppCompatActivity)getContext()).getSupportFragmentManager(),fragment.getTag());
    }

    private void showLogInFragment() {
        LogInBSFragment fragment = new LogInBSFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isProfileActivity",false);
        fragment.setArguments(bundle);
        fragment.show(((AppCompatActivity)getContext()).getSupportFragmentManager(), fragment.getTag());
    }

    private void addLikeAPI() {

        AddLikeRequest likeRequest = new AddLikeRequest();

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
        data.setVideoId(video.getVid());
        data.setType("likes");
        data.setCount("1");
        data.setUserId(video.getUserId());

        Request request = new Request();
        request.setData(data);

        likeRequest.setAuth(auth);
        likeRequest.setRequest(request);
        likeRequest.setService("SaveLikes");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<LikenShareResponse> responseCall = apiInterface.addLike(likeRequest);
        responseCall.enqueue(new Callback<LikenShareResponse>() {
            @Override
            public void onResponse(Call<LikenShareResponse> call, Response<LikenShareResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        String[] ids = readFromFile(getContext()).split(",");
                        Log.e("IDS", "onResponse: "+stringArrayToString(ids));
                        List<String> list = new ArrayList<>(Arrays.asList(ids));
                        list.add(video.getVid());
                        Set<String> stringSet = new HashSet<>( list );
                        ids = stringSet.toArray(new String[] {});
                        Log.e("Straing", "onResponse: "+stringArrayToString(ids));
                        writeToFile(stringArrayToString(ids),getContext());
                    }
                }
            }

            @Override
            public void onFailure(Call<LikenShareResponse> call, Throwable t) {

            }
        });

    }

    private String readFromFile(Context context) {
        String data = "";

        try {

            InputStream stream = context.openFileInput("like.txt");


            InputStreamReader streamReader = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(streamReader);
            String recievedData;

            StringBuilder builder = new StringBuilder();

            while ((recievedData = reader.readLine()) != null) {
                builder.append(recievedData);
            }

            stream.close();

            data = builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    private void writeToFile(String text, Context context) {
        try {
            OutputStreamWriter stream = new OutputStreamWriter(context.openFileOutput("like.txt",Context.MODE_PRIVATE));
            stream.write(text);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("VideoAdapter", "writeToFile: failed");
        }
    }

    private static String stringArrayToString(String[] stringArray) {
        StringBuilder sb = new StringBuilder();
        for ( String element : stringArray ) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append( element );
        }
        return sb.toString();
    }

    private BroadcastReceiver onComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long refrenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            Log.i("INrefrenceId", "" + refrenceId);
            list.remove(refrenceId);
            if (list.isEmpty()) {
                File file;

                Uri path;

                if (Build.VERSION.SDK_INT >= 24) {
                    file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                            + File.separator + "/TikTuk/" + filename);
                    path = FileProvider.getUriForFile(context, "com.xitij.tiktuk.provider", file);
                } else {
                    file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + "/TikTuk/" + filename);
                    path = Uri.fromFile(file);
                }


                String channelid = context.getString(R.string.default_notification_channel_id);
                Log.i("Path", "" + path.toString());
                Intent open = new Intent(Intent.ACTION_VIEW);
                open.setDataAndType(path, "video/*");
                open.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, open, 0);
                Log.i("INSIDE refrenceId", "" + refrenceId);
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelid)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent)
                        .setContentTitle(filename)
                        .setContentText("Download Complete")
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setSound(defaultSoundUri)
                        .setAutoCancel(true);
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(channelid, "TikTuk Notification", NotificationManager.IMPORTANCE_HIGH);
                    assert notificationManager != null;
                    notificationManager.createNotificationChannel(channel);
                }
                if (notificationManager != null) {
                    notificationManager.notify(m/*Notification id*/, builder.build());
                }

            }
        }
    };

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(onComplete);
        super.onDestroy();
    }
}
