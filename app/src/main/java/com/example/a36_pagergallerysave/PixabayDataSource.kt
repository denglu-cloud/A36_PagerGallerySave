package com.example.a36_pagergallerysave

import android.content.Context
import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson

/**
 * @author  DengLu
 * @date  2020/7/23 0:56
 * @version 1.0
 * @description :
 *
 */
class PixabayDataSource(private val context:Context) :PageKeyedDataSource<Int,PhotoItem>(){

    private val queryKey = arrayOf("cat", "dog", "car", "beauty", "phone", "computer", "flower", "animal").random()


    /**
     *  loadInitial：第一次加载
     */
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, PhotoItem>
    ) {
        val url =  "https://pixabay.com/api/?key=12472743-874dc01dadd26dc44e0801d61&q=${queryKey}&per_page=50&page=1"
        StringRequest(
            Request.Method.GET,
            url,
            Response.Listener {
                val dataList = Gson().fromJson(it,Pixabay::class.java).hits.toList()
                //将dataList中的数据放到callback中
                // loadInitial中的callback.onResult后面两个参数是第一、第二页？
                callback.onResult(dataList,null,2)
            },
            Response.ErrorListener {
                Log.d("hello","loadInitial:$it")

            }
        ).also { VolleySingleton.getInstance(context).requestQueue.add(it) }
    }

    /**
     *  loadAfter：加载下一页
     */
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {
        val url =  "https://pixabay.com/api/?key=12472743-874dc01dadd26dc44e0801d61&q=${queryKey}&per_page=50&page=&{params.key}"
        StringRequest(
            Request.Method.GET,
            url,
            Response.Listener {
                val dataList = Gson().fromJson(it,Pixabay::class.java).hits.toList()
                //loadAfter中的callback.onResult是加载下一页
                callback.onResult(dataList,params.key + 1 )
            },
            Response.ErrorListener {
                Log.d("hello","loadAfter:$it")

            }
        ).also { VolleySingleton.getInstance(context).requestQueue.add(it) }
    }

    /**
     * loadBefore:往前加载
     */
    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {
        TODO("Not yet implemented")
    }




}