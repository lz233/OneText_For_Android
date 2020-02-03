package com.lz233.onetext.tools.feed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.lz233.onetext.R;
import com.lz233.onetext.activity.SetFeedActivity;
import com.lz233.onetext.tools.utils.FeedUtil;
import com.lz233.onetext.tools.utils.FileUtil;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    private List<Feed> feedList;

    public FeedAdapter(List<Feed> feedList) {
        this.feedList = feedList;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        final FeedAdapter.ViewHolder holder = new ViewHolder(view);
        /*holder.feedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Feed feed = feedList.get(position);
                Toast.makeText(v.getContext(), "you clicked view " + feed.getFeedName(), Toast.LENGTH_SHORT).show();
            }
        });*/
        holder.feedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Feed feed = feedList.get(position);
                SharedPreferences sharedPreferences = feed.getContext().getSharedPreferences("setting", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("feed_code", position);
                editor.remove("feed_latest_refresh_time");
                editor.remove("widget_latest_refresh_time");
                editor.remove("onetext_code");
                editor.apply();
                if (feed.getFeedTypeImageID() == R.drawable.ic_cloud) {
                    FileUtil.deleteFile(feed.getContext().getFilesDir().getPath() + "/OneText/OneText-Library.json");
                }
                Intent intent = new Intent("com.lz233.onetext.updatefeedlist");
                intent.setPackage(feed.getContext().getPackageName());
                feed.getContext().sendBroadcast(intent);
                /*Intent intent2 = new Intent("com.lz233.onetext.issettingupdated");
                intent2.setPackage(feed.getContext().getPackageName());
                feed.getContext().sendBroadcast(intent2);*/
            }
        });
        holder.feed_edit_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to-do
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(parent.getContext(), SetFeedActivity.class);
                intent.putExtra("feed_int", position);
                parent.getContext().startActivity(intent);
            }
        });
        holder.feed_delete_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Feed feed = feedList.get(position);
                if (getItemCount() == 1) {
                    Snackbar.make(v, feed.getContext().getText(R.string.feed_cannot_delete_text), Snackbar.LENGTH_SHORT).show();
                } else {
                    /*Intent intent3 = new Intent("com.lz233.onetext.issettingupdated");
                    intent3.setPackage(feed.getContext().getPackageName());
                    feed.getContext().sendBroadcast(intent3);*/
                    FeedUtil feedUtil = new FeedUtil(view.getContext());
                    feedUtil.deleteFeed(position);
                    Intent intent = new Intent("com.lz233.onetext.updatefeedlist");
                    intent.setPackage(feed.getContext().getPackageName());
                    feed.getContext().sendBroadcast(intent);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Feed feed = feedList.get(position);
        holder.feed_name_textview.setText(feed.getFeedName());
        int feedTypeImageID = feed.getFeedTypeImageID();
        if (feed.ifSelected()) {
            if (feedTypeImageID == R.drawable.ic_cloud) {
                holder.feed_type_imageview.setImageResource(R.drawable.ic_cloud_two_tone);
            } else if (feedTypeImageID == R.drawable.ic_file) {
                holder.feed_type_imageview.setImageResource(R.drawable.ic_file_two_tone);
            } else if (feedTypeImageID == R.drawable.ic_world) {
                holder.feed_type_imageview.setImageResource(R.drawable.ic_world_two_tone);
            }
        } else {
            holder.feed_type_imageview.setImageResource(feedTypeImageID);
        }
    }

    @Override
    public int getItemCount() {
        return this.feedList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View feedView;
        LinearLayout feed_select_linearlayout;
        ImageView feed_type_imageview;
        TextView feed_name_textview;
        TextView feed_edit_textview;
        TextView feed_delete_textview;

        public ViewHolder(View view) {
            super(view);
            feedView = view;
            feed_select_linearlayout = view.findViewById(R.id.feed_select_linearlayout);
            feed_type_imageview = view.findViewById(R.id.feed_type_imageview);
            feed_name_textview = view.findViewById(R.id.feed_name_textview);
            feed_edit_textview = view.findViewById(R.id.feed_edit_textview);
            feed_delete_textview = view.findViewById(R.id.feed_delete_textview);
        }
    }
}
