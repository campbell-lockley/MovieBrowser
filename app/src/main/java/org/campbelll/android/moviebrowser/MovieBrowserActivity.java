package org.campbelll.android.moviebrowser;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;


public class MovieBrowserActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "MovieBrowserActivity";

    // onNavigationListener to handle the navigation list selections
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String[] view_list = getResources().getStringArray(R.array.view_list);
        String selected = view_list[position];
        Log.d(TAG, selected + " selected");
        Toast.makeText(getApplicationContext(), "Displaying " + selected, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_browser);

        setupActionBar();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void setupActionBar() {
        final ActionBar actionBar = getSupportActionBar();

        // Use drop-down navigation
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//        SpinnerAdapter view_list_adapter = ArrayAdapter.createFromResource(this, R.array.view_list,
//                android.R.layout.simple_spinner_dropdown_item);
//        actionBar.setListNavigationCallbacks(view_list_adapter, this);
//
//        getSupportActionBar().setSelectedNavigationItem(nav_list_selected);

        // Use custom menu
        ViewGroup v = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.actionbar_top, null);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(v, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL | Gravity.RIGHT));

        // Display icon and disable title
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setIcon(R.drawable.ic_launcher);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Populate navigation list
        Spinner view_list = (Spinner)findViewById(R.id.nav_list_spinner);
        ArrayAdapter view_list_adapter = ArrayAdapter.createFromResource(this, R.array.view_list,
                android.R.layout.simple_spinner_dropdown_item);
        view_list_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        view_list.setAdapter(view_list_adapter);
        view_list.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_browser, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_browser:
                Log.d(TAG, "Browser selected");
                Toast.makeText(getApplicationContext(), "Browser Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_settings:
                Log.d(TAG, "Settings selected");
                Toast.makeText(getApplicationContext(), "Settings Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_legal:
                Log.d(TAG, "Legal selected");
                Toast.makeText(getApplicationContext(), "Legal Notices Selected", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
