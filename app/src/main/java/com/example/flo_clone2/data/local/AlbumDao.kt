package com.example.flo_clone2.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.flo_clone2.data.entities.Album
import com.example.flo_clone2.data.entities.Like

@Dao
interface AlbumDao {
    @Insert
    fun insert(album: Album)

    @Update
    fun update(album: Album)

    @Delete
    fun delete(album: Album)

    /**
     * id에 맞는 Album 반환
     */
    @Query("SELECT * FROM AlbumTable WHERE id = :id")
    fun getAlbum(id: Int): Album

    /**
     * Album 전체 조회
     */
    @Query("SELECT * FROM AlbumTable")
    fun getAlbums(): List<Album>
    // ArrayList를 반환할 시, Not sure how to convert a Cursor to this method's return type 해당 오류 발생
    // --> List 반환!

    /**
     * LikeTable에 사옹자가 좋아요한 데이터를 추가해주는 insert 구문
     */
    @Insert
    fun likeAlbum(like: Like)

    /**
     * Album Fragment에 들어갈 때 사용자가 좋아요를 눌렀는지, 누르지 않았는지를 확인해주는 함수
     */
    // Like 표시를 하여, LikeTable에 인자로 넘긴 userId와 albumId를 갖는 항목이 있는지 조회
    // 만약에 해당하는 항목이 없다면, null이므로 리턴값을 nullable하게 설정
    @Query("SELECT id FROM LikeTable WHERE userId = :userId AND albumId = :albumId")
    fun isLikeAlbum(userId: Int, albumId:Int): Int?

    /**
     * 좋아요 취소
     */
    @Query("DELETE FROM LikeTable WHERE userId = :userId AND albumId = :albumId")
    fun disLikeAlbum(userId:Int, albumId: Int)

    /**
     * 보관함에서 유저들을 구분하여 좋아요한 앨범들을 가져오는 쿼리
     */
    // LEFT JOIN: ON 구문에서 왼쪽에 있는 테이블을 기준으로 다른 테이블을 붙여주는(JOIN) 것
    @Query("SELECT AT.* FROM LikeTable as LT " +
            "LEFT JOIN AlbumTable as AT ON LT.albumId = AT.id " +
            "WHERE LT.userId = :userId")
    fun getLikedAlbums(userId:Int) : List<Album>
}
// 이제, MainActivity에서 AlbumTable에 데이터를 추가해주고,
// AlbumTable에서 데이터를 가져와서
// 기존에 HomeFragment에서 추가해준 더미데이터 부분만 변경