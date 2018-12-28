package com.horizon.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.horizon.R;

public class bookshelf_fragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View messageLayout = inflater.inflate(R.layout.shelf_layout, container, false);
        //ImageButton PresentLoc = (ImageButton) messageLayout.findViewById(R.id.addbook); //此处使得Button和xml中的按钮联系
        //PresentLoc.setOnClickListener(new LocationCheckedListener());
        return messageLayout;
    }

    /**class LocationCheckedListener implements View.OnClickListener {

      public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), HomeFilebrowsing.class); //从前者跳到后者，特别注意的是，在fragment中，用getActivity()来获取当前的activity
            getActivity().startActivity(intent);
            }
        }**/
    }

