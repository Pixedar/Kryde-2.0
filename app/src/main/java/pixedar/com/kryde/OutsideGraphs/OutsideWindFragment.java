package pixedar.com.kryde.OutsideGraphs;

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

public class OutsideWindFragment extends Fragment {
    private LineChart mChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wind_chart, container, false);

        mChart = (LineChart) rootView.findViewById(R.id.wind_chart);
        ChartsSettings.setChart(mChart);
        ChartsSettings.setXaxis(mChart.getXAxis());
        ChartsSettings.setYaxis(mChart.getAxisLeft());
        mChart.getAxisLeft().setAxisMinimum(-0.7f);
        mChart.getAxisLeft().setSpaceTop(0);
        return rootView;
    }

    public void setListener(WeatherDataController weatherDataController) {
        weatherDataController.setOnDataArrivedListener(new WeatherDataController.OnDataArrivedListener() {
            @Override
            public void dataArrived(ArrayList<Entry>[] result) {
                LineDataSet lineDataSet = ChartsSettings.getLineDataSet(result[5], ContextCompat.getColor(getContext(), R.color.outsideColor));
                //  ChartsSettings.setColorfulLine(lineDataSet);

                LineDataSet lineDataSet2 = ChartsSettings.getLineDataSet(result[6], ContextCompat.getColor(getContext(), R.color.outsideColor));
                lineDataSet2.setFillAlpha(100);
                //  lineDataSet2.setMode(LineDataSet.Mode.CUBIC_BEZIER);

                LineData lineData = new LineData(lineDataSet, lineDataSet2);
                lineData.setDrawValues(false);
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
