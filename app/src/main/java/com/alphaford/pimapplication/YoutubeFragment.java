package com.alphaford.pimapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alphaford.pimapplication.Models.Chaine;
import com.alphaford.pimapplication.Models.History;
import com.alphaford.pimapplication.Models.Recepteur;
import com.alphaford.pimapplication.Models.historiqueChaine;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YoutubeFragment extends android.support.v4.app.Fragment {
    PieChart pieChart;
    BarChart barChart;
    RadioGroup radioGroup;
    RadioButton radioButton;
    ArrayList<Recepteur> recepteurs = new ArrayList<>();
    Chaine chaine;
    ConnexionManager cnx = new ConnexionManager();
    String HttpUrl = cnx.getPath(":8080/api/historys");
    ArrayList<historiqueChaine> listtochart = new ArrayList<>();
    RequestQueue queue ;
    GsonBuilder gsonBuilder;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor ;
    private Gson gson;
    View rootView;
    int compteur =0;
    private List<History> channels;
    private List<History> channelsfiltered;
    private List<History> programsfiltered;

    private List<historiqueChaine> historiqueChaines;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(cnx.getPath(":8088"));
        } catch (URISyntaxException e) {}
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         rootView = inflater.inflate(R.layout.fragment_youtube, container, false);
        fetchrecepteurs();
        //fetchLocations("channel");
        pieChart=rootView.findViewById(R.id.pieCharty);
        barChart=rootView.findViewById(R.id.barCharty);
        radioGroup=rootView. findViewById(R.id.rgrp);

        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        channels=new ArrayList<History>();
        channelsfiltered=new ArrayList<History>();
        programsfiltered=new ArrayList<History>();

        historiqueChaines=new ArrayList<historiqueChaine>();
        chaine=new Chaine();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int radioId=radioGroup.getCheckedRadioButtonId();
            radioButton = rootView.findViewById(checkedId);
            if (radioButton.getText().equals("Channels"))
            {
                barChart.clear();
                pieChart.clear();
                fetchLocations("channel");


            }
            if (radioButton.getText().equals("Programs"))
            {
                barChart.clear();
                pieChart.clear();
                fetchLocations("program");

            }
        });


        return rootView;
    }



    private void setBarChartData(int count) {
        float barWidth=0.9f;

        ArrayList<BarEntry> yVals=new ArrayList<>();
        Collections.sort(historiqueChaines, (o1, o2) -> o2.getNb_minute().compareTo(o1.getNb_minute()));
        if (historiqueChaines.size()>10) {
            for (int i = 0; i < 10; i++) {

                Long value = (historiqueChaines.get(i).getNb_minute());
                yVals.add(new BarEntry(i, value));
            }
        }else{
            for (int i = 0; i < historiqueChaines.size(); i++) {

                Long value = (historiqueChaines.get(i).getNb_minute());
                yVals.add(new BarEntry(i, value));
            }
        }
        BarDataSet set = new BarDataSet(yVals,"Stats for the channels which have a Youtube channel");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        set.setDrawValues(true);
        BarData data=new BarData(set);
        data.setBarWidth(barWidth);
        barChart.setData(data);
        barChart.invalidate();
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                if (radioButton.getText().equals("Channels"))
                {
                    Toast.makeText(getContext(),"Channel: "+historiqueChaines.get((int)h.getX()).getNom_chaine(),Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(),"Program: "+historiqueChaines.get((int)h.getX()).getNom_chaine(),Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onNothingSelected() {

            }
        });
        barChart.animateY(5000);


    }

    private void addDataPie(int count) {
        ArrayList<PieEntry> yEntrys=new ArrayList<>();
        ArrayList<String > xEntrys=new ArrayList<>();
        Collections.sort(historiqueChaines, (o1, o2) -> o2.getNb_minute().compareTo(o1.getNb_minute()));

        if (count>10) {
            for (int i = 0; i < 10; i++) {
                yEntrys.add(new PieEntry(historiqueChaines.get(i).getNb_minute()));

            }
            for (int i = 0; i < 10; i++) {
                xEntrys.add(historiqueChaines.get(i).getNom_chaine());
            }
        }else {
            for (int i = 0; i < count; i++) {
                yEntrys.add(new PieEntry(historiqueChaines.get(i).getNb_minute()));

            }
            for (int i = 0; i < count ; i++) {
                xEntrys.add(historiqueChaines.get(i).getNom_chaine());
            }
        }
        //create the data set
        PieDataSet pieDataSet=new PieDataSet(yEntrys,"Stats for the channels which have a Youtube channel");
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
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                if (radioButton.getText().equals("Channels"))
                {
                    Toast.makeText(getContext(),"Channel: "+historiqueChaines.get((int)h.getX()).getNom_chaine(),Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(),"Program: "+historiqueChaines.get((int)h.getX()).getNom_chaine(),Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onNothingSelected() {

            }
        });
        pieChart.invalidate();
        pieChart.animateY(5000);

    }




    private void fetchrecepteurs() {
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        sharedPref = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
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

    private final com.android.volley.Response.Listener<String> onPostsLoaded2 = response -> {

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
               // Log.d("h", h.toString());
                recepteurs.add(h);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    };

    private final com.android.volley.Response.ErrorListener onPostsError2 = error -> Log.e("PostActivity", error.toString());


    private void fetchLocations(String s) {
        sharedPref = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        if (s.equals("channel")) {
            StringRequest request = new StringRequest(com.android.volley.Request.Method.GET, HttpUrl, onPostsLoadedch, onPostsError) {
                @Override

                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("x-access-token", sharedPref.getString("token", null));
                    headers.put("Content-Type", "application/json");

                    return headers;
                }

            };
            queue.add(request);
        }else{
            StringRequest request = new StringRequest(com.android.volley.Request.Method.GET, HttpUrl, onPostsLoadedprog, onPostsError) {
                @Override

                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("x-access-token", sharedPref.getString("token", null));
                    headers.put("Content-Type", "application/json");

                    return headers;
                }

            };
            queue.add(request);
        }


    }

    private final com.android.volley.Response.Listener<String> onPostsLoadedch = new com.android.volley.Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            //Log.d("static token",LoginActivity.token  );
            // mMapView.onResume();
            //Log.d("string result ",response);
            Type listType = new TypeToken<List<History>>(){}.getType();
            JSONArray jsonArray = new JSONArray();
            JSONObject objJson = new JSONObject();
            Log.d("filling the charts",response);
            channels.clear();
            historiqueChaines.clear();
            channelsfiltered.clear();
            try {
                jsonArray = new JSONArray(response);
                //Log.d("json aray",jsonArray.toString());

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
                    if (checkIfMyRecep(recepteur))
                        channels.add(h);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            List<String> ch = new ArrayList<>();
            for (int i=0 ;i<channels.size();i++){

                if(!ch.contains(channels.get(i).getChannel())){
                    ch.add(channels.get(i).getChannel());
                    channelsfiltered.add(channels.get(i));
                }

            }

        fetchyoutubech();

        }
    };

    private final com.android.volley.Response.Listener<String> onPostsLoadedprog = new com.android.volley.Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            //Log.d("static token",LoginActivity.token  );
            // mMapView.onResume();
            //Log.d("string result ",response);
            Type listType = new TypeToken<List<History>>(){}.getType();
            JSONArray jsonArray = new JSONArray();
            JSONObject objJson = new JSONObject();
            Log.d("filling the charts",response);
            channels.clear();
            historiqueChaines.clear();
            channelsfiltered.clear();
            try {
                jsonArray = new JSONArray(response);
                //Log.d("json aray",jsonArray.toString());

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
                    if (checkIfMyRecep(recepteur))
                        channels.add(h);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            List<String> ch = new ArrayList<>();

            for (int i=0 ;i<channels.size();i++){

                if(!ch.contains(channels.get(i).getProgram())){
                    ch.add(channels.get(i).getProgram());
                    programsfiltered.add(channels.get(i));
                }

            }

        fetchyoutubeprog();
        }
    };
    private final com.android.volley.Response.ErrorListener onPostsError = new com.android.volley.Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("PostActivity", error.toString());
        }


    };



    boolean checkIfMyRecep(String id){
        //Log.d("recepteurs",recepteurs.toString());
        for( Recepteur r : recepteurs) {
            if (id.equals(r.getId_rec()))
                return true;
        }
        return false;
    }


    private void fetchyoutubech() {
        JSONArray jsonArray = new JSONArray();

        try{
            for (int i = 0; i < channelsfiltered.size(); i++) {

                JSONObject jsonBodyObj = new JSONObject();


                jsonBodyObj.put("channel", channelsfiltered.get(i).getChannel());
                jsonBodyObj.put("program", channelsfiltered.get(i).getProgram());

                jsonArray.put(jsonBodyObj);

            }

        }catch (JSONException e){
            e.printStackTrace();
        }

        final String requestBody = jsonArray.toString();
        Log.d("json body",requestBody);
        JsonArrayRequest request = new JsonArrayRequest (Request.Method.POST, cnx.getPath(":5000/channel"),null, onPostsLoaded3, onPostsError3)
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("x-access-token", sharedPref.getString("token",null));
                headers.put("Content-Type", "application/json");

                return headers;
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    return null;
                }
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);

    }

    private final com.android.volley.Response.Listener<JSONArray > onPostsLoaded3 = response -> {

        Type listType = new TypeToken<List<History>>() {}.getType();

        historiqueChaines.clear();
        Log.d("youtube channel",response.toString());
        try{
            for (int i = 0; i < response.length(); i++) {

                JSONObject objJson = response.getJSONObject(i);
                // Log.d("json aray", jsonArray.toString());

                // here you can get id,name,city...
                String Title = objJson.getString("Title");
                String Views = objJson.getString("Views");
                Long v = Long.valueOf(Views);
                if (v>0)
                    historiqueChaines.add(new historiqueChaine(Title,v));

            }

        }catch (JSONException e){
            e.printStackTrace();
        }

//        for (int i=0 ;i<historiqueChaines.size() ; i++) {
//            if (historiqueChaines.get(i).getNom_chaine().equals(channels.get(i).getChannel()))
//                listtochart.add(historiqueChaines.get(i));
//        }

        Log.d("his",historiqueChaines.toString());
        //Log.d("ch",ch.toString());
        if (historiqueChaines.size()==0)
            Toast.makeText(getActivity(), "No charts to display", Toast.LENGTH_LONG).show();
        else {
            int count = historiqueChaines.size();
            addDataPie(count);
            setBarChartData(count);
        }
        //Log.d("liste history",channels.toString());


    };

    private void fetchyoutubeprog() {
        JSONArray jsonArray = new JSONArray();

        try{
            for (int i = 0; i < programsfiltered.size(); i++) {

                JSONObject jsonBodyObj = new JSONObject();


                jsonBodyObj.put("channel", programsfiltered.get(i).getChannel());
                jsonBodyObj.put("program", programsfiltered.get(i).getProgram());

                jsonArray.put(jsonBodyObj);

            }

        }catch (JSONException e){
            e.printStackTrace();
        }

        final String requestBody = jsonArray.toString();
        Log.d("json body",requestBody);
        JsonArrayRequest request = new JsonArrayRequest (Request.Method.POST, cnx.getPath(":5000/program"),null, onPostsLoaded4, onPostsError3)
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("x-access-token", sharedPref.getString("token",null));
                headers.put("Content-Type", "application/json");

                return headers;
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    return null;
                }
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);

    }

    private final com.android.volley.Response.Listener<JSONArray > onPostsLoaded4 = response -> {

        Type listType = new TypeToken<List<History>>() {}.getType();

        historiqueChaines.clear();
        Log.d("youtube channel",response.toString());
        try{
            for (int i = 0; i < response.length(); i++) {

                JSONObject objJson = response.getJSONObject(i);
                // Log.d("json aray", jsonArray.toString());

                // here you can get id,name,city...
                String Title = objJson.getString("Title");
                String Views = objJson.getString("Views");
                Long v = Long.valueOf(Views);
                if (v>0)
                    historiqueChaines.add(new historiqueChaine(Title,v));

            }

        }catch (JSONException e){
            e.printStackTrace();
        }

        Log.d("his",historiqueChaines.toString());
        //Log.d("ch",ch.toString());
        if (historiqueChaines.size()==0)
            Toast.makeText(getActivity(), "No charts to display", Toast.LENGTH_LONG).show();
        else {
            int count = historiqueChaines.size();
            addDataPie(count);
            setBarChartData(count);
        }
        //Log.d("liste history",channels.toString());


    };
    private final com.android.volley.Response.ErrorListener onPostsError3 = error -> Log.e("PostActivity", error.toString());
}
