package com.xitij.tiktuk.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdIconView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdsManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.xitij.tiktuk.Listner.OnPostRemoveCallback;
import com.xitij.tiktuk.Listner.OnPostRemoveListener;
import com.xitij.tiktuk.Listner.OnVideoListener;
import com.xitij.tiktuk.POJO.Video.Video;
import com.xitij.tiktuk.R;

import java.util.ArrayList;
import java.util.List;

public class UserPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final String VIDEO_BASE_URL = "http://3.212.177.112:80/uploads/videos/";
    private static final int AD_DISPLAY_FREQUENCY = 7;
    private static final int POST_TYPE = 0;
    private static final int AD_TYPE = 1;
    private static final String TAG = UserPostAdapter.class.getSimpleName();

    private List<NativeAd> mAdItems;

    private Context context;
    private List<Video> videos;
    private OnVideoListener videoListener;
    private OnPostRemoveListener onPostRemoveListener;
    private boolean isChecked = false;
    private boolean longClickable = false;

    public void setOnPostRemoveListener(OnPostRemoveListener onPostRemoveListener) {
        this.onPostRemoveListener = onPostRemoveListener;
    }

    public void setVideoListener(OnVideoListener videoListener) {
        this.videoListener = videoListener;
    }

    private NativeAdsManager mNativeAdsManager;

    public UserPostAdapter(Context context, List<Video> videos) {
        this.context = context;
        this.videos = videos;
        mAdItems = new ArrayList<>();
        mNativeAdsManager = new NativeAdsManager(context, context.getString(R.string.facebook_ads_native_image), 5);
        mNativeAdsManager.loadAds(NativeAdBase.MediaCacheFlag.ALL);

        //todo undo this
        //AdSettings.addTestDevice("35dfc531-e77e-46f4-9e93-154971dcf3ab");

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == AD_TYPE) {

            View inflatedView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.native_ad_unit_test, viewGroup, false);
            return new AdHolder(inflatedView);

        } else {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_rv_post,viewGroup,false);
            return new MyViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        int item = getItemViewType(position);

        if (item == POST_TYPE) {
            //todo undo for ads
            int index = position - ((position+2) / AD_DISPLAY_FREQUENCY);

            /*if (position>4) {
                index = position - (position / AD_DISPLAY_FREQUENCY) - 1;
            }*/

            MyViewHolder myViewHolder = (MyViewHolder) viewHolder;

            String thumb_url = videos.get(index).getThumbUrl();
            if (thumb_url.isEmpty()) {
                //Picasso.get().load(R.drawable.ic_video).into(myViewHolder.imageView);
                myViewHolder.imageView.setImageDrawable(myViewHolder.itemView.getContext().getResources().getDrawable(R.drawable.ic_video));
                myViewHolder.imageView.setBackgroundColor(Color.parseColor("#39476C"));
            }
            else {
                Log.i("Thumb", "onBindViewHolder: "+VIDEO_BASE_URL+thumb_url);
                Picasso.get().load(VIDEO_BASE_URL+thumb_url).placeholder(R.drawable.post_placeholder).into(myViewHolder.getTarget());
            }

            myViewHolder.imageView.setLongClickable(isLongClickable());

            if (isChecked) {
                myViewHolder.imageView.setClickable(false);
                myViewHolder.ivDelete.setSelected(true);
                myViewHolder.ivDelete.setVisibility(View.VISIBLE);
            }
            else {
                myViewHolder.imageView.setClickable(true);
                myViewHolder.ivDelete.setVisibility(View.GONE);
            }

        }else if (viewHolder.getItemViewType() == AD_TYPE) {

            NativeAd ad;

            if (mAdItems.size() > position / AD_DISPLAY_FREQUENCY) {
                ad = mAdItems.get(position / AD_DISPLAY_FREQUENCY);
            } else {
                ad = mNativeAdsManager.nextNativeAd();
                if (ad == null) {
                    Log.d(TAG, "onBindViewHolder() returned: " + position);
                }
                mAdItems.add(ad);
            }

            AdHolder adHolder = (AdHolder) viewHolder;
            //adHolder.setIsRecyclable(false);
            adHolder.adChoicesContainer.removeAllViews();

            if (ad != null) {

                adHolder.tvAdTitle.setText(ad.getAdvertiserName());
                adHolder.tvAdBody.setText(ad.getAdBodyText());
                adHolder.tvAdSocialContext.setText(ad.getAdSocialContext());
                adHolder.tvAdSponsoredLabel.setText(ad.getSponsoredTranslation());
                adHolder.btnAdCallToAction.setText(ad.getAdCallToAction());
                adHolder.btnAdCallToAction.setVisibility(
                        ad.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                AdChoicesView adChoicesView = new AdChoicesView(adHolder.itemView.getContext(),
                        ad, true);
                adHolder.adChoicesContainer.addView(adChoicesView, 0);

                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(adHolder.ivAdIcon);
                clickableViews.add(adHolder.mvAdMedia);
                clickableViews.add(adHolder.btnAdCallToAction);
                ad.registerViewForInteraction(
                        adHolder.itemView,
                        adHolder.mvAdMedia,
                        adHolder.ivAdIcon,
                        clickableViews);
            }

            /*AdHolder adHolder = (AdHolder) viewHolder;

            NativeBannerAd ad;

            if (mAdItems.size() > position / AD_DISPLAY_FREQUENCY) {
                ad = mAdItems.get(position / AD_DISPLAY_FREQUENCY);
            } else {
                ad = new NativeBannerAd(context,context.getString(R.string.facebook_ads_native_banner));
                ad.loadAd(NativeAdBase.MediaCacheFlag.ALL);
                mAdItems.add(ad);
            }

            LayoutInflater inflater = LayoutInflater.from(adHolder.itemView.getContext());
            // Inflate the Ad view.  The layout referenced is the one you created in the last step.
            LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.native_banner_layout, adHolder.adLayout, false);
            adHolder.adLayout.addView(adView);

            // Add the AdChoices icon
            RelativeLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
            AdOptionsView adOptionsView = new AdOptionsView(adHolder.itemView.getContext(), ad, adHolder.adLayout);
            adChoicesContainer.removeAllViews();
            adChoicesContainer.addView(adOptionsView, 0);

            // Create native UI using the ad metadata.
            TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
            TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
            TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
            AdIconView nativeAdIconView = adView.findViewById(R.id.native_icon_view);
            Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

            // Set the Text.
            nativeAdCallToAction.setText(ad.getAdCallToAction());
            nativeAdCallToAction.setVisibility(
                    ad.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
            nativeAdTitle.setText(ad.getAdvertiserName());
            nativeAdSocialContext.setText(ad.getAdSocialContext());
            sponsoredLabel.setText(ad.getSponsoredTranslation());

            // Register the Title and CTA button to listen for clicks.
            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(nativeAdTitle);
            clickableViews.add(nativeAdCallToAction);
            ad.registerViewForInteraction(adView, nativeAdIconView, clickableViews);*/

        }

    }

    @Override
    public int getItemCount() {
        return videos.size() + mAdItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        //todo undo for ads
        return position % AD_DISPLAY_FREQUENCY == 5 ? AD_TYPE : POST_TYPE;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.iv_post:
                break;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, Target, View.OnLongClickListener {
        final RenderScript rs = RenderScript.create(context);
        private ImageView imageView,ivDelete;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_post);
            ivDelete = itemView.findViewById(R.id.iv_post_delete);

            ivDelete.setClickable(false);

            imageView.setOnClickListener(this);
            ivDelete.setOnClickListener(this);
            imageView.setOnLongClickListener(this);

        }

        private Target getTarget() {
            return this;
        }

        @Override
        public void onClick(View v) {
            //todo undo for ads
            if (v.getId() == R.id.iv_post) {
                int position = getAdapterPosition();
                Log.i(TAG, "onClick: "+(position - ((position+2)/AD_DISPLAY_FREQUENCY)));
                videoListener.onVideoSelected(position - ((position+2) / AD_DISPLAY_FREQUENCY),videos);
            }
            if (v.getId() == R.id.iv_post_delete) {
                int position = getAdapterPosition();
                onPostRemoveListener.onPostRemoved(position - ((position+2) / AD_DISPLAY_FREQUENCY), new OnPostRemoveCallback() {
                    @Override
                    public void onSuccess(int position) {
                        videos.remove(position);
                        isChecked = false;
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFail() {
                        Toast toast = Toast.makeText(context, "Unable to delete.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                });
            }
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //bitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/4,bitmap.getHeight()/4,false);
            //bitmap.compress(Bitmap.CompressFormat.JPEG,50,null);
            int targetWidth = itemView.getWidth();

            double aspectRatio = (double) bitmap.getHeight() / (double) bitmap.getWidth();

            int targetHeight = (int) (targetWidth * aspectRatio);

            if (targetHeight<=0 && targetWidth<=0) {
                targetHeight = bitmap.getHeight()/4;
                targetWidth = bitmap.getWidth()/4;
            }

            Bitmap result = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false);

            imageView.setImageBitmap(result);

            Bitmap bitmapOriginal = Bitmap.createScaledBitmap(result,50,100,false);//bitmap.copy(bitmap.getConfig(),false);
            //.getDrawingCache().copy(bitmap.getConfig(), false);

            final Allocation input = Allocation.createFromBitmap(rs, bitmapOriginal); //use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(23f);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmapOriginal);

            //BitmapUtils bitmapUtils = new BitmapUtils();

            //bitmapOriginal = bitmapUtils.getRoundedBitmap(bitmapOriginal,10,0);

            Drawable drawable = new BitmapDrawable(context.getResources(),bitmapOriginal);
            imageView.setBackground(drawable);

        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            //imageView.setImageBitmap(bitmap);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }

        @Override
        public boolean onLongClick(View v) {
            isChecked = true;
            notifyDataSetChanged();
            return true;
        }
    }

    /*private static class AdHolder extends RecyclerView.ViewHolder {

        NativeAdLayout adLayout;

        AdHolder(View view) {
            super(view);

            adLayout = view.findViewById(R.id.native_banner_ad_container);

        }
    }*/

    private static class AdHolder extends RecyclerView.ViewHolder {
        MediaView mvAdMedia;
        AdIconView ivAdIcon;
        TextView tvAdTitle;
        TextView tvAdBody;
        TextView tvAdSocialContext;
        TextView tvAdSponsoredLabel;
        Button btnAdCallToAction;
        LinearLayout adChoicesContainer;

        AdHolder(View view) {
            super(view);

            mvAdMedia = (MediaView) view.findViewById(R.id.native_ad_media);
            tvAdTitle = (TextView) view.findViewById(R.id.native_ad_title);
            tvAdBody = (TextView) view.findViewById(R.id.native_ad_body);
            tvAdSocialContext = (TextView) view.findViewById(R.id.native_ad_social_context);
            tvAdSponsoredLabel = (TextView) view.findViewById(R.id.native_ad_sponsored_label);
            btnAdCallToAction = (Button) view.findViewById(R.id.native_ad_call_to_action);
            ivAdIcon = (AdIconView) view.findViewById(R.id.native_ad_icon);
            adChoicesContainer = (LinearLayout) view.findViewById(R.id.ad_choices_container);

        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        this.isChecked = checked;
    }

    public boolean isLongClickable() {
        return longClickable;
    }

    public void setLongClickable(boolean longClickable) {
        this.longClickable = longClickable;
    }

    public void clearAds() {
        mAdItems.clear();
    }

}
