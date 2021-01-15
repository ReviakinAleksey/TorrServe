package ru.yourok.torrserve.ui.fragments.play.viewmodels

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.yourok.torrserve.app.App
import ru.yourok.torrserve.server.api.Api
import ru.yourok.torrserve.server.models.torrent.Torrent

class TorrentViewModel : ViewModel() {
    private val data: MutableLiveData<Torrent> = MutableLiveData()

    fun loadTorrent(link: String, hash: String, title: String, poster: String, save: Boolean): LiveData<Torrent>? {
        if (hash.isNotEmpty())
            loadHash(hash)
        else if (link.isNotEmpty())
            loadLink(link, title, poster, save)
        else return null
        return data
    }

    private fun loadHash(hash: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val torr = Api.getTorrent(hash)
            withContext(Dispatchers.Main) {
                data.value = torr
            }
        }
    }

    private fun loadLink(link: String, title: String, poster: String, save: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val scheme = Uri.parse(link).scheme
                val torr = if (ContentResolver.SCHEME_ANDROID_RESOURCE == scheme || ContentResolver.SCHEME_FILE == scheme) {
                    uploadFile(link, save)//TODO title & poster
                    throw Exception("not released")
                } else
                    Api.addTorrent(link, title, poster, save)
                withContext(Dispatchers.Main) {
                    data.value = torr
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    //TODO message
                    App.Toast(e.message ?: return@withContext)
                }
            }
        }
    }

    private fun uploadFile(link: String, save: Boolean) {
        val fis = App.context.contentResolver.openInputStream(Uri.parse(link))
        Api.uploadTorrent(fis, save)
    }
}