package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.codepath.apps.mysimpletweets.models.Tweet;

import java.util.List;

/**
 * Created by Prajakta on 5/23/2015.
 */
public class TweetArrayAdapter extends ArrayAdapter<Tweet>{


    public TweetArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1, tweets);

    }
}
