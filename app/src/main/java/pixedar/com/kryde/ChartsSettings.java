package pixedar.com.kryde;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ChartsSettings {
    public static LineDataSet getLineDataSet(ArrayList<Entry> val, int color){
        LineDataSet lineDataSet = new LineDataSet(val, "set");
      // lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setValueTextColor(Color.BLACK);
       // lineDataSet.setCubicIntensity(0f);

       // lineDataSet.setLineWidth(0.0f);
        lineDataSet.setDrawFilled(true);
       lineDataSet.setFillColor(color);
        lineDataSet.setColor(color,0);
        lineDataSet.setFillAlpha(255);
        lineDataSet.setDrawValues(false);
        lineDataSet.setHighlightEnabled(false);
        return lineDataSet;
    }
    public static void setColorfulLine(LineDataSet lineDataSet){
        lineDataSet.getEntryCount();
        int colors[] = new int[lineDataSet.getEntryCount()];
        for(int k =0; k< colors.length;k++ ){
            colors[k] = Color.HSVToColor(new float[]{map((int)(lineDataSet.getEntryForIndex(k).getY()*100),(int)lineDataSet.getYMin()*100, (int)lineDataSet.getYMax()*100,280,0),1,1});
        }

        lineDataSet.setColors(colors);
        lineDataSet.setLineWidth(0.5f);
        lineDataSet.setFillAlpha(220);
    }
    public static void setXaxis(XAxis x){
      //  x.setLabelCount(10, false);
        //x.setAvoidFirstLastClipping(true);

        x.setTextColor(Color.WHITE);
        x.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        x.setAxisLineColor(Color.WHITE);
        x.setDrawGridLines(false);
        x.setValueFormatter(new IAxisValueFormatter() {
            private SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm");
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mFormat.format(new Date((long)value));
            }
        });
    }
    public static void setYaxis(YAxis y){
        y.setLabelCount(3, false);
        y.setSpaceTop(40);
        y.setSpaceBottom(40);
        y.setDrawZeroLine(false);
        y.setTextColor(Color.BLACK);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);
        y.setEnabled(true);
    }
    public static void setChart(LineChart mChart){
        mChart.setViewPortOffsets(0, 0, 0, 0);
        mChart.getDescription().setEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.getAxisRight().setEnabled(false);
        mChart.getLegend().setEnabled(false);
        mChart.setMaxVisibleValueCount(Integer.MAX_VALUE);

    }
    public static LineDataSet getMaxima(final ArrayList<Entry> val){

        float maxX = 0;
        float maxY = -100;
        int index = 0;
        int maxIndex= 0;
        for(Entry e: val){
            if(e.getY() > maxY ){
                maxX = e.getX();
                maxY = e.getY();
                maxIndex =index;
            }
            index++;
        }
        float minX = 0;
        float minY = 10000;
        index = 0;
        int minIndex = 0;
        for(Entry e: val){
            if(e.getY() < minY ){
                minX = e.getX();
                minY = e.getY();
                minIndex = index;
            }
            index++;
        }
        LineDataSet lineDataSet2 =  new LineDataSet(Arrays.asList(new Entry(maxX, maxY), new Entry(minX, minY)), "set2");
        lineDataSet2.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.format("%.1f", value);
            }
        });
        lineDataSet2.setCircleRadius(1.5f);
        lineDataSet2.setCircleColorHole(Color.BLACK);
       // lineDataSet2.setValueTextColor(Color.RED);
        lineDataSet2.setDrawValues(true);
        lineDataSet2.setColor(Color.RED,0);
        lineDataSet2.setCircleColor(Color.BLACK);
        return lineDataSet2;
    }

    private static int map(int x, int in_min, int in_max, int out_min, int out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
}
