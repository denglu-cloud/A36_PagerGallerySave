package com.example.a36_pagergallerysave

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.gallery_cell.view.*
import kotlinx.android.synthetic.main.gallery_footer.view.*

class GalleryAdapter(private val galleryViewModel: GalleryViewModel):PagedListAdapter<PhotoItem, RecyclerView.ViewHolder>(DIFFCALLBACK) {
    //网络状态
    private var networkStatus : NetworkStatus? = null
    //页脚，进度处理器提示
    private var hasFooter = false

    /**
     * 网络中断后，有网络时自动刷新一下，不用点击底部的重新加载
     */
    init {
        galleryViewModel.retry()
    }

    /**
     * 更新网络状态
     */
    fun updateNetworkStatus(networkStatus: NetworkStatus?){
        this.networkStatus = networkStatus
        //根据网络状态决定是否展示页脚进度条，INITIAL_LOADING是第一次加载
        if(networkStatus == NetworkStatus.INITIAL_LOADING) hideFooter() else showFooter()
    }

    /**
     * 判断是否要显示页脚的进度条
     */
    private fun hideFooter(){
        if(hasFooter){
            //告诉适配器删掉这一行
            notifyItemRemoved(itemCount - 1)
        }
        hasFooter = false
    }
    private fun showFooter(){
        if(hasFooter){
            //先刷新内容
            notifyItemChanged(itemCount - 1)
        }else{
            //再如果没有hasFooter就让它出现
            hasFooter = true
            notifyItemInserted(itemCount - 1)
        }
    }

    /**
     * 返回这个列表有多少个元素
     */
    override fun getItemCount(): Int {
        return super.getItemCount() + if(hasFooter) 1 else 0
    }

    /**
     * 第一组数据加载前不用加载footer
     * 管理footer是否应该存在
     */
    override fun getItemViewType(position: Int): Int {
        return if(hasFooter && position == itemCount - 1) R.layout.gallery_footer else R.layout.gallery_cell
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        /*
            给不同类型的View绑定不同的事件
            就是画廊所在页面和进度条所在页面
         */
        return when(viewType){
            R.layout.gallery_cell -> PhotoViewHolder.newInstance(parent).also { holder ->
                holder.itemView.setOnClickListener{
                    Bundle().apply{
                        //采用两个页面共享一个ViewModel的方法
                        putInt("PHOTO_POSITION",holder.adapterPosition)
                        holder.itemView.findNavController()
                            .navigate(R.id.action_galleryFragment_to_pagerPhotoFragment,this)
                    }
                }
            }

            else -> FooterViewHolder.newInstance(parent).also {
                it.itemView.setOnClickListener{
                    galleryViewModel.retry()
                }
            }
        }
    }

    /**
     * 绑定
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        /*
            根据不同的view绑定不同的数据或组件
         */
        when(holder.itemViewType){
            //gallery_footer页脚绑定网络状态netWorksStatus
            R.layout.gallery_footer -> (holder as FooterViewHolder).bindWithNetworkStatus(networkStatus)
            //gallery_cell画廊绑定照片photoItem
            else -> {
                val photoItem = getItem(position) ?: return
                (holder as PhotoViewHolder).bindWithPhotoItem(photoItem)

            }

        }

    }

    object DIFFCALLBACK: DiffUtil.ItemCallback<PhotoItem>() {
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId == newItem.photoId
        }
    }

}

/**
 * 两个类型的ViewHolder
 * 一个放图片，一个在页脚放进度提示
 */
class PhotoViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
    /*
         companion object类似java的静态函数，属于类的？
     */
    companion object {
        fun newInstance(parent:ViewGroup): PhotoViewHolder{
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.gallery_cell,parent,false)
            return PhotoViewHolder(view)
        }
    }

    fun bindWithPhotoItem(photoItem: PhotoItem){
        /*
            对view进行绑定
         */
        with(itemView) {
            shimmerLayoutCell.apply {
                setShimmerColor(0x55FFFFFF)
                setShimmerAngle(0)
                startShimmerAnimation()
            }
            textViewUser.text = photoItem.photoUser
            textViewLikes.text = photoItem.photoLikes.toString()
            textViewFavorites.text = photoItem.photoFavorites.toString()
            imageView.layoutParams.height = photoItem.photoHeight
        }

//        itemView.shimmerLayoutCell.apply {
//            setShimmerColor(0x55FFFFFF)
//            setShimmerAngle(0)
//            startShimmerAnimation()
//        }

        Glide.with(itemView)
            .load(photoItem.previewUrl)
            .placeholder(R.drawable.ic_baseline_photo__gray_24)
            .listener(object :RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false.also { itemView.shimmerLayoutCell?.stopShimmerAnimation() }
                }

            })
            .into(itemView.imageView)

    }
}


class FooterViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
    companion object {
        fun newInstance(parent:ViewGroup): FooterViewHolder{
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.gallery_footer,parent,false)
            //使显示图片的位置信息居中展示
            (view.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
            return FooterViewHolder(view)
        }
    }

    /*
        根据不同的网络状态绑定FooterViewHolder
        这里有bug，提示信息没有出现，是不是没有调用这个方法？？？？？？？？？？？？？？？
        而且只能加载50张照片？？？？
     */
    fun bindWithNetworkStatus(networkStatus: NetworkStatus?){
        with(itemView){
            when(networkStatus){
                //网络中断
                NetworkStatus.FAILED -> {
                    textView.text = "点击重试"
                    //失败了就不要让它转动
                    progressBar.visibility = View.GONE
                    isClickable = true
                }

                //加载完成
                NetworkStatus.COMPLETED -> {
                    textView.text = "加载完毕"
                    progressBar.visibility = View.GONE
                    isClickable = false
                }

                //正在加载
                else-> {
                    textView.text = "正在加载"
                    progressBar.visibility = View.VISIBLE
                    isClickable = false
                }

            }

        }
    }

}