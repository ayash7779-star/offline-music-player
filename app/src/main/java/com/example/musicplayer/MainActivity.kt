package com.example.musicplayer

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.musicplayer.ui.PlayerScreen
import com.example.musicplayer.ui.PlayerViewModel
import com.example.musicplayer.ui.theme.OfflineMusicPlayerTheme
import com.example.musicplayer.util.AudioLoader

class MainActivity : ComponentActivity() {

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) reload()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        PlayerViewModel.get(this) // connect MediaController early
        reload()
        setContent {
            OfflineMusicPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlayerScreen(onRequestPermission = { requestPermission.launch(perm()) })
                }
            }
        }
    }

    private fun reload() {
        val vm = PlayerViewModel.get(this)
        vm.setQueue(AudioLoader.loadAll(this))
    }

    private fun perm() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_AUDIO
        else
            Manifest.permission.READ_EXTERNAL_STORAGE
}
