package com.andrgree.lifestat.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.TimePicker;

import com.andrgree.lifestat.R;
import com.andrgree.lifestat.preference.DatePreference;
import com.andrgree.lifestat.preference.TimePreference;

/**
 * Created by grag on 12/1/15.
 */
public class MainSettingFragment extends PreferenceFragment {
    CheckBoxPreference notifyUser;
    ListPreference countNotification;
    PreferenceCategory notificatonTimeList;
    EditTextPreference nameUser;
    DatePreference birthDate;

    private void refreshNotificationFields() {
        notifyUser = (CheckBoxPreference) findPreference("pref_notify_user");
        countNotification  = (ListPreference) findPreference("pref_count_notification");
        notificatonTimeList  = (PreferenceCategory) findPreference("pref_notificaton_time_list");
        birthDate  = (DatePreference) findPreference("pref_birth_date");
        countNotification.setEnabled(notifyUser.isChecked());
        notificatonTimeList.setEnabled(notifyUser.isChecked());
        countNotification.setSummary(countNotification.getValue());
        birthDate.setSummary(DatePreference.formatter().format(birthDate.getDate().getTime()));
        Integer count = Integer.parseInt(countNotification.getValue());
        for (int i = 1; i <= 5; i++) {
            TimePreference time = (TimePreference) findPreference("pref_time_" + i);
            time.setEnabled(notifyUser.isChecked() && i <= count);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preference, false);
        for (int i = 1; i <= 5; i++) {
            TimePreference time = (TimePreference) findPreference("pref_time_" + i);
            time.setTitle(time.getTitle() + " " + i);
        }
        refreshNotificationFields();

        notifyUser.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                countNotification.setEnabled(notifyUser.isChecked());
                refreshNotificationFields();
                return false;
            }
        });

        countNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                for (int i = 1; i <= 5; i++) {
                    TimePreference time = (TimePreference) findPreference("pref_time_" + i);
                    time.setEnabled(false);
                }
                Integer count = Integer.parseInt(newValue.toString());
                countNotification.setSummary(newValue.toString());
                for (int i = 1; i <= count; i++) {
                    TimePreference time = (TimePreference) findPreference("pref_time_" + i);
                    time.setEnabled(true);
                }
                return true;
            }
        });

        nameUser = (EditTextPreference) findPreference("pref_user_name");
        nameUser.setSummary(nameUser.getText());
        nameUser.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                nameUser.setSummary(newValue.toString());
                return true;
            }
        });
    }

}
