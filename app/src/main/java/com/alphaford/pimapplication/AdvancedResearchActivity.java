package com.alphaford.pimapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.alphaford.pimapplication.Models.Chaine;
import com.alphaford.pimapplication.Models.History;
import com.alphaford.pimapplication.Models.Recepteur;
import com.alphaford.pimapplication.Models.historiqueChaine;
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
import com.github.nkzawa.emitter.Emitter;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvancedResearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    Spinner AgeSpinner,RegionSpinner,NombreSpinner;
    PieChart pieChart;
    BarChart barChart;
    Button search;
    LinearLayout layoutAge,layoutRegion,layoutNombre;
    RadioGroup radioGroup;
    RadioButton radioButton;
    ArrayList<History> locations = new ArrayList<>();
    ArrayList<Recepteur> recepteurs = new ArrayList<>();

    Chaine chaine;
    ConnexionManager cnx = new ConnexionManager();
    String HttpUrl = cnx.getPath(":8080/api/historys");
    RequestQueue requestQueue;

    RequestQueue queue ;
    GsonBuilder gsonBuilder;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor ;
    private Gson gson;

    private List<History> channels;
    private List<historiqueChaine> historiqueChaines;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(cnx.getPath(":8088"));
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_research);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        mSocket.connect();
        mSocket.on("output", onNewMessage);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        AgeSpinner=(Spinner) findViewById(R.id.spinner_age);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AdvancedResearchActivity.this,
                R.array.age_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AgeSpinner.setAdapter(adapter);

        RegionSpinner=(Spinner) findViewById(R.id.spinner_region);
        ArrayAdapter<CharSequence> adapter_region = ArrayAdapter.createFromResource(AdvancedResearchActivity.this,
                R.array.region_array, android.R.layout.simple_spinner_item);
        adapter_region.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        RegionSpinner.setAdapter(adapter_region);

        NombreSpinner=(Spinner) findViewById(R.id.spinner_nbr);
        ArrayAdapter<CharSequence> adapter_nbr = ArrayAdapter.createFromResource(AdvancedResearchActivity.this,
                R.array.nombre_array, android.R.layout.simple_spinner_item);
        adapter_nbr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        NombreSpinner.setAdapter(adapter_nbr);
        AgeSpinner.setOnItemSelectedListener(this);

        pieChart=(PieChart) findViewById(R.id.pieChart);
        barChart=(BarChart) findViewById(R.id.barChart);
        search=(Button) findViewById(R.id.search);
        layoutAge=(LinearLayout)findViewById(R.id.layoutAge);
        layoutRegion=(LinearLayout) findViewById(R.id.layoutRegion);
        layoutNombre=(LinearLayout) findViewById(R.id.layoutNombre);
        radioGroup=(RadioGroup) findViewById(R.id.radioGroupe);
        queue = Volley.newRequestQueue(getApplicationContext());
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        channels=new ArrayList<History>();
        historiqueChaines=new ArrayList<historiqueChaine>();
        chaine=new Chaine();
        fetchrecepteurs();

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
            Intent i=new Intent(AdvancedResearchActivity.this, StatsActivity.class);
            startActivity(i);

        }  else if (id == R.id.advanced_research) {



        }  else if (id == R.id.youtube_statistic) {
            Intent i=new Intent(AdvancedResearchActivity.this, YoutubeStatisticActivity.class);
            startActivity(i);

        } else if (id == R.id.watcher) {
            Intent i=new Intent(AdvancedResearchActivity.this, WatcherActivity.class);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private void setBarChartData(int count) {
        float barWidth=0.9f;

        ArrayList<BarEntry> yVals=new ArrayList<>();
        for(int i=0;i<historiqueChaines.size();i++){

            int value = (int) (historiqueChaines.get(i).getNbr_teles());
            yVals.add(new BarEntry(i, (int) value));


        }
        BarDataSet set = new BarDataSet(yVals,"teles chaines");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        set.setDrawValues(true);
        BarData data=new BarData(set);
        data.setBarWidth(barWidth);
        barChart.setData(data);
        barChart.invalidate();
        barChart.animateY(500);


    }

    private void addDataPie() {
        ArrayList<PieEntry> yEntrys=new ArrayList<>();
        ArrayList<String > xEntrys=new ArrayList<>();
        for (int i=0 ;i<historiqueChaines.size();i++){
            yEntrys.add(new PieEntry(historiqueChaines.get(i).getNbr_teles()));

        }
        for (int i=0 ;i<historiqueChaines.size();i++){
            xEntrys.add(historiqueChaines.get(i).getNom_chaine());
        }
        //create the data set
        PieDataSet pieDataSet=new PieDataSet(yEntrys,"Teles Chaines");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);
        //add Colors to dataSet
        ArrayList<Integer> colors=new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.GRAY);
        colors.add(Color.CYAN);
        colors.add(Color.RED);
        colors.add(Color.YELLOW);
        colors.add(Color.GREEN);
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        //add legends to chart
        Legend legend=pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        //create Pie data object
        PieData pieData=new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();

    }

    public void checkRadioButton(View view){
        layoutAge.setVisibility(View.GONE);
        layoutRegion.setVisibility(View.GONE);
        layoutNombre.setVisibility(View.GONE);

        search.setVisibility(View.VISIBLE);
        int radioId=radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        if (radioButton.getText().equals("Region"))
        {

            layoutRegion.setVisibility(View.VISIBLE);


            search.setOnClickListener(v -> {
                fetchLocations("region");
            });
        }
        if (radioButton.getText().equals("Membre Family"))
        {
            layoutNombre.setVisibility(View.VISIBLE);
            search.setOnClickListener(v -> {
                fetchLocations("nombre");
            });
        }
        if (radioButton.getText().equals("Age"))
        {
            layoutAge.setVisibility(View.VISIBLE);
            search.setOnClickListener(v -> {
                fetchLocations("age");
            });
        }
    }






    private void fetchrecepteurs() {
        queue = Volley.newRequestQueue(getApplicationContext());

        sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        // Log.d("hhhhhhhhhhhhhh",sharedPref.getString("username",null));
        StringRequest request = new StringRequest(com.android.volley.Request.Method.GET, cnx.getPath(":8080/api/receivers/")+sharedPref.getString("username",null), onPostsLoaded2, onPostsError2)
        {
            @Override

            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("x-access-token", sharedPref.getString("token",null));
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
                    String id_rec = objJson.getString("id_rec");
                    String client = objJson.getString("client");
                    String fam_region = objJson.getString("fam_region");
                    String fam_size = objJson.getString("fam_size");

                    String fam_age = objJson.getString("fam_age");
                    //Date date = objJson.get

                    Recepteur h = new Recepteur(id_rec, client, fam_region, fam_size, fam_age);
                    Log.d("h", h.toString());
                    recepteurs.add(h);

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


    boolean checkIfMyRecepAge(String id,String type){
        Log.d("typessss age",type);

        for( Recepteur r : recepteurs) {

            if (id.equals(r.getId_rec())) {
                int a = Integer.valueOf(r.getFam_age());
                Log.d("age family",String.valueOf(a));
                switch (type) {
                    case "inf 12ans": {

                        if (a <= 12) {
                            return true;

                        }
                        break;
                    }
                    case "sup 12ans": {
                        if (a > 12) {
                            return true;
                        }
                        break;
                    }
                    case "inf 18ans": {
                        if (a <= 18) {
                            return true;
                        }
                        break;
                    }
                    case "sup 18ans": {
                        if (a > 18) {
                            return true;
                        }
                        break;
                    }
                    case "sup 28ans": {
                        if (a > 28) {
                            return true;
                        }
                        break;
                    }
                    case "sup 35ans": {
                        if (a > 35) {
                            return true;
                        }
                        break;
                    }
                    case "sup 45ans": {
                        if (a > 45) {
                            return true;
                        }
                        break;
                    }
                    case " sup 50ans": {
                        if (a > 50) {
                            return true;
                        }
                        break;
                    }

                }
            }
        }
        return false;
    }

    boolean checkIfMyRecepRegion(String id,String type){
        Log.d("type region ",type);

        for( Recepteur r : recepteurs) {


            if (id.equals(r.getId_rec())) {
                String a = r.getFam_region();
                Log.d("rec region",a);
                switch (type) {
                    case "North": {

                        if (a.equals(type))
                            return true;
                        break;
                    }
                    case "South": {
                        if (a.equals(type))
                            return true;
                        break;
                    }
                    case "East": {
                        if (a.equals(type))
                            return true;
                        break;
                    }
                    case "West": {
                        if (a.equals(type))
                            return true;
                        break;
                    }


                }
            }
        }
        return false;
    }

    boolean checkIfMyRecepNombre(String id,String type){
        Log.d("type nbr",type);

        for( Recepteur r : recepteurs) {

            if (id.equals(r.getId_rec())) {
                int a = Integer.valueOf(r.getFam_size());
                Log.d("nombre family",String.valueOf(a));
                switch (type) {
                    case "2 ou plus": {

                        if (a >= 2)
                            return true;
                        break;
                    }
                    case "3 ou plus": {
                        if (a >= 3)
                            return true;
                        break;
                    }
                    case "4 ou plus": {
                        if (a >= 4)
                            return true;
                        break;
                    }
                    case "5 ou plus": {
                        if (a >= 5)
                            return true;
                        break;
                    }
                    case "plus 6": {
                        if (a >= 6)
                            return true;
                        break;
                    }
                }
            }
        }
        return false;
    }

    boolean checkIfMyRecep(String id){
        for( Recepteur r : recepteurs) {
            if (id.equals(r.getId_rec()))
                return true;
        }
        return false;
    }
    private void fetchLocations(String type) {
        sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        StringRequest request;
        if (type.equals("age")) {
            request = new StringRequest(com.android.volley.Request.Method.GET, HttpUrl, onPostsLoadedAge, onPostsError) {
                @Override

                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("x-access-token", sharedPref.getString("token", null));
                    headers.put("Content-Type", "application/json");

                    return headers;
                }

            };
        }
        else if (type.equals("region")){
            request = new StringRequest(com.android.volley.Request.Method.GET, HttpUrl, onPostsLoadedRegion, onPostsError) {
                @Override

                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("x-access-token", sharedPref.getString("token", null));
                    headers.put("Content-Type", "application/json");

                    return headers;
                }

            };
        }else {
            request = new StringRequest(com.android.volley.Request.Method.GET, HttpUrl, onPostsLoadedNbr, onPostsError) {
                @Override

                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("x-access-token", sharedPref.getString("token", null));
                    headers.put("Content-Type", "application/json");

                    return headers;
                }

            };
        }
        queue.add(request);

    }

    private final com.android.volley.Response.Listener<String> onPostsLoadedAge = new com.android.volley.Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
