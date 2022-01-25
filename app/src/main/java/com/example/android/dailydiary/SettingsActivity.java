package com.example.android.dailydiary;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class NamePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference nameSetting = findPreference(getString(R.string.settings_name_key));
            nameSetting.setOnPreferenceChangeListener(this);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(nameSetting.getContext());
            String preferenceString = preferences.getString(nameSetting.getKey(), "");
            onPreferenceChange(nameSetting, preferenceString);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            preference.setSummary(value.toString());
            return true;
        }
    }
}