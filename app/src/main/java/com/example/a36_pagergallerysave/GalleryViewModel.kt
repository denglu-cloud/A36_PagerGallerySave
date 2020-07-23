package com.example.a36_pagergallerysave

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.toLiveData

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    //toLiveData的参数是随便给的，因为pageSize的大小不是在这里决定的
    val pageListLiveData = PixabayDataSourceFactory(application).toLiveData(1)

    /**
     * 下拉刷新
     */
    fun resetQuery(){
        //invalidate()表示使无效，无效后工厂就会重新生成DataSource对象(但为啥？？？)，从而达到换一个关键词重新查
        pageListLiveData.value?.dataSource?.invalidate()
    }

//    private val _photoListLive = MutableLiveData<List<PhotoItem>>()
//    val photoListLive : LiveData<List<PhotoItem>>
//    get() = _photoListLive
//
//    fun fetchData() {
//        val stringRequest = StringRequest(
//            Request.Method.GET,
//            getUrl(),
//            Response.Listener {
//                _photoListLive.value = Gson().fromJson(it,Pixabay::class.java).hits.toList()
//            },
//            Response.ErrorListener {
//                Log.d("hello",it.toString())
//            }
//        )
//        VolleySingleton.getInstance(getApplication()).requestQueue.add(stringRequest)
//    }
//
//    private fun getUrl():String {
//        return "https://pixabay.com/api/?key=12472743-874dc01dadd26dc44e0801d61&q=${keyWords.random()}&per_page=100"
//    }
//
//    private val keyWords = arrayOf("cat", "dog", "car", "beauty", "phone", "computer", "flower", "animal")
}