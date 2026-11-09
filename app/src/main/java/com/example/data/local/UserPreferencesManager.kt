package com.example.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.theme.AppearanceMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "animeflix_prefs")

data class UserSettings(
    val appearanceMode: AppearanceMode = AppearanceMode.GLASS_DEFAULT,
    val reducedMotion: Boolean = false,
    val defaultQuality: String = "1080p",
    val defaultSubtitle: String = "English",
    val defaultPlaybackSpeed: Float = 1.0f,
    val autoPlayNext: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val profileName: String = "Anime Lover",
    val profileBio: String = "Just a dreamer...",
    val showWelcomeBanner: Boolean = true
)

class UserPreferencesManager(private val context: Context) {

    private object Keys {
        val APPEARANCE = stringPreferencesKey("appearance_mode")
        val REDUCED_MOTION = booleanPreferencesKey("reduced_motion")
        val DEFAULT_QUALITY = stringPreferencesKey("default_quality")
        val DEFAULT_SUBTITLE = stringPreferencesKey("default_subtitle")
        val AUTO_PLAY_NEXT = booleanPreferencesKey("auto_play_next")
        val NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
        val PROFILE_NAME = stringPreferencesKey("profile_name")
        val PROFILE_BIO = stringPreferencesKey("profile_bio")
        val SHOW_WELCOME = booleanPreferencesKey("show_welcome_banner")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        val modeName = prefs[Keys.APPEARANCE] ?: AppearanceMode.GLASS_DEFAULT.name
        val mode = runCatching { AppearanceMode.valueOf(modeName) }
            .getOrDefault(AppearanceMode.GLASS_DEFAULT)

        UserSettings(
            appearanceMode = mode,
            reducedMotion = prefs[Keys.REDUCED_MOTION] ?: false,
            defaultQuality = prefs[Keys.DEFAULT_QUALITY] ?: "1080p",
            defaultSubtitle = prefs[Keys.DEFAULT_SUBTITLE] ?: "English",
            autoPlayNext = prefs[Keys.AUTO_PLAY_NEXT] ?: true,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS] ?: true,
            profileName = prefs[Keys.PROFILE_NAME] ?: "Anime Lover",
            profileBio = prefs[Keys.PROFILE_BIO] ?: "Just a dreamer...",
            showWelcomeBanner = prefs[Keys.SHOW_WELCOME] ?: true
        )
    }

    suspend fun setAppearanceMode(mode: AppearanceMode) {
        context.dataStore.edit { it[Keys.APPEARANCE] = mode.name }
    }

    suspend fun setReducedMotion(enabled: Boolean) {
        context.dataStore.edit { it[Keys.REDUCED_MOTION] = enabled }
    }

    suspend fun setDefaultQuality(quality: String) {
        context.dataStore.edit { it[Keys.DEFAULT_QUALITY] = quality }
    }

    suspend fun setDefaultSubtitle(subtitle: String) {
        context.dataStore.edit { it[Keys.DEFAULT_SUBTITLE] = subtitle }
    }

    suspend fun setAutoPlayNext(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_PLAY_NEXT] = enabled }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFICATIONS] = enabled }
    }

    suspend fun updateProfile(name: String, bio: String) {
        context.dataStore.edit {
            it[Keys.PROFILE_NAME] = name.trim().ifEmpty { "Anime Lover" }
            it[Keys.PROFILE_BIO] = bio.trim().ifEmpty { "Just a dreamer..." }
        }
    }

    suspend fun setShowWelcomeBanner(show: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_WELCOME] = show }
    }
}
