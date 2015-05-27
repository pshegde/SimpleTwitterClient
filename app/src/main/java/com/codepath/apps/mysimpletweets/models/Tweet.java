package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prajakta on 5/20/2015.
 */
@Table(name = "Tweets")
public class Tweet extends Model implements Serializable {
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

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel out, int flags) {
//        out.writeString(body);
//        out.writeLong(uid);
//        out.writeString(createdAt);
//        out.writeParcelable(user,flags);
//    }
//
//    public static final Parcelable.Creator<Tweet> CREATOR
//            = new Parcelable.Creator<Tweet>() {
//        @Override
//        public Tweet createFromParcel(Parcel in) {
//            return new Tweet(in);
//        }
//
//        @Override
//        public Tweet[] newArray(int size) {
//            return new Tweet[size];
//        }
//    };
//
//    private Tweet(Parcel in) {
//        body = in.readString();
//        uid = in.readInt();
//        createdAt = in.readString();
//        user = in.readParcelable(com.codepath.apps.mysimpletweets.models.User.class.getClassLoader());
//    }


}
