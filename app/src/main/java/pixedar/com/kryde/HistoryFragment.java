package pixedar.com.kryde;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.github.mikephil.charting.data.Entry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Wiktor on 2017-09-04.
 */

public class HistoryFragment extends Fragment{
    CardView mainCard;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.history_fragment, container, false);

        mainCard = (CardView)rootView.findViewById(R.id.awesome_card);


        final CardView card1 = (CardView)rootView.findViewById(R.id.card1r);
        final CardView card2 = (CardView)rootView.findViewById(R.id.card2r);
        final CardView card3 = (CardView)rootView.findViewById(R.id.card3r);
        final CardView card4 = (CardView)rootView.findViewById(R.id.card4r);
        final CardView card5 = (CardView)rootView.findViewById(R.id.card5r);

        final boolean[] sw = {false,false,false,false,false};


        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(0, 160)
                .setDuration(200);


        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // get the value the interpolator is at
                Integer value = (Integer) animation.getAnimatedValue();
                // I'm going to set the layout's height 1:1 to the tick
                // card1.getLayoutParams().height = value.intValue();
                for (int k=0; k < sw.length;k++){
                    if(sw[k]){
                        update(k,value,card1,card2,card3,card4,card5);
                        break;
                    }
                }
            }
            private void update(int index,int value,CardView ... cardViews){
                for(int k =0; k < cardViews.length;k++){
                    if(k == index){
                        cardViews[k].setScaleY(1 +(float)(value/1000.0f));
                        cardViews[k].setScaleX(1 +(float)(value/800.0f));
                        cardViews[k].requestLayout();
                    }
                }
            }
        });

// create a new animationset
        final AnimatorSet set = new AnimatorSet();
// since this is the only animation we are going to run we just use
// play
        set.play(slideAnimator);
// this is how you set the parabola which controls acceleration
        set.setInterpolator(new AccelerateDecelerateInterpolator());
// start the animation

        card1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                updateCards(1,sw,card1,card2,card3,card4,card5);
                set.start();
            //    animCard();
                return false;
            }
        });

        card2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                updateCards(2,sw,card1,card2,card3,card4,card5);
                set.start();
                set.start();
           //     animCard();
                return false;
            }
        });


        card3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                updateCards(3,sw,card1,card2,card3,card4,card5);
                set.start();
                set.start();
           //     animCard();
                return false;
            }
        });


        card4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                updateCards(4,sw,card1,card2,card3,card4,card5);
                set.start();
         //       animCard();
                return false;
            }
        });

        card5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                updateCards(5,sw,card1,card2,card3,card4,card5);
                set.start();
           //     animCard();
                return false;
            }
        });

        // get the center for the clipping circl

/*

        File file1= new File(Environment.getExternalStorageDirectory() + "/WeatherData/","req.txt");
        file1.createNewFile();
        BufferedReader inputStreamA =null;
        inputStreamA = new BufferedReader(new FileReader(file1));
        String data = "";
        while ((data = inputStreamA.readLine()) != null) {
            Parser.parseReg(data);
        }
        inputStreamA.close();
*/


        return rootView;
    }

    private class reqData{
        float max;
        float min;
        ArrayList<Entry> data;
    }
    public reqData getReghomeTemp(int index) throws IOException {
        File file1= new File(Environment.getExternalStorageDirectory() + "/WeatherData/","req.txt");
        file1.createNewFile();
        BufferedReader inputStreamA =null;
        inputStreamA = new BufferedReader(new FileReader(file1));
        String data = "";
        while ((data = inputStreamA.readLine()) != null) {

            String[] values = data.split(";");
            String[] time = values[0].split("-");
            float t = Integer.valueOf(time[0])*365 + getMonthDay(Integer.valueOf(time[1])-1) + Integer.valueOf(time[2]);

        }
        return null;

    }

    private float getMonthDay(int value){
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

/*
    private void animCard(){
        int cx = (mainCard.getLeft() + mainCard.getRight()) / 2;
        int cy = mainCard.getTop()-200;
      //  cx = (int) event.getX();
       // cy = (int) event.getX();
        // get the final radius for the clipping circle
        int dx = Math.max(cx, mainCard.getWidth() - cx);
        int dy = Math.max(cy, mainCard.getHeight()- cy);
        float finalRadius = (float) Math.hypot(dx, dy);

        Animator animator =
                ViewAnimationUtils.createCircularReveal(mainCard, cx, cy, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(700);
        animator.start();
    }
*/

    private void updateCards(int index,boolean[] sw,CardView ... cardViews){
        for(int k =0; k < cardViews.length;k++){
            if(k == index-1){
                cardViews[k].setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
                sw[k] = true;
            }else {
                cardViews[k].setBackgroundColor(ContextCompat.getColor(getContext(), R.color.cardview_light_background));
                cardViews[k].setScaleY(1);
                cardViews[k].setScaleX(1);
                sw[k] = false;
            }
        }
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);

    }


}
