package com.example.flo_clone2.ui.main.album

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.flo_clone2.ui.main.home.HomeFragment
import com.example.flo_clone2.R
import com.example.flo_clone2.data.local.SongDatabase
import com.example.flo_clone2.databinding.FragmentAlbumBinding
import com.example.flo_clone2.data.entities.Album
import com.example.flo_clone2.data.entities.Like
import com.example.flo_clone2.ui.main.MainActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson

class AlbumFragment : Fragment() {

    lateinit var binding: FragmentAlbumBinding
    private var gson: Gson = Gson()
    private val information = arrayListOf("수록곡", "상세 정보", "영상")

    // 현재 들어간 앨범이 좋아요가 눌려 있는지를 나타내는 변수
    private var isLiked: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)

        // HomeFragment에서 생성한 argument에 들어있는 Json형태의 데이터를 다시 album 객체로 변환
        val albumJson = arguments?.getString("album")
        val album = gson.fromJson(albumJson, Album::class.java)
        // Home에서 넘어온 데이터를 반영
        isLiked = isLikedAlbum(album.id)
        setInit(album)
        setOnClickListeners(album)

        /**
         * TabLayout 하단 영역의 ViewPager 구현
         * SongFragment, DetailFragment, VideoFragment 와 albumContentVP를, albumAdapter를 통해 연결
         */
        val albumAdapter = AlbumVPAdapter(this)
        binding.albumContentVp.adapter = albumAdapter

        /**
         * TabLayout과 ViewPager을 연결하는 중재자
         * Tab이 선택될 때, ViewPager의 위치를 선택된 Tab과 동기화하고,
         * ViewPager가 슬라이드될 때, Tab의 위치를 동기화함.
         * 연결할 TabLayout과 ViewPager를 차례로 인자로 받음
         */
        TabLayoutMediator(binding.albumContentTb, binding.albumContentVp) {
                tab, position ->
            tab.text = information[position] // tab의 위치에 따라 tab 바에 있는 글자를 information list의 요소들로 지정
        }.attach() // TabLayout과 ViewPager를 붙여주는 코드

        return binding.root
    }

    /**
     * 넘겨받은 album 객체의 정보로 AlbumFragement에서의 앨범 제목, 가수명, 앨범 이미지를 재설정
     */
    private fun setInit(album: Album) {
        binding.albumAlbumIv.setImageResource(album.coverImg!!)
        binding.albumMusicTitleTv.text = album.title.toString()
        binding.albumSingerNameTv.text = album.singer.toString()

        if (isLiked) { // 현재 좋아요가 눌려져 있다면,
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else { // 현재 좋아요가 눌려져 있지 않다면,
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
    }

    /**
     * 현재 로그인 되어 있는 유저를 알기 위한 함수
     */
    private fun getJwt(): Int {
        // Fragment에서 Activity의 context를 가져오기 위해 activity?를 사용
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return spf!!.getInt("jwt", 0) // !!로 null 불가능을 명시하고, 디폴트값으로 0을 반환케 함. 이를 통해 0이 반환되면 jwt가 저장되어 있지 않다는 것을 알 수 있음
    }

    /**
     * 사용자가 앨범을 눌러 AlbumFragment로 왔을 때, 좋아요가 눌려 있는지 아닌지를 따지는 함수
     */
    private fun isLikedAlbum(albumId: Int): Boolean {
        val songDB = SongDatabase.getInstance(requireContext())!!
        val userId = getJwt()

        // 어떤 유저가 해당 앨범을 좋아요 했는지 확인해주기 위한 변수
        val likeId : Int? = songDB.albumDao().isLikeAlbum(userId, albumId)

        // 어떤 유저가 좋아요를 했다면, null이 아님. -> true
        // 좋아요를 하지 않았다면, likeId는 null이 되기 때문에 false를 반환
        return likeId != null
    }

    /**
     * 앨범에 좋아요를 눌러주었을 때 DB에 저장해주는 함수
     */
    private fun likeAlbum(userId: Int, albumId: Int) {
        val songDB = SongDatabase.getInstance(requireContext())!!
        val like = Like(userId, albumId) // 종아요를 눌렀을 때, LikeTable에 정보를 추가해주기 위해서

        songDB.albumDao().likeAlbum(like)
    }

    /**
     * 사용자가 앨범을 눌러 AlbumFragment로 왔을 때, 좋아요 누른 것을 해제하면 LikeTable에서 해당 유저와 앨범이 일치하는 항목을 지워주는 함수
     */
    private fun disLikeAlbum(userId: Int, albumId: Int){
        val songDB = SongDatabase.getInstance(requireContext())!!

        songDB.albumDao().disLikeAlbum(userId, albumId)
    }

    private fun setOnClickListeners(album: Album) {
        val userId = getJwt()

        /**
         * 좋아요 버튼 클릭 이벤트
         */
        binding.albumLikeIv.setOnClickListener {
            if (isLiked) { // 좋아요 -> 좋취
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
                Log.d("FLOW:AlbumFrag", "Like -> disLike")
                disLikeAlbum(userId, album.id)
            } else { // 좋취 -> 좋아요
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
                Log.d("FLOW:AlbumFrag", "disLike -> Like")
                likeAlbum(userId, album.id)
            }

            isLiked = !isLiked
        }

        /**
         * 뒤로 가기
         */
        binding.albumBackIv.setOnClickListener {
            // context sd : MainActivity 안에 있는 Fragment를 어디서 변경할 건지 명시하는 것.
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, HomeFragment())
                .commitAllowingStateLoss()
        }

    }
}