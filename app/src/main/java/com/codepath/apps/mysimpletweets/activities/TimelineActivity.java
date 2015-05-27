package com.codepath.apps.mysimpletweets.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.adapters.TweetArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.scrolllistener.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.utilities.TwitterUtilities;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends ActionBarActivity {

    private List<Tweet> tweets;
    private TweetArrayAdapter aTweets;
    private ListView lvTweets;
    private String max_id;
    private String since_id;
    private SwipeRefreshLayout swipeContainer;

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

        getSupportActionBar().setTitle(R.string.home);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                                                                        .getColor(R.color.blue)));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_bird1);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        if (!isNetworkAvailable()) {
            Toast.makeText(this, R.string.no_network_string, Toast.LENGTH_SHORT).show();
            //show cached tweets
            // Construct adapter plugging in the array source
            // Query ActiveAndroid for list of data
            List<Tweet> queryResults = new Select().from(Tweet.class)
                    .orderBy("CreatedAt ASC").limit(100).execute();
            // Load the result into the adapter using `addAll`
            aTweets.addAll(queryResults);

        } else {
            populateTimeline();
        }
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
                Intent i = new Intent(TimelineActivity.this, TweetDisplayActivity.class);
                Tweet result = tweets.get(position);
                i.putExtra("tweet_selected", result);   //either be serializable or parcelable
                startActivity(i);
            }

        });

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
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

    }

    public void fetchTimelineAsync(int page) {
        TwitterUtilities.getRestClient().getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // ...the data has come back, add new items to your adapter...
                aTweets.clear();
                List<Tweet> list = Tweet.fromJSONArray(response);
                aTweets.addAll(list);
                int lastTweet = list.size() - 1;
                max_id = String.valueOf(list.get(lastTweet).getUid());
                since_id = String.valueOf(list.get(0).getUid());//list.get(0);
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                Toast.makeText(getBaseContext(),R.string.no_network_string,Toast.LENGTH_SHORT).show();
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

    }

    //send api req to timeline json and crate tweet obj using deserialize and create tweet
    private void populateTimeline(){
        TwitterUtilities.getRestClient().getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());

                //deserialize json

                //create models
                //load the model data into listview
                aTweets.clear();
                List<Tweet> list = Tweet.fromJSONArray(response);
                aTweets.addAll(list);
                int lastTweet = list.size() - 1;
                max_id = String.valueOf(list.get(lastTweet).getUid());
                since_id = String.valueOf(list.get(0).getUid());//list.get(0);

                //save to database
                saveTweetsOfflineStorage(list,true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                Toast.makeText(getBaseContext(),R.string.no_tweets,Toast.LENGTH_SHORT).show();
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void saveTweetsOfflineStorage(List<Tweet> tweetsList, boolean onNoScroll) {

        if(onNoScroll) {
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

        if(id==R.id.action_compose_tweet) {
            //launch a second age form activity
            onComposeTweet();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode==20){
            populateTimeline();
        }
    }

    private void onComposeTweet() {
        Toast.makeText(getBaseContext(),"Compose tweet",Toast.LENGTH_SHORT).show();
        Intent composeIntent = new Intent(this, ComposeTweetActivity.class);
        startActivityForResult(composeIntent, 20);
    }

    // Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset, int total) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
        if (offset>4)
                return;

        TwitterUtilities.getRestClient().getHomeTimelineScroll(since_id, max_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());

                //deserialize json

                //create models
                //load the model data into listview
                // aTweets.clear();
                List<Tweet> list = Tweet.fromJSONArray(response);
                int lastTweet = list.size() - 1;
                if (list.size() == 0)
                    Toast.makeText(getBaseContext(), "No tweets found", Toast.LENGTH_SHORT).show();
                max_id = String.valueOf(list.get(lastTweet).getUid());
                since_id = String.valueOf(list.get(0).getUid());//list.get(0);
                aTweets.addAll(list);
                saveTweetsOfflineStorage(list,false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                Toast.makeText(getBaseContext(),R.string.no_tweets,Toast.LENGTH_SHORT).show();
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
