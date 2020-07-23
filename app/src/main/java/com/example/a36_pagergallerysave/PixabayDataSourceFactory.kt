package com.example.a36_pagergallerysave

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource


/**
 * @author  DengLu
 * @date  2020/7/23 1:25
 * @version 1.0
 * @description :
 *
 */
class PixabayDataSourceFactory(private val context:Context): DataSource.Factory<Int,PhotoItem>() {

    private val _pixabayDataSource = MutableLiveData<PixabayDataSource>()
    val pixabayDataSource : LiveData<PixabayDataSource> = _pixabayDataSource

    /**
     * 表示什么意思？
     * 如果这里不写，网络状态的日志将打印不了
     *
     * 表示把PixabayDataSource(context)的值赋值给_pixabayDataSource？
     */
    override fun create(): DataSource<Int, PhotoItem> {
        return PixabayDataSource(context).also { _pixabayDataSource.postValue(it) }
    }
}