# Olico

App propia basada en **UI + YouTube/Innertube + player/lyrics + MediaSession** de BitChord,
sin web/docs/fixtures del monstruo completo.

## Qué se conserva de BitChord
- UI Compose (Home, Library, Search, Player, Lyrics, Settings)
- Innertube / YouTube Music + StreamResolver + NewPipe
- Reproducción en segundo plano + notificación (Media3 MediaSession)
- Módulo TV
- Conectores Spotify (código BitChord)

## Qué es Olico
- `applicationId` `com.music.olico`
- Nombre **Olico**
- Puente `com.music.olico.OlicoConfig` → Ukiyo + Kokoro

## Repo
Sube este árbol a GitHub como **olico**.

## Build
CI: `.github/workflows/android.yml`  
Local: Android Studio / `./gradlew :app:assembleProdRelease`
