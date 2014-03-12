package com.mike.pages;

import com.mike.vcardparsingapplication.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HistoryFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View webserviceview = inflater.inflate(R.layout.historyfragment,
				container, false);

		return webserviceview;

	}
}
