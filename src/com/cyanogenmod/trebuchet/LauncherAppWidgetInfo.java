/*
 * Copyright (C) 2009 The Android Open Source Project
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

package com.cyanogenmod.trebuchet;

import android.appwidget.AppWidgetHostView;
import android.content.ComponentName;
import android.content.ContentValues;

/**
 * Represents a widget (either instantiated or about to be) in the Launcher.
 */
class LauncherAppWidgetInfo extends ItemInfo {

    /**
     * Indicates that the widget hasn't been instantiated yet.
     */
    static final int NO_ID = -1;

    /**
     * Identifier for this widget when talking with
     * {@link android.appwidget.AppWidgetManager} for updates.
     */
    int appWidgetId = NO_ID;

    ComponentName providerName;

    // TODO: Are these necessary here?
    int minWidth = -1;
    int minHeight = -1;

    /**
     * View that holds this widget after it's been created.  This view isn't created
     * until Launcher knows it's needed.
     */
    AppWidgetHostView hostView = null;

    /**
     * Constructor for use with AppWidgets that haven't been instantiated yet.
     */
    LauncherAppWidgetInfo(ComponentName providerName) {
        itemType = LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET;
        this.providerName = providerName;

        // Since the widget isn't instantiated yet, we don't know these values. Set them to -1
        // to indicate that they should be calculated based on the layout and minWidth/minHeight
        spanX = -1;
        spanY = -1;
    }

    LauncherAppWidgetInfo(int appWidgetId) {
        itemType = LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET;
        this.appWidgetId = appWidgetId;
    }

    @Override
    void onAddToDatabase(ContentValues values) {
        super.onAddToDatabase(values);
        values.put(LauncherSettings.Favorites.APPWIDGET_ID, appWidgetId);
    }

    @Override
    public String toString() {
        return "AppWidget(id=" + Integer.toString(appWidgetId) + ")";
    }

    @Override
    void unbind() {
        super.unbind();
        hostView = null;
    }
}
