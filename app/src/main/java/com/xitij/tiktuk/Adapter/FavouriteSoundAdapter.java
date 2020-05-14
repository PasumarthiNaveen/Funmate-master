package com.xitij.tiktuk.Adapter;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xitij.tiktuk.POJO.sound.Sound;
import com.xitij.tiktuk.R;

import java.util.ArrayList;

public class FavouriteSoundAdapter extends RecyclerView.Adapter<FavouriteSoundAdapter.CustomViewHolder > {
    public Context context;

    ArrayList<Sound> datalist;
    public interface OnItemClickListener {
        void onItemClick(View view, int postion, Sound item);
    }

    public FavouriteSoundAdapter.OnItemClickListener listener;


    public FavouriteSoundAdapter(Context context, ArrayList<Sound> arrayList, FavouriteSoundAdapter.OnItemClickListener listener) {
        this.context = context;
        datalist= arrayList;
        this.listener=listener;
    }

    @Override
    public FavouriteSoundAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sound_layout,viewGroup,false);
        view.setLayoutParams(new RecyclerView.LayoutParams(getScreenWidth(viewGroup.getContext())-50, RecyclerView.LayoutParams.WRAP_CONTENT));
        FavouriteSoundAdapter.CustomViewHolder viewHolder = new FavouriteSoundAdapter.CustomViewHolder(view);
        return viewHolder;
    }


    @Override
    public int getItemCount() {
        return datalist.size();
    }




    @Override
    public void onBindViewHolder(final FavouriteSoundAdapter.CustomViewHolder holder, final int i) {
        holder.setIsRecyclable(false);

        try {

            holder.sound_name.setText(datalist.get(i).sound_name);
            holder.description_txt.setText(datalist.get(i).description);
            Picasso.get().load(datalist.get(i).thum)
                    .into(holder.sound_image);
            holder.bind(i, datalist.get(i), listener);

           /* if(SoundList_A.running_sound_id.equals(datalist.get(i).id)){
                holder.itemView.findViewById(R.id.pause_btn).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.done).setVisibility(View.VISIBLE);
            }*/

        }catch (Exception e){

        }

    }

    private static int getScreenWidth(Context context) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;

    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageButton done,fav_btn;
        TextView sound_name,description_txt;
        ImageView sound_image;

        public CustomViewHolder(View view) {
            super(view);
            //  image=view.findViewById(R.id.image);
            done=view.findViewById(R.id.done);
            fav_btn=view.findViewById(R.id.fav_btn);


            sound_name=view.findViewById(R.id.sound_name);
            description_txt=view.findViewById(R.id.description_txt);
            sound_image=view.findViewById(R.id.sound_image);

        }

        public void bind(final int pos , final Sound item, final FavouriteSoundAdapter.OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v,pos,item);
                }
            });

            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v,pos,item);
                }
            });

            fav_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v,pos,item);
                }
            });

        }


    }




}

