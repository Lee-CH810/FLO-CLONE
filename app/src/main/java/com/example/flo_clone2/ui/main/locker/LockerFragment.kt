package com.example.flo_clone2.ui.main.locker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.flo_clone2.data.local.SongDatabase
import com.example.flo_clone2.ui.login.LoginActivity
import com.example.flo_clone2.databinding.FragmentLockerBinding
import com.example.flo_clone2.ui.main.MainActivity
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment : Fragment() {

    lateinit var binding: FragmentLockerBinding
    private val tabInfo = arrayListOf("저장된 곡", "음악 파일", "저장 앨범")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerBinding.inflate(layoutInflater, container, false)

        val lockerAdapter = LockerVPAdapter(this)
        binding.lockerTabVp.adapter = lockerAdapter

        TabLayoutMediator(binding.lockerTb, binding.lockerTabVp){
                tab, position ->
            tab.text = tabInfo[position]
        }.attach()

        binding.lockerLogin.setOnClickListener{
            // 현재 위치가 fragment이므로 activity라고 context를 명시
            startActivity(Intent(activity, LoginActivity::class.java))
        }

        val songDB = SongDatabase.getInstance(requireContext())!!
        val userId = getJwt()
        val likedAlbums = songDB.albumDao().getLikedAlbums(userId)

        Log.d("LockerFrag/GetAlbums", likedAlbums.toString())

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        initViews()
    }

    /**
     * 현재 sharedPreference에 JWT값이 저장되어 있는지 확인
     * LockerFragment에서 로그인 상황인지, 로그아웃 상황인지 구분하기 위해 사용
     */
    private fun getJwt(): Int {
        // Fragment에서 Activity의 context를 가져오기 위해 activity?를 사용
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return spf!!.getInt("jwt", 0) // !!로 null 불가능을 명시하고, 디폴트값으로 0을 반환케 함. 이를 통해 0이 반환되면 jwt가 저장되어 있지 않다는 것을 알 수 있음
    }

    /**
     * getJwt를 통해 알아낸 정보를 통해 "로그인"을 띄울 지, "로그아웃"을 띄울 지를 경정
     */
    private fun initViews() {
        val jwt : Int = getJwt()
        if (jwt == 0) { // 로그인 되어 있지 않은 상황
            binding.lockerLogin.text = "로그인"
            binding.lockerLogin.setOnClickListener{ // 로그인 글자 누르면 LoginActivity로 이동
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        } else { // 로그인 되어 있는 상황
            binding.lockerLogin.text = "로그아웃"
            binding.lockerLogin.setOnClickListener{
                // 로그아웃 진행
                logout()
                startActivity(Intent(activity, MainActivity::class.java)) // 로그아웃과 함께 MainActivity로 이동
            }
        }

    }

    /**
     * 로그아웃 함수
     */
    private fun logout() {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        val editor = spf!!.edit()

        editor.remove("jwt") // jwt라는 키 값에 저장된 값을 삭제함
        editor.apply()
    }
}