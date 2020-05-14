package com.xitij.tiktuk.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vollink.jiaozivideoplayer.JZMediaManager;
import com.vollink.jiaozivideoplayer.Jzvd;
import com.vollink.jiaozivideoplayer.JzvdMgr;
import com.xitij.tiktuk.API.ApiClient;
import com.xitij.tiktuk.API.ApiInterface;
import com.xitij.tiktuk.Adapter.VideoAdapter;
import com.xitij.tiktuk.Listner.OnLoadMoreListener;
import com.xitij.tiktuk.POJO.Video.Request.Live.Data;
import com.xitij.tiktuk.POJO.Video.Request.Live.Request;
import com.xitij.tiktuk.POJO.Video.Request.Live.LiveVideoRequest;
import com.xitij.tiktuk.POJO.Video.Video;
import com.xitij.tiktuk.POJO.Video.VideoResponse;
import com.xitij.tiktuk.POJO.service.Service;
import com.xitij.tiktuk.POJO.service.ServiceResponse;
import com.xitij.tiktuk.POJO.service.data.VideoCountData;
import com.xitij.tiktuk.R;
import com.xitij.tiktuk.View.LoadingView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;

public class LiveVideoFragment extends Fragment implements RecyclerView.OnChildAttachStateChangeListener, OnLoadMoreListener {
    private static final String TAG = PopularVideoFragment.class.getSimpleName();
    private LoadingView loadingView;
    private TextView tvError;
    private VideoAdapter adapter;
    private List<Video> videos;

    private int page;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video,container,false);

        RecyclerView recyclerView = view.findViewById(R.id.rv_popular);
        loadingView = view.findViewById(R.id.list_loading);
        tvError = view.findViewById(R.id.tv_internet_error);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        //layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setItemViewCacheSize(5);
        ///recyclerView.getRecycledViewPool().setMaxRecycledViews(VideoAdapter.AD_TYPE,10);

        videos = new ArrayList<>();

        adapter = new VideoAdapter(getActivity(),videos,recyclerView);
        adapter.setOnLoadMoreListener(this);
        recyclerView.setAdapter(adapter);
        page = 1;
        getVideoAPI(1);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.addOnChildAttachStateChangeListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == SCROLL_STATE_TOUCH_SCROLL) {

                }

                Jzvd.releaseAllVideos();
                int pos = layoutManager.findFirstVisibleItemPosition();

                Log.i("Current","Position: "+layoutManager.findFirstVisibleItemPosition());
                if (layoutManager.findViewByPosition(pos) != null)
                {
                    if (layoutManager.getItemViewType(layoutManager.findViewByPosition(pos)) != VideoAdapter.AD_TYPE) {
                        Jzvd jzvd = layoutManager.findViewByPosition(pos).findViewById(R.id.player);
                        if (jzvd != null) {
                            countVideoView(videos.get(pos).getVid());
                            jzvd.startButton.performClick();
                        }
                    }
                }


            }
        });
        Log.i(TAG, "onCreateView: CAlled");
        return view;
    }

    private void getVideoAPI(final int page) {
        LiveVideoRequest videoRequest = new LiveVideoRequest();

        Request request = new Request();

        String contestId = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("contest_id","1");

        Data data = new Data();
        data.setId(contestId);
        data.setPage(page);

        request.setData(data);

        videoRequest.setService("getContestLivevideo");
        videoRequest.setRequest(request);


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<VideoResponse> videoResponseCall = apiInterface.getLiveVideos(videoRequest);

        videoResponseCall.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {

                if (page == 1) {
                    loadingView.setVisibility(View.GONE);
                }
                Log.i(TAG, "onResponse: "+response.code());
                if (response.isSuccessful()){
                    if (response.body().getSuccess() == 1){
                        videos.addAll(response.body().getVideos());
                        adapter.notifyDataSetChanged();
                        adapter.setLoaded();
                        if (page != 1) {
                            Intent intent = new Intent("load_more");
                            intent.putExtra("isLoading",false);
                            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                        }
                        Log.i(TAG, "onResponse: "+videos.size());
                    }
                    else{
                        Log.e(TAG, "onResponse: no success");
                        if (page != 1) {
                            Intent intent = new Intent("load_more");
                            intent.putExtra("isLoading",false);
                            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                        }
                        else {
                            if (videos.size() == 0) {
                                tvError.setVisibility(View.VISIBLE);
                                tvError.setText(R.string.no_video_yet);
                            }
                            else {
                                tvError.setVisibility(View.GONE);
                            }
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                final CharSequence CONNECTEXCEPTION = "java.net.ConnectException";
                if (page == 1) {
                    loadingView.setVisibility(View.GONE);
                    if (t.toString().contains(CONNECTEXCEPTION)){
                        tvError.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    Intent intent = new Intent("load_more");
                    intent.putExtra("isLoading",false);
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                }
                Log.e(TAG, "onFailure: "+t.getMessage()+" "+t.toString());
            }
        });
    }

    private void countVideoView(String vId) {

        Service service = new Service();

        com.xitij.tiktuk.POJO.service.Auth auth = new com.xitij.tiktuk.POJO.service.Auth();

        int id = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("id",-111);
        String token = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("token","null");

        String contestId = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("contest_id","1");

        if (id == -111 || token.equals("null")) {
            Log.i(TAG, "addVideoAPI: returned");
            return;
        }

        auth.setId(id);
        auth.setToken(token);

        VideoCountData data = new VideoCountData();
        data.setUserId(""+id);
        data.setContId(contestId);
        data.setVideoId(vId);

        com.xitij.tiktuk.POJO.service.Request request = new com.xitij.tiktuk.POJO.service.Request();
        request.setData(data);

        service.setAuth(auth);
        service.setRequest(request);
        service.setService("videoview");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ServiceResponse> call = apiInterface.countView(service);
        call.enqueue(new Callback<ServiceResponse>() {
            @Override
            public void onResponse(Call<ServiceResponse> call, Response<ServiceResponse> response) {
            }

            @Override
            public void onFailure(Call<ServiceResponse> call, Throwable t) {
            }
        });

    }

    @Override
    public void onChildViewAttachedToWindow(@NonNull View view) {
        Log.i("this","is being called detached "+ JZMediaManager.getCurrentUrl());
        Jzvd jzvd = view.findViewById(R.id.player);
        if (jzvd != null && jzvd.jzDataSource.containsTheUrl(JZMediaManager.getCurrentUrl())) {
            Jzvd currentJzvd = JzvdMgr.getCurrentJzvd();
            if (currentJzvd != null && currentJzvd.currentScreen != Jzvd.SCREEN_WINDOW_FULLSCREEN) {
                Jzvd.releaseAllVideos();
            }
        }
    }

    @Override
    public void onChildViewDetachedFromWindow(@NonNull View view) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null) adapter.onDestroy();
    }

    @Override
    public void onLoadMore() {
        Log.e(TAG, "onLoadMore: TRUE");
        Intent intent = new Intent("load_more");
        intent.putExtra("isLoading",true);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
        page++;
        getVideoAPI(page);
    }
}