package com.alphaford.pimapplication.StatsDay;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alphaford.pimapplication.ConnexionManager;
import com.alphaford.pimapplication.LoginActivity;
import com.alphaford.pimapplication.Models.Bouquet;
import com.alphaford.pimapplication.Models.Chaine;
import com.alphaford.pimapplication.Models.History;
import com.alphaford.pimapplication.Models.Recepteur;
import com.alphaford.pimapplication.Models.historiqueBouquet;
import com.alphaford.pimapplication.Models.historiqueChaine;
import com.alphaford.pimapplication.R;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
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
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by LENOVO on 02/01/2018.
 */

public class FragChannelVues extends android.support.v4.app.Fragment {
    private static String TAG="statFragment";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor ;
    PieChart pieChart;
    BarChart barChart;
    ArrayList<History> locations = new ArrayList<>();
    ArrayList<Recepteur> recepteurs = new ArrayList<>();

    Chaine chaine;
    ConnexionManager cnx = new ConnexionManager();
    String HttpUrl = cnx.getPath(":8080/api/historys");
    RequestQueue requestQueue;

    RequestQueue queue ;
    GsonBuilder gsonBuilder;
    private List<History> channels;
    private List<historiqueChaine> historiqueChaines;
    private Gson gson;
    Socket socket;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(cnx.getPath(":8088"));
        } catch (URISyntaxException e) {}
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_stat, container, false);
        mSocket.connect();
        mSocket.on("output", onNewMessage);

        channels=new ArrayList<History>();
        historiqueChaines=new ArrayList<historiqueChaine>();
        chaine=new Chaine();
        pieChart=(PieChart) rootView.findViewById(R.id.pieChart);
        barChart=(BarChart) rootView.findViewById(R.id.barChart);
        requestQueue = Volley.newRequestQueue(getActivity());

        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterText("Stats Par nb telespectateurs");
        pieChart.setCenterTextSize(10);
        pieChart.setDrawEntryLabels(true);
        barChart.getDescription().setEnabled(false);

        //setBarChartData(10);
        barChart.setFitBars(true);
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        fetchrecepteurs();

        fetchLocations();
        /*try {
            CallSocket();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }*/
        Log.d("liste history",locations.toString());
<<<<<<< Updated upstream

//        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
//            @Override
//            public void onValueSelected(Entry e, Highlight h) {
//                String chaine="";
//                int pos1=e.toString().indexOf("(sum): ");
//
//                /*for (int i=0;i<channels.size();i++){
//                   chaine =channels.get(i).getChannel();
//                    Toast.makeText(getContext(),"Chaine: "+chaine+"\n"+"program : "+channels.get(i).getProgram()+"",Toast.LENGTH_SHORT).show();
//
//
//                }*/
//                // Toast.makeText(getContext(),"Chaine: "+chaine+"\n"+"program : "+e.getData().toString()+"",Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void onNothingSelected() {
//
//            }
//        });
=======
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String chaine="";
                int pos1=e.toString().indexOf("(sum): ");
                int index = Math.round(h.getX());
                System.out.println("chaaaine"+index);
                chaine = historiqueChaines.get(index).getNom_chaine();
                System.out.println("Chaneeel name"+chaine);
                Toast.makeText(getContext(),"Channel : "+chaine,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String chaine="";
                int pos1=e.toString().indexOf("(sum): ");
                int index = Math.round(h.getX());
                System.out.println("chaaaine"+index);
                chaine = historiqueChaines.get(index).getNom_chaine();
                System.out.println("Chaneeel name"+chaine);
                Toast.makeText(getContext(),"Channel : "+chaine,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected() {

            }
        });
>>>>>>> Stashed changes


        return rootView;

    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray data = (JSONArray) args[0];
                    Log.d("socket reslt","sockeeeeeeet");
                    fillthechar(data.toString());
                }
            });
        }


    };


    private void setBarChartData(int count) {
        float barWidth=0.9f;

        ArrayList<BarEntry> yVals=new ArrayList<>();
        for(int i=0;i<historiqueChaines.size();i++){

            int value = (int) (historiqueChaines.get(i).getNbr_teles());
            yVals.add(new BarEntry(i, (int) value));


        }
        BarDataSet set = new BarDataSet(yVals,"Channel Views");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        set.setDrawValues(true);
        BarData data=new BarData(set);
        data.setBarWidth(barWidth);
        barChart.setData(data);
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                Toast.makeText(getContext(),"Channel: "+historiqueChaines.get((int)h.getX()).getNom_chaine(),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected() {

            }
        });
        barChart.invalidate();
        barChart.animateY(3000);


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
        PieDataSet pieDataSet=new PieDataSet(yEntrys,"Channel Views");
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

                 Toast.makeText(getContext(),"Channel: "+historiqueChaines.get((int)h.getX()).getNom_chaine(),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected() {

            }
        });
        pieChart.invalidate();
        pieChart.animateY(3000);

    }

    private void fetchLocations() {
        sharedPref = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        StringRequest request = new StringRequest(com.android.volley.Request.Method.GET, HttpUrl, onPostsLoaded, onPostsError)
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

    private final com.android.volley.Response.Listener<String> onPostsLoaded = new com.android.volley.Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
           // Log.d("static token",LoginActivity.token  );
            // mMapView.onResume();
            //Log.d("string result ",response);
            Type listType = new TypeToken<List<History>>(){}.getType();
            fillthechar(response);

        }
    };

    private final com.android.volley.Response.ErrorListener onPostsError = new com.android.volley.Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("PostActivity", error.toString());
        }


    };

    void fillthechar(String response){
        JSONArray jsonArray = new JSONArray();
        JSONObject objJson = new JSONObject();
        Log.d("filling the charts",response);
        channels.clear();
        historiqueChaines.clear();
        try {
            jsonArray = new JSONArray(response);
            //Log.d("json aray",jsonArray.toString());
            int j=0;
            for (int i = 0; i < jsonArray.length(); i++) {

                objJson = jsonArray.getJSONObject(i);

                // here you can get id,name,city...
                String id = objJson.getString("_id");
                String recepteur = objJson.getString("recepteur");
                String bouquet = objJson.getString("bouquet");
                String channel = objJson.getString("channel");
                String program = objJson.getString("program");
                int duree = objJson.getInt("duree");
                if (duree>0)
                    Log.d("this is history", objJson.toString());

                //Date date = objJson.get
                History h =new History(recepteur,bouquet,channel,program,duree);
                //Log.d("h",h.toString());
                if (checkIfMyRecep(recepteur)) {
                    channels.add(h);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

       // Log.d("liste new history",channels.toString());
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
        //Log.d("ch",ch.toString());
        if (historiqueChaines.size()==0)
            Toast.makeText(getActivity(), "No charts to display", Toast.LENGTH_LONG).show();

        addDataPie();
        setBarChartData(channels.size());
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
                    //Log.d("h", h.toString());
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

    boolean checkIfMyRecep(String id){
        //Log.d("recepteurs",recepteurs.toString());
        for( Recepteur r : recepteurs) {
            if (id.equals(r.getId_rec()))
                return true;
        }
        return false;
    }
}
