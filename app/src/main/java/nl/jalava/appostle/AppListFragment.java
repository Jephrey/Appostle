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

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class AppListFragment extends Fragment {
    final static String TAG = "APPLIST";
    public final static String PREFS_APP_TYPE = "APP_TYPE";

    // Listener.
    private OnItemSelectedListener listener;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    // Interface for communication with listener.
    public interface OnItemSelectedListener {
        void onAppSelected(AppInfo app);
    }

    private ListView appList;
    private ProgressBar progress;
    private TextView progress_loading;
    private Context mContext;
    private PackageManager pm;
    private AppInfoAdapter adapter;
    private int curType;              // Current app type.
    private SharedPreferences mPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getActivity().getSharedPreferences("AppListFragment", Context.MODE_PRIVATE);
        curType = mPrefs.getInt(PREFS_APP_TYPE, 0);
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt(PREFS_APP_TYPE, curType);
        ed.apply();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.app_list, container, false);
        appList = (ListView) view.findViewById(R.id.listView1);
        progress = (ProgressBar) view.findViewById(R.id.progressBar);
        progress_loading = (TextView) view.findViewById(R.id.progress_loading);

        progress.bringToFront();
        progress_loading.bringToFront();

        mContext = view.getContext();

        // Make list clickable and fast scrollable.
        appList.setClickable(true);
        appList.setFastScrollEnabled(true);

        // OnClick
        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfo o = (AppInfo) appList.getItemAtPosition(position);
                listener.onAppSelected(o);
            }
        });

        // Swipe to refresh.
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_apps);
        mSwipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new UpdateAppList().execute();
            }
        });

        // Prevent always refreshing when pulling listview down.
        appList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    mSwipeRefreshLayout.setEnabled(true);
                } else mSwipeRefreshLayout.setEnabled(false);
            }
        });


        pm = mContext.getPackageManager();

        new UpdateAppList().execute();

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.refresh:
                new UpdateAppList().execute();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void doUpdate(int currentType) {
        if (currentType >= 0) {
            curType = currentType;
        }
        new UpdateAppList().execute();
    }

    private void updateApps() {

        List<PackageInfo> apps = pm.getInstalledPackages(0);
        Vector<AppInfo> app_data = new Vector<>();

        for (PackageInfo pi: apps) {
            ApplicationInfo ai;
            try {
                ai = pm.getApplicationInfo(pi.packageName, 0);
            } catch (NameNotFoundException e) {
                continue;
            }

            // Which apps?
            switch (curType) {
                case 0:	if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) continue; // Skip system apps.
                    break;
                case 1: if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) == 0) continue; // Skip installed apps.
                    break;
            }

            String name = (String) pm.getApplicationLabel(ai);

            String appFile = ai.sourceDir;

            // Get last updated date.
            long updated = pi.lastUpdateTime;

            // Localized date.
            String dateString = android.text.format.DateFormat.getDateFormat(mContext).format(new Date(updated));

            // Get app info.
            AppInfo app = new AppInfo();
            app.name = name;
            app.date = dateString;
            app.version = pi.versionName;
            app.packageName = pi.packageName;
            app.lastUpdateTime = updated;
            app_data.add(app);
        }

        // Copy the Vector into the array.
        AppInfo[] app_data2 = new AppInfo[app_data.size()];
        app_data.copyInto(app_data2);

        adapter = new AppInfoAdapter(mContext, R.layout.app_row, app_data2);

        // Sort array by date descending.
        adapter.sort(new Comparator<AppInfo>() {
            public int compare(AppInfo app1, AppInfo app2) {
                int comp = 0;
                if (app1.lastUpdateTime > app2.lastUpdateTime) {
                    comp = -1;
                } else if (app1.lastUpdateTime < app2.lastUpdateTime) {
                    comp = 1;
                }
                return comp;
            }
        });
    }

    private class UpdateAppList extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            updateApps();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
            progress_loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progress.setVisibility(View.GONE);
            progress_loading.setVisibility(View.GONE);
            appList.setAdapter(adapter);

            // Fill the detail view if available.
            FragmentManager fm = getFragmentManager();
            if (fm != null) {
                AppDetailFragment det;
                det = (AppDetailFragment) fm.findFragmentById(R.id.app_detail);
                if ((det != null) && det.isInLayout()) {
                    if (adapter.getCount() > 0 ) {
                        det.fillDetail(adapter.data[0].packageName, adapter.data[0].date);
                    }
                }
            }

            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement AppListFragment.OnItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
