package com.aatorque.stats;

import android.content.Context;
import android.os.Bundle;
import timber.log.Timber;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.apps.auto.sdk.StatusBarController;

public class CreditsFragment extends CarFragment {

    public CreditsFragment() {
        // Required empty public constructor
    }

    @Override
    public void setupStatusBar(StatusBarController sc) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Timber.i("onAttach");

        setTitle(getContext().getString(R.string.activity_credits_title));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.i("onCreateView");
        return inflater.inflate(R.layout.fragment_credits, container, false);



    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.i("onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.i("onResume");
    }

    @Override
    public void onPause() {
        Timber.i("onPause");
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        Timber.i("onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        Timber.i("onStop");
        super.onStop();
    }

    @Override
    public void onDetach() {
        Timber.i("onDetach");
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        Timber.i("onDestroy");
        super.onDestroy();
    }
}