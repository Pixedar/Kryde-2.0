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

/**
 * Created by Wiktor on 2017-09-01.
 */

public class HomePressFragment extends Fragment {
    private LineChart mChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.press_chart, container, false);

        mChart = (LineChart) rootView.findViewById(R.id.press_chart);
        ChartsSettings.setChart(mChart);
        ChartsSettings.setXaxis(mChart.getXAxis());
        ChartsSettings.setYaxis(mChart.getAxisLeft());
        //mChart.invalidate();

        return rootView;
    }
    public void setListener(WeatherDataController weatherDataController){
        weatherDataController.setOnDataArrivedListener(new WeatherDataController.OnDataArrivedListener() {
            @Override
            public void dataArrived(ArrayList<Entry>[] result) {
                LineDataSet lineDataSet = ChartsSettings.getLineDataSet(result[4],ContextCompat.getColor(getContext(), R.color.homeColor));
                LineData lineData = new LineData(ChartsSettings.getMaxima(result[4]),lineDataSet);
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
