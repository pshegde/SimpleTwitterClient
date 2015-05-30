package com.codepath.apps.mysimpletweets.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prajakta on 5/20/2015.
 */
@Table(name = "Tweets")
public class Tweet extends Model implements Parcelable {
   @Column(name = "body")
    private String body;
    @Column(name = "uid")
    private long uid; //db id for the tweet
    @Column(name = "createdAt")
    private String createdAt;
    @Column(name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;


    public Tweet() {
        super();
    }

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
            //tweet.description = jsonObject.getString("description");
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

    public void setBody(String body) {
        this.body = body;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.body);
        dest.writeLong(this.uid);
        dest.writeString(this.createdAt);
        dest.writeParcelable(this.user, 0);

    }

    private Tweet(Parcel in) {
        this.body = in.readString();
        this.uid = in.readLong();
        this.createdAt = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());

    }

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
