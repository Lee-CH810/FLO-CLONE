package com.example.flo_clone2

import androidx.room.*

@Dao
interface SongDao {
    @Insert
    fun insert(song: Song)

    @Update
    fun update(song: Song)

    @Delete
    fun delete(song: Song)

    @Query("SELECT * FROM SongTable")
    fun getSongs(): List<Song>

    @Query("SELECT * FROM SongTable WHERE id = :id")
    fun getSong(id: Int): Song

    /**
     * 좋아요에 대한 DB 업데이트
     */
    @Query("UPDATE SongTable SET isLike= :isLike WHERE id = :id")
    fun updateIsLikeById(isLike: Boolean,id: Int)

    @Query("SELECT * FROM SongTable WHERE isLike= :isLike")
    fun getLikedSongs(isLike: Boolean): List<Song>

    /**
     * 앨범 index를 통해 Song 조회
     */
    @Query("SELECT id FROM SongTable WHERE albumIdx = :albumIdx")
    fun getSongByAlbumIdx(albumIdx: Int): Int

    @Query("SELECT * FROM SongTable WHERE albumIdx = :albumIdx")
    fun getSongsByAlbumIdx(albumIdx: Int): List<Song>
}