package com.example.flo_clone2.ui.main.locker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo_clone2.data.local.SongDatabase
import com.example.flo_clone2.databinding.FragmentSavedAlbumBinding

class SavedAlbumFragment : Fragment() {
    /** 전역 변수 설정 */
    lateinit var binding : FragmentSavedAlbumBinding
    lateinit var albumDB : SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedAlbumBinding.inflate(layoutInflater, container, false)

        albumDB = SongDatabase.getInstance(requireContext())!!

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        initRecyclerView()
    }

    private fun initRecyclerView() {
        // RV layoutManager 설정
        binding.lockerSavedAlbumRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        /** Adapter 생성, 아벤트 처리, Adapter-RecyclerView 연결 */
        // Adapter 선언
        val albumRVAdapter = SavedAlbumRVAdapter()

        // 리스너 객체 생성 및 전달
        albumRVAdapter.setMyItemClickListener(object: SavedAlbumRVAdapter.MyItemClickListener{

            override fun onRemoveAlbum(albumId: Int) {
                albumDB.albumDao().getLikedAlbums(getJWT())
            }
        })
        // Adapter - RecyclerView 연결
        binding.lockerSavedAlbumRecyclerView.adapter = albumRVAdapter

        /** RecyclerView에 들어갈 Data set 초기화 */
        // 현재 로그인 되어 있는 유저 확인해서 좋아요한 앨범들 찾아서 추가
        albumRVAdapter.addAlbums(albumDB.albumDao().getLikedAlbums(getJWT()) as ArrayList)
        Log.d("SavedAlbumFrag", albumDB.albumDao().getLikedAlbums(getJWT()).toString())
    }

    /**
     * 현재 로그인 되어 있는 유저 확인
     */
    private fun getJWT(): Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        val jwt = spf!!.getInt("jwt", 0)
        Log.d("Main_Act/Get_JWT", "jwt_token: $jwt")

        return jwt
    }
}