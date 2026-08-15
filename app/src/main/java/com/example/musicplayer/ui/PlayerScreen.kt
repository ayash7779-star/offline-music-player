package com.example.musicplayer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicplayer.data.AudioItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    onRequestPermission: () -> Unit,
    vm: PlayerViewModel = viewModel()
) {
    val items by vm.items.observeAsState(emptyList())
    val now by vm.nowPlaying.observeAsState()
    val playing by vm.isPlaying.observeAsState(false)
    val pos by vm.position.observeAsState(0L)
    val dur by vm.duration.observeAsState(0L)

    Scaffold(topBar = {
        TopAppBar(title = { Text("Offline Music") })
    }) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {

            now?.let { song -> NowPlayingCard(song, playing, pos, dur, vm) }

            LazyColumn(Modifier.fillMaxSize()) {
                items(items) { item ->
                    PlaylistRow(item, item == now) { }
                    HorizontalDivider()
                }
            }

            if (items.isEmpty()) {
                Column(Modifier.padding(16.dp)) {
                    Text("No music loaded yet.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onRequestPermission) {
                        Text("Allow access to music")
                    }
                }
            }
        }
    }
}

@Composable
private fun NowPlayingCard(
    song: AudioItem,
    playing: Boolean,
    pos: Long,
    dur: Long,
    vm: PlayerViewModel
) {
    Column(Modifier.padding(16.dp)) {
        Box(
            Modifier.size(200.dp).clip(RoundedCornerShape(12.dp)).align(Alignment.CenterHorizontally)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(song.albumArt).build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(song.title, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(song.artist, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))

        song.lyrics?.let {
            Text(it, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(12.dp))
        }

        Slider(
            value = pos.toFloat().coerceAtMost(dur.toFloat().coerceAtLeast(1f)),
            onValueChange = { vm.seekTo(it.toLong()) },
            valueRange = 0f..(dur.toFloat().coerceAtLeast(1f))
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(fmt(pos)); Text(fmt(dur))
        }

        Row(
            Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { vm.prev() }) { Icon(Icons.Default.SkipPrevious, "Previous") }
            FilledIconButton(onClick = { vm.togglePlay() }) {
                Icon(if (playing) Icons.Default.Pause else Icons.Default.PlayArrow, "Play/Pause")
            }
            IconButton(onClick = { vm.next() }) { Icon(Icons.Default.SkipNext, "Next") }
        }

        var vol by remember { mutableStateOf(1f) }
        var bass by remember { mutableStateOf(0f) }
        Text("Volume")
        Slider(vol, { vol = it; vm.setVolume(it) })
        Text("Bass Boost")
        Slider(bass, { bass = it; vm.setBassBoost((it * 1000).toInt()) })
    }
}

@Composable
private fun PlaylistRow(item: AudioItem, active: Boolean, onTap: () -> Unit) {
    ListItem(
        headlineContent = { Text(item.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        supportingContent = { Text(item.artist, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        leadingContent = { Icon(Icons.Default.MusicNote, null) },
        modifier = Modifier.clickable { onTap() }
    )
}

private fun fmt(ms: Long): String {
    val s = ms / 1000
    return "%d:%02d".format(s / 60, s % 60)
}
