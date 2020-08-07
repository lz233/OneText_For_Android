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
import com.lz233.onetext.tools.utils.CoreUtil;
import com.lz233.onetext.tools.utils.FileUtil;

import org.json.JSONException;

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
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);
        final FeedAdapter.ViewHolder holder = new ViewHolder(view);
        /*holder.feedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Feed feed = feedList.get(position);
                Toast.makeText(v.getContext(), "you clicked view " + feed.getFeedName(), Toast.LENGTH_SHORT).show();
            }
        });*/
        holder.feedView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            Feed feed = feedList.get(position);
            SharedPreferences sharedPreferences = feed.getContext().getSharedPreferences("setting", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            int currentPosition = sharedPreferences.getInt("feed_code",0);
            Feed feed1 = feedList.get(currentPosition);
            if (position!=currentPosition){
                editor.putInt("feed_code", position);
                editor.remove("feed_latest_refresh_time");
                editor.remove("widget_latest_refresh_time");
                editor.remove("onetext_code");
                if (feed.getFeedTypeImageID() == R.drawable.ic_cloud) {
                    FileUtil.deleteFile(feed.getContext().getFilesDir().getPath() + "/OneText/OneText-Library.json");
                }
                int originalFeedTypeImageID = 0;
                if (feed1.getFeedTypeImageID()==R.drawable.ic_cloud_two_tone){
                    originalFeedTypeImageID = R.drawable.ic_cloud;
                }else if (feed1.getFeedTypeImageID()==R.drawable.ic_file_two_tone){
                    originalFeedTypeImageID = R.drawable.ic_file;
                }else if (feed1.getFeedTypeImageID()==R.drawable.ic_world_two_tone){
                    originalFeedTypeImageID = R.drawable.ic_world;
                }
                feedList.set(currentPosition,new Feed(feed1.getContext(),originalFeedTypeImageID,feed1.getFeedName(),false));
                notifyItemChanged(currentPosition);
                int feedTypeImageID = 0;
                if (feed.getFeedTypeImageID()==R.drawable.ic_cloud){
                    feedTypeImageID=R.drawable.ic_cloud_two_tone;
                }else if (feed.getFeedTypeImageID()==R.drawable.ic_file){
                    feedTypeImageID=R.drawable.ic_file_two_tone;
                }else if (feed.getFeedTypeImageID()==R.drawable.ic_world){
                    feedTypeImageID=R.drawable.ic_world_two_tone;
                }
                feedList.set(position,new Feed(feed.getContext(),feedTypeImageID,feed.getFeedName(),true));
                notifyItemChanged(position);
                editor.apply();
            }
            /*Intent intent = new Intent("com.lz233.onetext.updatefeedlist");
            intent.setPackage(feed.getContext().getPackageName());
            feed.getContext().sendBroadcast(intent);*/
        });
        /*holder.feed_edit_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(parent.getContext(), SetFeedActivity.class);
                intent.putExtra("feed_int", position);
                parent.getContext().startActivity(intent);
            }
        });*/
        holder.feed_delete_textview.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            Feed feed = feedList.get(position);
            if (getItemCount() == 1) {
                //Snackbar.make(v, feed.getContext().getText(R.string.feed_cannot_delete_text), Snackbar.LENGTH_SHORT).show();
            } else {
                /*Intent intent3 = new Intent("com.lz233.onetext.issettingupdated");
                intent3.setPackage(feed.getContext().getPackageName());
                feed.getContext().sendBroadcast(intent3);*/
                try {
                    CoreUtil coreUtil = new CoreUtil(feed.getContext());
                    coreUtil.deleteFeed(position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent("com.lz233.onetext.updatefeedlist");
                intent.setPackage(feed.getContext().getPackageName());
                feed.getContext().sendBroadcast(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Feed feed = feedList.get(position);
        holder.feed_name_textview.setText(feed.getFeedName());
        holder.feed_type_imageview.setImageResource(feed.getFeedTypeImageID());
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
