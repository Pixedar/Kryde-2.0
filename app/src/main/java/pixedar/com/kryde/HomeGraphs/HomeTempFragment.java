package pixedar.com.kryde.HomeGraphs;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

import pixedar.com.kryde.ChartsSettings;
import pixedar.com.kryde.R;
import pixedar.com.kryde.WeatherDataController;

public class HomeTempFragment extends Fragment  {
    private LineChart mChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.temp_chart, container, false);

        mChart = rootView.findViewById(R.id.temp_chart);
        ChartsSettings.setChart(mChart);
        ChartsSettings.setXaxis(mChart.getXAxis());
        ChartsSettings.setYaxis(mChart.getAxisLeft());
        return rootView;
    }

    public void setListener(WeatherDataController weatherDataController){
        weatherDataController.setOnDataArrivedListener(new WeatherDataController.OnDataArrivedListener() {
            @Override
            public void dataArrived(ArrayList<Entry>[] result) {
                LineDataSet lineDataSet = ChartsSettings.getLineDataSet(result[1],ContextCompat.getColor(getContext(), R.color.homeColor));
               LineData lineData = new LineData(lineDataSet,ChartsSettings.getMaxima(result[1]));
             //   LineData lineData = new LineData(lineDataSet);
                    mChart.setData(lineData);
                    mChart.invalidate();
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
    }



}
