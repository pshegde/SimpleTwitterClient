package com.codepath.apps.mysimpletweets.fragments;


import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utilities.TwitterUtilities;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prajakta on 5/31/2015.
 */
public  class FollowersListFragment extends UsersListFragment {

    public static FollowersListFragment newInstance(String screen_name) {
        FollowersListFragment fragmentDemo = new FollowersListFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screen_name);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    //to be overridden in fragments
    public void fetchTimelineAsync(int page) {

    }

    public void populateTimeline() {
        showProgressBar();
        String screenName = getArguments().getString("screen_name", "");
        TwitterUtilities.getRestClient().getFollowersIds(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                try {
                    List<Integer> listFollowers = new ArrayList<Integer>();
                    JSONArray friends = response.getJSONArray("ids");
                    setNextCursor(response.getInt("next_cursor"));
                    int len = friends.length();
                    for (int i = 0; i < len; i++) {
                        listFollowers.add(friends.getInt(i));
                    }

                    TwitterUtilities.getRestClient().getUserDetails(listFollowers, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            //super.onSuccess(statusCode, headers, response);
                            Log.d("DEBUG", response.toString());
                            hideProgressBar();
                            addAll(User.fromJSONArray(response), true);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            hideProgressBar();
                            Log.d("DEBUG", errorResponse.toString());
                            Toast.makeText(getActivity(), R.string.no_user_string, Toast.LENGTH_SHORT).show();
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

    public void customLoadMoreDataFromApi(int offset, int total){
        Log.d("DEBUG", "**next cursor "+getNextCursor() + "offset " +offset);
        if(getNextCursor() == 0 || offset>4)
            return;
        showProgressBar();
        String screenName = getArguments().getString("screen_name", "");
        TwitterUtilities.getRestClient().getFollowersIdsScroll(screenName, String.valueOf(getNextCursor()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                try {
                    List<Integer> listFollowers = new ArrayList<Integer>();
                    JSONArray friends = response.getJSONArray("ids");
                    setNextCursor(response.getInt("next_cursor"));
                    int len = friends.length();
                    for (int i = 0; i < len; i++) {
                        listFollowers.add(friends.getInt(i));
                    }

                    TwitterUtilities.getRestClient().getUserDetails(listFollowers, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            //super.onSuccess(statusCode, headers, response);
                            Log.d("DEBUG", response.toString());
                            hideProgressBar();
                            addAll(User.fromJSONArray(response), true);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            hideProgressBar();
                            Log.d("DEBUG", errorResponse.toString());
                            Toast.makeText(getActivity(), R.string.no_user_string, Toast.LENGTH_SHORT).show();
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
}
