package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.utilities.TwitterUtilities;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Prajakta on 5/30/2015.
 */
public class HomeTimelineFragment extends TweetsListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isNetworkAvailable()) {
            Toast.makeText(getActivity(), R.string.no_network_string, Toast.LENGTH_SHORT).show();
            //show cached tweets
            // Construct adapter plugging in the array source
            // Query ActiveAndroid for list of data
            List<Tweet> queryResults = new Select().from(Tweet.class)
                    .orderBy("CreatedAt ASC").limit(100).execute();
            // Load the result into the adapter using `addAll`
            addAll(queryResults,true);

        } else {
            populateTimeline();
        }

    }
    //send api req to timeline json and crate tweet obj using deserialize and create tweet
    public void populateTimeline() {
        Toast.makeText(getActivity(),"postttt",Toast.LENGTH_SHORT);
        showProgressBar();
        TwitterUtilities.getRestClient().getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());
                addAll(Tweet.fromJSONArray(response), true);
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                hideProgressBar();
                Log.d("DEBUG", errorResponse.toString());
                Toast.makeText(getActivity(), R.string.no_tweets, Toast.LENGTH_SHORT).show();
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    // Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset, int total) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
        if (offset>4)
            return;
        showProgressBar();
        TwitterUtilities.getRestClient().getHomeTimelineScroll(getMaxId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                hideProgressBar();
                Log.d("DEBUG", response.toString());

                //deserialize json

                //create models
                //load the model data into listview
                // aTweets.clear();
//                List<Tweet> list = Tweet.fromJSONArray(response);
//                int lastTweet = list.size() - 1;
//                if (list.size() == 0)
//                    Toast.makeText(getActivity(), "No tweets found", Toast.LENGTH_SHORT).show();
//                max_id = String.valueOf(list.get(lastTweet).getUid());
//                since_id = String.valueOf(list.get(0).getUid());//list.get(0);
//                aTweets.addAll(list);
//                saveTweetsOfflineStorage(list, false);
                addAll(Tweet.fromJSONArray(response), false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                hideProgressBar();
                Toast.makeText(getActivity(), R.string.no_tweets, Toast.LENGTH_SHORT).show();
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public void fetchTimelineAsync(int page) {
        TwitterUtilities.getRestClient().getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // ...the data has come back, add new items to your adapter...
//                aTweets.clear();
//                List<Tweet> list = Tweet.fromJSONArray(response);
//                aTweets.addAll(list);
//                int lastTweet = list.size() - 1;
//                max_id = String.valueOf(list.get(lastTweet).getUid());
//                since_id = String.valueOf(list.get(0).getUid());//list.get(0);
                addAll(Tweet.fromJSONArray(response), true);
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                Toast.makeText(getActivity(), R.string.no_network_string, Toast.LENGTH_SHORT).show();
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

    }
}
