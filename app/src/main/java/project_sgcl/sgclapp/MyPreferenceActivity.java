package project_sgcl.sgclapp;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import java.util.List;
import java.util.prefs.Preferences;

/**
 * User: Joaquín & María * Date: 17/03/15
 * Time: 10:16
 */


public class MyPreferenceActivity extends  PreferenceActivity {

    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.app_prefs);

    }

    public static class PrefsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.app_prefs);
        }
    }

    public class SettingsActivity extends Activity
    {
        @Override
        protected  void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
        }
    }
}
