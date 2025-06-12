package com.example.flo_clone2.ui.main.locker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo_clone2.data.entities.Song
import com.example.flo_clone2.data.local.SongDatabase
import com.example.flo_clone2.databinding.FragmentSavedSongBinding
import com.example.flo_clone2.ui.main.MainActivity

class SavedSongFragment : Fragment() {

    lateinit var binding: FragmentSavedSongBinding
    lateinit var songDB: SongDatabase // 원래라면 다른 DB를 쓰거나, SongDao에서 islike인 데이터만 골라와야 할텐데, 번거로우니 일단은 넘김
    var isClick: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedSongBinding.inflate(inflater, container, false)

        songDB = SongDatabase.getInstance(requireContext())!! // 부모 Context 없이 DB 생성

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        /** 보관함 곡 초기화 - RoomDB 이용 가능할 듯 -> 이용함 */
        initSavedSongs()
    }

    /**
     * SavedSong 리스트 초기화
     * MusicDatabase의 데이터를 리스트에 추가
     */
    private fun initSavedSongs() {
        binding.lockerContentRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        /** RecyclerView */
        val savedSongRVAdapter = SavedSongRVAdapter() // adapter와 data list 연결

        /** 전체선택 버튼 클릭 */
        binding.lockerSelectAllLayout.setOnClickListener{
            setSelectButton(true)
        }
        binding.lockerUnselectAllLayout.setOnClickListener{
            setSelectButton(false)
        }

        /** item 클릭 시 이벤트 처리: more 클릭 시 삭제 */
        savedSongRVAdapter.setMyLockerItemClickListener(object:
            SavedSongRVAdapter.lockerItemClickListener {
            // 클릭 리스너를 구체화하여 DB도 업데이트
            override fun onRemoveSong(songId: Int) {
                // DB에 곡의 좋아요 상태 변경
                songDB.songDao().updateIsLikeById(false, songId)
            }
        })

        binding.lockerContentRv.adapter = savedSongRVAdapter // adapter와 RecyclerView 연결
        // 좋아요 된 곡들만 추가
        savedSongRVAdapter.addSongs(songDB.songDao().getLikedSongs(true) as ArrayList<Song>)
        Log.d("savedSongRVAdapter", songDB.songDao().getLikedSongs(true).toString())
    }

    private fun setSelectButton(isClick: Boolean) {
        val mainContext = context as MainActivity

        if (isClick) {
            // 선택 해제 -> 전체 선택
            mainContext.binding.mainBnv.visibility = View.VISIBLE
            mainContext.binding.bottomSheetDialog.visibility = View.GONE

            binding.lockerSelectAllLayout.visibility = View.VISIBLE
            binding.lockerUnselectAllLayout.visibility = View.GONE
        } else {
            // 전체 선택 -> 선택 해제
            mainContext.binding.mainBnv.visibility = View.GONE
            mainContext.binding.bottomSheetDialog.visibility = View.VISIBLE

            binding.lockerSelectAllLayout.visibility = View.GONE
            binding.lockerUnselectAllLayout.visibility = View.VISIBLE
        }
    }
}