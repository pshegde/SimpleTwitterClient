package com.codepath.apps.mysimpletweets.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prajakta on 5/20/2015.
 */
public class Tweet {
    private String body;
    private long uid; //db id for the tweet
    private String createdAt;
    private User user;

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public User getUser() {
        return user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    //Deserialize the JSON into a java obj
    //Tweet.fromJSON("{ ... }"} = ><tweet>
    public static Tweet fromJSON(JSONObject jsonObject){
        Tweet tweet = new Tweet();
        //extract values from json

        try {
            tweet.body = jsonObject.getString("text");

            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static List<Tweet> fromJSONArray(JSONArray tweetsJSON) {
        List<Tweet> list = new ArrayList<>();
        for(int i=0;i<tweetsJSON.length();i++){
            try {
                JSONObject tweetJSON = tweetsJSON.getJSONObject(i);
                Tweet tweet = fromJSON(tweetJSON);
                if(tweet != null)
                    list.add(tweet);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

        }
        return list;
    }
}
