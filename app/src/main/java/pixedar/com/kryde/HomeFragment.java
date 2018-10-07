package pixedar.com.kryde;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.github.mikephil.charting.data.Entry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import pixedar.com.kryde.HomeGraphs.HomePagerAdapter;
import pixedar.com.kryde.OutsideGraphs.OutsidePagerAdapter;

/**
 * Created by Wiktor on 2017-09-01.
 */

public class HomeFragment extends Fragment {
    //public static TabLayout tabLayout;

    //public static TabLayout tabLayout2;
    TabLayout  tabLayout;
    TabLayout  tabLayout2;
   // Bluetooth bluetooth;

    Parser parser;
    ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment, container, false);
        initUI(rootView);
       // bluetooth = new Bluetooth(getContext(),this);
       // bluetooth.connect(progressBar);
        parser= new Parser();
        return rootView;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initUI(View rootView){

        tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout2);
        tabLayout.getTabAt(0).setText(String.valueOf(Parser.getCurrentTemp1())+" °C");
        tabLayout.getTabAt(1).setText(String.valueOf(Parser.getCurrentHum1())+" %");
        tabLayout.getTabAt(2).setText(String.valueOf(Parser.getCurrentPress())+" hPa");

        tabLayout2 =  rootView.findViewById(R.id.tab_layout_outside);
        tabLayout2.getTabAt(0).setText(String.valueOf(Parser.getCurrentTemp2()) +" °C");
        tabLayout2.getTabAt(1).setText(String.valueOf(Parser.getCurrentHum2())+" %");
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        SeekBar seekBar2 = rootView.findViewById(R.id.seekBar);

        String url = "https://api.thingspeak.com/channels/333150/feeds.json?results=2";

        final WeatherDataController weatherDataController = new WeatherDataController(getContext(),progressBar);


        final ViewPager viewPager = rootView.findViewById(R.id.pager2);

        final HomePagerAdapter adapter = new HomePagerAdapter
                (getActivity().getSupportFragmentManager(), tabLayout.getTabCount(),weatherDataController);

        final OutsidePagerAdapter adapter2= new OutsidePagerAdapter
                (getActivity().getSupportFragmentManager(), tabLayout2.getTabCount(),weatherDataController);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
       // weatherDataController.loadData("days",32);  //////////////// odkomentować
        weatherDataController.setKeepUpdating(true);
        weatherDataController.loadData("days",1);
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.d("GGG",String.valueOf(seekBar.getProgress()));
                weatherDataController.dataRangeChanged(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
             //   progressBar.setVisibility(View.VISIBLE);
            ///    weatherDataController.loadData("days",seekBar.getProgress());
            }
        });
        weatherDataController.setOnDataArrivedListener(new WeatherDataController.OnDataArrivedListener() {
            @Override
            public void dataArrived(ArrayList<Entry>[] result) {
               tabLayout.getTabAt(0).setText(String.valueOf(result[1].get(result[1].size()-1).getY())+" °C");
              //  tabLayout.getTabAt(0).setCustomView(R.layout.tab_layout);
                tabLayout.getTabAt(1).setText(String.valueOf(result[3].get(result[3].size()-1).getY())+" %");
                tabLayout.getTabAt(2).setText(String.valueOf(result[4].get(result[4].size()-1).getY())+" hPa");
                tabLayout2.getTabAt(0).setText(String.valueOf(result[0].get(result[0].size()-1).getY()) +" °C");
                tabLayout2.getTabAt(1).setText(String.valueOf(result[2].get(result[2].size()-1).getY())+" %");
                tabLayout2.getTabAt(2).setText(String.valueOf(result[6].get(result[6].size()-1).getY())+" km/h");
            }

            @Override
            public void dataUpdated(Entry[] result) {

            }

            @Override
            public void dailyMaximaArrived(ArrayList<Entry[][]> result) {

            }

            @Override
            public void dataRangeChanged(int entries) {

            }
        });


        final ViewPager viewPager2 = (ViewPager) rootView.findViewById(R.id.pager_outside);
        viewPager2.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });



        viewPager2.setAdapter(adapter2);
        viewPager2.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout2));
        viewPager2.setOffscreenPageLimit(3);
        tabLayout2.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void updateUI(){
      //  HomeTempFragment.update(Parser.getHomeTemp(), getContext());


        tabLayout.getTabAt(0).setText(String.valueOf(Parser.getCurrentTemp1()) + " °C");
        tabLayout.getTabAt(1).setText(String.valueOf(Parser.getCurrentHum1()) + " %");
        tabLayout.getTabAt(2).setText(String.valueOf(Parser.getCurrentPress()) + " hPa");

        tabLayout2.getTabAt(0).setText(String.valueOf(Parser.getCurrentTemp2()) + " °C");
        tabLayout2.getTabAt(1).setText(String.valueOf(Parser.getCurrentHum2()) + " %");


    }



    private class SaveTask extends AsyncTask<Object, Object,Boolean> {

        @Override
        protected void onPreExecute() {
            progressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        }

        @Override
        protected Boolean doInBackground(Object... devices) {
            try {
                saveData();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void saveData() throws IOException {
   /*     getContext().getSharedPreferences("PREFS_NAME",getContext().MODE_PRIVATE)
                .edit()
                .putString("jsonarray", jsonArray.toString())
                .apply();*/

       /* SharedPreferences pref = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);

        String json_array = pref.getString("jsonarray", null);

        JSONArray jsoArray=new JSONArray(json_array);*/

        File file1= new File(Environment.getExternalStorageDirectory() + "/WeatherData/","current.txt");
        BufferedWriter outputStreamA =null;
            file1.createNewFile();
            outputStreamA = new BufferedWriter(new FileWriter(file1, false));

            for(int k =0; k < Parser.getHomeTemp().size();k++){
                outputStreamA.append(String.valueOf((int)Parser.getOutTemp().get(k).getX())).append(";");
                outputStreamA.append(String.valueOf(Parser.getOutTemp().get(k).getY())).append(";");
                outputStreamA.append(String.valueOf(Parser.getOutHum().get(k).getY())).append(";");
                outputStreamA.append(String.valueOf(Parser.getHomeTemp().get(k).getY())).append(";");
                outputStreamA.append(String.valueOf(Parser.getHomeHum().get(k).getY())).append(";");
                outputStreamA.append(String.valueOf(Parser.getPress().get(k).getY())).append(";").append("\r\n");
                Log.d("GG", String.valueOf(k));
            }
            outputStreamA.flush();
            outputStreamA.close();

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("delay", Parser.delay);
        editor.apply();

    }

}
