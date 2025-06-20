package com.example.flo_clone2.ui.main.home

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.flo_clone2.R
import com.example.flo_clone2.data.entities.Album
import com.example.flo_clone2.data.entities.Song
import com.example.flo_clone2.data.local.SongDatabase
import com.example.flo_clone2.databinding.FragmentHomeBinding
import com.example.flo_clone2.ui.main.MainActivity
import com.example.flo_clone2.ui.main.album.AlbumFragment
import com.google.gson.Gson

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    private lateinit var songDB: SongDatabase
    val albumDatas = arrayListOf<Album>() // ArrayList
    private var song = Song()
    private var nowPos: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        initAlbumDatas()

        /**
         * banner를 위한 Adapter
         * 따로 Adapter 클래스를 만들어줄 필요가 있음
         */
        val bannerAdapter = BannerVPAdapter(this)
        // 추가할 fragment: BannerFragment - 배너 이미지가 들어있는 fragment
        // 배너로 사용될 이미지를 담은 BannerFragment를 추가
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        /**
         * ViewPager와 Adapter를 연결
         */
        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL // ViewPager의 슬라이드 방향을 좌우로 설정

        /**
         * recyclerView에 Adapter 연결 및 LayoutManager 추가
         */
        // adapter와 데이터 리스트 연결
        val albumRVAdapter = AlbumRVAdapter(albumDatas)
        // recyclerView에 adapter 연결
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
        // LayoutManager 설정. 수평으로 만들기 위해 LinearLayoutManager를 사용하고, HORIZONTAL 속성 사용.
        binding.homeTodayMusicAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        /**
         * HomeFragment에서 adapter로 listener 객체를 넘겨줌
         * onItemClick(): item이 클릭 되었을 때의 이벤트 처리를 위한 함수. AlbumFragment로 전환하는 작업 수행하면 됨.
         * 정리 1
         * adapter로 클릭 리스너를 넘겨주는 setMyItemClickListener를 호출. 매개변수로는 object(무명 객체) 선언
         * MyItemClickListener를 확장하는 무명 객체를 선언하여 바로 onItemClick함수를 override
         * onItemClick의 override를 통해 object, 무명 객체가 화면을 AlbumFragment로 넘기는 작업을 작성
         * 이후, listener 객체를 adapter로 넘겨주어 onBindView함수를 통해 item 클릭 이벤트를 처리
         *
         * 정리 ver.2
         * MyItemClickListener를 확장하는 클래스의 무명 객체가 바로 생성되어
         * MyItemClickListener에서 override를 넘긴 onItemClick(클릭 이벤트 발생 시의 작업)을 이곳에서 override
         * 따라서, AlbumFragment로의 전환을 발생시키고, 이후 setMyItemClickListener에서 listener 객체를 adapter로 전달
         * listener 객체를 받은 adapter는 이를 onBindViewHolder 메서드에 활용하여 item 선택 시의 작업을 수행
         *
         * 정리 ver.3
         * setMyItemClickListener를 통해 인자로 전달된 무명 객체가 adapter로 전달
         * onBindViewHolder에서 item의 클릭 시 album 객체를 인자로 넘긴 onItemClick을 수행하고
         * override된 onItemClick 함수에서 받은 album 정보를 바탕으로 Fragment 전환을 시도함.
         */
        albumRVAdapter.setMyItemClickListener(object: AlbumRVAdapter.MyItemClickListener{

            override fun onItemClick(album: Album) {
                changeAlbumFragment(album)
            }
            /** Album Item View 위의 재생 버튼 누르면, Miniplayer에 전달 */
            override fun onPlayAlbum(album: Album) {
                Log.d("Flow", "HomeFrag: onPlayAlbum")
                val homeContext = context as MainActivity // MainActivity의 context를 받아옴

                /** song SharedPreference에 songId를 저장 */
                val homeSP = homeContext.getSharedPreferences("song", MODE_PRIVATE)
                val homeSPEditor = homeSP.edit()
//                val songId = songDB.songDao().getSongByAlbumIdx(album.id)
                val songsInAlbum = songDB.songDao().getSongsByAlbumIdx(album.id)
                val songId = songsInAlbum[0].id

                Log.d("song spf", "Home.onPlayAlbum: " + homeSP.getInt("songId", -1).toString())
                Log.d("Song ID", "Home: $songId")
                homeSPEditor.putInt("songId", songId) // 선택한 앨범의 Song의 id
                homeSPEditor.apply()

                Log.d("song spf", "Home.onPlayAlbum: " + homeSP.getInt("songId", -1).toString())
                /** songId에 맞는 song 반환 */
                song = songDB.songDao().getSong(songId)

                /** MiniPlayer에 반영 */
                homeContext.setMiniPlayer(song)
                homeContext.songs.clear()
                homeContext.songs.addAll(songsInAlbum)
                homeContext.nowPos = 0
            }

            // item 삭제 이벤트
//            override fun onRemoveAlbum(position: Int) {
//                albumRVAdapter.removeItem(position)
//            }
        })

        return binding.root
    }

    private fun initAlbumDatas() {
        // 데이터 리스트 생성 더미 데이터
        // 원래는 데이터를 서버에서 받아옴
//        albumDatas.apply {
//            add(Album("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp))
//            add(Album("LILAC", "아이유 (IU)", R.drawable.img_album_exp2))
//            add(Album("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3))
//            add(Album("Boy with LUV", "방탄소년단 (BTS)", R.drawable.img_album_exp4))
//            add(Album("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5))
//            add(Album("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp6))
//        }
        // 같은 context 안에서는 함수만 사용 가능하고, 변수는 사용 불가
//        songDB = SongDatabase.getInstance(context as MainActivity)!!
//        --> 강의 듣기 전에는 MainActivity의 context 가져와서 사용했음
        songDB = SongDatabase.getInstance(requireContext())!!
        albumDatas.addAll(songDB.albumDao().getAlbums())
    }

    private fun changeAlbumFragment(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                }
            })
            .commitAllowingStateLoss()
    }
}