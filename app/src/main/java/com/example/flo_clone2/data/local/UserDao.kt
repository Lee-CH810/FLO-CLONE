package com.example.flo_clone2.data.local

import androidx.room.*
import com.example.flo_clone2.data.entities.User

@Dao
interface UserDao {
    @Insert
    fun insert(user: User)

    /** 전체 조회 */
    @Query("SELECT * FROM UserTable")
    fun getUsers(): List<User>

    /** 입력한 이메일과 비밀번호로 일치하는 user 찾기
     * 정보가 있을 수도, 없을 수도 있으니 User?를 리턴*/
    @Query("SELECT * FROM UserTable WHERE email = :email AND password = :password")
    fun getUser(email: String, password: String): User?
}