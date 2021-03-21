package com.example.chintesample2

import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionUtil
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes

class MainActivity : AppCompatActivity() {


    //initialize variables
    lateinit var playerView: PlayerView
    lateinit var progressBar: ProgressBar
    lateinit var btFullScreen: ImageView
    lateinit var simpleExoPlayer: SimpleExoPlayer
    var flag: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {

        AppCenter.start(
            application, "6b10bd79-31f1-4f72-adc7-66fe44c557ba",
            Analytics::class.java, Crashes::class.java
        )


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Assign Variables

        playerView = findViewById(R.id.player_view)
        progressBar = findViewById(R.id.progress_bar)
        btFullScreen = playerView.findViewById(R.id.bt_fullscreen)
        val videoUrl = "https://i.imgur.com/7bMqysJ.mp4"
        initPlayer()
        loadVod(videoUrl)

        btFullScreen.setOnClickListener(View.OnClickListener {

          if(flag){
              btFullScreen.setImageDrawable(getDrawable(R.drawable.ic_fullscreen))
              requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

              flag = false
          }else{

              btFullScreen.setImageDrawable(getDrawable(R.drawable.ic_fullscreen_exit))
              requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

              flag = true

          }


        })

    }

    fun initPlayer(){
        //Make Activity
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)

        //Video url


        //initialize load control
        val loadcontrol = DefaultLoadControl()
        //iniditalize band width
        val bandwithMeter = DefaultBandwidthMeter.Builder(baseContext).build()

        //initialize track selector
        val adaptiveTrackSelection = AdaptiveTrackSelection.Factory()
        val trackSelector = DefaultTrackSelector()


        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this,DefaultTrackSelector(adaptiveTrackSelection),loadcontrol)

        playerView.player = simpleExoPlayer
        simpleExoPlayer.addListener(object : Player.EventListener {

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when(playbackState){
                    ExoPlayer.STATE_BUFFERING -> progressBar.visibility = View.VISIBLE
                    ExoPlayer.STATE_READY -> progressBar.visibility = View.GONE
                }
            }
        })
    }

    fun loadVod(url: String){
        val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "Exo"),DefaultBandwidthMeter())
        val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url))

        simpleExoPlayer.prepare(mediaSource)
        simpleExoPlayer.playWhenReady = true

    }



    override fun onPause() {
        super.onPause()
        simpleExoPlayer.playWhenReady = false
        simpleExoPlayer.playbackState
    }

    override fun onRestart() {
        super.onRestart()
        simpleExoPlayer.playWhenReady = true
        simpleExoPlayer.playbackState
    }
}