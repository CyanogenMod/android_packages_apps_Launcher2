/*
 * Copyright (C) 2010 The Android Open Source Project
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

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.os.Parcelable;

/**
 * We pass this object with a drag from the customization tray
 */
class PendingAddItemInfo extends ItemInfo {
    /**
     * The component that will be created.
     */
    ComponentName componentName;
}

class PendingAddWidgetInfo extends PendingAddItemInfo {
    int minWidth;
    int minHeight;
    int previewImage;
    int icon;

    // Any configuration data that we want to pass to a configuration activity when
    // starting up a widget
    String mimeType;
    Parcelable configurationData;

    public PendingAddWidgetInfo(AppWidgetProviderInfo i, String dataMimeType, Parcelable data) {
        itemType = LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET;
        componentName = i.provider;
        minWidth = i.minWidth;
        minHeight = i.minHeight;
        previewImage = i.previewImage;
        icon = i.icon;
        if (dataMimeType != null && data != null) {
            mimeType = dataMimeType;
            configurationData = data;
        }
    }
}