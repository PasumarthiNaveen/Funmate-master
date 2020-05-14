package com.xitij.tiktuk.Adapter;

import android.content.Context;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.xitij.tiktuk.API.ApiClient;
import com.xitij.tiktuk.API.ApiInterface;
import com.xitij.tiktuk.Listner.OnLoadMoreListener;
import com.xitij.tiktuk.POJO.Auth;
import com.xitij.tiktuk.POJO.Comment.Delete.Request.Data;
import com.xitij.tiktuk.POJO.Comment.Delete.Request.DeleteCommentRequest;
import com.xitij.tiktuk.POJO.Comment.Delete.Request.Request;
import com.xitij.tiktuk.POJO.Comment.Delete.Response.DeleteCommentResponse;
import com.xitij.tiktuk.POJO.Comment.Recieve.Response.Comment;
import com.xitij.tiktuk.R;
import com.xitij.tiktuk.Transformation.CircleTransform;
import com.xitij.tiktuk.View.LoadingView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private static final String PROFILE_PIC = "http://tiktukreward.ga/uploads/profile_pic/";

    private static final int ITEM_VIEW = 381;
    private static final int LOADING_VIEW = 180;

    private OnProfileCommentorListener onProfileCommentorListener;

    private Context context;
    private List<Comment> comments;
    private int postion;
    private int userId;

    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    private OnCommentRemoveListener onCommentRemoveListener;

    public CommentAdapter(Context context, List<Comment> comments, int userId, int postion, RecyclerView recyclerView) {
        this.context = context;
        this.comments = comments;
        this.userId = userId;
        this.postion = postion;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = layoutManager.getItemCount();
                    lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <=  (lastVisibleItem + visibleThreshold)) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public void setOnProfileCommentorListener(OnProfileCommentorListener onProfileCommentorListener) {
        this.onProfileCommentorListener = onProfileCommentorListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setOnCommentRemoveListener(OnCommentRemoveListener onCommentRemoveListener) {
        this.onCommentRemoveListener = onCommentRemoveListener;
    }

    @Override
    public int getItemViewType(int position) {
        return comments.get(position) != null ? ITEM_VIEW : LOADING_VIEW;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == ITEM_VIEW) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_rv_comment_right,viewGroup,false);
            return new MyViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_rv_progress,viewGroup,false);
            return new ProgressHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {

        if (holder instanceof MyViewHolder) {
            String commment = comments.get(i).getComment();
            ((MyViewHolder)holder).tvComment.setText(commment);

            if (comments.get(i).getProfilepic().isEmpty()) {
                Picasso.get().load(R.drawable.blank_profile).transform(new CircleTransform()).into(((MyViewHolder)holder).ivCommentor);
            }
            else {
                Picasso.get().load(PROFILE_PIC+comments.get(i).getProfilepic()).transform(new CircleTransform()).into(((MyViewHolder)holder).ivCommentor);
            }

            if (comments.get(i).getUserId().equals(""+userId)){
                ((MyViewHolder)holder).linlayComment.setVisibility(View.VISIBLE);
                ((MyViewHolder)holder).tvCommentStatus.setVisibility(View.INVISIBLE);
            }
            else {
                ((MyViewHolder)holder).linlayComment.setVisibility(View.GONE);
            }
        }
        else {
            ((ProgressHolder)holder).progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.iv_post:
                break;
        }
    }

    public void setLoaded() {
        loading = false;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvComment,tvDeleteComment,tvCommentStatus;
        private ImageView ivCommentor;
        private LinearLayout linlayComment;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvComment = itemView.findViewById(R.id.tv_comment);
            tvDeleteComment = itemView.findViewById(R.id.tv_delete_comment);
            tvCommentStatus = itemView.findViewById(R.id.tv_comment_status);

            ivCommentor = itemView.findViewById(R.id.iv_commentor_photo);

            linlayComment = itemView.findViewById(R.id.linlayUserComment);

            ivCommentor.setOnClickListener(this);

            tvDeleteComment.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.iv_commentor_photo:
                    onProfileCommentorListener.onProfileClick(v,getAdapterPosition(),comments.get(getAdapterPosition()));
                    break;
                case R.id.tv_delete_comment:
                    deleteComment(getAdapterPosition());
                    onCommentRemoveListener.onCommentRemoved(postion);
                    break;
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

    private void deleteComment(int position) {
        String commentId = comments.get(position).getId();
        comments.remove(position);
        notifyItemRemoved(position);
        getDeleteAPI(commentId);
    }

    private void getDeleteAPI(String commentId) {
        DeleteCommentRequest deleteCommentRequest = new DeleteCommentRequest();

        int id = PreferenceManager.getDefaultSharedPreferences(context).getInt("id",-111);
        String token = PreferenceManager.getDefaultSharedPreferences(context).getString("token","null");

        if (id == -111 || token.equals("null")) {
            Toast.makeText(context, "Comment was not deleted!", Toast.LENGTH_SHORT).show();
            return;
        }
        Auth auth = new Auth();
        auth.setId(id);
        auth.setToken(token);

        Data data = new Data();
        data.setId(commentId);

        Request request = new Request();
        request.setData(data);

        deleteCommentRequest.setAuth(auth);
        deleteCommentRequest.setRequest(request);
        deleteCommentRequest.setService("deletevideoComment");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<DeleteCommentResponse> responseCall = apiInterface.removeVideoComment(deleteCommentRequest);
        responseCall.enqueue(new Callback<DeleteCommentResponse>() {
            @Override
            public void onResponse(Call<DeleteCommentResponse> call, Response<DeleteCommentResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getSuccess() != 1){
                        Toast.makeText(context, "Comment was not deleted!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Log.i("CommentAdapter", "onResponse: Deleted Comment");
                    }
                }
            }

            @Override
            public void onFailure(Call<DeleteCommentResponse> call, Throwable t) {

            }
        });
    }

    public interface OnProfileCommentorListener {
        void onProfileClick(View view, int position, Comment comment);
    }

    public interface OnCommentRemoveListener
    {
        void onCommentRemoved(int position);
    }

}
