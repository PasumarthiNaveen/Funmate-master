package com.xitij.tiktuk.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.xitij.tiktuk.API.ApiClient;
import com.xitij.tiktuk.API.ApiInterface;
import com.xitij.tiktuk.Adapter.PeopleAdapter;
import com.xitij.tiktuk.Listner.OnPeopleListener;
import com.xitij.tiktuk.POJO.Following.Data;
import com.xitij.tiktuk.POJO.Following.FollowingRequest;
import com.xitij.tiktuk.POJO.Following.FollowingResponse;
import com.xitij.tiktuk.POJO.Following.Request;
import com.xitij.tiktuk.POJO.People;
import com.xitij.tiktuk.R;
import com.xitij.tiktuk.View.LoadingView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingFragment extends Fragment {

    private PeopleAdapter adapter;
    private List<People> peoples;
    private OnPeopleListener onPeopleListener;
    private LoadingView loadingView;

    public void setOnPeopleListener(OnPeopleListener onPeopleListener) {
        this.onPeopleListener = onPeopleListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following,container,false);

        Toolbar toolbar = view.findViewById(R.id.following_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Following");

        loadingView = view.findViewById(R.id.loader_following);

        String user_id = getArguments().getString("userId");


        RecyclerView recyclerView = view.findViewById(R.id.rv_following);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        peoples = new ArrayList<>();
        adapter = new PeopleAdapter(getContext(),peoples,recyclerView);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new PeopleAdapter.onItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position, People people) {
                //startActivity(new Intent(getContext(),UserActivity.class));
                onPeopleListener.onListClicked(people);
            }
        });

        loadingView.setVisibility(View.VISIBLE);

        getFollowingAPI(user_id);


        return view;
    }

    private void getFollowingAPI(String user_id) {

        FollowingRequest followingRequest = new FollowingRequest();


        Data data = new Data();
        data.setUserId(user_id);

        Request request = new Request();
        request.setData(data);

        followingRequest.setRequest(request);
        followingRequest.setService("getUserfollower");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<FollowingResponse> responseCall = apiInterface.getFollowingPeople(followingRequest);
        responseCall.enqueue(new Callback<FollowingResponse>() {
            @Override
            public void onResponse(Call<FollowingResponse> call, Response<FollowingResponse> response) {
                loadingView.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        peoples.clear();
                        peoples.addAll(response.body().getPeople());
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<FollowingResponse> call, Throwable t) {
                loadingView.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        MenuItem updateProfile = menu.findItem(R.id.user_menu);

        if (updateProfile != null) {
            updateProfile.setVisible(false);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            }

        }

        return super.onOptionsItemSelected(item);
    }
}
