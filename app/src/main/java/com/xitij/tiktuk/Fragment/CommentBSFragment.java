package com.xitij.tiktuk.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xitij.tiktuk.API.ApiClient;
import com.xitij.tiktuk.API.ApiInterface;
import com.xitij.tiktuk.Adapter.CommentAdapter;
import com.xitij.tiktuk.Listner.OnLoadMoreListener;
import com.xitij.tiktuk.POJO.Auth;
import com.xitij.tiktuk.POJO.Comment.Recieve.Response.Comment;
import com.xitij.tiktuk.POJO.Comment.Recieve.Request.CommentRequest;
import com.xitij.tiktuk.POJO.Comment.Recieve.Response.CommentResponse;
import com.xitij.tiktuk.POJO.Comment.Recieve.Request.Data;
import com.xitij.tiktuk.POJO.Comment.Recieve.Request.Request;
import com.xitij.tiktuk.POJO.Comment.Send.Request.AddCommentRequest;
import com.xitij.tiktuk.POJO.Comment.Send.Response.AddCommentResponse;
import com.xitij.tiktuk.R;
import com.xitij.tiktuk.Utils.PreciseCount;
import com.xitij.tiktuk.View.LoadingView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentBSFragment extends BottomSheetDialogFragment implements CommentAdapter.OnProfileCommentorListener, View.OnClickListener, OnLoadMoreListener {
    private CommentAdapter adapter;
    private List<Comment> comments;
    private Button btnSend;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private EditText etComment;
    private String totalComments,userId,videoId;;
    private OnCommentAddListener onCommentAddListener;
    private CommentAdapter.OnCommentRemoveListener onCommentRemoveListener;
    private int position;
    private int adapterPosition;

    private int page;
    private LoadingView loadingView;

    protected Handler handler;
    private String profilePic;

    private Auth auth;

    public CommentBSFragment() {

    }

    public void setOnCommentAddListener(OnCommentAddListener onCommentAddListener,int position) {
        this.onCommentAddListener = onCommentAddListener;
        this.position = position;
    }

    public void setOnCommentRemoveListener(CommentAdapter.OnCommentRemoveListener onCommentRemoveListener, int adapterPosition) {
        this.onCommentRemoveListener = onCommentRemoveListener;
        this.adapterPosition = adapterPosition;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bs_comment,container,false);

        if (getArguments() != null) {
            totalComments = PreciseCount.from(getArguments().getString("totalComment","0")) + " comments";
            userId = getArguments().getString("user_id","null");
            videoId = getArguments().getString("vid","null");
            profilePic = getArguments().getString("profilePic");
        }
        else {
            totalComments = "No comments";
            userId = "null";
            videoId = "null";
        }

        int id = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("id",-111);
        String token = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("token","null");

        if (id == -111 || token.equals("null")) {
            dismiss();
        }

        auth = new Auth();
        auth.setId(id);
        auth.setToken(token);

        page = 1;
        loadingView = view.findViewById(R.id.comment_loader);
        loadingView.setVisibility(View.VISIBLE);
        comments = new ArrayList<>();
        getCommentAPI(page,view);



        return view;
    }

    private void getCommentAPI(final int page, final View view) {
        CommentRequest commentRequest = new CommentRequest();

        Data data = new Data();
        data.setVid(videoId);
        data.setPage(page);

        Request request = new Request();
        request.setData(data);

        commentRequest.setAuth(auth);
        commentRequest.setRequest(request);
        commentRequest.setService("getvideoComment");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<CommentResponse> commentResponseCall = apiInterface.getVideoComment(commentRequest);
        commentResponseCall.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getSuccess() == 1){
                        //comments.clear();
                        if (page == 1) {
                            comments.addAll(response.body().getData());
                            initView(view);
                        }
                        else {
                            comments.remove(comments.size()-1);
                            adapter.notifyItemRemoved(comments.size());
                            comments.addAll(response.body().getData());
                            adapter.setLoaded();
                            adapter.notifyDataSetChanged();
                        }
                    }
                    else {
                        if (page != 1) {
                            comments.remove(comments.size()-1);
                            adapter.notifyItemRemoved(comments.size());
                        }
                        else {
                            totalComments = "No comments";
                            initView(view);
                        }
                    }
                }
                if (page == 1) {
                    loadingView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                if (page == 1) {
                    loadingView.setVisibility(View.GONE);
                }
            }
        });


    }

    private void initView(View view) {

        TextView tvTotalComment = view.findViewById(R.id.tv_total_comment);

        tvTotalComment.setText(totalComments);

        int id = PreferenceManager.getDefaultSharedPreferences(view.getContext()).getInt("id",-111);

        recyclerView = view.findViewById(R.id.rv_comment);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CommentAdapter(getContext(),comments,id,adapterPosition, recyclerView);
        adapter.setOnCommentRemoveListener(onCommentRemoveListener);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(0);
        adapter.setOnLoadMoreListener(this);
        adapter.setOnProfileCommentorListener(this);

        etComment = view.findViewById(R.id.et_comment_box);
        etComment.addTextChangedListener(new MyTextwatcher(etComment));

        btnSend = view.findViewById(R.id.btn_comment_send);
        btnSend.setVisibility(View.INVISIBLE);
        btnSend.setOnClickListener(this);

        handler = new Handler();
    }

    @Override
    public void onProfileClick(View view, int position, Comment comment) {
        Log.i("Profile","Clicked: "+comment.getId());
    }

    @Override
    public void onClick(View v) {
        //layoutManager.setStackFromEnd(false);
        final String text = etComment.getText().toString();
        Comment comment = new Comment();
        comment.setComment(text);
        comment.setVideoId(videoId);
        comment.setCount("0");
        comment.setCreationDatetime(""+System.currentTimeMillis());
        comment.setUserId(userId);
        comment.setDeletionDatetime("");
        comment.setModificationDatetime(""+System.currentTimeMillis());
        comment.setProfilepic(profilePic);
        comment.setId("101");
        comment.setType("comment");
        comment.setShare("0");
        comment.setIsDeleted("0");
        comments.add(0,comment);
        adapter.notifyItemInserted(0);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 500ms
                //recyclerView.scrollToPosition(comments.size()-1);
                recyclerView.scrollToPosition(0);
                if (layoutManager.findViewByPosition(0) != null){
                    TextView tvStatus = Objects.requireNonNull(layoutManager.findViewByPosition(0)).findViewById(R.id.tv_comment_status);
                    tvStatus.setVisibility(View.VISIBLE);
                }
                sendCommentAPI(text);
            }
        }, 1000);
        etComment.setText("");

    }

    private void sendCommentAPI(String comment) {
        AddCommentRequest commentRequest = new AddCommentRequest();

        com.xitij.tiktuk.POJO.Comment.Send.Request.Data data = new com.xitij.tiktuk.POJO.Comment.Send.Request.Data();
        data.setComment(comment);
        data.setType("comment");
        data.setVideoId(videoId);
        data.setUserId(userId);

        com.xitij.tiktuk.POJO.Comment.Send.Request.Request request = new com.xitij.tiktuk.POJO.Comment.Send.Request.Request();
        request.setData(data);

        commentRequest.setAuth(auth);
        commentRequest.setRequest(request);
        commentRequest.setService("SaveComment");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<AddCommentResponse> responseCall = apiInterface.addVideoComment(commentRequest);
        responseCall.enqueue(new Callback<AddCommentResponse>() {
            @Override
            public void onResponse(Call<AddCommentResponse> call, Response<AddCommentResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getSuccess() == 1){
                        onCommentAddListener.onCommentAdded(position);
                        Comment commment = comments.get(0);
                        commment.setId(""+response.body().getId());
                        //adapter.notifyItemChanged(comments.size()-1);
                        if (layoutManager.findViewByPosition(0) != null){
                            TextView tvStatus = Objects.requireNonNull(layoutManager.findViewByPosition(0)).findViewById(R.id.tv_comment_status);
                            tvStatus.setText("Sent");
                            tvStatus.setVisibility(View.VISIBLE);
                        }
                        else {
                            Log.e("TAG", "layout manager: is null");
                        }

                        Log.i("Comment","success");
                    }
                }
                else {
                    Log.e("Comment","Not success "+response.code());
                }
                layoutManager.setStackFromEnd(true);
            }

            @Override
            public void onFailure(Call<AddCommentResponse> call, Throwable t) {
                layoutManager.setStackFromEnd(true);
            }
        });

    }

    @Override
    public void onLoadMore() {

        comments.add(null);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyItemInserted(comments.size()-1);
                page++;
                getCommentAPI(page,null);
            }
        });

    }

    private class MyTextwatcher implements TextWatcher {
        EditText editText;
        MyTextwatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (editText.getText().toString().isEmpty()) {
                btnSend.setVisibility(View.INVISIBLE);
            }
            else {
                btnSend.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public interface OnCommentAddListener
    {
        void onCommentAdded(int position);
    }

}
