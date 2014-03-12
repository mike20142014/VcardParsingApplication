package com.mike.pages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mike.vcardparsingapplication.R;

public class MainFragment extends Fragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View webserviceview = inflater.inflate(R.layout.mainfragment, container, false);

        return webserviceview;

    }
	
}
