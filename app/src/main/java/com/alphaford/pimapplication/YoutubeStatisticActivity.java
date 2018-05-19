package com.alphaford.pimapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alphaford.pimapplication.Models.Chaine;
import com.alphaford.pimapplication.Models.History;
import com.alphaford.pimapplication.Models.Recepteur;
import com.alphaford.pimapplication.Models.historiqueChaine;
import com.alphaford.pimapplication.StatsDay.FragChannelMin;
import com.alphaford.pimapplication.StatsDay.FragChannelVues;
import com.alphaford.pimapplication.StatsDay.FragProgMin;
import com.alphaford.pimapplication.StatsDay.FragProgVues;
import com.alphaford.pimapplication.StatsDay.StatsActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YoutubeStatisticActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {



    private YoutubeStatisticActivity.SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_statistic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new YoutubeStatisticActivity.SectionsPagerAdapter(getSupportFragmentManager());

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
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.general_statistic) {
            Intent i=new Intent(YoutubeStatisticActivity.this, StatsActivity.class);
            startActivity(i);

        }  else if (id == R.id.advanced_research) {
            Intent i=new Intent(YoutubeStatisticActivity.this, AdvancedResearchActivity.class);
            startActivity(i);


        }  else if (id == R.id.youtube_statistic) {
            // Intent i=new Intent(StatsActivity.this, StatsYearActivity.class);
            //startActivity(i);

        } else if (id == R.id.watcher) {
            Intent i=new Intent(YoutubeStatisticActivity.this, WatcherActivity.class);
            startActivity(i);

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
                    YoutubeFragment f2 =new YoutubeFragment();
                    return f2;
                case 1 : AllStatisticFragment f =new AllStatisticFragment();
                    return f;

                default: return null;

            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Youtube Statistic";
                case 1:
                    return "All statistics";

            }
            return null;
        }
    }





}
