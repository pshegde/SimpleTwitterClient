package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.activities.ProfileActivity;
import com.codepath.apps.mysimpletweets.activities.TweetDisplayActivity;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.utilities.TwitterUtilities;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Prajakta on 5/23/2015.
 */
public class TweetArrayAdapter extends ArrayAdapter<Tweet>{
    private Tweet tweet;

    private static class ViewHolder {
        ImageView ivProfileImage;
        TextView tvUserName;
        TextView tvBody;
        TextView tvDate;
        TextView tvName;
    }

    public TweetArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get tweet
        tweet = getItem(position);
        //find or inflate the template
//        if(convertView == null){
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet,parent,false);
//        }
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_tweet, parent, false);
            viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            convertView.setTag(viewHolder);


        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), TweetDisplayActivity.class);
                i.putExtra("tweet_selected", tweet);   //either be serializable or parcelable
                getContext().startActivity(i);
            }
        });
        viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ProfileActivity.class);
                i.putExtra("user_selected", tweet.getUser());   //either be serializable or parcelable
                getContext().startActivity(i);
            }
        });

        //find the subviews to fill data in the template

        //populate data into subview
        viewHolder.tvUserName.setText("@" +tweet.getUser().getScreenName());
        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvDate.setText(TwitterUtilities.getRelativeTimeAgo(tweet.getCreatedAt()));
        viewHolder.tvName.setText(tweet.getUser().getName());
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(viewHolder.ivProfileImage);
        //return view to be inserted in the list
        return convertView;
    }
}
