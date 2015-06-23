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

import android.graphics.drawable.Drawable;

/**
 * Created by Jeffrey on 31-10-2014.
 */
class AppInfo {
    public Drawable icon;
    public String name;
    public String date;
    public String version;
    public String packageName;
    long lastUpdateTime;

    public AppInfo(){
        super();
        this.name = "";
        this.version = "";
        this.packageName = "";
        this.date = "";
        this.lastUpdateTime = 0;
    }
}
