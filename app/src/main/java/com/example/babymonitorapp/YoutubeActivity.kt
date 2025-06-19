package com.example.babymonitorapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope // For lifecycleScope.launch
import com.google.gson.Gson
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.example.babymonitorapp.PlaylistAdapter
import com.example.babymonitorapp.databinding.ActivityYoutubeBinding // Import ViewBinding class
import com.example.babymonitorapp.PlaylistItemsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException
import kotlin.io.path.inputStream
import androidx.core.view.isVisible
import com.google.android.material.bottomnavigation.BottomNavigationView

class YoutubeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityYoutubeBinding
    private var youtubePlayerInstance: YouTubePlayer? = null
    private lateinit var playlistAdapter: PlaylistAdapter
    private var bottomNav: BottomNavigationView? = null

    // --- Configuration ---
    // !!! IMPORTANT: Replace with your actual Playlist ID !!!
    private val playlistId = BuildConfig.PLAYID// Your Baby Shark playlist

    // !!! SECURITY WARNING: Storing API keys in client-side code is not recommended for production.
    // Consider using a backend server to make API requests.
    private val apiKey = BuildConfig.YTAPI // REPLACE WITH YOUR VALID API KEY

    private val tag = "YoutubeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYoutubeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupYoutubePlayer()
        setupRecyclerView()

        fetchPlaylistItems()

        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, MainActivity3::class.java))
                    true
                }
                R.id.baby -> {
                    startActivity(Intent(this, YoutubeActivity::class.java))
                    true
                }
                R.id.community -> {
                    startActivity(Intent(this, Community::class.java))
                    true
                }
                R.id.settings -> {
                    startActivity(Intent(this, SettingsView::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupYoutubePlayer() {
        lifecycle.addObserver(binding.youtubePlayerView) // Essential for lifecycle management

        binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youtubePlayerInstance = youTubePlayer
                // Optionally, load the first video of the playlist once fetched and adapter is ready
            }
        })
    }

    private fun setupRecyclerView() {
        playlistAdapter = PlaylistAdapter { videoId ->
            youtubePlayerInstance?.loadVideo(videoId, 0f)
            // Optionally scroll player into view or highlight selected item
        }
        binding.recyclerViewPlaylist.adapter = playlistAdapter
        // Layout manager is set in XML, but can also be set here:
        // binding.recyclerViewPlaylist.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchPlaylistItems() {
        binding.progressBarPlaylist.visibility = View.VISIBLE
        binding.textErrorPlaylist.visibility = View.GONE

        lifecycleScope.launch(Dispatchers.IO) { // Use lifecycleScope for coroutines tied to Activity lifecycle
            try {
                val apiUrl = "https://www.googleapis.com/youtube/v3/playlistItems" +
                        "?part=snippet" +
                        "&playlistId=$playlistId" +
                        "&maxResults=50" + // Max is 50 per request
                        "&key=$apiKey"

                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 15000 // 15 seconds
                connection.readTimeout = 15000   // 15 seconds

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()
                    reader.close()

                    val playlistResponse = Gson().fromJson(response, PlaylistItemsResponse::class.java)

                    withContext(Dispatchers.Main) {
                        binding.progressBarPlaylist.visibility = View.GONE
                        if (playlistResponse != null && playlistResponse.items.isNotEmpty()) {
                            playlistAdapter.submitList(playlistResponse.items)
                            // Optionally load the first video
                            playlistResponse.items.firstOrNull()?.snippet?.resourceId?.videoId?.let { firstVideoId ->
                                // Check if player is ready, otherwise this might be called too early
                                // youtubePlayerInstance?.loadVideo(firstVideoId, 0f)
                            }
                        } else {
                            showError("Playlist is empty or no items found.")
                            Log.w(tag, "Playlist empty or null response. Raw: $response")
                        }
                    }
                } else {
                    val errorBody = connection.errorStream?.bufferedReader()?.readText() ?: "No error body"
                    Log.e(tag, "Error fetching playlist. Code: $responseCode, Message: ${connection.responseMessage}, Body: $errorBody")
                    withContext(Dispatchers.Main) {
                        showError("Error: ${connection.responseMessage} ($responseCode)")
                    }
                }
                connection.disconnect()
            } catch (e: UnknownHostException) {
                Log.e(tag, "Network error fetching playlist", e)
                withContext(Dispatchers.Main) {
                    showError("Network error. Please check your internet connection.")
                }
            } catch (e: Exception) {
                Log.e(tag, "Exception fetching playlist", e)
                withContext(Dispatchers.Main) {
                    showError("Error: ${e.localizedMessage ?: "An unexpected error occurred."}")
                }
            } finally {
                withContext(Dispatchers.Main) {
                    if (binding.progressBarPlaylist.isVisible) { // Ensure progress bar is hidden if an early error occurs
                        binding.progressBarPlaylist.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun showError(message: String) {
        binding.progressBarPlaylist.visibility = View.GONE
        binding.textErrorPlaylist.text = message
        binding.textErrorPlaylist.visibility = View.VISIBLE
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


    // `onDestroy` is handled by `lifecycle.addObserver(binding.youtubePlayerView)` for the player view
    // No need for explicit `youtubePlayerView.release()` if using lifecycle observer correctly.
}