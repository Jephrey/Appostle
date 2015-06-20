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

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class AppDetailActivity extends AppCompatActivity {
    public static final String PACKAGE_NAME = "package_name";
    public static final String PACKAGE_DATE = "package_date";

    private AppDetailFragment detail = null;
    private String package_name = null;
    private String package_date = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Activity in landscape mode then finish and go back to the start Activity.
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
            return;
        }

        detail = (AppDetailFragment) getFragmentManager().findFragmentById(R.id.app_detail);

        if (detail == null) {
            detail = new AppDetailFragment();
            getFragmentManager().beginTransaction().add(android.R.id.content, detail).commit();
        }

        // Get name and date from the extras bundle.
        Bundle extra = this.getIntent().getExtras();
        package_name = extra.getString(PACKAGE_NAME);
        package_date = extra.getString(PACKAGE_DATE);
    }

    // Handle the back button.
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        detail.fillDetail(package_name, package_date);
    }
} 