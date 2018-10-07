package pixedar.com.kryde;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class WeatherDataController {
    private ArrayList<OnDataArrivedListener> listeners = new ArrayList<>();
    private RequestQueue requestQueue;
    private String channelID = "333150";
    private String dailyMaximaChannelId = "544874";
    private String dailyMaximaApiKey = "15CNBVEG8QAQS7QM";
    private String fileName = "weatherData";
    private Context context;
    private JSONObject channelInfo;
    private long lastUpdatedTime = 10000;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private boolean keepUpdating;
    public WeatherDataController(Context context, ProgressBar progressBar) {
        this.progressBar = progressBar;
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public void setDailyMaximaApiKey(String dailyMaximaApiKey){
        this.dailyMaximaApiKey = dailyMaximaApiKey;
    }
    public void setKeepUpdating(boolean keepUpdating){
        this.keepUpdating = keepUpdating;
    }

    public void setDailyMaximaChannelId(String dailyMaximaChannelId) {
        this.dailyMaximaChannelId = dailyMaximaChannelId;
    }

    public void setOnDataArrivedListener(OnDataArrivedListener listener) {
        this.listeners.add(listener);
    }

    public void loadData(String type, int amount) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd", Locale.GERMAN);
        sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        fileName = sdf.format(Calendar.getInstance().getTime());
        if (sharedPreferences.contains(fileName)) {
            String jsonArrayString = sharedPreferences.getString(fileName, null);
         //   String dailyMaximaJsonArrayString = sharedPreferences.getString("dailyMaxima", null);
            try {
                JSONArray jsonArray = new JSONArray(jsonArrayString);
                ArrayList<Entry>[] feed = readJsonArray(jsonArray);
                String startTime = jsonArray.getJSONObject(jsonArray.length() - 1).getString("created_at");
                String url = "https://api.thingspeak.com/channels/" + channelID + "/feeds.json?start=" + startTime;
                loadWeatherDataFromServer(url, feed, jsonArray);
               // jsonArray = new JSONArray(dailyMaximaJsonArrayString);
            //    url = "https://api.thingspeak.com/channels/" + dailyMaximaChannelId +"/feeds.json?api_key=" +dailyMaximaApiKey +"&start="+ startTime;
             //   loadDailyMaximaFromServer(url);
            } catch (Exception e) {
                msg(e.toString());
                e.printStackTrace();
            }
        } else {
            loadWeatherDataFromServer("https://api.thingspeak.com/channels/" + channelID + "/feeds.json?" + type + "=" + String.valueOf(amount));
          //  loadDailyMaximaFromServer("https://api.thingspeak.com/channels/" + dailyMaximaChannelId +"/feeds.json?api_key=" +dailyMaximaApiKey +"&days=360");
            msg("that will be long");
        }
    }

    private void keepUpdating(){
            final Handler handler = new Handler();
            SimpleDateFormat sdf2= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.GERMAN);
            try {
               long time = sdf2.parse(channelInfo.getString("updated_at")).getTime();
                 Log.d("GGG", sdf2.format(time));
               long interval = Calendar.getInstance().getTimeInMillis() - (lastUpdatedTime+((LedFragment.sendingInterval[0])* 1000L));
            //    Log.d("GGG", sdf2.format(lastUpdatedTime+(LedFragment.sendingInterval[0])* 1000L));
              //  Log.d("GGG", sdf2.format(Calendar.getInstance().getTimeInMillis()));
                sdf2 = new SimpleDateFormat("mm:ss", Locale.GERMAN);
              //  Log.d("GGG", String.valueOf(sdf2.format(interval)));
            //Log.d("GGG", String.valueOf(interval/1000.0f/60.0f));
                final java.lang.Runnable runnable = new java.lang.Runnable() {
                    @Override
                    public void run() {
                        loadLastEntryFromServer("https://thingspeak.com/channels/333150/feeds/last.json");
                        progressBar.setVisibility(View.VISIBLE);
                        handler.postDelayed(this, 20000);
                    }
                };
                handler.postDelayed(runnable,20000);
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
    }

    private void loadLastEntryFromServer(String url){
        executeJsonObjectReqest(url, new Runnable() {
            @Override
            public void run() {
                try {
                    Entry[] entry =  new Entry[8];
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.GERMAN);
                    long time = sdf.parse(getResponse().getString("created_at")).getTime();
                    for(int k = 0; k < 8;k++){
                        entry[k] = new Entry(time, (float) getResponse().getDouble("field" + String.valueOf(k+1)));
                    }
                    progressBar.setVisibility(View.GONE);
                    for (OnDataArrivedListener l : listeners) {
                        l.dataUpdated(entry);
                    }
                } catch (Exception e) {
                    msg(e.toString());
                    e.printStackTrace();
                }

            }
        });
    }
    private void loadWeatherDataFromServer(String url) {
        executeJsonObjectReqest(url, new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray feeds = getResponse().getJSONArray("feeds");
                    ArrayList<Entry>[] feed = readJsonArray(feeds);
                    channelInfo = getResponse().getJSONObject("channel");
                    progressBar.setVisibility(View.GONE);
                    for (OnDataArrivedListener l : listeners) {
                        l.dataArrived(feed);
                    }
                    saveWeatherData(fileName,feeds.toString(), true);
                    if(keepUpdating){
                        keepUpdating();
                    }
                } catch (Exception e) {
                    msg(e.toString());
                    e.printStackTrace();
                }

            }
        });
    }
    private void loadWeatherDataFromServer(String url, final ArrayList<Entry>[] feed, final JSONArray feeds2) {
        executeJsonObjectReqest(url, new Runnable() {
            @Override
            public void run() {
                try {
                     try {
                        JSONArray feeds = getResponse().getJSONArray("feeds");
                        readJsonArray(feeds, feed);
                        saveWeatherData(fileName,feeds2.toString() + feeds.toString(), false);
                        channelInfo = getResponse().getJSONObject("channel");
                        progressBar.setVisibility(View.GONE);
                        for (OnDataArrivedListener l : listeners) {
                            l.dataArrived(feed);
                        }
                         if(keepUpdating){
                             keepUpdating();
                         }
                    } catch (Exception e) {
                        msg(e.toString());
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    msg(e.toString());
                    e.printStackTrace();
                }

            }
        });
    }
    private void loadDailyMaximaFromServer(String url){
        executeJsonObjectReqest(url, new Runnable(){
            @Override
            public void run() {
                try {
                    JSONArray feeds = getResponse().getJSONArray("feeds");
                    ArrayList<Entry>[] feed = readJsonArray(feeds);
                    progressBar.setVisibility(View.GONE);
                    for (OnDataArrivedListener l : listeners) {
                  //      l.dailyMaximaArrived(feed);
                    }
                    saveWeatherData("dailyMaxima",feeds.toString(), true);
                } catch (Exception e) {
                    msg(e.toString());
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadDailyMaximaFromServer(String url, final ArrayList<Entry>[] feed, final JSONArray feeds2){
        executeJsonObjectReqest(url, new Runnable(){
            @Override
            public void run() {
                try {
                    JSONArray feeds = getResponse().getJSONArray("feeds");
                    ArrayList<Entry>[] feed = readJsonArray(feeds);
                    progressBar.setVisibility(View.GONE);
                    for (OnDataArrivedListener l : listeners) {
                  //      l.dailyMaximaArrived(feed);
                    }
                    saveWeatherData("dailyMaxima",feeds.toString(), false);
                } catch (Exception e) {
                    msg(e.toString());
                    e.printStackTrace();
                }
            }
        });
    }


    private void executeJsonObjectReqest(String url, final Runnable runnable) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        runnable.setResponse(response);
                        runnable.run();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        msg(error.toString());
                        error.printStackTrace();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void saveWeatherData(String fileName,String array, boolean clear) {
        if (clear) {
            sharedPreferences
                    .edit()
                    .clear()
                    .putString(fileName, array)
                    .apply();
        } else {
            sharedPreferences
                    .edit()
                    .putString(fileName, array)
                    .apply();
        }
    }

    private ArrayList<Entry[][]> convertJsonToDailyMaxima(JSONArray feeds) throws Exception {
        ArrayList<Entry[][]> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.GERMAN);
        for (int k = 0; k < feeds.length(); k++) {
            long time = sdf.parse(feeds.getJSONObject(k).getString("created_at")).getTime();
            Entry[][] entry = new Entry[7][3];
            for (int j = 0; j < entry.length; j++) {
                try {
                    String[] vals = feeds.getJSONObject(k).getString("field" + String.valueOf(j + 1)).split(",");
                    for(int i = 0; i < vals.length;i++){
                        entry[j][i] = new Entry((float)time,Float.valueOf(vals[i]));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                result.add(entry);
            }
        }
        return result;
    }

    private ArrayList<Entry>[] readJsonArray(JSONArray feeds) throws Exception {
        ArrayList<Entry>[] result = new ArrayList[7];
        for (int k = 0; k < result.length; k++) {
            result[k] = new ArrayList<>();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.GERMAN);
        long time = 1000;
        for (int k = 0; k < feeds.length(); k++) {
             time = sdf.parse(feeds.getJSONObject(k).getString("created_at")).getTime();
            for (int j = 0; j < result.length; j++) {
                try {
                    result[j].add(new Entry(time, (float) feeds.getJSONObject(k).getDouble("field" + String.valueOf(j + 1))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
     //   lastUpdatedTime = time;
        return result;
    }

    private void readJsonArray(JSONArray feeds, ArrayList<Entry>[] result) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.GERMAN);
        long time = 1000;
        for (int k = 0; k < feeds.length(); k++) {
           time = sdf.parse(feeds.getJSONObject(k).getString("created_at")).getTime();
            for (int j = 0; j < result.length; j++) {
                try {
                    result[j].add(new Entry(time, (float) feeds.getJSONObject(k).getDouble("field" + String.valueOf(j + 1))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
      //  lastUpdatedTime = time;
    }


    public void dataRangeChanged(int value) {
        for (OnDataArrivedListener l : listeners) {
            l.dataRangeChanged(value);
        }
    }

    private void msg(final String s) {
        Handler mainHandler = new Handler(context.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            }
        };
        mainHandler.post(myRunnable);

    }

    public interface OnDataArrivedListener {
        void dataArrived(ArrayList<Entry>[] result);

        void dataUpdated(Entry[] result);

        void dailyMaximaArrived(ArrayList<Entry[][]> result);

        void dataRangeChanged(int entries);
    }

    private class Runnable implements java.lang.Runnable {
        private JSONObject response;

        public void setResponse(JSONObject response) {
            this.response = response;
        }

        public JSONObject getResponse() {
            return response;
        }

        @Override
        public void run() {
        }
    }
}
