package com.example.a36_pagergallerysave

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val factory = PixabayDataSourceFactory(application)
    //toLiveData的参数是随便给的，因为pageSize的大小不是在这里决定的
    val pagedListLiveData = factory.toLiveData(1)
    //Transformation是中间人LiveData,用一个LiveData观察另外一个LiveData,pixabayDataSource就是一个LiveData
    val networkStatus = Transformations.switchMap(factory.pixabayDataSource){it.networkStatus}



//    val pageListLiveData = PixabayDataSourceFactory(application).toLiveData(1)

    /**
     * 下拉刷新
     */
    fun resetQuery(){
        //invalidate()表示使无效，无效后工厂就会重新生成DataSource对象(但为啥？？？)，从而达到换一个关键词重新查
        pagedListLiveData.value?.dataSource?.invalidate()
    }


    /**
     * 网络状态有问题时，重新刷新
     * 点击菜单栏的选项
     */
    fun retry(){
        //invoke()是执行的意思
        factory.pixabayDataSource.value?.retry?.invoke()
    }

}