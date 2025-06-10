
package com.example.flo_clone2.ui.main

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo_clone2.ui.main.home.HomeFragment
import com.example.flo_clone2.R
import com.example.flo_clone2.data.entities.Album
import com.example.flo_clone2.data.entities.Song
import com.example.flo_clone2.data.local.SongDatabase
import com.example.flo_clone2.ui.song.SongActivity
import com.example.flo_clone2.databinding.ActivityMainBinding
import com.example.flo_clone2.ui.main.locker.LockerFragment
import com.example.flo_clone2.ui.main.look.LookFragment
import com.example.flo_clone2.ui.main.search.SearchFragment

class MainActivity : AppCompatActivity() {

    /**
     * 전역 변수 선언
     */
    lateinit var binding: ActivityMainBinding

    lateinit var timer: Timer
    private var mediaPlayer: MediaPlayer? = null

    lateinit var songDB: SongDatabase
    val songs = arrayListOf<Song>()
    var nowPos = 0
//    private var gson: Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setTheme(R.style.Theme_Flo) // 앱이 로드되어 MainActivity가 실행되면 다시 원래 테마로 바꾸어 줌.

        Log.d("Flow", "MainAct: onCreate")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // DB에 데이터 주입
        inputDummySongs()
        inputDummyAlbums()
        initPlayList()

        // 그냥 Intent만 사용해도 액티비티 전환을 일으킬 수 있음
        // startActivity를 활용해서 Intent를 수행할 수 있고,
        // 해당 Intent에는 어디(this)에서 어느 곳(SongActivity)로 이동해야 하는지를 명시.
//        binding.mainPlayerCl.setOnClickListener {
//            startActivity(Intent(this, SongActivity::class.java))
//        }

        /**
         * MainActicity의 miniplayer 클릭 시 SongActivity로의 전환과 함께, miniplayer에서의 곡 제목과 가수명을 넘겨주는 부분
         * 음악에 대한 정보를 담기 위해 Song data clss 생성
         * + 현재 재생 정도, 총 재생 시간, 재생 여부를 추가로 초기화
         */

//        SharedPreferences에 저장된 Song의 값을 가져올 것이므로, 지워도 됨.
//        val song = Song(binding.mainMiniplayerTitleTv.text.toString(), binding.mainMiniplayerSingerTv.text.toString(), 0, 60, false, "music_lilac")

        initClickListener()

