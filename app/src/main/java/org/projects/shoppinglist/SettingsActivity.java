package org.projects.shoppinglist;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by tobias on 05-04-2016.
 */
public class SettingsActivity extends PreferenceActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager manager = getPreferenceManager();
        manager.setSharedPreferencesName("settings_pref");
        addPreferencesFromResource(R.xml.prefs);
    }
}
