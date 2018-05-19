package com.alphaford.pimapplication.StatsDay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alphaford.pimapplication.ConnexionManager;
import com.alphaford.pimapplication.LoginActivity;
import com.alphaford.pimapplication.Models.Chaine;
import com.alphaford.pimapplication.Models.History;
import com.alphaford.pimapplication.Models.Recepteur;
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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

/**
 * Created by LENOVO on 02/01/2018.
 */

public class FragChannelMin extends android.support.v4.app.Fragment {
    private static String TAG="statFragment";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor ;
    PieChart pieChart;
    BarChart barChart;
    ArrayList<History> locations = new ArrayList<>();
    ArrayList<Recepteur> recepteurs = new ArrayList<>();
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" , Locale.ENGLISH);
    Date dateDebutChaine = new Date();
    Date dateFinChaine = new Date();
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
        View rootView = inflater.inflate(R.layout.frag_stat2, container, false);
        mSocket.connect();
        mSocket.on("output", onNewMessage);
        fetchrecepteurs();
        channels=new ArrayList<History>();
        historiqueChaines=new ArrayList<historiqueChaine>();
        chaine=new Chaine();
        pieChart=(PieChart) rootView.findViewById(R.id.pieChart);
        barChart=(BarChart) rootView.findViewById(R.id.barChart);
        requestQueue = Volley.newRequestQueue(getActivity());

