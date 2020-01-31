package com.lz233.onetext.tools.feed;

import android.content.Context;

public class Feed {
    private Context context;
    private int feed_type_imageview_id;
    private String feed_name_textview_text;
    private Boolean ifSelected;

    public Feed(Context context, int feed_type_imageview_id, String feed_name_textview_text, Boolean ifSelected) {
        this.feed_type_imageview_id = feed_type_imageview_id;
        this.feed_name_textview_text = feed_name_textview_text;
        this.ifSelected = ifSelected;
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public int getFeedTypeImageID() {
        return feed_type_imageview_id;
    }

    public String getFeedName() {
        return feed_name_textview_text;
    }

    public Boolean ifSelected() {
        return ifSelected;
    }
}
