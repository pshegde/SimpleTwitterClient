package com.codepath.apps.mysimpletweets.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.fragments.FollowersListFragment;
import com.codepath.apps.mysimpletweets.fragments.FriendsListFragment;
import com.codepath.apps.mysimpletweets.fragments.TweetsListFragment;
import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utilities.TwitterUtilities;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

public class ProfileActivity extends ActionBarActivity {
    private User user;
    private ImageView ivProfileImage;
    private TextView tvName;
    private TextView tvTagline;
    private TextView tvFollowersCount;
    private TextView tvFriendsCount;

    private ViewPager vpPager;
    private ProfilePagerAdapter vpAdapter;
    private PagerSlidingTabStrip tabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.blue)));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_bird1);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvName = (TextView) findViewById(R.id.tvName);
        tvTagline = (TextView) findViewById(R.id.tvTagline);
        tvFollowersCount = (TextView) findViewById(R.id.tvFollowers);
        tvFriendsCount = (TextView) findViewById(R.id.tvFriends);

        TwitterUtilities.getRestClient().getUserCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.fromJSON(response);
                getSupportActionBar().setTitle("@" + user.getScreenName());
                populateProfileHeader(user);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                Toast.makeText(getBaseContext(), R.string.no_user_string, Toast.LENGTH_SHORT).show();
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
        //get the screenname

        String screenName = getIntent().getStringExtra("screen_name");
        //create the user timeline fragment
        //if(savedInstanceState == null) {
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            UserTimelineFragment frag = UserTimelineFragment.newInstance(screenName);
//            ft.replace(R.id.flContainer, frag);
//            ft.commit();
        //get the viewpager
        vpPager = (ViewPager) findViewById(R.id.vpProfile);


        //set the viewpager adapter fr the pager
        vpAdapter = new ProfilePagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(vpAdapter);
        vpAdapter.setScreenName(screenName);

        //find the sliding tabstrip
        tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabsProfile);

        //attavh tabstrip to the viewpager
        tabStrip.setViewPager(vpPager);
        //  }
    }

    private void populateProfileHeader(User user) {
        tvName.setText(user.getName());
        tvTagline.setText(user.getTagLine());
        Picasso.with(this).load(user.getProfileImageUrl()).into(ivProfileImage);

        tvFriendsCount.setText(Integer.toString(user.getFriendsCount()) + " Followers");
        tvFollowersCount.setText(Integer.toString(user.getFollowersCount()) + " Friends");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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

    //return the order of fragments in the view pager
    public class ProfilePagerAdapter extends FragmentPagerAdapter {
        private String tabtitles[] = {"Tweets","Friends","Followers"};
        UserTimelineFragment hf;
        private String screenName;

        public ProfilePagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0) {
                hf =  UserTimelineFragment.newInstance("");
                return hf;
            } else if(position == 1){
                return new FriendsListFragment().newInstance("") ;
            } else if(position == 2) {
                return new FollowersListFragment().newInstance("");
            } else
                return null;
        }

        @Override
        public int getCount() {
            return tabtitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabtitles[position];
        }

        public TweetsListFragment getRegisteredFragment(int i) {
            if(i==0)
                return hf;
            return null;
        }

        public void setScreenName(String screenName) {
            this.screenName = screenName;
        }
    }
}
