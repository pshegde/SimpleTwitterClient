package com.codepath.apps.mysimpletweets.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.activities.TweetDisplayActivity;
import com.codepath.apps.mysimpletweets.adapters.TweetArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.scrolllistener.EndlessScrollListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prajakta on 5/29/2015.
 */
public abstract class TweetsListFragment extends Fragment{
    private List<Tweet> tweets;
    private TweetArrayAdapter aTweets;
    private ListView lvTweets;

    private String max_id;
    //private String since_id;
    public SwipeRefreshLayout swipeContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);

        //connect adapter
        lvTweets.setAdapter(aTweets);


        // Attach the listener to the AdapterView onCreate
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                customLoadMoreDataFromApi(page, totalItemsCount);
                // or customLoadMoreDataFromApi(totalItemsCount);
            }
        });
        lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), TweetDisplayActivity.class);
                Tweet result = tweets.get(position);
                i.putExtra("tweet_selected", result);   //either be serializable or parcelable
                startActivity(i);
            }

        });

        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create the arraylist
        tweets = new ArrayList<>();
        //construct adapter from data source
        aTweets = new TweetArrayAdapter(getActivity(),tweets);
    }

    public void addAll(List<Tweet> list,boolean clear){
        if(clear)
            aTweets.clear();
        //List<Tweet> list = Tweet.fromJSONArray(response);
        aTweets.addAll(list);
        int lastTweet = list.size() - 1;
        max_id = String.valueOf(list.get(lastTweet).getUid());
        //since_id = String.valueOf(list.get(0).getUid());//list.get(0);
        //save to database
        saveTweetsOfflineStorage(list, true);
    }

    private void saveTweetsOfflineStorage(List<Tweet> tweetsList, boolean clear) {

        if(clear) {
            // Deleting all earlier items
//            Tweet item = Tweet.load(Tweet.class, 1);
//            item.delete();
//            User user = User.load(User.class,2);
//            user.delete();
// or with
            List<Tweet> queryResults = new Select().from(Tweet.class)
                    .execute();
            for(Tweet t:queryResults) {
                new Delete().from(Tweet.class).where("uid = ?", t.getUid()).execute();
            }
            List<User> userResults = new Select().from(User.class).execute();
            for(User u:userResults) {
                new Delete().from(User.class).where("uid = ?", u.getUid()).execute();
            }
        }
        for(Tweet tweet:tweetsList) {
            // Create a category
            User user = tweet.getUser();
            user.save();

            // Create an item
            tweet.save();
        }
    }

    public Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    //to be overridden in fragments
    public abstract void fetchTimelineAsync(int page) ;
    public abstract void populateTimeline();
    public abstract void customLoadMoreDataFromApi(int offset, int total);

    //getter setters
    public String getMaxId() {
        return max_id;
    }

    public void setMaxId(String max_id) {
        this.max_id = max_id;
    }




}
