package com.vanced.manager.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.vanced.manager.BuildConfig
import com.vanced.manager.R

object InternetTools {

    private const val TAG = "VMNetTools"

    fun openUrl(Url: String, color: Int, context: Context) {
        val customTabPrefs = getDefaultSharedPreferences(context).getBoolean("use_customtabs", true)
        if (customTabPrefs) {
            val builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(ContextCompat.getColor(context, color))
            val customTabsIntent = builder.build()
            customTabsIntent.intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            customTabsIntent.launchUrl(context, Uri.parse(Url))
        } else
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Url)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun getFileNameFromUrl(url: String) = url.substring(url.lastIndexOf('/')+1, url.length)

    suspend fun getObjectFromJson(url: String, obj: String): String {
        return try {
            JsonHelper.getJson(url).string(obj) ?: ""
        } catch  (e: Exception) {
            Log.e(TAG, "Error: ", e)
            ""
        }
    }

    suspend fun getArrayFromJson(url: String, array: String): MutableList<String> {
        return try {
            JsonHelper.getJson(url).array<String>(array)?.value ?: mutableListOf("null")
        } catch (e: Exception) {
            Log.e(TAG, "Error: ", e)
            mutableListOf("null")
        }
    }

    suspend fun getJsonInt(file: String, obj: String, context: Context): Int {
        val installUrl = getDefaultSharedPreferences(context).getString("install_url", baseUrl)
        return try {
            JsonHelper.getJson("$installUrl/$file").int(obj) ?: 0
        } catch (e: Exception) {
            Log.e(TAG, "Error: ", e)
            0
        }
    }

    suspend fun getJsonString(file: String, obj: String, context: Context): String {
        val installUrl = getDefaultSharedPreferences(context).getString("install_url", baseUrl)
        return try {
            JsonHelper.getJson("$installUrl/$file").string(obj) ?: context.getString(R.string.unavailable)
        } catch (e: Exception) {
            Log.e(TAG, "Error: ", e)
            context.getString(R.string.unavailable)
        }
    }

    suspend fun isUpdateAvailable(): Boolean {
        val result = try {
            JsonHelper.getJson("https://ytvanced.github.io/VancedBackend/manager.json").int("versionCode") ?: 0
        } catch (e: Exception) {
            0
        }

        return result > BuildConfig.VERSION_CODE
    }

    const val baseUrl = "https://vanced.app/api/v1"

}