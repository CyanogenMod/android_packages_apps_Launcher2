/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher2.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.android.launcher.R;

public class Preferences extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String TAG = "Launcher.Preferences";
    private static final String HOMESCREEN_COUNT = "ui_homescreen_general_count";
    private ListPreference mHomescreenCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        PreferenceScreen prefSet = getPreferenceScreen();
        mHomescreenCount = (ListPreference) prefSet.findPreference(HOMESCREEN_COUNT);
        mHomescreenCount.setOnPreferenceChangeListener(this);
        setPrefSummary(mHomescreenCount,getResources().getString(R.string.preferences_interface_homescreen_general_count_summary) + mHomescreenCount.getEntry());
        SharedPreferences prefs =
            getSharedPreferences(PreferencesProvider.PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(PreferencesProvider.PREFERENCES_CHANGED, true);
                editor.commit();
    }

    private void setPrefSummary(Preference preference, String summary) {
        preference.setSummary(summary);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if ( preference == mHomescreenCount) {
            setPrefSummary(mHomescreenCount,getResources().getString(
                    R.string.preferences_interface_homescreen_general_count_summary) + newValue.toString());
            return true;
        }
        return false;
    }
}
