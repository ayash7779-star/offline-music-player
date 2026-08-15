# Offline Music Player (Android)

A clean, offline-first Android music player built with **Kotlin + Jetpack Compose + Media3 (ExoPlayer)**. Plays local MP3/audio files from device storage — no streaming, no ads, no internet required.

## Features

- **Play / Pause / Seek** with a scrubber and time display
- **Playlist** auto-populated from on-device audio via MediaStore
- **Local folder MP3 loading** (reads all music on device automatically)
- **Volume control** slider
- **Bass Boost** slider (stub — wire to Media3 Effect pipeline for production)
- **Album art** display via Coil
- **Lyrics** display (when available in metadata)
- **Foreground service** with MediaSession for background playback + notification controls

## Tech Stack

| Layer | Choice |
|-------|--------|
| UI | Jetpack Compose + Material 3 |
| Media | Media3 ExoPlayer + MediaSession |
| Image loading | Coil |
| Architecture | ViewModel + LiveData (single-activity) |
| Build | Gradle Kotlin DSL |
| CI/CD | GitHub Actions → auto APK build on push |

## Project Structure

```
app/src/main/java/com/example/musicplayer/
├── MainActivity.kt              # Entry point, permission handling
├── data/AudioItem.kt            # Song data class
├── util/AudioLoader.kt           # MediaStore loader
├── service/PlaybackService.kt    # Foreground MediaSessionService (ExoPlayer)
├── ui/
│   ├── PlayerScreen.kt           # Compose UI (now-playing card + playlist)
│   ├── PlayerViewModel.kt        # State + MediaController bridge
│   └── theme/Theme.kt            # Material 3 dynamic theming
```

## GitHub Actions CI/CD

The workflow at `.github/workflows/build.yml` automatically:
1. Checks out the code on every push/PR to `main`/`master`
2. Sets up JDK 17 + Gradle
3. Builds the debug APK (`./gradlew assembleDebug`)
4. Uploads the APK as a downloadable workflow artifact (30-day retention)

After a push, download the APK from: **Actions tab → latest run → Artifacts → `offline-music-player-debug-apk`**

## Build Locally

```bash
# Prerequisites: Android Studio Hedgehog+, JDK 17, Android SDK (compileSdk 34)
git clone <your-repo-url>
cd offline-music-player
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

## Getting the Gradle Wrapper (first-time setup)

If `gradle/wrapper/gradle-wrapper.jar` is not present (it's a binary, not committed here), generate it once:

```bash
gradle wrapper --gradle-version 8.7
```

Then commit the generated `gradlew`, `gradlew.bat`, and `gradle/wrapper/*` files.

## Permissions

- `READ_MEDIA_AUDIO` (Android 13+) or `READ_EXTERNAL_STORAGE` (≤ Android 12)
- `FOREGROUND_SERVICE` + `FOREGROUND_SERVICE_MEDIA_PLAYBACK` for background playback

## Notes

- Bass Boost is a UI stub; integrate `androidx.media3.common.audio.BassBoost` via the ExoPlayer audio processor chain for actual effect.
- Lyrics: this scaffold reads `AudioItem.lyrics`; extend `AudioLoader` to parse `.lrc` files or ID3 USLT frames for real lyrics.
- Album art is fetched from MediaStore's album-art URI.

## License

MIT — free to use, modify, and distribute.
