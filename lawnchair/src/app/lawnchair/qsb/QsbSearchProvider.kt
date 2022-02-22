package app.lawnchair.qsb

import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import com.android.launcher3.R

sealed class QsbSearchProvider(
    val id: String,
    val name: String,
    @DrawableRes val icon: Int = R.drawable.ic_qsb_search,
    @DrawableRes val themedIcon: Int = icon,
    val themingMethod: ThemingMethod = ThemingMethod.TINT,
    open val packageName: String,
    open val action: String? = null,
    open val supportVoiceIntent: Boolean = false,
    open val website: String
) {

    fun createSearchIntent() = Intent(action)
        .addFlags(INTENT_FLAGS)
        .setPackage(packageName)

    fun createVoiceIntent(): Intent = if (supportVoiceIntent) {
        handleCreateVoiceIntent()
    } else {
        error("supportVoiceIntent is false but createVoiceIntent() was called for $name")
    }

    fun createWebsiteIntent() = Intent(Intent.ACTION_VIEW, Uri.parse(website))
        .addFlags(INTENT_FLAGS)

    open fun handleCreateVoiceIntent(): Intent =
        Intent(Intent.ACTION_VOICE_COMMAND)
            .addFlags(INTENT_FLAGS)
            .setPackage(packageName)

    object None : QsbSearchProvider(id = "", name = "", packageName = "", website = "")

    data class UnknownProvider(
        override val packageName: String,
        override val action: String? = null
    ) : QsbSearchProvider(
        id = "",
        name = "",
        packageName = packageName,
        action = action,
        website = ""
    )

    object AppSearch : QsbSearchProvider(
        id = "app-search",
        name = "App Search",
        icon = R.drawable.ic_qsb_search,
        themingMethod = ThemingMethod.TINT,
        packageName = "",
        website = ""
    )

    object Google : QsbSearchProvider(
        id = "google",
        name = "Google",
        icon = R.drawable.ic_super_g_color,
        themingMethod = ThemingMethod.THEME_BY_NAME,
        packageName = "com.google.android.googlequicksearchbox",
        action = "android.search.action.GLOBAL_SEARCH",
        supportVoiceIntent = true,
        website = "https://www.google.com/"
    )

    object GoogleGo : QsbSearchProvider(
        id = "google_go",
        name = "Google Go",
        icon = R.drawable.ic_super_g_color,
        themingMethod = ThemingMethod.THEME_BY_NAME,
        packageName = "com.google.android.apps.searchlite",
        action = "android.search.action.GLOBAL_SEARCH",
        supportVoiceIntent = true,
        website = "https://www.google.com/"
    ) {

        override fun handleCreateVoiceIntent(): Intent =
            createSearchIntent().putExtra("openMic", true)
    }

    object Duck : QsbSearchProvider(
        id = "duckduckgo",
        name = "DuckDuckGo",
        icon = R.drawable.ic_duckduckgo,
        themedIcon = R.drawable.ic_duckduckgo_tinted,
        themingMethod = ThemingMethod.TINT,
        packageName = "com.duckduckgo.mobile.android",
        action = "com.duckduckgo.mobile.android.NEW_SEARCH",
        website = "https://duckduckgo.com/"
    )

    companion object {

        internal const val INTENT_FLAGS =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        fun values() = listOf(
            AppSearch,
            Google,
            GoogleGo,
            Duck
        )

        /**
         * Resolve the search provider using its ID, or use Google as a fallback.
         */
        fun fromId(id: String): QsbSearchProvider =
            values().firstOrNull { it.id == id } ?: Google

        fun resolve(packageName: String): QsbSearchProvider =
            values().firstOrNull { it.packageName == packageName } ?: UnknownProvider(packageName)
    }
}