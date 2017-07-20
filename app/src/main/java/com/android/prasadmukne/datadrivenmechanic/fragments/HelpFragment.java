package com.android.prasadmukne.datadrivenmechanic.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.prasadmukne.datadrivenmechanic.R;

/**
 * Created by prasad.mukne on 7/14/2017.
 */

public class HelpFragment extends Fragment
{

	public static HelpFragment newInstance(String argument)
	{
		HelpFragment fragment = new HelpFragment();
		Bundle args = new Bundle();
		//args.putString(PAYMENT_MODE, param1);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View rootView = inflater.inflate(R.layout.fragment_help_screen, container, false);
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.help);
		try
		{
			//initialiseAndSetUI(rootView);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return rootView;
	}
}
