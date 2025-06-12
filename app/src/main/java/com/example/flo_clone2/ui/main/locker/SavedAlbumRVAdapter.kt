package com.example.flo_clone2.ui.main.locker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo_clone2.data.entities.Album
import com.example.flo_clone2.databinding.ItemLockerAlbumBinding

class SavedAlbumRVAdapter(): RecyclerView.Adapter<SavedAlbumRVAdapter.ViewHolder>() {
    /** 전역 변수 설정 */
    // RecyclerView Adapter 내부에서 사용할 ArrayList
    private val albums = ArrayList<Album>()
    // Adapter 내부에서 사용할 리스너 객체
    private lateinit var mItemClickListener: MyItemClickListener

    /** 인터페이스 */
    interface MyItemClickListener {
        fun onRemoveAlbum(albumId: Int)
    }

    /**
     * 리스너 객체 할당
     */
    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    /**
     * ViewHolder에 레이아웃 입힌 아이템 던짐
     */
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): SavedAlbumRVAdapter.ViewHolder {
        val binding: ItemLockerAlbumBinding = ItemLockerAlbumBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    /**
     * 데이터 바인딩
     */
    override fun onBindViewHolder(holder: SavedAlbumRVAdapter.ViewHolder, position: Int) {
        holder.bind(albums[position])

        holder.binding.itemAlbumMoreIv.setOnClickListener {
            mItemClickListener.onRemoveAlbum(albums[position].id)
            removeAlbum(position)
        }
    }

    override fun getItemCount(): Int = albums.size

    /**
     * 앨범 리스트 초기화
     */
    @SuppressLint("NotifyDataSetChanged")
    fun addAlbums(albums: ArrayList<Album>) {
        this.albums.clear()
        this.albums.addAll(albums)

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeAlbum(position: Int) {
        albums.removeAt(position)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemLockerAlbumBinding): RecyclerView.ViewHolder(binding.root) {
        /** 데이터 바인딩 */
        fun bind(album: Album) {
            binding.itemAlbumImgIv.setImageResource(album.coverImg!!)
            binding.itemAlbumTitleTv.text = album.title
            binding.itemAlbumSingerTv.text = album.singer
        }
    }
}