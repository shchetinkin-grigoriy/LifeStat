package com.andrgree.lifestat.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrgree.lifestat.R;

/**
 * Created by grag on 12/1/15.
 *
 * Класс для сохранения остальных настроек пользователя
 */
public class OtherSettingFragment extends Fragment {

    public static OtherSettingFragment init(int val) {
        OtherSettingFragment settingFragment = new OtherSettingFragment();
        Bundle args = new Bundle();
        args.putInt("val", val);
        settingFragment.setArguments(args);

        return settingFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_other_setting, container, false);

        Bundle args = getArguments();
        //((TextView) rootView.findViewById(R.id.textView)).setText("Page " + args.getInt("page_position"));

        return rootView;
    }
}
