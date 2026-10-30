# Google Sign-In → Kokoro (Pokke) — guía

Objetivo: **un solo login** con Google sirve para YouTube Music **y** cuenta Pokke (Supabase Kokoro).

## 1. Google Cloud Console
1. Entra a https://console.cloud.google.com/
2. Crea un proyecto (ej. **Pokke**).
3. **APIs y servicios → Pantalla de consentimiento OAuth**  
   - Tipo: Externo (o Interno si es Workspace).  
   - Nombre app: Pokke.  
   - Añade tu email de contacto.
4. **Credenciales → Crear credenciales → ID de cliente de OAuth**
   - Tipo **Aplicación web**
   - Nombre: Pokke Web
   - **URI de redirección autorizados** (importante para Supabase):
     ```
     https://ajbmpgnzkgtcmulocftd.supabase.co/auth/v1/callback
     ```
5. Copia el **ID de cliente** (termina en `.apps.googleusercontent.com`).

## 2. Supabase Kokoro
1. Proyecto Kokoro → **Authentication → Providers → Google**
2. Activa Google.
3. Pega **Client ID** y **Client Secret** de Google.
4. Guarda.

## 3. En el código Pokke
En `PokkeConfig.kt`:
```kotlin
const val GOOGLE_WEB_CLIENT_ID = "TU_ID.apps.googleusercontent.com"
```

## 4. v0.2
Cablearemos Credential Manager / Custom Tabs para completar el token con Supabase
`/auth/v1/token?grant_type=id_token` y dejar la sesión en `KokoroAuth`.

Hasta entonces el botón Google está **preparado** pero deshabilitado si el ID está vacío.
EOF

cat > /home/workdir/artifacts/pokke-v01/STATUS.md << 'EOF'
# Pokke v0.1

## Hecho
- Proyecto limpio `com.music.pokke` (repo gradle name Olico)
- Home: Ukiyo recent + trends (`/api/trends` o fallback)
- Search con % si YT off y vacío
- Library, Settings (A–I toggles), Account (Kokoro email), Admin (URL API)
- Player full + mini + lyrics sync skeleton + MediaSession background
- Ukiyo ON / YouTube OFF / Spotify OFF defaults
- Silent harvest when YT on
- MUSIC_API_BASE = https://steve-inquire-satisfy-tear.trycloudflare.com
- CI debug APK

## v0.2
- Port BitChord full player UI + lyrics polish
- YouTube Innertube + Google session bridge
- Spotify playlists / canvas
- Real EQ / crossfade / party
- Trends table plays (Kokoro) for Ukiyo + YT
EOF

cd /home/workdir/artifacts && rm -f pokke-v01.zip && zip -r -q pokke-v01.zip pokke-v01
ls -lh pokke-v01.zip
find pokke-v01 -name "*.kt" | wc -l
póndescription