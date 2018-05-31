package com.alphaford.pimapplication.StatsDay;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alphaford.pimapplication.ConnexionManager;
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
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PeriodFragment extends Fragment {
    private static String TAG="statFragment";
    private DatePickerDialog.OnDateSetListener mOnDateSetListener;
    private DatePickerDialog.OnDateSetListener mOnDateSetListenerEnd;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor ;
    PieChart pieChart;
    BarChart barChart;
    RadioGroup radioGroup;
    TextView startDate,endDate;
    Button submit;
    ArrayList<Recepteur> recepteurs = new ArrayList<>();
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    Date dateDebutChaine = new Date();
    DateTime dateFinChaine;
    Date StartDate,Enddate;
    Chaine chaine;
    ConnexionManager cnx = new ConnexionManager();
    String Startdate,EndDate;
    String HttpUrl = cnx.getPath(":8080/api/historys");
    RequestQueue requestQueue;
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    RadioButton channelsd,programsd;
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
        View rootView = inflater.inflate(R.layout.fragment_period, container, false);
        mSocket.connect();
        mSocket.on("output", onNewMessage);
        fetchrecepteurs();
        channels=new ArrayList<History>();
        historiqueChaines=new ArrayList<historiqueChaine>();
        chaine=new Chaine();

        submit=(Button)rootView.findViewById(R.id.submit);
        pieChart=(PieChart) rootView.findViewById(R.id.pieChart);
        barChart=(BarChart) rootView.findViewById(R.id.barChart);
        radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroupe);
        requestQueue = Volley.newRequestQueue(getActivity());
        pieChart.setRotationEnabled(true);
        endDate=rootView.findViewById(R.id.endDate);
        startDate= rootView.findViewById(R.id.startDate);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterText("Stats Par nb minutes");
        pieChart.setCenterTextSize(10);
        pieChart.setDrawEntryLabels(true);
        barChart.getDescription().setEnabled(false);
        //setBarChartData(10);
        barChart.setFitBars(true);
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String chaine="";
                int pos1=e.toString().indexOf("(sum): ");
                int index = Math.round(h.getX());
                System.out.println("chaaaine"+index);
                chaine = historiqueChaines.get(index).getNom_chaine();
                System.out.println("Chaneeel name"+chaine);
                if (channelsd.isChecked())
                Toast.makeText(getContext(),"Channel : "+chaine,Toast.LENGTH_SHORT).show();
                if (programsd.isChecked())
                    Toast.makeText(getContext(),"Program : "+chaine,Toast.LENGTH_SHORT).show();
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
                if (channelsd.isChecked())
                    Toast.makeText(getContext(),"Channel : "+chaine,Toast.LENGTH_SHORT).show();
                if (programsd.isChecked())
                    Toast.makeText(getContext(),"Program : "+chaine,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        mOnDateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                Startdate = dayOfMonth+"/"+ month +"/"+year;
                startDate.setText(Startdate);

            }
        };
        mOnDateSetListenerEnd=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                EndDate = dayOfMonth+"/"+ month +"/"+year;
                endDate.setText(EndDate);
            }
        };

        channelsd = rootView.findViewById(R.id.channelsd);
        programsd = rootView.findViewById(R.id.programsd);
        submit.setOnClickListener(v -> {
            if (channelsd.isChecked()){
                fetchLocations("channel");
            }
            if (programsd.isChecked()){
                fetchLocations("program");
            }
        });
        startDate.setClickable(true);
        endDate.setClickable(true);
        startDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Calendar cal=Calendar.getInstance();
                int year=cal.get(Calendar.YEAR);
                int month=cal.get(Calendar.MONTH);
                int day=cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mOnDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal=Calendar.getInstance();
                int year=cal.get(Calendar.YEAR);
                int month=cal.get(Calendar.MONTH);
                int day=cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mOnDateSetListenerEnd,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
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
        BarDataSet set = new BarDataSet(yVals,"Stats/Minutes");
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
        Collections.sort(historiqueChaines, (o1, o2) -> o2.getNb_minute().compareTo(o1.getNb_minute()));

        if (historiqueChaines.size()>10) {
            for (int i = 0; i < 10; i++) {
                yEntrys.add(new PieEntry(historiqueChaines.get(i).getNb_minute()));

            }
            for (int i = 0; i < 10; i++) {
                xEntrys.add(historiqueChaines.get(i).getNom_chaine());
            }
        }else {
            for (int i = 0; i < historiqueChaines.size(); i++) {
                yEntrys.add(new PieEntry(historiqueChaines.get(i).getNb_minute()));

            }
            for (int i = 0; i < historiqueChaines.size(); i++) {
                xEntrys.add(historiqueChaines.get(i).getNom_chaine());
            }
        }
        //create the data set
        PieDataSet pieDataSet=new PieDataSet(yEntrys,"Stats/Minutes");
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
        pieChart.animateY(500);

    }
    private void fetchLocations(String type) {
        sharedPref = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        StringRequest request = null;
        if (type.equals("channel")){
            request = new StringRequest(com.android.volley.Request.Method.GET, HttpUrl, onPostsLoaded, onPostsError)
            {
                @Override

                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("x-access-token", sharedPref.getString("token",null));
                    headers.put("Content-Type", "application/json");

                    return headers;
                }

            };
        }else if (type.equals("program")){
            request = new StringRequest(com.android.volley.Request.Method.GET, HttpUrl, onPostsLoadedPrograms, onPostsError)
            {
                @Override

                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("x-access-token", sharedPref.getString("token",null));
                    headers.put("Content-Type", "application/json");

                    return headers;
                }

            };
        }

        queue.add(request);

    }

    private final com.android.volley.Response.Listener<String> onPostsLoaded = new com.android.volley.Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Type listType = new TypeToken<List<History>>(){}.getType();
            fillthechar(response);
        }
    };
    private final com.android.volley.Response.Listener<String> onPostsLoadedPrograms = new com.android.volley.Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

            Type listType = new TypeToken<List<History>>(){}.getType();
            fillthecharprogram(response);
            // int i = Integer.valueOf(response);
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

            for (int i = 0; i < jsonArray.length(); i++) {

                objJson = jsonArray.getJSONObject(i);

                // here you can get id,name,city...
                String id = objJson.getString("_id");
                String recepteur = objJson.getString("recepteur");
                String bouquet = objJson.getString("bouquet");
                String channel = objJson.getString("channel");
                String program = objJson.getString("program");
                int duree = objJson.getInt("duree");
                JSONObject date = objJson.getJSONObject("date");
                Date d = new Date(date.getLong("value"));
                //System.out.println("Dateeee"+d+"ddddddd"+date);
                DateTime dateTime = new DateTime(d);

                History h =new History(recepteur,bouquet,channel,program,duree);
                try {
//                    if (StartDate.equals("Start Date")||EndDate.equals("End Date"))
//                        break;
                    StartDate = format.parse(Startdate);
                    Enddate = format.parse(EndDate);
                    DateTime s= new DateTime(StartDate);
                    DateTime e=new DateTime(Enddate);

                    System.out.println("s = "+s+"  e= "+e+"  h= "+dateTime);

                if (checkIfMyRecep(recepteur)&& dateTime.isAfter(s) && dateTime.isBefore(e) )
                    channels.add(h);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        List<String> ch = new ArrayList<>();
        int totaleDuree=0;
        long nbMinuteTot=0;
        DateTime d = DateTime.now();

        for (int i=0 ;i<channels.size();i++){
            for (int j=0 ;j<channels.size();j++){
                if ((channels.get(i).getChannel().equals(channels.get(j).getChannel()))){
                    totaleDuree= totaleDuree + channels.get(j).getDuree();
                    System.out.println("dureeee"+totaleDuree+"channeel "+channels.get(j).getChannel());
                    nbMinuteTot = TimeUnit.SECONDS.toMinutes(totaleDuree);
                }
            }
            if(!ch.contains(channels.get(i).getChannel())){
                ch.add(channels.get(i).getChannel());
                historiqueChaines.add(new historiqueChaine(channels.get(i).getChannel(),nbMinuteTot));
            }
            totaleDuree=0;
        }

        //Log.d("historique",historiqueChaines.toString());

        addDataPie();
        setBarChartData(channels.size());
    }
    void fillthecharprogram(String response){
        JSONArray jsonArray = new JSONArray();
        JSONObject objJson = new JSONObject();
        Log.d("filling the charts",response);
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
                int duree = objJson.getInt("duree");
                //Date date = objJson.get
                JSONObject date = objJson.getJSONObject("date");
                Date d = new Date(date.getLong("value"));
                DateTime dateTime = new DateTime(d);

                History h =new History(recepteur,bouquet,channel,program,duree);
                try {
                    StartDate = format.parse(Startdate);
                    Enddate = format.parse(EndDate);
                    DateTime s= new DateTime(StartDate);
                    DateTime e=new DateTime(Enddate);

                    System.out.println("s = "+s+"  e= "+e+"  h= "+dateTime);

                    if (checkIfMyRecep(recepteur)&& dateTime.isAfter(s) && dateTime.isBefore(e) )
                        channels.add(h);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        List<String> ch = new ArrayList<>();
        int totaleDuree=0;
        long nbMinuteTot=0;
        DateTime d = DateTime.now();
        for (int i=0 ;i<channels.size();i++){
            for (int j=0 ;j<channels.size();j++){
                if ((channels.get(i).getProgram().equals(channels.get(j).getProgram()))){
                    totaleDuree= totaleDuree + channels.get(j).getDuree();
                    System.out.println("dureeee"+totaleDuree+"program "+channels.get(j).getProgram());
                    nbMinuteTot = TimeUnit.SECONDS.toMinutes(totaleDuree);
                    // int k = j+1;
                    // dateFinChaine = channels.get(k).getDate();
                    //nb_minute= DifferenceBetweenDate(channels.get(j).getDate(),dateFinChaine);
                    //long diffMinutes= d.getMillis() - channels.get(j).getDate().getMillis();
                    //long diffMinutes=dateFinChaine.getMillis() - channels.get(j).getDate().getMillis();
                    //  nb_minute = TimeUnit.MILLISECONDS.toMinutes(diffMinutes);
                    //  nbMinuteTot = nbMinuteTot + nb_minute;
                    // System.out.println("NBM"+nbMinuteTot+"nbbb"+nb_minute);
                }
            }
            if(!ch.contains(channels.get(i).getProgram())){
                ch.add(channels.get(i).getProgram());
                historiqueChaines.add(new historiqueChaine(channels.get(i).getProgram(),nbMinuteTot));
            }
            totaleDuree=0;
        }
        //Log.d("historique",historiqueChaines.toString());

        addDataPie();
        setBarChartData(channels.size());
    }


    private void fetchrecepteurs() {
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        sharedPref = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        // Log.d("hhhhhhhhhhhhhh",sharedPref.getString("username",null));
        StringRequest request = new StringRequest(com.android.volley.Request.Method.GET, cnx.getPath(":8080/api/receivers/"+sharedPref.getString("username",null)), onPostsLoaded2, onPostsError2)
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
            //Log.d("liste receiver",recepteurs.toString());

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
}
