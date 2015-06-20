/*
 * Copyright (C) 2013-2015 Jeffrey Rusterholz
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
package nl.jalava.appostle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener,  AppListFragment.OnItemSelectedListener {

    private Context mContext;

    private Toolbar mToolbar;
    private NavigationView mDrawer;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getBaseContext();

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);

        // Spinner for choosing application type.
        Spinner spinner_app = (Spinner) findViewById(R.id.app_types);
        spinner_app.setVisibility(View.VISIBLE);
        ArrayAdapter<CharSequence> apptype = ArrayAdapter.createFromResource(mToolbar.getContext(), R.array.app_types, android.R.layout.simple_spinner_item);
        apptype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_app.setAdapter(apptype);

        // Select.
        int app_type = getSharedPreferences("AppListFragment", Context.MODE_PRIVATE).getInt(AppListFragment.PREFS_APP_TYPE, 0);
        spinner_app.setSelection(app_type);

        spinner_app.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                AppListFragment fragment = (AppListFragment) getFragmentManager().findFragmentById(R.id.listFragment);
                if (fragment != null && fragment.isVisible()) {
                    fragment.doUpdate(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // Not used.
            }
        });

        /*
        mDrawer = (NavigationView) findViewById(R.id.main_drawer);
        mDrawer.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        mDrawerLayout.closeDrawer(GravityCompat.START);
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.refresh:
                AppListFragment fragment = (AppListFragment) getFragmentManager().findFragmentById(R.id.listFragment);
                if (fragment != null && fragment.isVisible()) {
                      fragment.doUpdate(-1);
                }
                break;
            case R.id.menu_about:
                AboutDialog about = new AboutDialog(this);
                String title = mContext.getString(R.string.about) + " " + mContext.getString(R.string.app_name);
                about.setTitle(title);
                about.setCanceledOnTouchOutside(true);
                about.show();
                break;
            case R.id.menu_settings:
                //startActivity(new Intent(this, Preferences.class));
                return (true);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        return false;
    }

    @Override
    public void onAppSelected(AppInfo app) {
        AppDetailFragment fragment = (AppDetailFragment) getFragmentManager().findFragmentById(R.id.app_detail);
        if (fragment != null && fragment.isInLayout()) {
            fragment.fillDetail(app.packageName, app.date);
        } else {
            Intent intent = new Intent(getApplicationContext(), AppDetailActivity.class);
            Bundle extra = new Bundle();
            extra.putString(AppDetailActivity.PACKAGE_NAME, app.packageName);
            extra.putString(AppDetailActivity.PACKAGE_DATE, app.date);
            intent.putExtras(extra);
            startActivity(intent);
        }
    }
}
