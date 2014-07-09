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

package com.android.launcher2;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.os.UserHandle;

/**
 * Stores the list of all applications for the all apps view.
 */
class AllAppsList {
    public static final int DEFAULT_APPLICATIONS_NUMBER = 42;
    
    /** The list off all apps. */
    public ArrayList<ApplicationInfo> data =
            new ArrayList<ApplicationInfo>(DEFAULT_APPLICATIONS_NUMBER);
    /** The list of apps that have been added since the last notify() call. */
    public ArrayList<ApplicationInfo> added =
            new ArrayList<ApplicationInfo>(DEFAULT_APPLICATIONS_NUMBER);
    /** The list of apps that have been removed since the last notify() call. */
    public ArrayList<ApplicationInfo> removed = new ArrayList<ApplicationInfo>();
    /** The list of apps that have been modified since the last notify() call. */
    public ArrayList<ApplicationInfo> modified = new ArrayList<ApplicationInfo>();

    private IconCache mIconCache;

    /**
     * Boring constructor.
     */
    public AllAppsList(IconCache iconCache) {
        mIconCache = iconCache;
    }

    /**
     * Add the supplied ApplicationInfo objects to the list, and enqueue it into the
     * list to broadcast when notify() is called.
     *
     * If the app is already in the list, doesn't add it.
     */
    public void add(ApplicationInfo info) {
        if (findActivity(data, info.componentName, info.user)) {
            return;
        }
        data.add(info);
        added.add(info);
    }
    
    public void clear() {
        data.clear();
        // TODO: do we clear these too?
        added.clear();
        removed.clear();
        modified.clear();
    }

    public int size() {
        return data.size();
    }

    public ApplicationInfo get(int index) {
        return data.get(index);
    }

    /**
     * Add the icons for the supplied apk called packageName.
     */
    public void addPackage(Context context, String packageName, UserHandle user) {
        LauncherApps launcherApps = (LauncherApps)
                context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
        final List<LauncherActivityInfo> matches = launcherApps.getActivityList(packageName,
                user);

        for (LauncherActivityInfo info : matches) {
            add(new ApplicationInfo(info, user, mIconCache, null));
        }
    }

    /**
     * Remove the apps for the given apk identified by packageName.
     */
    public void removePackage(String packageName, UserHandle user) {
        final List<ApplicationInfo> data = this.data;
        for (int i = data.size() - 1; i >= 0; i--) {
            ApplicationInfo info = data.get(i);
            final ComponentName component = info.intent.getComponent();
            if (info.user.equals(user) && packageName.equals(component.getPackageName())) {
                removed.add(info);
                data.remove(i);
            }
        }
        // This is more aggressive than it needs to be.
        mIconCache.flush();
    }

    /**
     * Add and remove icons for this package which has been updated.
     */
    public void updatePackage(Context context, String packageName, UserHandle user) {
        LauncherApps launcherApps = (LauncherApps)
                context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
        final List<LauncherActivityInfo> matches = launcherApps.getActivityList(packageName,
                user);
        if (matches.size() > 0) {
            // Find disabled/removed activities and remove them from data and add them
            // to the removed list.
            for (int i = data.size() - 1; i >= 0; i--) {
                final ApplicationInfo applicationInfo = data.get(i);
                final ComponentName component = applicationInfo.intent.getComponent();
                if (user.equals(applicationInfo.user)
                        && packageName.equals(component.getPackageName())) {
                    if (!findActivity(matches, component, user)) {
                        removed.add(applicationInfo);
                        mIconCache.remove(component);
                        data.remove(i);
                    }
                }
            }

            // Find enabled activities and add them to the adapter
            // Also updates existing activities with new labels/icons
            int count = matches.size();
            for (int i = 0; i < count; i++) {
                final LauncherActivityInfo info = matches.get(i);
                ApplicationInfo applicationInfo = findApplicationInfoLocked(
                        info.getComponentName().getPackageName(),
                        info.getComponentName().getClassName(),
                        user);
                if (applicationInfo == null) {
                    add(new ApplicationInfo(info, user,
                            mIconCache, null));
                } else {
                    mIconCache.remove(applicationInfo.componentName);
                    mIconCache.getTitleAndIcon(applicationInfo, info, null);
                    modified.add(applicationInfo);
                }
            }
        } else {
            // Remove all data for this package.
            for (int i = data.size() - 1; i >= 0; i--) {
                final ApplicationInfo applicationInfo = data.get(i);
                final ComponentName component = applicationInfo.intent.getComponent();
                if (user.equals(applicationInfo.user)
                        && packageName.equals(component.getPackageName())) {
                    removed.add(applicationInfo);
                    mIconCache.remove(component);
                    data.remove(i);
                }
            }
        }
    }

    /**
     * Returns whether <em>apps</em> contains <em>component</em> for a specific
     * user profile.
     */
    private static boolean findActivity(List<LauncherActivityInfo> apps, ComponentName component,
            UserHandle user) {
        for (LauncherActivityInfo info : apps) {
            if (info.getUser().equals(user)
                    && info.getComponentName().equals(component)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether <em>apps</em> contains <em>component</em> for a specific
     * user profile.
     */
    private static boolean findActivity(ArrayList<ApplicationInfo> apps, ComponentName component,
            UserHandle user) {
        final int N = apps.size();
        for (int i = 0; i < N; i++) {
            final ApplicationInfo info = apps.get(i);
            if (info.user.equals(user) && info.componentName.equals(component)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find an ApplicationInfo object for the given packageName, className and
     * user profile.
     */
    private ApplicationInfo findApplicationInfoLocked(String packageName, String className,
            UserHandle user) {
        for (ApplicationInfo info: data) {
            final ComponentName component = info.intent.getComponent();
            if (user.equals(info.user) && packageName.equals(component.getPackageName())
                    && className.equals(component.getClassName())) {
                return info;
            }
        }
        return null;
    }
}
