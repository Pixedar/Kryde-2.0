package pixedar.com.kryde;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.data.Entry;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import io.apptik.widget.MultiSlider;

public class LedFragment extends Fragment implements OnBluetoothConnectedListener {

    private boolean btConnected = false;
  //  ProgressBar progressBar;
    SeekBar brightnessSlider;
    View rootView;
    final int index[] = new int[]{0};
    final int[][] rgbColorValue = {{1,2,3},{4,5,1}};
    final int[] hsvColorValue = {1,2};
    final int[] colorSelected = {0};
    final boolean[] clockSelected = {false,false};
    final boolean[] dataArrived ={false};
    final ImageView colorModesSelects[] = new ImageView[4];
    final LinearLayout colorModes[] = new LinearLayout[4];
    final CardView cards[] = new CardView[4];
    final TextView colorTextVievs[] = new TextView[12];
    ProgressBar progressBar;
    SeekBar sendingIntervalSlider;
    SeekBar oversamplingSlider;
    TextView sendingIntervalText;
    TextView oversamplingText;
    Switch recieveModeSwitch;
    Switch turnOnDisplaySwitch;
    Switch dimSwitch;
    final static int sendingInterval[] = {18};
    final boolean recieveMode[] =  {false};
    final int lastHsvColoValue[] = {1,3};
    final Handler handler[] = {new Handler()};
    final boolean clockColor[] = {false,false};

    Bluetooth bluetooth;
    final MultiSlider rangeSlider[] = new MultiSlider[1];
    int maximaSelectedIndex = 0;
    @Override
    public void onResume() {
        bluetooth.connect(progressBar);
        super.onResume();
    }

