package com.andrgree.lifestat.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.andrgree.lifestat.R;

/**
 * Created by grag on 12/1/15.
 *
 * Информацию о дне (описание лунного дня, зодиакального дня и т.д.)
 */
public class DayDetailFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day_detail, container, false);

        ScrollView scroller = new ScrollView(getActivity());
        scroller.addView(view);
        return scroller;
    }
}
