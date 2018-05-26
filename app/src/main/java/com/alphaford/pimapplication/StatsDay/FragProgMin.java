package com.alphaford.pimapplication.StatsDay;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alphaford.pimapplication.ConnexionManager;
import com.alphaford.pimapplication.LoginActivity;
import com.alphaford.pimapplication.Models.Chaine;
import com.alphaford.pimapplication.Models.History;
import com.alphaford.pimapplication.Models.Program;
import com.alphaford.pimapplication.Models.historiqueChaine;
import com.alphaford.pimapplication.R;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class FragProgMin extends android.support.v4.app.Fragment {
    private static String TAG="statFragment";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor ;
    PieChart pieChart;
    BarChart barChart;
    ArrayList<History> locations = new ArrayList<>();

    ConnexionManager cnx = new ConnexionManager();

    String HttpUrl =cnx.getPath( ":8080/api/historys");
    RequestQueue requestQueue;

    RequestQueue queue ;
    GsonBuilder gsonBuilder;
    private List<History> channels;

    private List<Program> historiquePrgrams;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
    Date dateDebutChaine = new Date();
    Date dateFinChaine = new Date();
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
        View rootView = inflater.inflate(R.layout.fragment_frag_stat_bouquet, container, false);
        //mSocket.connect();
        //mSocket.on("output", onNewMessage);

        channels=new ArrayList<History>();

        historiquePrgrams = new ArrayList<Program>();

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
        //fetchLocations();

        /*try {
            CallSocket();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }*/

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String chaine="";
                int pos1=e.toString().indexOf("(sum): ");

                /*for (int i=0;i<channels.size();i++){
                   chaine =channels.get(i).getChannel();
                    Toast.makeText(getContext(),"Chaine: "+chaine+"\n"+"program : "+channels.get(i).getProgram()+"",Toast.LENGTH_SHORT).show();


                }*/
                // Toast.makeText(getContext(),"Chaine: "+chaine+"\n"+"program : "+e.getData().toString()+"",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected() {

            }
        });


        return rootView;

    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.d("socket reslt","sockeeeeeeet");
                    //Log.d("socket reslt",data.toString());
                }
            });
        }


    };



    private void setBarChartData(int count) {
        float barWidth=0.9f;

        ArrayList<BarEntry> yVals=new ArrayList<>();
        for(int i=0;i<historiquePrgrams.size();i++){

            int value = (int) (historiquePrgrams.get(i).getNb_minute());
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
        for (int i=0 ;i<historiquePrgrams.size();i++){
            yEntrys.add(new PieEntry(historiquePrgrams.get(i).getNb_minute()));

        }
        for (int i=0 ;i<historiquePrgrams.size();i++){
            xEntrys.add(historiquePrgrams.get(i).getNom_program());
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
            //Log.d("static token",LoginActivity.token  );
            // mMapView.onResume();
            //Log.d("string result ",response);
            Type listType = new TypeToken<List<History>>(){}.getType();
            /*List<History> locations = new Gson().fromJson(response  , listType);

            Log.d("list all ",locations.toString());
*/
            //int [] tab = new int[locations.size()];
            JSONArray jsonArray = new JSONArray();
            JSONObject objJson = new JSONObject();

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
                    String date = objJson.getString("date");
//
//                    try {
//                        dateDebutChaine = sdf.parse(date);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
                    History h =new History(recepteur,bouquet,channel,program/*,dateDebutChaine*/);
                    //Log.d("h",h.toString());
                    channels.add(h);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("liste history",channels.toString());
            List<String> ch = new ArrayList<>();
            long nb_minute=0;
            long nbMinuteTot=0;

//            for (int i=0 ;i<channels.size();i++){
//                for (int j=0 ;j<channels.size();j++){
//                    if( (channels.get(i).getProgram().equals(channels.get(j).getProgram()))){
//                        dateFinChaine = channels.get(j+1).getDate();
//                        nb_minute=DifferenceBetweenDate(channels.get(j).getDate(),dateFinChaine);
//                        nbMinuteTot = nbMinuteTot + nb_minute;
//                    }
//                }
//                if(!ch.contains(channels.get(i).getProgram())){
//                    ch.add(channels.get(i).getProgram());
//
//                    historiquePrgrams.add(new Program(channels.get(i).getProgram(),nbMinuteTot));
//                }
//                nbMinuteTot=0;
//            }
            Log.d("his",historiquePrgrams.toString());
            //Log.d("ch",ch.toString());
            addDataPie();
            setBarChartData(channels.size());
        }
    };

    private final com.android.volley.Response.ErrorListener onPostsError = new com.android.volley.Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("PostActivity", error.toString());
        }


    };
    public long DifferenceBetweenDate(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

//        System.out.println("startDate : " + startDate);
//        System.out.println("endDate : "+ endDate);
//        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        //System.out.printf(                "%d days, %d hours, %d minutes, %d seconds%n",                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
        return elapsedMinutes;
    }

}
