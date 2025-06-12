# FLO 앱 클론 코딩

유데미 강의를 통해 공부하며 FLO 앱 클론 코딩을 진행함

### 구조

#### data
<ul>
  entities : 데이터 클래스
  <ul>
    <li>Album</li>
    <li>Like</li>
    <li>Song</li>
    <li>User</li>
  </ul>
  <br/>
  local : Dao 및 DataBase
  <ul>
    <li>SongDataBase</li>
    <li>AlbumDao</li>
    <li>SongDao</li>
    <li>UserDao</li>
  </ul>  
</ul>

#### ui
<ul>
  main : MainActivity와 그 화면을 구성하는 Fragment 파일들
  <ul>
    <li>MainActivity</li>
  </ul>
  <br/>
  home : HomeFragment와 그에 관련된 파일
  <ul>
    <li>HomeFragment</li>
    <li>BannerFragment</li>
    <li>BannerVPAdapter</li>
    <li>AlbumRVAdapter</li>
  </ul>
  <br/>
  look : LookFragment와 그에 관련된 파일 -> 현재 구현 X
  <ul>
    <li>LookFragment</li>
  </ul>
  <br/>
  search : SearchFragment와 그에 관련된 파일 -> 현재 구현 X
  <ul>
    <li>SearchFragment</li>
  </ul>
  <br/>
  locker : LockerFragment와 그에 관련된 파일 -> 현재 MusicFileFragment 구현 X
  <ul>
    <li>LockerFragment</li>
    <li>LockerVPAdapter</li>
    <li>SavedSongFragment</li>
    <li>SavedSongRVAdapter</li>
    <li>SavedAlbumFragment</li>
    <li>SavedAlbumRVAdapter</li>
    <li>MusicFileFragment</li>
  </ul>
  <br/>
  album : HomeFragment에서 앨범 아이템을 눌러 전환되는 Fragment
  <ul>
    <li>AlbumFragment</li>
    <li>AlbumVPAdapter</li>
    <li>DetailFragment</li>
    <li>SongFragment</li>
    <li>VideoFragment</li>
  </ul>
  <br/>
  signup : 회원가입과 관련된 Activity
  <ul>
    <li>SignupActivity</li>
  </ul>
  <br/>
  login : 로그인과 관련된 Activity
  <ul>
    <li>LoginActivity</li>
  </ul>
  <br/>
  song : 노래 재생 화면과 관련된 Activity
  <ul>
    <li>SongActivity</li>
  </ul>  
</ul>

<hr/>

### 구현 현황
<ol>
  <li>Look, Search, MusicFile Fragment가 구현되지 않음</li>
  <li>SignUp, Login 과정을 구현하면서, Song과 관련되어 Like 테이블을 분리하고 그에 맞추어서 리팩토링하는 과정이 필요</li>
</ol>

### 실행 영상

https://github.com/user-attachments/assets/7f252828-c6b1-40e6-b140-61c89731f385

