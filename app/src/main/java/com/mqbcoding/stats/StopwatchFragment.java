package com.mqbcoding.stats;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.github.anastr.speedviewlib.Speedometer;
import com.github.anastr.speedviewlib.components.Indicators.ImageIndicator;
import com.google.android.apps.auto.sdk.StatusBarController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StopwatchFragment extends CarFragment {
    private String selectedFont;
    private final String TAG = "StopwatchFragment";

    TextView textSwTimer;
    TextView textSeconds;
    TextView textTimer;


    Button btnStart, btnPause, btnReset, btnLap;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    Handler handler;
    int Seconds, Minutes, MilliSeconds, Laps ;
    long Hours;
    ListView listView ;
    String[] ListElements = new String[] {  };
    private Speedometer mStopwatch;


    List<String> ListElementsArrayList ;

    ArrayAdapter<String> adapter ;

    public StopwatchFragment() {
        // Required empty public constructor
    }

    @Override
    public void setupStatusBar(StatusBarController sc) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach");

        setTitle(getContext().getString(R.string.activity_stopwatch_title));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_stopwatch, container, false);

//Get shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        selectedFont = sharedPreferences.getString("selectedFont", "segments");


        //set textview to have a custom digital font:
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "digital.ttf");
        switch(selectedFont){
            case "segments":
                typeface = Typeface.createFromAsset(getContext().getAssets(), "digital.ttf");
                break;
            case "seat":
                typeface = Typeface.createFromAsset(getContext().getAssets(), "SEAT_MetaStyle_MonoDigit_Regular.ttf");
                break;
            case "audi":
                typeface = Typeface.createFromAsset(getContext().getAssets(), "AudiTypeDisplayHigh.ttf");
                break;
            case "vw":
                typeface = Typeface.createFromAsset(getContext().getAssets(), "VWTextCarUI-Regular.ttf");
                break;
            case "vw2":
                typeface = Typeface.createFromAsset(getContext().getAssets(), "VWThesis_MIB_Regular.ttf");
                break;
            case "frutiger":
                typeface = Typeface.createFromAsset(getContext().getAssets(), "Frutiger.otf");
                break;
            case "vw3":
                typeface = Typeface.createFromAsset(getContext().getAssets(), "VW_Digit_Reg.otf");
                break;
            case "skoda":
                typeface = Typeface.createFromAsset(getContext().getAssets(), "Skoda.ttf");
                break;
            case "larabie":
                typeface = Typeface.createFromAsset(getContext().getAssets(), "Larabie.ttf");
                break;
        }


        textSwTimer= rootView.findViewById(R.id.textSwTimer2);
        textSeconds = rootView.findViewById(R.id.textSeconds);
        textTimer = rootView.findViewById(R.id.textTimer);
        btnStart=rootView.findViewById(R.id.imgbtnSwStart);
        btnReset=rootView.findViewById(R.id.imgbtnSwReset);
        btnPause=rootView.findViewById(R.id.imgbtnSwPause);
        btnLap=rootView.findViewById(R.id.imgbtnSwSaveLap);
        listView=rootView.findViewById(R.id.listSwLaps);
        Laps = 1;
        mStopwatch = rootView.findViewById(R.id.dialMeasStopWatch);

        textSwTimer.setTypeface(typeface);
        textSeconds.setTypeface(typeface);
        textTimer.setTypeface(typeface);



//todo: make stopwatch clocks identical in styling, to the dashboardfragment

        mStopwatch = rootView.findViewById(R.id.dialMeasStopWatch);
        mStopwatch.setSpeedTextTypeface(typeface);

        mStopwatch.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mStopwatch.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int clockSize = mStopwatch.getHeight();
                if (clockSize==0){
                    clockSize=250;
                }
                //this is to enable an image as indicator.
                TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.themedNeedle});
                int resourceId = typedArray.getResourceId(0, 0);
                typedArray.recycle();

                ImageIndicator imageIndicator = new ImageIndicator(getContext(), resourceId, clockSize, clockSize);

                //give clocks a custom image indicator
                mStopwatch.setIndicator(imageIndicator);
            }

        });

        mStopwatch.speedPercentTo(100,5000);





        handler = new Handler();

        ListElementsArrayList = new ArrayList<String>(Arrays.asList(ListElements));

        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, ListElementsArrayList
        );

        listView.setAdapter(adapter);
        btnStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                StartTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable,0);
                btnStart.setVisibility(View.INVISIBLE);
                btnPause.setVisibility(View.VISIBLE);
                btnReset.setVisibility(View.INVISIBLE);
            //    reset.setEnabled(false);
                btnLap.setVisibility(View.VISIBLE);
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimeBuff += MillisecondTime;

                handler.removeCallbacks(runnable);

                //reset.setEnabled(true);
                btnReset.setVisibility(View.VISIBLE);

                btnPause.setVisibility(View.INVISIBLE);
                btnStart.setVisibility(View.VISIBLE);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MillisecondTime = 0L ;
                StartTime = 0L ;
                TimeBuff = 0L ;
                UpdateTime = 0L ;
                Seconds = 0 ;
                Minutes = 0 ;
                Hours = 0 ;
                MilliSeconds = 0 ;
                Laps = 1;

                textSwTimer.setText("00:00");
                textSeconds.setText("00");
                mStopwatch.speedTo(0,1000);
                ListElementsArrayList.clear();
                textTimer.setText("00:00:00:000");

                adapter.notifyDataSetChanged();
            }
        });


        btnLap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ListElementsArrayList.add(Laps+": "+ ((textTimer.getText().toString())));
                Laps = Laps + 1;
                adapter.notifyDataSetChanged();

            }
        });


        return rootView;
    }

    private static String formatInterval(final long millis) {
        final long hr = TimeUnit.MILLISECONDS.toHours(millis);
        final long min = TimeUnit.MILLISECONDS.toMinutes(millis - TimeUnit.HOURS.toMillis(hr));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(millis - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
        final long ms = TimeUnit.MILLISECONDS.toMillis(millis - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));
        return String.format("%02d:%02d:%02d.%03d", hr, min, sec, ms);
    }

    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Minutes = Seconds / 60;

            Hours = TimeUnit.MILLISECONDS.toHours(UpdateTime);

            Seconds = Seconds % 60;

            MilliSeconds = (int) (UpdateTime % 1000);

            textSwTimer.setText((String.format("%02d", Hours)) +":" +(String.format("%02d", Minutes)));
            textSeconds.setText(String.format("%02d", Seconds));
            textTimer.setText(formatInterval(UpdateTime));
            mStopwatch.speedTo(Seconds);

            handler.postDelayed(this, 0);
        }

    };

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDetach() {
        Log.i(TAG, "onDetach");
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }


}