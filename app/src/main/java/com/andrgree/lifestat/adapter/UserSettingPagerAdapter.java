package com.andrgree.lifestat.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.andrgree.lifestat.R;
import com.andrgree.lifestat.fragment.OtherSettingFragment;
import com.andrgree.lifestat.fragment.ParamSettingFragment;


/**
 * Created by grag on 11/27/15.
 *
 * Адаптер страниц настроек пользователя
 */
public class UserSettingPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    @Override
    //Получение элемента
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return OtherSettingFragment.init(position);
            case 1: return ParamSettingFragment.init(position);
        }
        return null;
    }

    // Инициализация
    public UserSettingPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    // Возвращение общего числа табов
    public int getCount() {
        return 2;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        Fragment fragment = getItem(position);
        switch (position) {
            case 0:
                return context.getText(R.string.other_setting_title);
            case 1:
                return context.getText(R.string.param_setting_title);
        }

        return "Page " + (position + 1);
    }
}
