package com.codepath.apps.mysimpletweets.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

public class TweetDisplayActivity extends ActionBarActivity {
    private ImageView ivDetailProfileImage;
    private TextView tvDetailName;
    private TextView tvDetailUserName;
    private TextView tvDetailBody;
    //private TextView tvDetailDesc;
    private TextView tvDetailDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_display);

        Tweet tweet = (Tweet) getIntent().getSerializableExtra("tweet_selected");

        getSupportActionBar().setTitle(R.string.home);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.blue)));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_bird1);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        ivDetailProfileImage = (ImageView) findViewById(R.id.ivDetailProfileImage);
        tvDetailName = (TextView) findViewById(R.id.tvDetailName);
        tvDetailBody = (TextView) findViewById(R.id.tvDetailBody);
        tvDetailUserName = (TextView) findViewById(R.id.tvDetailUserName);
        tvDetailDate = (TextView) findViewById(R.id.tvDetailDate);
       // tvDetailDesc = (TextView) findViewById(R.id.tvDetailDesc);

        tvDetailUserName.setText("@" +tweet.getUser().getScreenName());
        tvDetailBody.setText(tweet.getBody());
        tvDetailDate.setText(tweet.getCreatedAt());
        tvDetailName.setText(tweet.getUser().getName());

        Picasso.with(getBaseContext()).load(tweet.getUser().getProfileImageUrl()).into(ivDetailProfileImage);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tweet_display, menu);
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
}
