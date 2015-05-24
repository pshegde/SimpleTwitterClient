package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.adapters.TweetArrayAdapter;
import com.codepath.apps.mysimpletweets.applications.TwitterApplication;
import com.codepath.apps.mysimpletweets.clients.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.scrolllistener.EndlessScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends ActionBarActivity {

    private TwitterClient client;
    private List<Tweet> tweets;
    private TweetArrayAdapter aTweets;
    private ListView lvTweets;
    private String max_id;
    private String since_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        //create the arraylist
        tweets = new ArrayList<>();
        //construct adapter from data source
        aTweets = new TweetArrayAdapter(this,tweets);
        //connect adapter
        lvTweets.setAdapter(aTweets);
        client = TwitterApplication.getRestClient();
        populateTimeline();

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
    }

    //send api req to timeline json and crate tweet obj using deserialize and create tweet
    private void populateTimeline(){
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG",response.toString());

                //deserialize json

                //create models
                //load the model data into listview
                aTweets.clear();
                List<Tweet> list = Tweet.fromJSONArray(response);
                aTweets.addAll(list);
                int lastTweet = list.size()-1;
                max_id = String.valueOf(list.get(lastTweet).getUid());
                since_id = String.valueOf(list.get(0).getUid());//list.get(0);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG",errorResponse.toString());
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset, int total) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
        if (offset>4)
                return;

        client.getHomeTimelineScroll(since_id,max_id,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG",response.toString());

                //deserialize json

                //create models
                //load the model data into listview
               // aTweets.clear();
                List<Tweet> list = Tweet.fromJSONArray(response);
                int lastTweet = list.size()-1;
                if(list.size() == 0)
                    Toast.makeText(getBaseContext(),"No tweets found",Toast.LENGTH_SHORT).show();
                max_id = String.valueOf(list.get(lastTweet).getUid());
                since_id = String.valueOf(list.get(0).getUid());//list.get(0);
                aTweets.addAll(list);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG",errorResponse.toString());
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }


}
