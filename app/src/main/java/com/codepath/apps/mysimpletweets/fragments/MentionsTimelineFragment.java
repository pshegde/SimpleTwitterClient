package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.utilities.TwitterConstants;
import com.codepath.apps.mysimpletweets.utilities.TwitterUtilities;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Prajakta on 5/30/2015.
 */
public class MentionsTimelineFragment extends TweetsListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isNetworkAvailable()) {
            Toast.makeText(getActivity(), R.string.no_network_string, Toast.LENGTH_SHORT).show();
            //show cached tweets
            // Construct adapter plugging in the array source
            // Query ActiveAndroid for list of data
//            List<Tweet> queryResults = new Select().from(Tweet.class)
//                    .orderBy("CreatedAt ASC").limit(100).execute();
//            // Load the result into the adapter using `addAll`
//            addAll(queryResults,true);

        } else {
            populateTimeline(false);
        }
    }

    @Override
    public void fetchTimelineAsync(int page) {
        populateTimeline(true);

    }

    @Override
    public void populateTimeline(final boolean swipeRefresh) {
        if(!swipeRefresh){
            showProgressBar();
        }else {
            setMaxId(TwitterConstants.DEFAULT_MAX_ID);  //swipe refresh true so reset cursor
        }
        if(getMaxId() != TwitterConstants.DEFAULT_MAX_ID && !swipeRefresh)
            setClear(false);
        else
            setClear(true);
        TwitterUtilities.getRestClient().getMentionsTimeline(getMaxId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if(!swipeRefresh)
                    hideProgressBar();
                else
                    swipeContainer.setRefreshing(false);

                Log.d("DEBUG", response.toString());
                addAll(Tweet.fromJSONArray(response), isClear());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if(!swipeRefresh)
                    hideProgressBar();
                else
                    swipeContainer.setRefreshing(false);

                Log.d("DEBUG", errorResponse.toString());
                Toast.makeText(getActivity(), R.string.no_tweets, Toast.LENGTH_SHORT).show();
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    @Override
    public void customLoadMoreDataFromApi(int offset, int total) {
        if (offset>2)
            return;
        populateTimeline(false);
    }
}
