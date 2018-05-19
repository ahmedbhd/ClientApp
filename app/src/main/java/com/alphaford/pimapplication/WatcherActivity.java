package com.alphaford.pimapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import com.alphaford.pimapplication.StatsDay.StatsActivity;

public class WatcherActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ListView channelList;
    SearchView searchView;
    String[] channels = {"elhiwar","canal","bein","mtv","mbc","el watania","france 2"};
    ArrayAdapter<String> adapter;
    LinearLayout layoutWatcher;
    EditText seuilVues;
    Button submitWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watcher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        channelList = (ListView) findViewById(R.id.listeChaine);
        searchView = (SearchView) findViewById(R.id.searchBar);
        layoutWatcher = (LinearLayout) findViewById(R.id.layoutWatcher);
        submitWatcher = (Button) findViewById(R.id.submit_seuil);
        seuilVues = (EditText) findViewById(R.id.seuil_vues);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,channels);
        channelList.setAdapter(adapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return  false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
        channelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                layoutWatcher.setVisibility(View.VISIBLE);
            }
        });
        submitWatcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(WatcherActivity.this);
                builder1.setMessage("A notification will be sent !");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent intent = new Intent(WatcherActivity.this, StatsActivity.class);
                                startActivity(intent);
                            }
                        });


                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

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
            Intent i=new Intent(WatcherActivity.this, StatsActivity.class);
            startActivity(i);

        }  else if (id == R.id.advanced_research) {
            Intent i=new Intent(WatcherActivity.this, AdvancedResearchActivity.class);
            startActivity(i);


        }  else if (id == R.id.youtube_statistic) {
            Intent i=new Intent(WatcherActivity.this, YoutubeStatisticActivity.class);
            startActivity(i);

        } else if (id == R.id.watcher) {


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

}
