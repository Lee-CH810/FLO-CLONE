package com.example.flo_clone2.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo_clone2.data.local.SongDatabase
import com.example.flo_clone2.ui.signup.SignupActivity
import com.example.flo_clone2.databinding.ActivityLoginBinding
import com.example.flo_clone2.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /** 회원가입 버튼 */
        binding.loginSignUpTv.setOnClickListener{
            // 현재 위치가 activity이므로 this 라고 명시
            startActivity(Intent(this, SignupActivity::class.java))
        }

        /** 로그인 버튼 */
        binding.loginSignInBtn.setOnClickListener{
            login()
        }

    }

    private fun login() {
        /** 로그인 시 입력 정보의 유효성 처리 */
        if (binding.loginIdEt.text.toString().isEmpty() || binding.loginDirectInputEt.text.toString().isEmpty()) {
            Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.loginPasswordEt.text.toString().isEmpty()) {
            Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        /** 로그인 과정 */
        val email : String = binding.loginIdEt.text.toString() + "@" + binding.loginDirectInputEt.text.toString()
        val pwd : String = binding.loginPasswordEt.text.toString()

        val songDB = SongDatabase.getInstance(this)!!
        val user = songDB.userDao().getUser(email, pwd) // email과 password의 일치를 따짐

        // let문 안의 블록이 실행되는 경우는 DB에서 해당 유저를 찾았을 경우임
        // let문이 null check가 가능한 듯
        user?.let {
            Log.d("LOGIN_ACT/GET_USER", "userId : ${user.id}, $user")
            saveJWT(user.id) // user의 id를 넢어 JWT에 저장
            startMainActivity()
        }
        Toast.makeText(this, "회원 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show() // 회원정보가 없다면 표시됨
    }

    /**
     * 인자값으로 받은 JWT를 sharedPreference에 저장
     * 시작하기 전에 user table의 id를 jwt로 대체하여 진행하기로 약속했음
     */
    private fun saveJWT(jwt: Int) {
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        val editor = spf.edit()

        editor.putInt("jwt", jwt) // jwt라는 키값으로 jwt를 저장
        editor.apply()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}