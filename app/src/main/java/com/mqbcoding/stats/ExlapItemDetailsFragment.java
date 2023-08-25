package com.mqbcoding.stats;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.martoreto.aauto.vex.CarStatsClient;
import com.github.martoreto.aauto.vex.FieldSchema;
import com.google.android.apps.auto.sdk.StatusBarController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ExlapItemDetailsFragment extends CarFragment {

    public static final String ARG_SCHEMA = "schema";
    public static final String ARG_SELECTED_KEY = "selectedKey";

    private final String TAG = "ExlapDetailsFragment";
    private CarStatsClient mStatsClient;
    private Timer updateTimer;
    private Handler mHandler;
    private HashMap<String, FieldSchema> mSchema;
    private TextView tvCurrentValue, tvName, tvMin, tvMax, tvRes, tvDescr,
            tvMinUnit, tvMaxUnit, tvResUnit, tvCurrentUnit;
    private Runnable updateTimerRunnable;
    private String currentValue = "";
    private String selectedKey;

    public ExlapItemDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void setupStatusBar(StatusBarController sc) {

    }

    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_exlap_item_details, container, false);
        Button btn = rootView.findViewById(R.id.items_fragment_back_btn);
        tvName = rootView.findViewById(R.id.fragment_item_details_tv_itemName_value);
        tvMin = rootView.findViewById(R.id.fragment_item_details_tv_minVal_value);
        tvMax = rootView.findViewById(R.id.fragment_item_details_tv_maxVal_value);
        tvRes = rootView.findViewById(R.id.fragment_item_details_tv_resVal_value);
        tvDescr = rootView.findViewById(R.id.fragment_item_details_tv_descr_value);
        tvCurrentValue = rootView.findViewById(R.id.fragment_item_details_tv_currentValue_value);
        tvMinUnit = rootView.findViewById(R.id.fragment_item_details_tv_minVal_unit);
        tvMaxUnit = rootView.findViewById(R.id.fragment_item_details_tv_maxVal_unit);
        tvResUnit = rootView.findViewById(R.id.fragment_item_details_tv_resVal_unit);
        tvCurrentUnit = rootView.findViewById(R.id.fragment_item_details_tv_currentValue_unit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        mHandler = new Handler(Looper.getMainLooper());
        Bundle args = getArguments();
        selectedKey = args.getString(ARG_SELECTED_KEY);

        mSchema = (HashMap<String, FieldSchema>) args.getSerializable(ARG_SCHEMA);
        fillTextViews();
        tvName.setSelected(true);

        return rootView;
    }

    private void fillTextViews() {
        tvCurrentValue.setText(currentValue);
        if (mSchema.containsKey(selectedKey)) {
            FieldSchema field = mSchema.get(selectedKey);
            tvName.setText(selectedKey);
            tvMin.setText(String.valueOf(field.getMin()));
            tvMax.setText(String.valueOf(field.getMax()));
            tvRes.setText(String.valueOf(field.getResolution()));
            tvDescr.setText(String.valueOf(field.getDescription()));
            tvMinUnit.setText(field.getUnit());
            tvMaxUnit.setText(field.getUnit());
            tvResUnit.setText(field.getUnit());
            tvCurrentUnit.setText(field.getUnit());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onActivate");
        Intent serviceIntent = new Intent(getContext(), CarStatsService.class);
        getContext().bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        createAndStartUpdateTimer();
    }

    private void createAndStartUpdateTimer() {
        updateTimer = new Timer();
        updateTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                updateTimerRunnable = new Runnable() {
                    public void run() {
                        fillTextViews();
                    }
                };
                //experimental delay
                if (mHandler != null)
                    mHandler.postDelayed(updateTimerRunnable, 1);
            }

        }, 0, 250);//Update display 0,25 second
    }

    @Override
    public void onPause() {
        mStatsClient.unregisterListener(mCarStatsListener);
        getContext().unbindService(mServiceConnection);
        updateTimer.cancel();
        Log.i(TAG, "onDeactivate");
        super.onPause();
    }

    private final CarStatsClient.Listener mCarStatsListener = new CarStatsClient.Listener() {
        @Override
        public void onNewMeasurements(String provider, Date timestamp, final Map<String, Object> values) {
            if (values.containsKey(selectedKey)) {
                currentValue = String.valueOf(values.get(selectedKey));
            }
        }

        @Override
        public void onSchemaChanged() {
            mSchema.putAll(mStatsClient.getSchema());
        }
    };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG, "ServiceConnected");
            CarStatsService.CarStatsBinder carStatsBinder = (CarStatsService.CarStatsBinder) iBinder;
            mStatsClient = carStatsBinder.getStatsClient();
            mStatsClient.registerListener(mCarStatsListener);
            Map<String, Object> currentValues = mStatsClient.getMergedMeasurements();
            if (currentValues.containsKey(selectedKey)) {
                currentValue = (String)currentValues.get(selectedKey);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG, "ServiceDisconnected");
            mStatsClient.unregisterListener(mCarStatsListener);
        }
    };
}
