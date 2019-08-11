package com.example.music_app.Presenter



import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent



import android.provider.MediaStore
import android.util.Log

import androidx.core.app.NotificationManagerCompat
import com.example.music_app.AppConstant
import com.example.music_app.AppConstant.NOTIFICATION_ID


import com.example.music_app.Contract.MainContract
import com.example.music_app.Model.Entity.Entity.Song
import com.example.music_app.Service.MusicPlayService




class MainPresenter(view: MainContract.View): MainContract.Presenter {
//    override fun onReceive(p0: Context?, p1: Intent?) {
//        val action = p1?.action
//        if(AppConstant.PREV_ACTION.equals(action)){
//            Log.i("abc","$action")
//            playPrev()
//        } else if(AppConstant.PLAY_ACTION.equals(action)){
//            Log.i("abc","$action")
//            pause()
//        } else if(AppConstant.NEXT_ACTION.equals(action)){
//            Log.i("abc","$action")
//            playNext()
//        }
//    }
   
    private var view: MainContract.View? = view
    private lateinit var musicPlayservice: MusicPlayService
    val songs = ArrayList<Song>()
    private var currentPos: Int? = 0
//    private lateinit var notificationManager: NotificationManagerCompat

    override fun seekTo(position: Int) {
        musicPlayservice.seekTo(position)
    }
    fun getPos(): Int? {
        return musicPlayservice.getPos()
    }
//    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//    fun showNotification(){
//        notification = musicPlayservice.getMusicNotification()?.createNotification()!!
//        musicPlayservice.getMusicNotification()?.getNotification()!!.notify(MusicNotification.NOTIFICATION_ID,notification)
//
//    }

//    fun getSong(): Song {
//        return songs[currentPos!!]
//    }

    override fun setService(service: MusicPlayService) {
        musicPlayservice = service

    }


    override fun playSong(position: Int) {
        currentPos = position
        val songId = songs[currentPos!!].id
        val songTitle = songs[currentPos!!].title
        val songDuration = songs[currentPos!!].duration
        musicPlayservice.playMusic(songId)

        notification()

        view?.showPlayer(songTitle,songDuration)

    }

    override fun loadPlaylist(context: Context){
        val resolver = context.contentResolver
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION
        )
        val cursor = resolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null)
        if(cursor != null && cursor.moveToFirst()){
            val songTitle: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songId: Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            var songDuration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            do{
                val currentTitle: String = cursor.getString(songTitle)
                val currentArtist: String = cursor.getString(songArtist)
                val currentId: Int = cursor.getInt(songId)
                val currentDuration = cursor.getInt(songDuration)
                songs.add(Song(currentId, currentTitle, currentArtist, currentDuration))
            }while(cursor.moveToNext())
        }
        view?.showList(songs)
    }

    override fun playNext() {
        currentPos = currentPos?.plus(1)
        playSong(currentPos!!)
    }


    fun getPresenter(): MainPresenter {
        return this
    }
    override fun playPrev() {
        currentPos = currentPos?.minus(1)
        playSong(currentPos!!)
    }

    override fun pause(): Boolean {
        return musicPlayservice.pause()
    }

    fun notification(){
        val notification = musicPlayservice.getNotification()!!.createNotification(songs[currentPos!!])
        with(NotificationManagerCompat.from(musicPlayservice)){
            notify(NOTIFICATION_ID,notification)
        }

    }

}
