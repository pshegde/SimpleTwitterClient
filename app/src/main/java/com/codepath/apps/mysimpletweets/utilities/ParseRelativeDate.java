package com.codepath.apps.mysimpletweets.utilities;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Prajakta on 5/23/2015.
 */
public class ParseRelativeDate {
    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
            relativeDate = relativeDate.replace("minutes","m").replace("minutes","m").replace("weeks","w").replace("hours","h").replace("seconds","s").replace("minute","m")
                    .replace("days","d").replace("day","d")
                    .replace("week","w").replace("hour","h")
                    .replace("second","s").replace("ago","").replace(" ","");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
