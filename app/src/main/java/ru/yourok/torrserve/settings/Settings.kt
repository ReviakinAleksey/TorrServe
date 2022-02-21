package ru.yourok.torrserve.settings

import android.net.Uri
import androidx.preference.PreferenceManager
import ru.yourok.torrserve.R
import ru.yourok.torrserve.app.App
import java.io.File

object Settings {

    fun getServerAuth() = get("server_auth", "")

    fun getHosts(): List<String> {
        val prefs = PreferenceManager.getDefaultSharedPreferences(App.context)
        val ret = prefs.getStringSet("saved_hosts", mutableSetOf())
        return ret?.toList() ?: emptyList()
    }

    fun setHosts(hosts: List<String>) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(App.context)
        prefs.edit().putStringSet("saved_hosts", hosts.toMutableSet()).apply()
    }

    fun getLastViewDonate() = get("last_view_donate", 0L)
    fun setLastViewDonate(v: Long) = set("last_view_donate", v)

    fun getPlayer(): String = get(App.context.getString(R.string.player_pref_key), "")
    fun setPlayer(v: String) = set(App.context.getString(R.string.player_pref_key), v)

    fun getChooserAction(): Int = get("chooser_action", 0)
    fun setChooserAction(v: Int) = set("chooser_action", v)

    fun isAccessibilityOn(): Boolean = get("switch_accessibility", false)
    fun isBootStart(): Boolean = get("boot_start", false)
    fun isRootStart(): Boolean = get("root_start", false)
    fun isClientDownload(): Boolean = get("client_download", false)

    fun showBanner(): Boolean = get("show_banner", true)
    fun setShowBanner(v: Boolean) = set("show_banner", v)

    fun showCover(): Boolean = get("show_cover", true)

    fun getTheme(): String = get("theme", "dark")
    fun setTheme(v: String) = set("theme", v)

    fun getHost(): String = get("host", "http://127.0.0.1:8090")
    fun setHost(host: String) {
        var hst = host
        if (hst.isEmpty())
            hst = "http://127.0.0.1:8090"
        val url = Uri.parse(hst)
        if (url.scheme.isNullOrBlank())
            hst = "http://$hst"
        if (url.port == -1)
            hst = "$hst:8090"

        set("host", hst)
    }

    fun getTorrPath(): String {
        var filesDir: File?
        filesDir = App.context.getExternalFilesDir(null)

        if (filesDir?.canWrite() != true)
            filesDir = null

        if (filesDir == null)
            filesDir = App.context.filesDir

        if (filesDir == null)
            filesDir = File("/sdcard/TorrServe")

        if (!filesDir.exists())
            filesDir.mkdirs()

        return filesDir.path
    }

    fun <T> get(name: String, def: T): T {
        try {
            val prefs = PreferenceManager.getDefaultSharedPreferences(App.context)
            if (prefs.all.containsKey(name))
                return prefs.all[name] as T
            return def
        } catch (e: Exception) {
            return def
        }
    }

    private fun set(name: String, value: Any?) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(App.context)
        when (value) {
            is String -> prefs.edit().putString(name, value).apply()
            is Boolean -> prefs.edit().putBoolean(name, value).apply()
            is Float -> prefs.edit().putFloat(name, value).apply()
            is Int -> prefs.edit().putInt(name, value).apply()
            is Long -> prefs.edit().putLong(name, value).apply()
            is MutableSet<*>? -> prefs.edit().putStringSet(name, value as MutableSet<String>?).apply()
            else -> prefs.edit().putString(name, value.toString()).apply()
        }
    }
}
