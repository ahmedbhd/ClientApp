package com.alphaford.pimapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.Toast;

import com.alphaford.pimapplication.Models.History;
import com.alphaford.pimapplication.Models.Recepteur;
import com.alphaford.pimapplication.Models.historiqueChaine;
import com.alphaford.pimapplication.StatsDay.StatsActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WatcherActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ListView channelList;
    SearchView searchView;
    String[] channels = {"elhiwar","canal","bein","mtv","mbc","el watania","france 2"};
    private  List<String> chaines;
    List<String> ch = new ArrayList<>();
    ArrayAdapter<String> adapter;
    LinearLayout layoutWatcher;
    EditText seuilVues;
    Button submitWatcher;
    String permission;
    String nameFavorite;
    public SharedPreferences sharedPref,sharedPreferences;
    SharedPreferences.Editor editor,ed ;
    public static int aa;
    ConnexionManager cnx = new ConnexionManager();
    String HttpUrl = cnx.getPath(":8080/api/historys");
    RequestQueue queue ;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watcher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        chaines=new ArrayList<>();
        fetchchannels();
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
        seuilVues.setGravity(Gravity.CENTER);

        sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        ed = sharedPreferences.edit();
        permission = sharedPreferences.getString("permission",null);
        sharedPref = this.getSharedPreferences( "watcherPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences("watcherPref", MODE_PRIVATE).edit();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,chaines);
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
                nameFavorite = chaines.get(i);
                Log.d("Name Favooooooorite",nameFavorite);
            }
        });
        submitWatcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!seuilVues.getText().toString().equals("")){
                    String  v =seuilVues.getText().toString();
                    int valSeuil = Integer.valueOf(v);
                    editor.putInt("seuilWatcher", valSeuil);
                    editor.putString("nomFavoris",nameFavorite);
                    editor.commit();

                }

                aa = sharedPref.getInt("seuilWatcher", 0);
                System.out.println("Shaaared **** "+String.valueOf(aa));

                AlertDialog.Builder builder1 = new AlertDialog.Builder(WatcherActivity.this);
                builder1.setMessage("You will be notified as soon as "+nameFavorite+" achieves "+aa+" Viewers ");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent intentService=new Intent(WatcherActivity.this,NotificationService.class);
                                intentService.putExtra("seuil",aa);
                                intentService.putExtra("nomChaine",nameFavorite);
                                intentService.putExtra("token",token);
                                startService(intentService);
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
            if (permission.equals("3")){
                Intent i=new Intent(WatcherActivity.this, YoutubeStatisticActivity.class);
                startActivity(i);
            }else {
                Toast.makeText(WatcherActivity.this, "Permission denied", Toast.LENGTH_LONG).show();
            }

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
    private void fetchchannels() {
        queue = Volley.newRequestQueue(getApplicationContext());

        sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        ed = sharedPreferences.edit();
        token = sharedPreferences.getString("token",null);

        // Log.d("hhhhhhhhhhhhhh",sharedPref.getString("username",null));
        StringRequest request = new StringRequest(com.android.volley.Request.Method.GET, cnx.getPath(":8080/api/historys"), onPostsLoaded2, onPostsError2)
        {
            @Override

            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("x-access-token", sharedPreferences.getString("token",null));
                headers.put("Content-Type", "application/json");

                return headers;
            }

        };
        queue.add(request);

    }
    private final com.android.volley.Response.Listener<String> onPostsLoaded2 = new com.android.volley.Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

            Type listType = new TypeToken<List<History>>() {}.getType();

            JSONArray jsonArray = new JSONArray();
            JSONObject objJson = new JSONObject();

            try {
                jsonArray = new JSONArray(response);
                // Log.d("json aray", jsonArray.toString());

                for (int i = 0; i < jsonArray.length(); i++) {

                    objJson = jsonArray.getJSONObject(i);

                    // here you can get id,name,city...
                    String id = objJson.getString("_id");
                    String recepteur = objJson.getString("recepteur");
                    String bouquet = objJson.getString("bouquet");
                    String channel = objJson.getString("channel");
                    String program = objJson.getString("program");
                    //Date date = objJson.get
                    History h =new History(recepteur,bouquet,channel,program);
                    Log.d("haaaa", h.toString());
                    chaines.add(h.getChannel());
                    Log.d("chaineListe", chaines.get(0));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
    private final com.android.volley.Response.ErrorListener onPostsError2 = new com.android.volley.Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("PostActivity", error.toString());
        }


    };


}
