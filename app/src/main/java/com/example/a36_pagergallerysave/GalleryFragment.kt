package com.example.a36_pagergallerysave


import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.fragment_gallery.*

/**
 * A simple [Fragment] subclass.
 */
class GalleryFragment : Fragment() {
    //这是用kotlin新的方式创建的ViewModel
    //提升了ViewModel的范围，使两个Fragment都可以共享这个ViewModel
    private val galleryViewModel by activityViewModels<GalleryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    /*
        顶部菜单选项
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.swipeIndicator -> {
                swipeLayoutGallery.isRefreshing = true
                Handler().postDelayed({galleryViewModel.resetQuery() },1000)
            }

            /*
                点击重新刷新
                网络状态有问题时
             */
            R.id.menuRetry -> {
                galleryViewModel.retry()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu,menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        val galleryAdapter = GalleryAdapter(galleryViewModel)
        recyclerView.apply {
            adapter = galleryAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        }

        galleryViewModel.pagedListLiveData.observe(viewLifecycleOwner, Observer {
            galleryAdapter.submitList(it)

        })

        /*
            刷新数据
         */
        swipeLayoutGallery.setOnRefreshListener{
            galleryViewModel.resetQuery()
        }

        /*
               观察networkStatus状态的变化
         */
        galleryViewModel.networkStatus.observe(viewLifecycleOwner, Observer {
            Log.d("hello","onActivityCreated: $it")
            galleryAdapter.updateNetworkStatus(it)
            //底部转动的进度条，数据加载出来好后，如果是INITIAL_LOADING就让它出现
            swipeLayoutGallery.isRefreshing = it == NetworkStatus.INITIAL_LOADING
        })

    }


}

