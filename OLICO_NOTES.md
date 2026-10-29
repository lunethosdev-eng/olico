# Olico — SourceRegistry wiring

## Defaults
- **Ukiyo** seeded enabled (`OlicoConfig.MUSIC_API_BASE` + `/api/search`, `/api/songs`)
- **YouTube Music** seeded **disabled** — enable in Settings → Sources (opt-in)
- YouTube can be turned **off** (BitChord used to block that)

## Files
- `com.music.olico.OlicoConfig`
- `com.music.olico.ukiyo.UkiyoClient`
- `com.music.bitchord.data.sources.UkiyoSource`
- patches in `SourceRegistry.kt`

## UI
Unchanged BitChord UI; only source policy changed.

## Fresh install / clear data
If you already had BitChord prefs on device, clear app data once so the new seed applies.
