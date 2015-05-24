package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Prajakta on 5/23/2015.
 */
public class TweetArrayAdapter extends ArrayAdapter<Tweet>{


    public TweetArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1, tweets);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get tweet
        Tweet tweet = getItem(position);
        //find or inflate the template
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet,parent,false);
        }
        //find the subviews to fill data in the template
        ImageView ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
        //populate data into subview
        tvUserName.setText(tweet.getUser().getScreenName());
        tvBody.setText(tweet.getBody());
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
        //return view to be inserted in the list
        return convertView;
    }
}
