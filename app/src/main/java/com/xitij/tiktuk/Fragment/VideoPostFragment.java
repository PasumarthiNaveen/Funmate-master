package com.xitij.tiktuk.Fragment;

import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vollink.jiaozivideoplayer.JZMediaManager;
import com.vollink.jiaozivideoplayer.Jzvd;
import com.vollink.jiaozivideoplayer.JzvdMgr;
import com.xitij.tiktuk.API.ApiClient;
import com.xitij.tiktuk.API.ApiInterface;
import com.xitij.tiktuk.Adapter.VideoAdapter;
import com.xitij.tiktuk.POJO.Video.Video;
import com.xitij.tiktuk.POJO.service.Service;
import com.xitij.tiktuk.POJO.service.ServiceResponse;
import com.xitij.tiktuk.POJO.service.data.VideoCountData;
import com.xitij.tiktuk.R;
import com.xitij.tiktuk.View.LoadingView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoPostFragment extends Fragment implements RecyclerView.OnChildAttachStateChangeListener {
    private static final String TAG = VideoPostFragment.class.getSimpleName();
    private List<Video> videos;
    private VideoAdapter adapter;

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video,container,false);


        int position = getArguments().getInt("position");

        RecyclerView recyclerView = view.findViewById(R.id.rv_popular);
        LoadingView loadingView = view.findViewById(R.id.list_loading);
        loadingView.setVisibility(View.GONE);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        //layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new VideoAdapter(getActivity(),videos,recyclerView);

        recyclerView.setAdapter(adapter);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.scrollToPosition(position);

        recyclerView.addOnChildAttachStateChangeListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
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

        return view;
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
    public void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Jzvd.releaseAllVideos();
        if (adapter != null) adapter.onDestroy();
    }
}
