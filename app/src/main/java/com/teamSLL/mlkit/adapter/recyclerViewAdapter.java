package com.teamSLL.mlkit.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.youtube.player.YouTubeThumbnailView;
import com.squareup.picasso.Picasso;
import com.teamSLL.mlkit.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class recyclerViewAdapter extends RecyclerView.Adapter<recyclerViewAdapter.ViewHolder> {
    private List<VideoInfo> mVideoInfos;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    // data is passed into the constructor
    public recyclerViewAdapter(Context context, List<VideoInfo> mVideoInfos) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mVideoInfos = mVideoInfos;
    }

    // inflates the row layout from xml when needed
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    void setImage(ImageView view, String url){
        if(url == "") {
            view.setImageResource(R.drawable.transparent);
            view.setBackgroundColor(Color.WHITE);
            return;
        }
        Picasso.with(this.context).load(url).placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(view, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });
    }
    // binds the data to the view and textview in each row
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VideoInfo videoinfo = mVideoInfos.get(position);

        holder.videoTitle.setText(videoinfo.videoTitle);
        holder.channelID.setText(videoinfo.channelID);
        holder.videoViews.setText(videoinfo.videoViews);
        holder.uploadedTime.setText(videoinfo.uploadedTime);

        holder.channelThumbnail.setBackground(new ShapeDrawable(new OvalShape()));
        holder.channelThumbnail.setClipToOutline(true);

        setImage(holder.videoThumbnail, videoinfo.videoThumbnail);
        setImage(holder.channelThumbnail, videoinfo.channelThumbnail);


    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView videoThumbnail;
        ImageView channelThumbnail;
        TextView videoTitle;
        TextView channelID;
        TextView videoViews;
        TextView uploadedTime;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        ViewHolder(View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
            channelThumbnail = itemView.findViewById(R.id.channelThumbnail);
            videoTitle = itemView.findViewById(R.id.videoTitle);
            channelID = itemView.findViewById(R.id.channelID);
            videoViews = itemView.findViewById(R.id.videoViews);
            uploadedTime = itemView.findViewById(R.id.uploadedTime);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mVideoInfos.size();
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mVideoInfos.get(id).videoTitle;
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}