package com.xitij.tiktuk.Adapter;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.squareup.picasso.Picasso;
import com.xitij.tiktuk.Listner.OnContestListener;
import com.xitij.tiktuk.Listner.OnLoadMoreListener;
import com.xitij.tiktuk.POJO.Contest.Response.Contest;
import com.xitij.tiktuk.R;

import java.util.ArrayList;
import java.util.List;

public class ContestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String CONTEST_PATH = "http://tiktukreward.ga/uploads/contest/";
    private static final int AD_DISPLAY_FREQUENCY = 3;
    private static final int POST_TYPE = 0;
    private static final int AD_TYPE = 1;

    private OnContestListener onContestListener;
    private OnLoadMoreListener onLoadMoreListener;

    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;

    private List<Contest> contestList;
    private List<AdView> mAdItems;

    public ContestAdapter(List<Contest> contestList, RecyclerView recyclerView) {
        this.contestList = contestList;
        mAdItems = new ArrayList<>();

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

    public void setOnContestListener(OnContestListener onContestListener) {
        this.onContestListener = onContestListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == AD_TYPE) {

            View inflatedView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_rv_banner_ads, viewGroup, false);
            return new AdHolder(inflatedView);

        } else {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_rv_contest,viewGroup,false);
            return new MyViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        int item = getItemViewType(position);

        if (item == POST_TYPE) {

            //todo undo for ads
            int index = position - ((position + 1)/ AD_DISPLAY_FREQUENCY);

            Contest contest = contestList.get(index);
            Picasso.get().load(CONTEST_PATH+contest.getImage()).into(((MyViewHolder)viewHolder).contestImage);

        } else if (item == AD_TYPE) {
            AdHolder adHolder = (AdHolder) viewHolder;

            AdView adView;

            if (mAdItems.size() > position / AD_DISPLAY_FREQUENCY) {

                adView = mAdItems.get(position / AD_DISPLAY_FREQUENCY);

            } else {

                adView = new AdView(adHolder.itemView.getContext(), adHolder.itemView.getContext().getString(R.string.facebook_ads_medium_rectangle), AdSize.RECTANGLE_HEIGHT_250);
                //AdSettings.addTestDevice("32a00db4-6a1b-4782-86f4-d041098b4316");
                adView.loadAd();
                mAdItems.add(adView);

            }
            if (adHolder.adCardView.getChildCount() > 0) {
                adHolder.adCardView.removeAllViews();
            }
            if (adView.getParent() != null) {
                ((ViewGroup) adView.getParent()).removeView(adView);
            }
            adHolder.adCardView.addView(adView);
        }

    }

    @Override
    public int getItemCount() {
        return contestList.size() + mAdItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        //todo undo for ads
        return position % AD_DISPLAY_FREQUENCY == 2 ? AD_TYPE : POST_TYPE;
    }

    public void setLoaded() {
        loading = false;
    }

    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView contestImage;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            contestImage = itemView.findViewById(R.id.iv_contest);

            contestImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            onContestListener.onContestSelected(contestList.get(position/* - ((position+1) / AD_DISPLAY_FREQUENCY)*/));
        }
    }

    private static class AdHolder extends RecyclerView.ViewHolder {

        CardView adCardView;

        AdHolder(@NonNull View itemView) {

            super(itemView);
            adCardView = itemView.findViewById(R.id.cv_fb_banner_ad);

        }
    }

}