        /**
         * BottomNavigation 사용을 위한 초기화
         */
        initBottomNavigation()
    }

    /**
     * Activity 전환이 될 때 onStart부터 실행됨.
     * Ex. MainActivity에서 SongActivity로 갔다가, SongActivity에서 창을 닫고 다시 MainActivity로 돌아갈 때는 onStart부터 수행됨. 따라서 SongActivity에서 저장한 SharedPreference의 값을 적용하기 위해서는 onStart에서의 작업이 필요함
     */
    override fun onStart() {
        super.onStart()
// --------------------------------------------------
//        /**
//         * Song Activity에서의 Song 데이터를 MainActivity에서 MiniPlayer에 반영해주는 작업
//         */
//
//        // SharedPrefereces에 있는 Song 데이터를 가져오기
//        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE) // song이라는 이름의 sharedPreferences를 불러옴
//        val songJson = sharedPreferences.getString("songData", null) // sharedPreferences의 songData를 불러와서 songJson에 저장
//        // 가져온 값읗 song객체에 담기
//        song = if(songJson == null) {
//            // 만약 실행이 처음이라서 sharedPreferences에 저장된 값이 없을 경우, Song을 다음과 같이 초기화해줌.
//            Song("라일락", "아이유 (iU)", 0, 60, false, "music_lilac")
//        } else {
//            // SharedPreferences에 데이터가 있어 Json 값을 가져올 수 있다면, fromJson을 통해 Json값을 Song 클래스의 java 객체로 변환. 이를 song = ...을 통해 song에 할당.
//            gson.fromJson(songJson, Song::class.java)
//        }
// ---------------------------- MusicDatabase를 활용하게됨으로써
// ---------------------------- SharedPreference를 통해 DB의 곡을 ID를 통해 다룰 수 있게 됨.

        Log.d("Flow", "MainAct: onStart")
        /**
         * SongActivity에서의 Song 데이터를 SharedPreference와 MusicDatabase를 통해 가져와서 MiniPlayer에 반영
         */
        val spf = getSharedPreferences("song", MODE_PRIVATE) // song이라는 이름의 sharedPreference를 spf에 할당
        val songId = spf.getInt("songId", 0) // spf에서 songId를 불러와서 그 값을 songId에 저장
        Log.d("song spf", "MainAct.onStart: " + spf.getInt("songId", -1).toString())

        nowPos = getPlayingSongPosition(songId)

        // Log로 가져온 Song의 ID 확인
        Log.d("Song ID", songs[nowPos].id.toString())

        /** Timer 객체를 생성 및 실핼 */
        startTimer()
        // 가져온 데이터를 MiniPlayer에 반영
        setMiniPlayer(songs[nowPos])
    }

    /**
     * SharedPreference에서 받아온 id를 통해 songs(리스트)에서 일치하는 song(데이터)의 위치를 반환하는 함수 작성
     */
    private fun getPlayingSongPosition(songId: Int): Int {
        for (i in 0 until songs.size) {
            if (songs[i].id == songId) {
                return i
            }
        }
        return 0
    }

    private fun initPlayList(){
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }

    private fun initClickListener(){
        /**
         * Miniplayer 클릭 시
         */
        binding.mainPlayerCl.setOnClickListener {
            Log.d("Flow", "MainAct: setOnClickListener")
            /** SongDatabase를 사용한 후 - SharedPreference를 활용하여 Song의 ID를 넘겨 SongActivity에서 그 값을 가지고 DB를 조회하도록 함 */
            val spf = getSharedPreferences("song", MODE_PRIVATE)
            Log.d("song spf", "MainAct.setOnClickListener: " + spf.getInt("songId", -1).toString())
            val editor = spf.edit() // SharedPreference 작업을 위해 editor 선언
            editor.putInt("songId", songs[nowPos].id) // editor에 songId를 넣고
            editor.apply() // 적용
            Log.d("song spf", "MainAct.setOnClickListener: " + spf.getInt("songId", -1).toString())

            /** SongActivity로 넘어갈 때, MiniPlayer 정지 및 리소스 해제 */
            setPlayerStatus(false)

            mediaPlayer?.release() // 리소스 해제
            timer.interrupt()

            val intent = Intent(this, SongActivity::class.java) // SongActivity 시작 intent
            startActivity(intent) // SongActivity 시작
        }

        /**
         * 음악이 재생 및 정지 중일때, 재생 버튼의 변화 처리
         */
        binding.mainMiniplayerBtn.setOnClickListener {
            // 음악이 재생 중일 때 정지 버튼 띄움
            setPlayerStatus(true)
        }
        binding.mainPauseBtn.setOnClickListener {
            // 음악이 정지 중일 때, 재생 버튼 띄움
            setPlayerStatus(false)
        }

        /**
         * 이전 곡 이동 버튼 클릭 시
         */
        binding.mainMiniplayerPreviousIv.setOnClickListener{
            moveSong(-1)
        }
        /**
         * 다음 곡 이동 버튼 클릭 시
         */
        binding.mainMiniplayerNextIv.setOnClickListener {
            moveSong(+1)
        }
    }

    /**
     * 곡 이동 메서드
     */
    private fun moveSong(direct: Int) {
        /** songs의 크기를 벗어나서 nowPos가 변동될 때 넘어가지 않도록 처리 */
        if (nowPos + direct < 0) {
            Toast.makeText(this, "first song", Toast.LENGTH_SHORT).show()
            return
        }
        if (nowPos + direct > songs.size-1) {
            Toast.makeText(this, "last song", Toast.LENGTH_SHORT).show()
            return
        }

        /** 이전 버튼, 이후 버튼을 누름에 따라 현재 곡을 나타내는 nowPos에 변동 */
        nowPos += direct

        /** 재설정한 Song 데이터(현재 실행 중인 곡)를 바탕으로 Thread를 종료 후 재시작 */
        timer.interrupt()
        startTimer()

        /** 재설정한 Song 데이터에 맞춰서 MediaPlayer를 초기화 */
        mediaPlayer?.release()
        mediaPlayer = null // 리소스 해제

        Log.d("Like-moveSong", songs[nowPos].isLike.toString())
        setMiniPlayer(songs[nowPos]) // Song 데이터에 맞추어서 mediaPlayer 재설정
    }

    /**
     * songId에 맞는 song 데이터로 현재 song 변수를 수정
     */
    private fun setSongById(songId: Int, songDB: SongDatabase): Song {
        val song = if (songId == 0) {
            // songId == 0이면, 저장된 songId가 없다는 의미이므로 가장 처음 인덱스의 노래를 가져옴
            songDB.songDao().getSong(1)
        } else {
            // songId != 이면, 저장된 songId가 있으므로 해당 Song의 데이터를 가져옴
            songDB.songDao().getSong(songId)
        }

        return song
    }

    /**
     * 재생 중이면, 정지 버튼.
     * 정지 중이면, 재생 버튼을 띄우는 함수
     * @param isPlaying 현재 재생 중인지의 여부
     */
    fun setPlayerStatus(isPlaying: Boolean) {
        // 현재 버튼에 따라 재생하고 정지하는 작업은 setPlayerStatus에서 이루어지고 있으므로
        // 음악 리소스의 재생 및 정지도 여기서 다루도록 함.
        songs[nowPos].isPlaying = isPlaying // 음악 재생 및 정지시 상태 반영
        timer.isPlaying =
            isPlaying // 음악 재생 및 정지시 timer에 상태를 반영하여 progressbar와 진행 시간 textView에 영향을 줌

        if (isPlaying) {
            // 재생 중
            binding.mainMiniplayerBtn.visibility = View.GONE
            binding.mainPauseBtn.visibility = View.VISIBLE
            mediaPlayer?.start() // 음악 재생
        } else {
            // 정지 중
            binding.mainMiniplayerBtn.visibility = View.VISIBLE
            binding.mainPauseBtn.visibility = View.GONE
            if (mediaPlayer?.isPlaying == true) { // mediaPlayer 재생 중이 아닐 때 mediaPlayer를 정지시키면, 문제가 생길 수 있으므로 해당 조건을 추가
                mediaPlayer?.pause() // 음악 정지
            }
        }
    }


    /**
     * MiniPlayer에 Song 데이터를 반영
     * @param song 전역 변수 Song. SongActivity에서 받은 Song 데이터가 들어있음.
     */
    fun setMiniPlayer(song: Song) {
        Log.d("Flow", "MainAct: setMiniPlayer")
        this.songs[nowPos] = setSongById(song.id, SongDatabase.getInstance(this)!!)
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainProgressSb.progress = (song.second*100000) / song.playTime // song.second * 100,000인 이유는 seekbar의 max가 100,000이기 때문

        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)
    }

    /**
     * BottomNavigation 활용을 위한 초기화
     */
    private fun initBottomNavigation() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener { item ->
            when(item.itemId) {

                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    /**
     * DB에 데이터가 없다면 Data를 넣어주는 작업이 필요함.
     * 더미 데이터 초기화하는 함수임
     */
    private fun inputDummySongs() {
        val songDB = SongDatabase.getInstance(this)!! // Song data를 넣을 MusicDatabase 객체
//        songDB.clearAllTables()

        // SongDB에 데이터가 있는지 확인하기 위해서는 SongDB의 데이터를 다 받아와야 할 것.
        val songs = songDB.songDao().getSongs()

        // 만약 데이터가 있다면(song이 비어있지 않음) 함수를 종료시키고, 데이터가 없다면(song이 비어있음) 더미 데이터를 넣어주어야 함.
        if (songs.isNotEmpty()) return

        songDB.songDao().insert(
            Song(
                "Butter",
                "방탄소년단 (BTS)",
                0,
                164,
                false,
                "music_butter",
                R.drawable.img_album_exp,
                false,
                0
            )
        )
        songDB.songDao().insert(
            Song(
                "LILAC",
                "아이유 (IU)",
                0,
                215,
                false,
                "music_lilac",
                R.drawable.img_album_exp2,
                false,
                1
            )
        )
        songDB.songDao().insert(
            Song(
                "Next Level",
                "에스파 (AESPA)",
                0,
                222,
                false,
                "music_next",
                R.drawable.img_album_exp3,
                false,
                2
            )
        )
        songDB.songDao().insert(
            Song(
                "Boy with LUV",
                "방탄소년단 (BTS)",
                0,
                230,
                false,
                "music_boy",
                R.drawable.img_album_exp4,
                false,
                3
            )
        )
        songDB.songDao().insert(
            Song(
                "BBoom BBoom",
                "모모랜드 (MOMOLAND)",
                0,
                209,
                false,
                "music_bboom",
                R.drawable.img_album_exp5,
                false,
                4
            )
        )
        songDB.songDao().insert(
            Song(
                "Weekend",
                "태연 (Tae Yeon)",
                0,
                234,
                false,
                "music_weekend",
                R.drawable.img_album_exp6,
                false,
                5
            )
        )
        songDB.songDao().insert(
            Song(
                "TOO BAD (feat. Anderson .Paak)",
                "G-DRAGON",
                0,
                154,
                false,
                "music_too_bad",
                R.drawable.img_album_exp7,
                false,
                6
            )
        )
        songDB.songDao().insert(
            Song(
                "like JENNIE",
                "제니 (JENNIE)",
                0,
                124,
                false,
                "music_like_jennie",
                R.drawable.img_album_exp8,
                false,
                7
            )
        )
        songDB.songDao().insert(
            Song(
                "모르시나요(PROD.로코베리)",
                "조째즈",
                0,
                302,
                false,
                "music_dont_you_know",
                R.drawable.img_album_exp9,
                false,
                8
            )
        )
        songDB.songDao().insert(
            Song(
                "Flu",
                "아이유 (IU)",
                0,
                189,
                false,
                "music_flu",
                R.drawable.img_album_exp2,
                false,
                1
            )
        )

        // 데이터가 잘 들어갔는지 log를 통해 확인
        val _songs = songDB.songDao().getSongs()
        Log.d("DB data", _songs.toString())
    }

    private fun inputDummyAlbums() {
        val albumDB = SongDatabase.getInstance(this)!!
        /** Album Table이 초기화 되었는지 확인 */
        val albums = albumDB.albumDao().getAlbums()
        if (albums.isNotEmpty()) return

        /** 초기화 */
        albumDB.albumDao().insert(
            Album(
                0,
                "Butter",
                "방탄소년단 (BTS)",
                R.drawable.img_album_exp
            )
        )
        albumDB.albumDao().insert(
            Album(
                1,
                "IU 5th Album 'LILAC'",
                "아이유 (IU)",
                R.drawable.img_album_exp2
            )
        )
        albumDB.albumDao().insert(
            Album(
                2,
                "Next Level",
                "에스파 (AESPA)",
                R.drawable.img_album_exp3
            )
        )
        albumDB.albumDao().insert(
            Album(
                3,
                "MAP OF THE SOUL:PERSONA",
                "방탄소년단 (BTS)",
                R.drawable.img_album_exp4
            )
        )
        albumDB.albumDao().insert(
            Album(
                4,
                "GREAT!",
                "모모랜드 (MOMOLAND)",
                R.drawable.img_album_exp5
            )
        )
        albumDB.albumDao().insert(
            Album(
                5,
                "Weekend",
                "태연 (Tae Yeon)",
                R.drawable.img_album_exp6
            )
        )
        albumDB.albumDao().insert(
            Album(
                6,
                "Übermensch",
                "G-DRAGON",
                R.drawable.img_album_exp7
            )
        )
        albumDB.albumDao().insert(
            Album(
                7,
                "Ruby",
                "제니 (JENNIE)",
                R.drawable.img_album_exp8
            )
        )
        albumDB.albumDao().insert(
            Album(
                8,
                "모르시나요",
                "조째즈",
                R.drawable.img_album_exp9
            )
        )
    }

    private fun startTimer() {
        timer = Timer(songs[nowPos].playTime, songs[nowPos].isPlaying)
        timer.start()
    }

    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true) : Thread() {
        private var second: Int = 0 // 진행 시간(초)
        private var mills: Float = 0f // 진행 시간(밀리세컨드). progressbar에서 100을 1000으로 나누어 세밀하게 표현해서.

        // Timer class가 호출되면 실행할 함수
        override fun run() {
            super.run()
            try {
                // progressbar 뷰 렌더링
                while (true) { // 무한루프가 아니라, isPlaying으로 해야 하는 거 아님? --> 아님. isPlaying으로 하게 되면, 중간에 음악을 정지한 경우에 스레드가 종료되기 때문에 다시 시작해주어야 하는 문제가 생김
                    if (second >= playTime) { // 진행 시간이 음악 길이와 같아지거나 커지면 종료
                        break
                    }
                    if (isPlaying) { // 음악 재생을 중지하면, isPlaying=false로 인해 해당 작업은 중지됨. 하지만, 스레드는 계속 실행 중이기에 효율적이지는 않음. 이를 멈추는 것에는 Interupt를 이용하는 방법이 있음.
                        sleep(50) // 50 밀리세컨드 동안 정지
                        mills += 50 // 정지된 50밀리 세컨드 반영

                        runOnUiThread { // 메인 스레드에 뷰 렌더링 요청
                            binding.mainProgressSb.progress =
                                ((mills / playTime) * 100).toInt() // mills를 playTime으로 나누고 퍼센테이지율로 환산하기 위해 100 곱함
                        }
                    }
                }

            } catch (e: InterruptedException){
                Log.d("Song", "스레드가 죽었습니다. ${e.message}")
            }
        }
    }
}