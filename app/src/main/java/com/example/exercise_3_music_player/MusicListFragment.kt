package com.example.exercise_3_music_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class MusicListFragment : Fragment() {

    private var player: ExoPlayer? = null
    private lateinit var songTitleText: TextView
    private lateinit var statusText: TextView
    private var currentPosition = 0

    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_music_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Views
        songTitleText = view.findViewById(R.id.songTitle)
        statusText = view.findViewById(R.id.statusText)
        val listView = view.findViewById<ListView>(R.id.songsListView)

        // Setup Adapter
        val adapter = ArrayAdapter(requireContext(), R.layout.white_text_item, songs)
        listView.adapter = adapter

        // List Click Listener
        listView.setOnItemClickListener { _, _, position, _ ->
            currentPosition = position
            playSong()
        }

        // Control Button Listeners
        view.findViewById<Button>(R.id.playButton).setOnClickListener { player?.play() }
        view.findViewById<Button>(R.id.pauseButton).setOnClickListener { player?.pause() }
        view.findViewById<Button>(R.id.stopButton).setOnClickListener {
            player?.stop()
            updateStatus("Stopped")
        }

        // Prev/Next Button Listeners
        view.findViewById<Button>(R.id.btnPrevious).setOnClickListener {
            currentPosition = if (currentPosition - 1 < 0) songs.size - 1 else currentPosition - 1
            playSong()
        }
        view.findViewById<Button>(R.id.btnNext).setOnClickListener {
            currentPosition = (currentPosition + 1) % songs.size
            playSong()
        }
    }

    private fun playSong() {
        val fullData = songs[currentPosition]
        val title = fullData.substringBefore(" - ")
        val url = fullData.substringAfter(" - ")

        songTitleText.text = title

        player?.let {
            it.stop()
            it.setMediaItem(MediaItem.fromUri(url))
            it.prepare()
            it.play()
        }
    }

    private fun updateStatus(status: String) {
        statusText.text = "Status: $status"
    }

    override fun onStart() {
        super.onStart()
        if (player == null) {
            player = ExoPlayer.Builder(requireContext()).build()
            player?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    updateStatus(if (isPlaying) "Playing" else "Paused")
                }
            })
        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}