package ru.yourok.torrserve.atv

import android.content.Intent
import android.net.Uri
import android.os.Build
import ru.yourok.torrserve.app.App
import ru.yourok.torrserve.server.models.torrent.Torrent
import ru.yourok.torrserve.ui.activities.play.PlayActivity

object Utils {

    fun isGoogleTV(): Boolean {
        return App.context.packageManager.hasSystemFeature("android.software.leanback") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    fun isAmazonDev(): Boolean {
        return App.context.packageManager.hasSystemFeature("amazon.hardware.fire_tv")
    }

    fun buildPendingIntent(torr: Torrent): Intent {
        val vintent = Intent(App.context, PlayActivity::class.java)
        vintent.setData(Uri.parse(torr.hash))
        vintent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        vintent.action = Intent.ACTION_VIEW
        vintent.putExtra("action", "play")
        vintent.putExtra("title", torr.title)
        vintent.putExtra("poster", torr.poster)
        vintent.putExtra("save", false)
        return vintent
    }
}