    @SuppressLint("ClickableViewAccessibility")


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.led_fragment, container, false);
        bluetooth = new Bluetooth(getContext(),this);

        final CardView redSelect = rootView.findViewById(R.id.redSelect);
        final CardView greenSelect = rootView.findViewById(R.id.greenSelect);
        final CardView blueSelect = rootView.findViewById(R.id.blueSelect);
        final CardView hsvSelect = rootView.findViewById(R.id.hsvSelect);
        final CardView clockSelect = rootView.findViewById(R.id.clockSelect);

        final CardView colorModeCard = rootView.findViewById(R.id.colorModeCard);
       progressBar = rootView.findViewById(R.id.progressBar2);
       final TextView rangeText = rootView.findViewById(R.id.rangeText);
        rangeSlider[0] = rootView.findViewById(R.id.range_slider);
        brightnessSlider =  rootView.findViewById(R.id.brightnessSeekBar);
        final TextView brightnessValue =  rootView.findViewById(R.id.brightnessText);

        dimSwitch = rootView.findViewById(R.id.dimSwitch);
        sendingIntervalSlider = rootView.findViewById(R.id.sendingIntervalSeekBar);
        oversamplingSlider = rootView.findViewById(R.id.oversamplingSeekBar);
        sendingIntervalText = rootView.findViewById(R.id.sendingIntervalText);
        oversamplingText =rootView.findViewById(R.id.oversamplingText);
        recieveModeSwitch = rootView.findViewById(R.id.recieveModeSwitch);
        turnOnDisplaySwitch = rootView.findViewById(R.id.displaySwitch);

        String url = "http://my-json-feed";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new com.android.volley.Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        dimSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (dimSwitch.isChecked()) {
                    final Calendar c = Calendar.getInstance();
                    final int h = c.get(Calendar.HOUR_OF_DAY);
                    final int m = c.get(Calendar.MINUTE);
                    msg("Select turn off time");
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {
                                    msg("Select turn on time");
                                    final int hour1 = hourOfDay;
                                    final int minute1 = minute;
                                    TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                                            new TimePickerDialog.OnTimeSetListener() {

                                                @Override
                                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                                      int minute) {
                                                    writeBytes((byte) 8, (byte) hour1, (byte) hourOfDay, (byte) minute1, (byte) minute);

                                                }
                                            }, h, m, true);
                                    timePickerDialog.show();

                                }
                            }, h, m, true);
                    timePickerDialog.show();
                }
            }
        });

        recieveModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(recieveMode[0]){
                   writeBytes((byte)9 , (byte) 1);
               }else{
                   writeBytes((byte)9 , (byte) 1);
               }
            }
        });
        sendingIntervalSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                sendingIntervalText.setText(String.valueOf((int)(progress/6))+" min" + String.valueOf((progress -((int)(progress/6)*6))*10 + " s"));

                oversamplingSlider.setMax(sendingIntervalSlider.getProgress()*10/2);
                //     writeBytes((byte) 1, (byte) seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sendingInterval[0] = sendingIntervalSlider.getProgress();
                writeBytes((byte)5, (byte) sendingIntervalSlider.getProgress());
            }
        });


        oversamplingSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                oversamplingText.setText(String.valueOf(progress)+" samples" );
                //     writeBytes((byte) 1, (byte) seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                writeBytes((byte)6, (byte) (10*(float)sendingIntervalSlider.getProgress()*10/oversamplingSlider.getProgress()));
            }
        });


        rangeSlider[0].setOnThumbValueChangeListener(new MultiSlider.SimpleChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int
                    thumbIndex, int value) {

                rangeText.setText("Range from " +String.valueOf(rangeSlider[0].getThumb(0).getValue())+" to " + String.valueOf(rangeSlider[0].getThumb(1).getValue()));


            }

        });
        rangeSlider[0].setOnTrackingChangeListener(new MultiSlider.OnTrackingChangeListener() {
            @Override
            public void onStartTrackingTouch(MultiSlider multiSlider, MultiSlider.Thumb thumb, int value) {

            }

            @Override
            public void onStopTrackingTouch(MultiSlider multiSlider, MultiSlider.Thumb thumb, int value) {
               // Log.d("DDD","FG");
                maxima[maximaSelectedIndex][0] = rangeSlider[0].getThumb(1).getValue();
                maxima[maximaSelectedIndex][1] = rangeSlider[0].getThumb(0).getValue();
                Log.d("GGG",String.valueOf(maximaSelectedIndex));
                if(maximaSelectedIndex != 2){
                    writeBytes((byte)4,(byte)maximaSelectedIndex,(byte)maxima[maximaSelectedIndex][0],(byte)maxima[maximaSelectedIndex][1]);
                }else{
                    writeBytes((byte)4,(byte)maximaSelectedIndex,(byte)(maxima[maximaSelectedIndex][0]-900),(byte)(maxima[maximaSelectedIndex][1]-900));
                }
                setCardColor(index[0]);
            }
        });

        final CheckBox checkBoxs[] = new CheckBox[8];
        for(int k =0; k < checkBoxs.length; k++){
            String id = "checkBox" + (k+1);
            checkBoxs[k] = rootView.findViewById(getResources().getIdentifier(id, "id",getContext().getPackageName()));
        }
        for(int k =0; k < checkBoxs.length; k++){
            final int finalK = k;
            checkBoxs[k].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    updateCheckBoxs(finalK,checkBoxs,false);
                    if(index[0] ==0||index[0] ==2){
                        if(index[0] == 0){
                            rgbColorValue[0][colorSelected[0]] = finalK+1;
                            writeBytes((byte) 3,(byte) 0,(byte)(rgbColorValue[0][0]),(byte)(rgbColorValue[0][1]),(byte)(rgbColorValue[0][2]));
                            int col[] = setLedColor(0);
                            cards[0].setCardBackgroundColor(Color.rgb(col[0],col[1],col[2]));

                        }else{
                            rgbColorValue[1][colorSelected[0]] = finalK+1;
                            writeBytes((byte) 3,(byte) 1,(byte)(rgbColorValue[1][0]),(byte)(rgbColorValue[1][1]),(byte)(rgbColorValue[1][2]));
                            int col[] = setLedColor(1);
                            cards[2].setCardBackgroundColor(Color.rgb(col[0],col[1],col[2]));
                        }

                        colorMode1TextUpdate();
                        colorMode2TextUpdate();
                    }else {
                        if(index[0] == 1){
                            hsvColorValue[0] = finalK+1;
                            writeBytes((byte) 3,(byte) 2,(byte)(hsvColorValue[0]));
                            int col[] = setLedColor(2);
                            cards[1].setCardBackgroundColor(Color.rgb(col[0],col[1],col[2]));
                        }else{
                            hsvColorValue[1] = finalK+1;
                            writeBytes((byte) 3,(byte) 3,(byte)(hsvColorValue[1]));
                            int col[] = setLedColor(3);
                            cards[3].setCardBackgroundColor(Color.rgb(col[0],col[1],col[2]));
                        }

                        colorTextVievs[4].setText(getColorName(hsvColorValue[0]));
                        colorTextVievs[10].setText(getColorName(hsvColorValue[1]));
                    }

              /*      try {
                        byte buffer[] = new byte[4];
                        bluetooth.getData(buffer);
                        setCardColor(index[0],buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    return false;
                }
            });
        }

        for(int k =0; k < cards.length; k++){
            String id = "colorModeCard" + (k+1);
            cards[k] = rootView.findViewById(getResources().getIdentifier(id, "id",getContext().getPackageName()));
        }

        final LinearLayout textL[] = new LinearLayout[4];
        for(int k =0; k < textL.length; k++){
            String id = "textL" + (k+1);
            textL[k] = rootView.findViewById(getResources().getIdentifier(id, "id",getContext().getPackageName()));
        }


        final ImageView arrows[] = new ImageView [4];
        for(int k =0; k < arrows.length; k++){
            String id = "returnArrow" + (k+1);
            arrows[k] = rootView.findViewById(getResources().getIdentifier(id, "id",getContext().getPackageName()));
        }

        for(int k =0; k < colorTextVievs.length; k++){
            String id = "text" + (k+1);
            colorTextVievs[k] = rootView.findViewById(getResources().getIdentifier(id, "id",getContext().getPackageName()));
        }


        for(int k =0; k < textL.length; k++){
            String id = "colorMode" + (k+1);
            colorModes[k] = rootView.findViewById(getResources().getIdentifier(id, "id",getContext().getPackageName()));
        }

        for(int k =0; k < colorModesSelects.length; k++){
            String id = "colorModeSelect" + (k+1);
            colorModesSelects[k] = rootView.findViewById(getResources().getIdentifier(id, "id",getContext().getPackageName()));
        }

        redSelect.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                colorSelected[0] = 0;
                redSelect.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                greenSelect.setCardBackgroundColor(getResources().getColor(R.color.light_gray));
                blueSelect.setCardBackgroundColor(getResources().getColor(R.color.light_gray));
           //     setCheckBoxsColor(checkBoxs);
                if(index[0] == 2){
                    updateCheckBoxs(rgbColorValue[1][ colorSelected[0]]-1, checkBoxs, true);
                }else {
                    updateCheckBoxs(rgbColorValue[index[0]][ colorSelected[0]]-1, checkBoxs, true);
                }
                return false;
            }
        });

        greenSelect.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
               colorSelected[0] = 1;
                greenSelect.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                blueSelect.setCardBackgroundColor(getResources().getColor(R.color.light_gray));
                redSelect.setCardBackgroundColor(getResources().getColor(R.color.light_gray));
            //    setCheckBoxsColor(checkBoxs);
                if(index[0] == 2){
                    updateCheckBoxs(rgbColorValue[1][ colorSelected[0]]-1, checkBoxs, true);
                }else {
                    updateCheckBoxs(rgbColorValue[index[0]][ colorSelected[0]]-1, checkBoxs, true);
                }
                return false;
            }
        });

        blueSelect.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
               colorSelected[0] = 2;
                blueSelect.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                redSelect.setCardBackgroundColor(getResources().getColor(R.color.light_gray));
                greenSelect.setCardBackgroundColor(getResources().getColor(R.color.light_gray));
              //  setCheckBoxsColor(checkBoxs);
                if(index[0] == 2){
                    updateCheckBoxs(rgbColorValue[1][colorSelected[0]]-1, checkBoxs, true);
                }else {
                    updateCheckBoxs(rgbColorValue[index[0]][colorSelected[0]]-1, checkBoxs, true);
                }
                return false;
            }
        });


        final LinearLayout drt = rootView.findViewById(R.id.card_view3);
        final LinearLayout settingslayout = rootView.findViewById(R.id.SettingsLayout);
        final LinearLayout rgbCheckBoxes1 = rootView.findViewById(R.id.rgbCheckboxs1);
        final LinearLayout rgbCheckBoxes2 = rootView.findViewById(R.id.rgbCheckboxs2);
        final LinearLayout drt2 = rootView.findViewById(R.id.drt);


        final boolean[] state = new boolean[]{false};

        cards[2].post(new Runnable() {
            @Override
            public void run() {

                final int height = drt.getMeasuredHeight()+16;
                final int height2 = (colorModeCard.getMeasuredHeight());

                final ValueAnimator anim = ValueAnimator.ofInt(cards[2].getMeasuredWidth()*2, (cards[2].getMeasuredWidth()*4)+15);
                final ValueAnimator slideAnim = ValueAnimator.ofInt(height, 0);
                final ValueAnimator cardAnim = ValueAnimator.ofInt(height2, (int) (height2*2.1));

                anim.setDuration(250);
                slideAnim.setDuration(250);
                cardAnim.setDuration(250);


                final boolean[] showCheckBoxesAtTheEnd = {false};

                clockSelect.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        hsvSelect.setCardBackgroundColor(getResources().getColor(R.color.light_gray));
                        clockSelect.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));

                        rgbCheckBoxes1.setVisibility(View.GONE);
                        rgbCheckBoxes2.setVisibility(View.GONE);
                        rangeSlider[0].setVisibility(View.GONE);
                        rangeText.setVisibility(View.GONE);

                        if(index[0] == 1){
                            clockColor[0] = true;
                            clockSelected[0] = true;
                            lastHsvColoValue[0] = hsvColorValue[0];
                            hsvColorValue[0] = 0;
                            writeBytes((byte) 3,(byte) 2,(byte)(hsvColorValue[0]));
                            int col[] = setLedColor(2);
                            cards[1].setCardBackgroundColor(Color.rgb(col[0],col[1],col[2]));
                            handler[0].postDelayed(new Runnable() {
                                    public void run() {
                                        if(clockColor[0]){
                                            int col[] = setLedColor(2);
                                            cards[1].setCardBackgroundColor(Color.rgb(col[0],col[1],col[2]));
                                            handler[0].postDelayed(this,1000);
                                        }
                                    }
                                }, 1000);
                        }else{
                            clockColor[1] = true;
                            clockSelected[1] = true;
                            lastHsvColoValue[1] = hsvColorValue[1];
                            hsvColorValue[1] = 0;
                            writeBytes((byte) 3,(byte) 3,(byte)(hsvColorValue[1]));
                            int col[] = setLedColor(3);
                            cards[3].setCardBackgroundColor(Color.rgb(col[0],col[1],col[2]));
                            handler[0].postDelayed(new Runnable() {
                                public void run() {
                                    if(clockColor[1]){
                                        int col[] = setLedColor(2);
                                        cards[3].setCardBackgroundColor(Color.rgb(col[0],col[1],col[2]));
                                        handler[0].postDelayed(this,1000);
                                    }
                                }
                            }, 1000);
                        }
                     //   cardAnimWhenClickSelected.start();
                        cardAnim.reverse();
                        return false;
                    }
                });

                hsvSelect.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        hsvSelect.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        clockSelect.setCardBackgroundColor(getResources().getColor(R.color.light_gray));

                        if(clockSelected[0]) {

                          //  cardAnimWhenClickSelected.reverse();
                            showCheckBoxesAtTheEnd[0] = true;
                            cardAnim.start();
                            if(index[0] == 1){
                                hsvColorValue[0] = lastHsvColoValue[0];
                                clockColor[0] = false;
                                clockSelected[0] = false;
                                writeBytes((byte) 3,(byte) 2,(byte)(hsvColorValue[0]));
                                int col[] = setLedColor(2);
                                cards[1].setCardBackgroundColor(Color.rgb(col[0],col[1],col[2]));
                            }else{
                                hsvColorValue[1] = lastHsvColoValue[1];
                                clockColor[1] = false;
                                clockSelected[1] = true;
                                writeBytes((byte) 3,(byte) 3,(byte)(hsvColorValue[1]));
                                int col[] = setLedColor(3);
                                cards[3].setCardBackgroundColor(Color.rgb(col[0],col[1],col[2]));
                            }

                        }

                        if(index[0] == 1){
                            updateCheckBoxs(hsvColorValue[0]-1 ,checkBoxs,true);
                        }else{
                            updateCheckBoxs(hsvColorValue[1]-1 ,checkBoxs,true);
                        }
                        return false;
                    }
                });


                cardAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int val = (Integer) valueAnimator.getAnimatedValue();
                        ViewGroup.LayoutParams layoutParams  = colorModeCard.getLayoutParams();
                        if(clockSelected[0]){
                            layoutParams.height = val -(int) (height2*1.4) ;
                        }
                        layoutParams.height = val;
                        colorModeCard.setLayoutParams(layoutParams);
                    }
                });

                cardAnim.addListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        if(showCheckBoxesAtTheEnd[0] ) {
                            rgbCheckBoxes1.setVisibility(View.VISIBLE);
                           rgbCheckBoxes2.setVisibility(View.VISIBLE);
                           rangeSlider[0].setVisibility(View.VISIBLE);
                            rangeText.setVisibility(View.VISIBLE);
                            showCheckBoxesAtTheEnd[0] = false;
                        }
                    }

                });

                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int val = (Integer) valueAnimator.getAnimatedValue();
                        ViewGroup.LayoutParams layoutParams = cards[index[0]].getLayoutParams();
                        layoutParams.width = val;
                        cards[index[0]].setLayoutParams(layoutParams);


                    }
                });
                anim.addListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {

                        if(index[0] > 1){
                            drt2.animate().alpha(0).setDuration(100);
                        }else  if(!state[0]){

                                drt.setVisibility(View.INVISIBLE);

                        }
                        if(!state[0]){
                            cardAnim.start();
                        }else if(index[0] <=1){
                            settingslayout.setVisibility(View.INVISIBLE);
                                cardAnim.reverse();

                        }
                    }
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        if(state[0]){
                            state[0] = false;
                        }else{
                            state[0] = true;
                        }
                        if(index[0] > 1){
                           // drt2.setVisibility(View.GONE);
                           // drt2.animate().alpha(0).setDuration(200);\
                            if(!state[0]){
                                drt2.setVisibility(View.VISIBLE);
                               // settingslayout.setVisibility(View.INVISIBLE);
                                drt2.animate().alpha(1).setDuration(100);
                                slideAnim.reverse();
                                cardAnim.reverse();

                            }else{
                            //    settingslayout.setVisibility(View.VISIBLE);
                                drt2.animate().alpha(0).setDuration(100);
                                slideAnim.start();
                            //    cardAnim.start();
                            }
                        }else if(state[0]){
                           drt.setVisibility(View.GONE);
                            settingslayout.setVisibility(View.VISIBLE);
                  /*          redSelect.setVisibility(View.VISIBLE);
                            blueSelect.setVisibility(View.VISIBLE);
                            greenSelect.setVisibility(View.VISIBLE);
                            hsvSelect.setVisibility(View.GONE);
                            clockSelect.setVisibility(View.GONE);*/

                            if(index[0] ==0) {
                                redSelect.setVisibility(View.VISIBLE);
                                blueSelect.setVisibility(View.VISIBLE);
                                greenSelect.setVisibility(View.VISIBLE);
                                hsvSelect.setVisibility(View.GONE);
                                clockSelect.setVisibility(View.GONE);
                            }else{
                                redSelect.setVisibility(View.GONE);
                                blueSelect.setVisibility(View.GONE);
                                greenSelect.setVisibility(View.GONE);
                                hsvSelect.setVisibility(View.VISIBLE);
                                clockSelect.setVisibility(View.VISIBLE);
                            }
                        }else{
                            settingslayout.setVisibility(View.GONE);
                            drt.setVisibility(View.VISIBLE);
                        }

                    }
                });

                slideAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int val = (Integer) valueAnimator.getAnimatedValue();
                        ViewGroup.LayoutParams layoutParams  = drt2.getLayoutParams();
                        layoutParams.height = val;
                        drt2.setLayoutParams(layoutParams);
                    }
                });
                slideAnim.addListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        if(state[0]){
                            drt2.setVisibility(View.GONE);
                            settingslayout.setVisibility(View.VISIBLE);
                            if(index[0] ==2) {
                                redSelect.setVisibility(View.VISIBLE);
                                blueSelect.setVisibility(View.VISIBLE);
                                greenSelect.setVisibility(View.VISIBLE);
                                hsvSelect.setVisibility(View.GONE);
                                clockSelect.setVisibility(View.GONE);
                            }else{
                                redSelect.setVisibility(View.GONE);
                                blueSelect.setVisibility(View.GONE);
                                greenSelect.setVisibility(View.GONE);
                                hsvSelect.setVisibility(View.VISIBLE);
                                clockSelect.setVisibility(View.VISIBLE);
                            }

               /*             redSelect.setVisibility(View.GONE);
                            blueSelect.setVisibility(View.GONE);
                            greenSelect.setVisibility(View.GONE);
                            hsvSelect.setVisibility(View.VISIBLE);
                            clockSelect.setVisibility(View.VISIBLE);*/
                        }else{
                            settingslayout.setVisibility(View.GONE);
                        }
                    }

                });

                for(int i = 0; i < colorModes.length; i++) {
                    final int finalI = i;
                    colorModes[i].setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            index[0] = finalI;
                            if(!state[0]) {
                                textL[finalI].setVisibility(View.GONE);
                                arrows[finalI].setVisibility(View.VISIBLE);
                                if(index[0] ==0){
                                    updateCheckBoxs(rgbColorValue[index[0]][ colorSelected[0]]-1, checkBoxs, true);
                                }else if(index[0] == 2) {
                                    updateCheckBoxs(rgbColorValue[1][ colorSelected[0]]-1, checkBoxs, true);
                                }else if(index[0] == 1){
                                        updateCheckBoxs(hsvColorValue[0]-1, checkBoxs, true);
                                        if(!clockSelected[0]){
                                            hsvSelect.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                            clockSelect.setCardBackgroundColor(getResources().getColor(R.color.light_gray));
                                        }else{
                                            hsvSelect.setCardBackgroundColor(getResources().getColor(R.color.light_gray));
                                            clockSelect.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                        }

                                }else{
                                    updateCheckBoxs(hsvColorValue[1]-1, checkBoxs, true);
                                    if(!clockSelected[1]){
                                        hsvSelect.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                        clockSelect.setCardBackgroundColor(getResources().getColor(R.color.light_gray));
                                    }else{
                                        hsvSelect.setCardBackgroundColor(getResources().getColor(R.color.light_gray));
                                        clockSelect.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                    }
                                }
                                anim.start();
                            }else{
                                textL[finalI].setVisibility(View.VISIBLE);
                                arrows[finalI].setVisibility(View.GONE);
                                anim.reverse();
                            }

                            return false;
                        }
                    });
                }


            }
        });


        brightnessSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brightnessValue.setText(String.valueOf(progress)+"%");
           //     writeBytes((byte) 1, (byte) seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                writeBytes((byte) 1, (byte) seekBar.getProgress());
            //    writeData((byte) 1, (byte) seekBar.getProgress());
                // bluetooth.sendLedData(seekBar.getProgress()+6,getContext());
            }
        });

        for(int k = 0; k < colorModesSelects.length;k++){
            final int finalK = k;
            colorModesSelects[k].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(!state[0]) {
                        updateCards(finalK ,colorModesSelects);
                        if(clockSelected[0]){
                          //  write((byte) 3);
                        }else{
                            switch(finalK){
                                case 0: writeBytes((byte) 2, (byte) 0); break;
                                case 1:  writeBytes((byte) 2, (byte) 2); break;
                                case 3:  writeBytes((byte) 2, (byte) 3); break;
                                case 2:  writeBytes((byte) 2, (byte) 1); break;
                            }

                        }

                    }
                    return false;
                }
            });
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    private void sendSendString(int value){
    //    if(btConnected){
            try {
                bluetooth.sendString(String.valueOf(value));

            } catch (IOException e) {
                e.printStackTrace();
            }
     /*   }else {
            Toast.makeText(getContext(), "bluetooth not connected", Toast.LENGTH_LONG).show();
        }*/
    }


    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if(bluetooth!= null) {
            if (visible) {
       //        bluetooth.connect(progressBar);
            } else {
        //       bluetooth.disconnect();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            Log.d("GGG","visible");
        }
    }

    private void updateCards(int index,ImageView ... ImageViews){
        for(int k =0; k < ImageViews.length;k++){
            if(k == index){
              //  ImageViews[k].setImageDrawable(getResources().getDrawable(R.drawable.ic_select_24px));
                ImageViews[k].animate().scaleY(1).setDuration(100);
                ImageViews[k].animate().scaleX(1).setDuration(100);

            }else {
             //   ImageViews[k].setVisibility(View.INVISIBLE);
                ImageViews[k].animate().scaleY(0).setDuration(100);
                ImageViews[k].animate().scaleX(0).setDuration(100);
             //   ImageViews[k].setImageDrawable(null);


            }
        }
    }

    public void updateCheckBoxs(int index,CheckBox checkBoxes[],boolean st){
        for(int k =0; k < checkBoxes.length;k++){
            if(k == index){

                    if(st) {
                        checkBoxes[k].setChecked(true);
                    }

                int dt = 10;
                for(int g = 0; g < 2;g++) {
                    maximaSelectedIndex = k;
                    rangeSlider[0].setMax(maxima[k][0] + dt);
                    if (index < 5) {
                        rangeSlider[0].setMin(maxima[k][1] - dt);
                    } else {
                        rangeSlider[0].setMin(maxima[k][1]);
                    }
                    rangeSlider[0].getThumb(0).setValue(maxima[k][1]);
                    rangeSlider[0].getThumb(1).setValue(maxima[k][0]);
                }



             //   rangeSlider[0].setPro
         //       checkBoxes[k].setChecked(true);
            }else {
                checkBoxes[k].setChecked(false);
            }
        }
    }
    int maxima[][]= new int[8][2];
    void setCardColor(int index, byte val[]){
        float h = 0.25f;
        cards[index].setCardBackgroundColor(Color.rgb(val[0],val[1],val[2]));
        colorModes[0].setBackgroundColor(Color.rgb((int)((val[0])*h),(int)(val[1]*h),(int)(val[2]*h)));
    }
    void setCardColor(int index){
        int col[];
        switch(index){
            case 0:
                col = setLedColor(0);
                cards[0].setCardBackgroundColor(Color.rgb(col[0],col[1],col[2]));
                break;
            case 1:
                col = setLedColor(1);
                cards[2].setCardBackgroundColor(Color.rgb(col[0],col[1],col[2]));
                break;
            case 2:
                col = setLedColor(2);
                cards[1].setCardBackgroundColor(Color.rgb(col[0],col[1],col[2]));
                break;
            case 3:
                col = setLedColor(3);
                cards[3].setCardBackgroundColor(Color.rgb(col[0],col[1],col[2]));
                break;


        }

    }

    int[] setLedColor(int _mode){
        int r,g,b;
        int result[] = null;
        switch(_mode){
            case 0:
                r = getColor(rgbColorValue[0][0],false);
                g = getColor(rgbColorValue[0][1],false);
                b = getColor(rgbColorValue[0][2],false);
                result =  new int[]{r,g,b};
             break;
            case 1:
                r = getColor(rgbColorValue[1][0],false);
                g = getColor(rgbColorValue[1][1],false);
                b = getColor(rgbColorValue[1][2],false);
                result =  new int[]{r,g,b};
                break;
            case 2:

              //  Log.d("GGG",String.valueOf(val[39]));
                if( hsvColorValue[0] !=0){
                    result= HSV_to_RGB(265 -getColor( hsvColorValue[0],true),100,100);
                }else{
                    Calendar rightNow = Calendar.getInstance();
                    int hour = rightNow.get(Calendar.HOUR_OF_DAY);
                    int minute = rightNow.get(Calendar.MINUTE);
                    int second = rightNow.get(Calendar.SECOND);
                    r = map(hour,0,23,0,255);
                    g = map(minute,0,59,0,255);
                    b = map(second,0,59,0,255);
                    result =  new int[]{r,g,b};
                }
                break;
            case 3:
                if( hsvColorValue[1] !=0){
                    result = HSV_to_RGB(265 -getColor( hsvColorValue[1],true),100,100);
                }else{
                    Calendar rightNow = Calendar.getInstance();
                    int hour = rightNow.get(Calendar.HOUR_OF_DAY);
                    int minute = rightNow.get(Calendar.MINUTE);
                    int second = rightNow.get(Calendar.SECOND);
                    r = map(hour,0,23,0,255);
                    g = map(minute,0,59,0,255);
                    b = map(second,0,59,0,255);
                    result =  new int[]{r,g,b};
                }
                break;
        }
/*        Log.d("GGG1",String.valueOf(result[0]/255.0f));
        Log.d("GGG2",String.valueOf(result[1]/255.0f));
        Log.d("GGG3",String.valueOf(result[2]/255.0f));*/
        return result;
    }


     void updateUI(int[] val){
        float h = 0.25f;
        byte b = (byte) 255;
         brightnessSlider.setProgress(val[0]);
         colorModesSelects[val[1]].setScaleX(1);
         colorModesSelects[val[1]].setScaleY(1);

         switch(val[1]){
             case 0: updateCards(0,colorModesSelects); break;
             case 2:  updateCards(1,colorModesSelects); break;
             case 3:   updateCards(3,colorModesSelects); break;
             case 1: updateCards(2,colorModesSelects); break;
         }

         temp = val[29] + (val[30]/100.0f);
         hum = val[31] + (val[32]/100.0f);
         pres = val[33] + (val[34]/100.0f) +900;
         tempO = val[35] + (val[36]/100.0f) ;
         humO = (byte) val[37];
         currentWind = (byte) val[38];
         averangeWind = val[39] + (val[40]/100.0f) ;
         rain= (byte) val[41];


         maxima[0][0] = val[2];
         maxima[0][1] = val[3];
         maxima[1][0] = val[4];
         maxima[1][1] = val[5];
         maxima[2][0] = val[6]+900;
         maxima[2][1] = val[7]+900;
         maxima[3][0] = val[8];
         maxima[3][1] = val[9];
         maxima[4][0] = val[10];
         maxima[4][1] = val[11];
         maxima[5][0] = val[12];
         maxima[5][1] = 0;
         maxima[6][0] =  val[13];
         maxima[6][1] =  0;
         maxima[7][0] =  val[14];
         maxima[7][1] =  0;


         rgbColorValue[0][0] = val[20];
         rgbColorValue[0][1] = val[21];
         rgbColorValue[0][2] = val[22];
         rgbColorValue[1][0] = val[23];
         rgbColorValue[1][1] = val[24];
         rgbColorValue[1][2] = val[25];
         hsvColorValue[0] = val[26];
         hsvColorValue[1] = val[27];

         colorTextVievs[4].setText(getColorName(hsvColorValue[0]));


         colorTextVievs[10].setText(getColorName(hsvColorValue[1]));



         colorMode1TextUpdate();
         colorMode2TextUpdate();


         setCardColor(0);
         setCardColor(1);
         setCardColor(2);
         setCardColor(3);

         colorModes[0].setBackgroundColor(Color.rgb((int)((val[2])*h),(int)(val[3]*h),(int)(val[4]*h)));
         colorModes[1].setBackgroundColor(Color.rgb((int)(val[5]*h),(int)(val[6]*h),(int)(val[7]*h)));
         colorModes[2].setBackgroundColor(Color.rgb((int)(val[8]*h),(int)(val[9]*h),(int)(val[10]*h)));
         colorModes[3].setBackgroundColor(Color.rgb((int)(val[11]*h),(int)(val[12]*h),(int)(val[13]*h)));

         sendingIntervalSlider.setProgress(val[15]);
         if(0 != val[16]) {
             oversamplingSlider.setProgress(val[15] * 10 / val[16]);
         }

     }


    float tempO =0;
    byte humO = 0;
    float averangeWind =0;
    byte currentWind = 0;
    float temp = 0;
    float hum = 0;
    float pres = 0;
    byte rain =0;


    int getColor(int value,boolean hsv){
        int m;
        int k = 10;
        if(hsv){
            m = 265;
        }else{
            m =254;
        }
        switch(value){
            case 3: return map((int) (pres*k),maxima[2][0]*k,maxima[2][1]*k,m,0);
            case 1: return map((int) (temp*k),maxima[0][0]*k,maxima[0][1]*k,m,0);
            case 2: return map((int) (hum*k),maxima[1][0]*k,maxima[1][1]*k,m,0);
            case 4: return map((int) (tempO*k),maxima[3][0]*k,maxima[3][1]*k,m,0);
            case 5: return map(humO,maxima[4][1],maxima[4][0],0,m);
            //  case 6: return map(((currentWind + 0.5*lastCurrentWind)/1.5f)*k,0,maxWind*k,m,0);
            case 7: return map(currentWind*k,0,maxima[6][0]*k,0,m);
            case 6: return map((int) (averangeWind*k),0,maxima[5][0]*k,0,m);
            case 8: return map(rain,0,maxima[7][0],0,m);
            default: return m;
        }
    }
    int map(int x, int in_min, int in_max, int out_min, int out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }


    int[] HSV_to_RGB(float h, float s, float v)
    {
        float f,p,q,t;
        int i;
        int range = 255;
        int r,g,b;
        h = (float) Math.max(0.0, Math.min(360.0, h));
        s = (float) Math.max(0.0, Math.min(100.0, s));
        v = (float) Math.max(0.0, Math.min(100.0, v));

        s /= 100;
        v /= 100;

     /*   if(s == 0) {
            // Achromatic (grey)
            r = g = b = round(v*range);
            return;
        }*/

        h /= 60; // sector 0 to 5
        i = (int) Math.floor(h);
        f = h - i; // factorial part of h
        p = v * (1 - s);
        q = v * (1 - s * f);
        t = v * (1 - s * (1 - f));
        switch(i) {
            case 0:
                r = Math.round(range*v);
                g = Math.round(range*t);
                b = Math.round(range*p);
                break;
            case 1:
                r = Math.round(range*q);
                g = Math.round(range*v);
                b = Math.round(range*p);
                break;
            case 2:
                r = Math.round(range*p);
                g = Math.round(range*v);
                b = Math.round(range*t);
                break;
            case 3:
                r = Math.round(range*p);
                g = Math.round(range*q);
                b = Math.round(range*v);
                break;
            case 4:
                r = Math.round(range*t);
                g = Math.round(range*p);
                b = Math.round(range*v);
                break;
            default: // case 5:
                r = Math.round(range*v);
                g = Math.round(range*p);
                b = Math.round(range*q);
        }
        int result[] =  new int[]{r,g,b};
        return result;
    }


    void writeBytes(byte ... bytes){
        try {
           // for(byte b:bytes){
                bluetooth.writeBytes(bytes);
         //   }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void colorMode1TextUpdate(){
        colorTextVievs[0].setText("R: " +getColorName(rgbColorValue[0][0]));
        colorTextVievs[1].setText("G: " +getColorName(rgbColorValue[0][1]));
        colorTextVievs[2].setText("B: " +getColorName(rgbColorValue[0][2]));
    }

    void colorMode2TextUpdate(){
        colorTextVievs[6].setText("R: " +getColorName(rgbColorValue[1][0]));
        colorTextVievs[7].setText("G: " +getColorName(rgbColorValue[1][1]));
        colorTextVievs[8].setText("B: " +getColorName(rgbColorValue[1][2]));
    }

    void write(byte b){
        try {
            // for(byte b:bytes){
            bluetooth.write(b);
            //   }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    String getColorName(int index){
         switch(index){
             case 0: return "Clock";
             case 3: return "Pressure";
             case 1: return "Temperature •";
             case 2: return "Humidity • ";
             case 4: return "Temperature";
             case 5: return "Humidity";
             case 7: return "Wind";
             case 6: return "Averange Wind ";
             case 8: return "Rain ";
             default: return "";
         }
    }

    public void setListener(WeatherDataController weatherDataController){
        weatherDataController.setOnDataArrivedListener(new WeatherDataController.OnDataArrivedListener() {
            @Override
            public void dataArrived(ArrayList<Entry>[] result) {
                dataArrived[0] = true;
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


    @Override
    public void connected(int[] result) {
        //brightnessSlider.setProgress(result[0]);
        if(result != null) {
            Log.d("GGG", String.valueOf(result.length));
            Log.d("GGG", String.valueOf(result[0]));
            Log.d("GGG", String.valueOf(result[1]));
            Log.d("GGG", String.valueOf(result[2]));
            Log.d("GGG", String.valueOf(result[3]));
            Log.d("GGG", String.valueOf(result[4]));

            updateUI(result);
        }
    }
    private void msg(final String s) {
        Handler mainHandler = new Handler(getContext().getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
            }
        };
        mainHandler.post(myRunnable);

    }
}
