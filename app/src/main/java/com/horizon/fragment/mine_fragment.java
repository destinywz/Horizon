package com.horizon.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.horizon.R;

public class mine_fragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View contactsLayout = inflater.inflate(R.layout.mine_layout, container, false);
        return contactsLayout;
    }

}