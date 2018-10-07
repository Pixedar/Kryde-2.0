package pixedar.com.kryde;


import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class Parser {
    private static ArrayList<Entry> homeTemp = new ArrayList<>();
    private static ArrayList<Entry> outTemp =new ArrayList<>();
    private static ArrayList<Entry> homeHum =new ArrayList<>();
    private static ArrayList<Entry> outHum =new ArrayList<>();
    private static ArrayList<Entry> press =new ArrayList<>();

    private static ArrayList<Entry> reghomeTemp = new ArrayList<Entry>();
    private static ArrayList<Entry> regoutTemp =new ArrayList<Entry>();
    private static ArrayList<Entry> reghomeHum =new ArrayList<Entry>();
    private static ArrayList<Entry> regoutHum =new ArrayList<Entry>();
    private static ArrayList<Entry> regpress =new ArrayList<Entry>();

    private static ArrayList<Maxima> maximas =new ArrayList<Maxima>();

    private static float currentTemp1 = 25.4f;
    private static float currentHum1 = 60;
    private static float currentTemp2 = 25.4f;
    private static float currentHum2 = 60;
    private static float currentPress = 980;
    private static boolean onStart = true;

    public static int ledMode = 6;
    public static int dim= 255;

    public static int delay =0;

    public  static void parseCurrent(String data) throws Exception{

        String[] values = data.split(";");
        String[] time = values[0].split(":");



        float t = ((Integer.parseInt(time[0])+delay) * 3600f) + (Integer.parseInt(time[1]) * 60f);
        if(outTemp.size()!=0) {
            if (t < outTemp.get(outTemp.size() - 1).getX()) {
                delay += 24;
                t+=24*3600.0f;
            }
        }

        if(homeTemp.size()>240){
            homeHum.remove(0);
            homeTemp.remove(0);
            outHum.remove(0);
            outTemp.remove(0);
            press.remove(0);
        }

    //    float t = (Integer.parseInt(time[0]) * 3600f) + (Integer.parseInt(time[1]) * 60f);  //Integer.parseInt(time[0]) -3572f

            outTemp.add(new Entry(t, Float.parseFloat(values[1])));
            outHum.add(new Entry(t, Float.parseFloat(values[2])));
            homeTemp.add(new Entry(t, Float.parseFloat(values[3])));
            homeHum.add(new Entry(t, Float.parseFloat(values[4])));
            press.add(new Entry(t, Float.parseFloat(values[5])));
        if(onStart){
            currentTemp2 = outTemp.get(outTemp.size()-1).getY();
            currentTemp1 = homeTemp.get(homeTemp.size() -1).getY();
            currentHum1 = homeHum.get(homeHum.size()-1).getY();
            currentHum2 = outHum.get(outHum.size()-1).getY();
            currentPress = press.get(press.size()-1).getY();
            onStart = false;
        }

    }


    public static void parseReg(String data){
        String[] values = data.split(";");
        String[] time = values[0].split("-");

        float t = Integer.valueOf(time[0])*365 + getMonthDay(Integer.valueOf(time[1])-1) + Integer.valueOf(time[2]);

        Maxima maxima  = new Maxima();

        regoutTemp.add(new Entry(t,Float.parseFloat(values[1])));
        regoutHum.add(new Entry(t,Float.parseFloat(values[2])));
        maxima.outMaxTemp = Float.parseFloat(values[3]);
        maxima.outMinTemp = Float.parseFloat(values[4]);
        maxima.outMinHum = Float.parseFloat(values[5]);

        reghomeTemp.add(new Entry(t,Float.parseFloat(values[6])));
        reghomeHum.add(new Entry(t,Float.parseFloat(values[7])));
        maxima.homeMaxTemp = Float.parseFloat(values[8]);
        maxima.homeMinTemp = Float.parseFloat(values[9]);
        maxima.homeMinHum = Float.parseFloat(values[10]);

        regpress.add(new Entry(t,Float.parseFloat(values[11])));
        maxima.minPress = Float.parseFloat(values[12]);
        maxima.maxPress= Float.parseFloat(values[13]);

    }
    private static float getMonthDay(int value){
        switch (value){
            case 1: return 31;
            case 2: return 59;
            case 3: return 90;
            case 4: return 120;
            case 5: return 151;
            case 6: return 181;
            case 7: return 212;
            case 8: return 243;
            case 9: return 273;
            case 10: return 304;
            case 11: return 334;
            case 12: return 365;
            default: return 0;
        }
    }


    public static void restoreCurrent(String data){
        String[] values = data.split(";");
     //   String[] time = values[0].split(":");

        float t = Float.parseFloat(values[0]);
      //  float t = (Integer.parseInt(time[0]) * 3600f) + (Integer.parseInt(time[1]) * 60f);
        outTemp.add(new Entry(t, Float.parseFloat(values[1])));
        outHum.add(new Entry(t, Float.parseFloat(values[2])));
        homeTemp.add(new Entry(t, Float.parseFloat(values[3])));
        homeHum.add(new Entry(t, Float.parseFloat(values[4])));
        press.add(new Entry(t, Float.parseFloat(values[5])));
        currentTemp2 = outTemp.get(0).getY();
        currentTemp1 = homeTemp.get(0).getY();
        currentHum1 = homeHum.get(0).getY();
        currentHum2 = outHum.get(0).getY();
        currentPress = press.get(0).getY();
    }

    public static ArrayList<Entry> getHomeTemp(){
        return homeTemp;
    }
    public static ArrayList<Entry> getHomeHum(){
        return homeHum;
    }
    public static ArrayList<Entry> getOutTemp(){
        return outTemp;
    }
    public static ArrayList<Entry> getOutHum(){
        return outHum;
    }
    public static ArrayList<Entry> getPress(){
        return press;
    }
    static float getCurrentTemp1(){
        return currentTemp1;
    }
    static float getCurrentHum1(){
        return currentHum1;
    }
    static float getCurrentTemp2(){
        return currentTemp2;
    }
    static float getCurrentHum2(){
        return currentHum2;
    }
    static float getCurrentPress(){
        return currentPress;
    }
    int getLedMode(){
        return ledMode;
    }
}
