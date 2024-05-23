package com.msprysak.rentersapp.ui.media

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.model.Media
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.data.repositories.MediaRepository
import com.msprysak.rentersapp.interfaces.CallBack
import java.sql.Date

class MediaViewModel: ViewModel() {



    private val _mediaList: MutableLiveData<List<Media>> = MutableLiveData()
    val mediaList: MutableLiveData<List<Media>> get() = _mediaList

    private var _media: MutableLiveData<Media> = MutableLiveData()
    val media: MutableLiveData<Media> get() = _media

    private var _selectedImages: MutableLiveData<List<Uri>> = MutableLiveData()
    val selectedImages: MutableLiveData<List<Uri>> get() = _selectedImages

    private val mediaRepository = MediaRepository()
    init {
        _media.value = Media()
    }

    fun setupObserver(): LiveData<List<Pair<Media, User>>> {
        return mediaRepository.mediaListenerV2()
    }
    fun setupMediaListener(){
        mediaRepository.setupMediaListener {
            _mediaList.value = it
        }
    }

    fun createNewMedia(media: Media,selectedImages: List<Uri>, callBack: CallBack){
        mediaRepository.createNewMedia(media, selectedImages, callBack)
    }

    fun setMediaTitle(title: String){
        _media.value!!.mediaTitle = title
    }
    fun setMediaDate(date: Date){
        _media.value!!.mediaDate = date
    }
    fun setMediaImages(images: List<String>){
        _media.value!!.mediaImages = images
    }

}