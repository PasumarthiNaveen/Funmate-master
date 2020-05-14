package com.xitij.tiktuk.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xitij.tiktuk.Listner.OnLoadMoreListener;
import com.xitij.tiktuk.POJO.People;
import com.xitij.tiktuk.R;
import com.xitij.tiktuk.Transformation.CircleTransform;
import com.xitij.tiktuk.View.LoadingView;

import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    //private static final String VIDEO_BASE_URL = "http://192.168.0.105/demo/uploads/people/";
    private static final String PROFILE_URL = "http://tiktukreward.ga/uploads/profile_pic/";
    private static final int ITEM_VIEW = 381;
    private static final int LOADING_VIEW = 180;

    private onItemClickListener itemClickListener;

    private Context context;
    private List<People> people;

    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public PeopleAdapter(Context context, List<People> people, RecyclerView recyclerView) {
        this.context = context;
        this.people = people;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = layoutManager.getItemCount();
                    lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(onItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == ITEM_VIEW) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_rv_search,viewGroup,false);
            return new PeopleAdapter.MyViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_rv_progress,viewGroup,false);
            return new PeopleAdapter.ProgressHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {

        if (holder instanceof PeopleAdapter.MyViewHolder) {
            ((MyViewHolder)holder).tvPersonName.setText(people.get(i).getFirstName());
            ((MyViewHolder)holder).tvUserName.setText(people.get(i).getUsername());
            ((MyViewHolder)holder).tvPersonFollower.setText(people.get(i).getFollowerCount());


            String profilePic = people.get(i).getProfilepic();
            if (profilePic.isEmpty()) {
                Picasso.get().load(R.drawable.blank_profile).transform(new CircleTransform()).into(((MyViewHolder)holder).ivUser);
            }
            else {
                Picasso.get().load(PROFILE_URL+profilePic).transform(new CircleTransform()).resize(90,90).into(((MyViewHolder)holder).ivUser);
            }
        }
        else {
            ((ProgressHolder)holder).progressBar.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemViewType(int position) {
        return people.get(position) != null ? ITEM_VIEW : LOADING_VIEW;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.iv_post:
                break;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvPersonName,tvUserName,tvPersonFollower;
        private ImageView ivUser;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPersonName = itemView.findViewById(R.id.tv_person_name);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvPersonFollower = itemView.findViewById(R.id.tv_person_follower);
            ivUser = itemView.findViewById(R.id.iv_search_user);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClickListener(v, getAdapterPosition(), people.get(getAdapterPosition()));
            }
        }
    }

    class ProgressHolder extends RecyclerView.ViewHolder {

        private LoadingView progressBar;

        public ProgressHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_load_more);
        }
    }

    public interface onItemClickListener {
        void onItemClickListener(View view, int position, People people);
    }

}
