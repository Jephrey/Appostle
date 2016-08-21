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
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;

class AppInfoAdapter extends ArrayAdapter<AppInfo> implements Filterable {
    private final static String TAG = "APPINFO_ADAPTER";

    private final Context context;
    private int layoutResourceId;
    public Vector<AppInfo> mData;
    public Vector<AppInfo> mDataOriginal;

    public AppInfoAdapter(Context context, int layoutResourceId, Vector<AppInfo> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.mData = data;
        this.mDataOriginal = null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults results = new FilterResults();

                if (mDataOriginal == null) mDataOriginal = (Vector) mData.clone();
                mData.clear();

                if (constraint == null || constraint.length() == 0) {
                    results.values = mDataOriginal;
                    results.count = mDataOriginal.size();
                }
                else {
                    Vector<AppInfo> apps = new Vector<>();
                    String filter = constraint.toString().toUpperCase();
                    AppInfo clone;

                    for (AppInfo app: mDataOriginal) {
                        if (app.name.toUpperCase().contains(filter)) {
                            try {
                                clone = (AppInfo) app.clone();
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                                continue;
                            }

                            // Find the position of the search text.
                            String name = clone.name.toUpperCase();
                            int n = name.indexOf(filter);

                            // Split the app name.
                            String left = app.name.substring(0, n);
                            String mid = app.name.substring(n, n + filter.length());
                            String right = app.name.substring(n + filter.length(), app.name.length());

                            // Highlight the found text. Use color from resource.
                            int highlight;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                highlight = context.getResources().getColor(R.color.colorAccent, null);
                            } else {
                                //noinspection deprecation
                                highlight = context.getResources().getColor(R.color.colorAccent);
                            }

                            String сolorString = String.format("%X", highlight).substring(2);
                            clone.name = left + String.format("<font color=\"#%s\">" + mid + "</font>", сolorString) + right;

                            apps.add(clone);
                        }
                    }
                    results.values = apps.clone();
                    results.count = apps.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                for (AppInfo app : (Vector<AppInfo>) results.values) {
                    mData.add(app);
                }
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AppHolder holder;

        if (position >= mData.size()) return row;
        AppInfo app = mData.elementAt(position);

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new AppHolder();
            holder.image = (ImageView)row.findViewById(R.id.app_image);
            holder.info = (TextView)row.findViewById(R.id.app_name);
            row.setTag(holder);
        }
        else
        {
            holder = (AppHolder) row.getTag();
        }

        // App name, update date and version.
        String html = "<h3>" + app.name + "</h3>" + "<h7>" + app.date + "</h7>";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.info.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        } else {
            //noinspection deprecation
            holder.info.setText(Html.fromHtml(html));
        }

        try {
            app.icon = context.getPackageManager().getApplicationIcon(app.packageName);
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
            app.icon = null;
        }
        holder.image.setImageDrawable(app.icon);

        return row;
    }

    static class AppHolder
    {
        TextView info;
        ImageView image;
    }
}
