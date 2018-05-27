package com.alphaford.pimapplication.StatsDay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


import com.alphaford.pimapplication.AdvancedResearchActivity;
import com.alphaford.pimapplication.LoginActivity;
import com.alphaford.pimapplication.R;

import com.alphaford.pimapplication.WatcherActivity;
import com.alphaford.pimapplication.YoutubeStatisticActivity;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;


public class StatsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private StatsActivity.SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private DrawerLayout mDrawerLayout;
    String permissionClient="";
    ImageButton refresh;
    public SharedPreferences sharedPreferences;
    SharedPreferences.Editor ed ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
//            mSocket.connect();
//            mSocket.on("output", onNewMessage);
//            mSocket.on("eveneeeennt", onNewEvent);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        ed = sharedPreferences.edit();
        permissionClient = sharedPreferences.getString("permission",null);
        Log.d("permission",permissionClient);




        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new StatsActivity.SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.general_statistic) {

        }  else if (id == R.id.advanced_research) {
            Intent i = new Intent(StatsActivity.this, AdvancedResearchActivity.class);
            startActivity(i);
            if (permissionClient.toLowerCase().equals("gold") || permissionClient.toLowerCase().equals("platinium")) {
            }else {

                Toast.makeText(StatsActivity.this, "Permission denied", Toast.LENGTH_LONG).show();

            }

        }  else if (id == R.id.youtube_statistic) {
            if (!permissionClient.toLowerCase().equals("platinium")) {
                Toast.makeText(StatsActivity.this, "Permission denied", Toast.LENGTH_LONG).show();
            }else {
                Intent i = new Intent(StatsActivity.this, YoutubeStatisticActivity.class);
                startActivity(i);
            }

        } else if (id == R.id.watcher) {
            if (permissionClient.toLowerCase().equals("gold") || permissionClient.toLowerCase().equals("platinium")) {
                Intent i = new Intent(StatsActivity.this, WatcherActivity.class);
                startActivity(i);
            }else {

                Toast.makeText(StatsActivity.this, "Permission denied", Toast.LENGTH_LONG).show();

            }
        }
        else if (id == R.id.disconnect) {
                 /* Intent  i = new Intent(HomeActivity.this, LoginActivity.class);
                sharedPref = this.getSharedPreferences( "myPref", MODE_PRIVATE);
                editor = sharedPref.edit();
                editor.clear();
                editor.putInt("logIn", 0);
                editor.commit();
                startActivity(i);*/

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.d("socket reslt","sockeeeeeeet");

                    Log.d("socket reslt",data.toString());
                }
            });
        }


    };
    private Emitter.Listener onNewEvent = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //    JSONObject data = (JSONObject) args[0];
                    Log.d("socket reslt","sockeeeeeeet");

                    //    Log.d("socket reslt",data.toString());
                }
            });
        }


    };
    /**
     * A placeholder fragment containing a simple view.
     */


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0 :
                    FragChannelMin f2 =new FragChannelMin();
                    return f2;
                case 1 : FragChannelVues f =new FragChannelVues();
                    return f;
                case 2 : FragProgMin f3 =new FragProgMin();
                    return f3;
                case 3 : FragProgVues f4 =new FragProgVues();
                    return f4;

                default: return null;

            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Channel/Time";
                case 1:
                    return "Channel/Views";
                case 2:
                    return "Prog/Time";
                case 3 :
                    return "Prog/Views";
            }
            return null;
        }
    }
}