        getChainesFromDB();
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
        fetchLocations();
//        try {
//            CallSocket();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
        Log.d("liste history",locations.toString());

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
                    JSONArray data = (JSONArray) args[0];
                    Log.d("socket reslt", data.toString());
                    fillthechar(data.toString());
                }
            });
        }


    };



   public void getChainesFromDB() {

      /* AsyncTask<Integer, Void, Void> asyncTask = new AsyncTask<Integer, Void, Void>() {
           @Override
           protected Void doInBackground(Integer... stadeIds) {

               OkHttpClient client = new OkHttpClient();
               Request request = new Request.Builder()
                       .url(HttpUrl)
                       .build();

               try {
                   response = client.newCall(request).execute();
                   JSONArray array = new JSONArray(response.body().string());

                   for (int i = 0; i < array.length(); i++) {

                       JSONObject object = array.getJSONObject(i);

                       //c =new Colocation(object.getInt("id"), object.getString("titre"),object.getString("type"), object.getString("situation") ,Float.parseFloat(object.getString("surface")), object.getString("adress"), object.getString("meuble"), object.getString("prix"), Float.parseFloat(object.getString("intRating")), object.getString("photo"), object.getInt("user"));*//**//*//obj.getString("description"),obj.getString("photo"));
                       //r =new Rend(Integer.parseInt(object.getString("id")),object.getString("sujet"),object.getString("description"),object.getString("mail") ,object.getString("verif"), object.getString("nom"), object.getString("prenom"), object.getString("date"), object.getString("USER"));

                       chaine =new Chaine(object.getString("channel"));

                       channels.add(chaine);
                   }
                   Log.d("history ",channels.toString());


               } catch (IOException e) {
                   e.printStackTrace();
               } catch (JSONException e) {
                   e.printStackTrace();
               }
               return null;
           }

           @Override
           protected void onPostExecute(Void aVoid) {
               int count=0;
               historiqueChaine historiqueChaine=new historiqueChaine();
               setBarChartData(channels.size());
               for (int i=0 ;i<channels.size();i++){
                   for (int j=0 ;j<channels.size();j++){
                       if(channels.get(i).getNom_chaine().equals(channels.get(j).getNom_chaine())){
                           count++;

                       }
                   }
                   historiqueChaines.add(new historiqueChaine(channels.get(i).getNom_chaine(),count));
                   count=0;
               }
               addDataPie();
               System.out.println(channels.size());
           }
       };

       asyncTask.execute();


       RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
       JsonObjectRequest jsObjRequest = new JsonObjectRequest(
               com.android.volley.Request.Method.GET,HttpUrl,
               createChannelMapperObejct(chaine,date),
               new com.android.volley.Response.Listener<JSONObject>() {
                   @Override
                   public void onResponse(JSONObject response) {
                       Log.v("reponse", "" + response);
                   }

               }, new com.android.volley.Response.ErrorListener() {

           @Override
           public void onErrorResponse(VolleyError error) {
           }
       })
       {
           @Override

           public Map<String, String> getHeaders() throws AuthFailureError {
               HashMap<String, String> headers = new HashMap<>();
               headers.put("x-access-token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImFobWVkYmhkIiwiZW1haWwiOiJhaG1lZC5iZW5oZW5kYUBlc3ByaXQudG4iLCJpYXQiOjE1MjAzNTI2NTB9.8ZYXzcO6FJ8psy595qeyodwg_c_YSnjCLMfhyoa_vfQ");
               headers.put("Content-Type", "application/json");

               return headers;
           }
       };




       queue.add(jsObjRequest);

*/

   }

    private void setBarChartData(int count) {
        float barWidth=0.9f;

        ArrayList<BarEntry> yVals=new ArrayList<>();
        for(int i=0;i<historiqueChaines.size();i++){

            Long value =  (historiqueChaines.get(i).getNb_minute());
            yVals.add(new BarEntry(i,  value));


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
            yEntrys.add(new PieEntry(historiqueChaines.get(i).getNb_minute()));

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
//            Log.d("static token",LoginActivity.token  );
//            // mMapView.onResume();
//            Log.d("string result ",response);
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

        channels.clear();
        historiqueChaines.clear();
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
                JSONObject date = objJson.getJSONObject("date");
                Date d = new Date(Long.parseLong(date.getString("value")));
//                try {
//                    dateDebutChaine = sdf.parse(d.toString());
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                Log.d("date value",dateDebutChaine.toString());
                History h =new History(recepteur,bouquet,channel,program,dateDebutChaine);
                //  Log.d("date ta zebi",dateDebutChaine.toString());
                if (checkIfMyRecep(recepteur))
                    channels.add(h);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Log.d("liste history",channels.toString());
        List<String> ch = new ArrayList<>();
        long nb_minute=0;
        long nbMinuteTot=0;

        for (int i=0 ;i<channels.size();i++){
            for (int j=0 ;j<channels.size();j++){
                if( (channels.get(i).getChannel().equals(channels.get(j).getChannel()))){
                    dateFinChaine = channels.get(j).getDate();
                    nb_minute= DifferenceBetweenDate(channels.get(j).getDate(),dateFinChaine);
                    nbMinuteTot = nbMinuteTot + nb_minute;
                }
            }
            if(!ch.contains(channels.get(i).getChannel())){
                ch.add(channels.get(i).getChannel());
                historiqueChaines.add(new historiqueChaine(channels.get(i).getChannel(),nbMinuteTot));
            }
            nbMinuteTot=0;
        }
        Log.d("historique",historiqueChaines.toString());

        addDataPie();
        setBarChartData(channels.size());
    }


    private void fetchrecepteurs() {
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        sharedPref = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
       // Log.d("hhhhhhhhhhhhhh",sharedPref.getString("username",null));
        StringRequest request = new StringRequest(com.android.volley.Request.Method.GET, "http://192.168.1.2:8080/api/receivers/"+sharedPref.getString("username",null), onPostsLoaded2, onPostsError2)
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
        for( Recepteur r : recepteurs) {
            if (id.equals(r.getId_rec()))
                return true;
        }
        return false;
    }


    public long DifferenceBetweenDate(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

       //Log.d("startDate : " , startDate.toString());
      //System.out.println("endDate : "+ endDate);
     // Log.d("different : " , String.valueOf(different));

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

       // System.out.printf(                "%d days, %d hours, %d minutes, %d seconds%n",                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
        return elapsedMinutes;
    }
}