//            Log.d("static token",LoginActivity.token  );
//            // mMapView.onResume();
//            Log.d("string result ",response);
            Type listType = new TypeToken<List<History>>(){}.getType();
            fillthecharAge(response);
        }
    };
    private final com.android.volley.Response.Listener<String> onPostsLoadedRegion = new com.android.volley.Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
//            Log.d("static token",LoginActivity.token  );
//            // mMapView.onResume();
//            Log.d("string result ",response);
            Type listType = new TypeToken<List<History>>(){}.getType();
            fillthecharRegion(response);
        }
    };
    private final com.android.volley.Response.Listener<String> onPostsLoadedNbr = new com.android.volley.Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
//            Log.d("static token",LoginActivity.token  );
//            // mMapView.onResume();
//            Log.d("string result ",response);
            Type listType = new TypeToken<List<History>>(){}.getType();
            fillthecharNbr(response);
        }
    };
    private final com.android.volley.Response.ErrorListener onPostsError = new com.android.volley.Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("PostActivity", error.toString());
        }


    };


    void fillthecharAge(String response){
        JSONArray jsonArray = new JSONArray();
        JSONObject objJson = new JSONObject();

        channels.clear();
        historiqueChaines.clear();


        try {
            jsonArray = new JSONArray(response);
            Log.d("json aray",jsonArray.toString());

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
                //Log.d("h",h.toString());
                if (checkIfMyRecepAge(recepteur,AgeSpinner.getSelectedItem().toString()))
                    channels.add(h);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Log.d("liste history Age",channels.toString());
        List<String> ch = new ArrayList<>();
        int count=0;

        for (int i=0 ;i<channels.size();i++){
            for (int j=0 ;j<channels.size();j++){
                if( (channels.get(i).getChannel().equals(channels.get(j).getChannel()))){
                    count++;

                }
            }
            if(!ch.contains(channels.get(i).getChannel())){
                ch.add(channels.get(i).getChannel());
                historiqueChaines.add(new historiqueChaine(channels.get(i).getChannel(),count));
            }
            count=0;
        }
        Log.d("his",historiqueChaines.toString());
        if (historiqueChaines.size()==0)
            Toast.makeText(getBaseContext(), "No charts to display", Toast.LENGTH_LONG).show();

        //Log.d("ch",ch.toString());
        addDataPie();
        setBarChartData(channels.size());
    }

    void fillthecharRegion(String response){
        JSONArray jsonArray = new JSONArray();
        JSONObject objJson = new JSONObject();

        channels.clear();
        historiqueChaines.clear();



        try {
            jsonArray = new JSONArray(response);
            Log.d("json aray",jsonArray.toString());

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
                //  Log.d("h",h.toString());
                if (checkIfMyRecepRegion(recepteur,RegionSpinner.getSelectedItem().toString()))
                    channels.add(h);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
       // Log.d("liste history Region",channels.toString());
        List<String> ch = new ArrayList<>();
        int count=0;

        for (int i=0 ;i<channels.size();i++){
            for (int j=0 ;j<channels.size();j++){
                if( (channels.get(i).getChannel().equals(channels.get(j).getChannel()))){
                    count++;

                }
            }
            if(!ch.contains(channels.get(i).getChannel())){
                ch.add(channels.get(i).getChannel());
                historiqueChaines.add(new historiqueChaine(channels.get(i).getChannel(),count));
            }
            count=0;
        }
        Log.d("his",historiqueChaines.toString());
       // Log.d("ch",ch.toString());
        if (historiqueChaines.size()==0)
            Toast.makeText(getBaseContext(), "No charts to display", Toast.LENGTH_LONG).show();

        addDataPie();
        setBarChartData(channels.size());
    }

    void fillthecharNbr(String response){
        JSONArray jsonArray = new JSONArray();
        JSONObject objJson = new JSONObject();

        channels.clear();
        historiqueChaines.clear();


        try {
            jsonArray = new JSONArray(response);
            Log.d("json aray",jsonArray.toString());

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
                //Log.d("h",h.toString());
                if (checkIfMyRecepNombre(recepteur,NombreSpinner.getSelectedItem().toString()))
                    channels.add(h);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Log.d("liste history nombre",channels.toString());
        List<String> ch = new ArrayList<>();
        int count=0;

        for (int i=0 ;i<channels.size();i++){
            for (int j=0 ;j<channels.size();j++){
                if( (channels.get(i).getChannel().equals(channels.get(j).getChannel()))){
                    count++;

                }
            }
            if(!ch.contains(channels.get(i).getChannel())){
                ch.add(channels.get(i).getChannel());
                historiqueChaines.add(new historiqueChaine(channels.get(i).getChannel(),count));
            }
            count=0;
        }
        Log.d("his",historiqueChaines.toString());
       // Log.d("ch",ch.toString());
        if (historiqueChaines.size()==0)
            Toast.makeText(getBaseContext(), "No charts to display", Toast.LENGTH_LONG).show();

        addDataPie();
        setBarChartData(channels.size());
    }


    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray data = (JSONArray) args[0];
                    Log.d("socket reslt","sockeeeeeeet");
                    if (layoutAge.getVisibility()== View.VISIBLE){
                        fillthecharAge(data.toString());
                    }
                    if (layoutRegion.getVisibility()== View.VISIBLE){
                        fillthecharRegion(data.toString());
                    }
                    if (layoutNombre.getVisibility()== View.VISIBLE){
                        fillthecharNbr(data.toString());
                    }
                }
            });
        }


    };
}
