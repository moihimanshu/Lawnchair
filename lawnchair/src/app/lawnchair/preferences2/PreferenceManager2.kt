/*
 * Copyright 2022, Lawnchair
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.lawnchair.preferences2

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.lawnchair.icons.shape.IconShape
import app.lawnchair.icons.shape.IconShapeManager
import app.lawnchair.qsb.providers.Google
import app.lawnchair.qsb.providers.QsbSearchProvider
import app.lawnchair.theme.color.ColorOption
import com.android.launcher3.Utilities
import com.android.launcher3.util.MainThreadInitializedObject
import com.patrykmichalik.preferencemanager.PreferenceManager

class PreferenceManager2(private val context: Context) : PreferenceManager {

    override val preferencesDataStore = context.preferencesDataStore
    private val reloadHelper = ReloadHelper(context)

    val darkStatusBar = preference(
        key = booleanPreferencesKey(name = "dark_status_bar"),
        defaultValue = false,
    )

    val hotseatQsb = preference(
        key = booleanPreferencesKey(name = "dock_search_bar"),
        defaultValue = true,
        onSet = reloadHelper::restart,
    )

    val iconShape = preference(
        key = stringPreferencesKey(name = "icon_shape"),
        defaultValue = IconShape.Circle,
        parse = { IconShape.fromString(it) ?: IconShapeManager.getSystemIconShape(context) },
        save = { it.toString() },
    )

    val themedHotseatQsb = preference(
        key = booleanPreferencesKey(name = "themed_hotseat_qsb"),
        defaultValue = false,
    )

    val hotseatQsbProvider = preference(
        key = stringPreferencesKey(name = "dock_search_bar_provider"),
        defaultValue = Google,
        parse = { QsbSearchProvider.fromId(it) },
        save = { it.id },
        onSet = reloadHelper::recreate
    )

    val hotseatQsbForceWebsite = preference(
        key = booleanPreferencesKey(name = "dock_search_bar_force_website"),
        defaultValue = false,
        onSet = reloadHelper::recreate
    )

    val accentColor = preference(
        key = stringPreferencesKey(name = "accent_color"),
        parse = ColorOption::fromString,
        save = ColorOption::toString,
        onSet = reloadHelper::recreate,
        defaultValue = when {
            Utilities.ATLEAST_S -> ColorOption.SystemAccent
            Utilities.ATLEAST_O_MR1 -> ColorOption.WallpaperPrimary
            else -> ColorOption.LawnchairBlue
        },
    )

    val hiddenApps = preference(
        key = stringSetPreferencesKey(name = "hidden_apps"),
        defaultValue = setOf(),
    )

    val roundedWidgets = preference(
        key = booleanPreferencesKey(name = "rounded_widgets"),
        defaultValue = true,
        onSet = reloadHelper::reloadGrid,
    )

    val showStatusBar = preference(
        key = booleanPreferencesKey(name = "show_status_bar"),
        defaultValue = true,
    )

    val showTopShadow = preference(
        key = booleanPreferencesKey(name = "show_top_shadow"),
        defaultValue = true,
    )

    companion object {
        private val Context.preferencesDataStore by preferencesDataStore(
            name = "preferences",
            produceMigrations = { listOf(SharedPreferencesMigration(context = it).produceMigration()) },
        )

        @JvmField
        val INSTANCE = MainThreadInitializedObject(::PreferenceManager2)

        @JvmStatic
        fun getInstance(context: Context) = INSTANCE.get(context)!!
    }
}

@Composable
fun preferenceManager2() = PreferenceManager2.getInstance(LocalContext.current)
