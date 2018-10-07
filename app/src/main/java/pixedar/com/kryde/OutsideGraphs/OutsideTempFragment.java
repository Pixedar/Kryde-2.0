package pixedar.com.kryde.OutsideGraphs;

import android.graphics.Color;
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
import java.util.List;

import pixedar.com.kryde.ChartsSettings;
import pixedar.com.kryde.R;
import pixedar.com.kryde.WeatherDataController;

/**
 * Created by Wiktor on 2017-09-02.
 */

public class OutsideTempFragment extends Fragment{
    private LineChart mChart;
    private final float[]maxima ={0,1};
    private List<Entry> val;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.temp_chart2, container, false);

        mChart = (LineChart) rootView.findViewById(R.id.temp_chart2);
        ChartsSettings.setChart(mChart);

        ChartsSettings.setXaxis(mChart.getXAxis());
        ChartsSettings.setYaxis(mChart.getAxisLeft());
        return rootView;
    }

    public void setListener(WeatherDataController weatherDataController){
        weatherDataController.setOnDataArrivedListener(new WeatherDataController.OnDataArrivedListener() {
            @Override
            public void dataArrived(ArrayList<Entry>[] result) {
                if (mChart.getData() == null){
                    LineDataSet lineDataSet = ChartsSettings.getLineDataSet(result[0], ContextCompat.getColor(getContext(), R.color.outsideColor));
              /*      lineDataSet.setDrawFilled(false);
                    lineDataSet.setLineWidth(1);
                    lineDataSet.setColor(Color.BLACK);*/
                    val = result[0];
                    LineData lineData = new LineData(lineDataSet,ChartsSettings.getMaxima(result[0]));
                    maxima[0] = lineData.getXMin();
                    maxima[1] = lineData.getXMax();
                    mChart.setData(lineData);
                }else{
                    ((LineDataSet) mChart.getData().getDataSetByIndex(0)).setValues(result[0]);
                    ((LineDataSet) mChart.getData().getDataSetByIndex(1)).setValues(ChartsSettings.getMaxima(result[0]).getValues());
                    mChart.getData().notifyDataChanged();
                    mChart.notifyDataSetChanged();
                }


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

                if (mChart.getData() != null){
                /*        LineDataSet set = (LineDataSet)mChart.getData().getDataSetByIndex(0);
                        int range = (int) (((100-entries)/100.0f)*set.getValues().size());
                        ArrayList<Entry> values = new ArrayList<>();

                        for(int k = set.getValues().size()-1; k > set.getValues().size()-1 - range;k--){
                        //    Log.d("GGG",String.valueOf(k));
                         //   Log.d("GGG",String.valueOf(set.getValues().get(k)));
                         //   Log.d("GGG",String.valueOf(set.getValues().get(k).getX()));
                            values.add(new Entry(set.getValues().get(k).getX(),set.getValues().get(k).getX()));
                        }
                        if(values.size() >0){
                            set.setValues(values);
                        }*/
                     //   mChart.setVisibleXRange(mChart.getData().getXMin(),(entries/100.0f)*mChart.getData().getXMax());
                //    mChart.zoom(entries/100.0f,1,0,0);
              //      ViewPortHandler handler = mChart.getViewPortHandler();
                // Log.d("GGG", String.valueOf(mChart.getValuesByTouchPoint(handler.contentRight(), handler.contentBottom(), YAxis.AxisDependency.LEFT)));

                    LineDataSet set = (LineDataSet)mChart.getData().getDataSetByIndex(0);
                    if(entries > 950){
                     //   set.setMode(LineDataSet.Mode.);
                        set.setDrawValues(true);
                        set.setColor(Color.BLACK);
                        //set.setCubicIntensity(0);
                    }else{
                        set.setMode(LineDataSet.Mode.LINEAR);
                        set.setDrawValues(false);
                    }

                    set.setValues(val.subList((int)((entries/1000.0f)*(val.size() -1)), val.size()-1));
                    mChart.setData(new LineData(set));


        /*            XAxis x = mChart.getXAxis();
                    x.setAxisMinimum(maxima[0] +(maxima[1] - maxima[0])*(entries/100.0f));
                    YAxis y=  mChart.getAxisLeft();
                    y.setAxisMaximum(mChart.getYMax());
                    y.setAxisMinimum(mChart.getYMin());*/

                        mChart.getData().notifyDataChanged();
                       mChart.notifyDataSetChanged();
                        mChart.invalidate();
                }
            }
        });
    }

}


