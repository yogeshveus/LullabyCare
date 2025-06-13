package com.example.babymonitorapp

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.babymonitorapp.databinding.ActivityMediaPlayerBinding

class MediaPlayerActivity : AppCompatActivity() {

    lateinit var binding: ActivityMediaPlayerBinding

    lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMediaPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url="https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        player = ExoPlayer.Builder(this).build()
        binding.playerView.player=player

        val mediaItem=MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()



   }

    override fun onStart() {
        super.onStart()
        player.playWhenReady = true
    }

    override fun onStop() {
        super.onStop()
        player.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}

