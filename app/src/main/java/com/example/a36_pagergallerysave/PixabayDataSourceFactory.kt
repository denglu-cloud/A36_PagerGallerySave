package com.example.a36_pagergallerysave

import android.content.Context
import androidx.paging.DataSource


/**
 * @author  DengLu
 * @date  2020/7/23 1:25
 * @version 1.0
 * @description :
 *
 */
class PixabayDataSourceFactory(private val context:Context): DataSource.Factory<Int,PhotoItem>() {
    override fun create(): DataSource<Int, PhotoItem> {
        return PixabayDataSource(context)
    }
}