package com.example.flo_clone2.ui.main.locker

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo_clone2.data.entities.Song
import com.example.flo_clone2.databinding.ItemLockerSongBinding

/**
 * ViewHolder 생성 함수
 * ItemView 객체를 만들어 ViewHolder에 넣는다.
 */
class SavedSongRVAdapter() : RecyclerView.Adapter<SavedSongRVAdapter.ViewHolder>() {

    private val songs = ArrayList<Song>()

    /**
     * ClickListener 객체를 위한 Interface
     * ClickListener 객체에 클릭 이벤트 시 해야 할 작업들을 메서드로 작성할 수 있음.
     */
    interface lockerItemClickListener {
        /** Song 데이터(저장된 곡) 삭제 메서드 */
        fun onRemoveSong(songId: Int)
    }

    /** Adapter 내부에서 사용할 ClickListener 변수 선언 */
    private lateinit var myLockerItemClickListener : lockerItemClickListener

    /**
     * SavedSongRVAdapter에서의 전역변수 myLockerItemClickListener를 초기화하는 함수
     *
     * 외부에서 사용하려면 ClickListener 변수가 필요함
     * 그런데 myLockerItemClickListener를 바로 초기화할 수 없어서 이에 대한 함수가 필요
     */
    fun setMyLockerItemClickListener(itemClickListener: lockerItemClickListener) {
    // Fragment에서 이 클릭 리스너를 구체화할 수 있는 set 함수를 만들어야 함
        Log.d("Flow", "setMyLockerItemClickListener")
        myLockerItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ItemLockerSongBinding = ItemLockerSongBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )

        return ViewHolder(binding)
    }

    /**
     * ViewHolder 데이터 바인딩 함수
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // songList의 item을 순차적으로 bind
        holder.bind(songs[position])

        /** ViewHoler 클릭 이벤트 처리: more버튼 누르면 삭제 */
        // (1) RecyclerView에서의 삭제와 (2) DB에서의 좋아요 false
        // 두 가지 작업이 필요함
        holder.binding.itemLockerSongMoreIv.setOnClickListener{
            Log.d("Flow", "SavedSongRVAdapter onBindViewHolder")
            myLockerItemClickListener.onRemoveSong(songs[position].id) // item이 눌렸을 때 클릭 리스너가 동작
            removeSong(position)
        }
    }

    /**
     * RecyclerView의 크기 반환
     */
    override fun getItemCount(): Int = songs.size

    /**
     * RecyclerView에 좋아요한 노래를 모두 더해주는 함수
     */
    @SuppressLint("NotifyDataSetChanged")
    fun addSongs(songs: ArrayList<Song>) {
        this.songs.clear()
        this.songs.addAll(songs)

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeSong(position: Int) {
        songs.removeAt(position)

        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemLockerSongBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song) {
            binding.itemLockerSongTitleTv.text = song.title
            binding.itemLockerSongSingerTv.text = song.singer
            binding.itemLockerSongCoverIv.setImageResource(song.coverImg!!)
        }
    }
}