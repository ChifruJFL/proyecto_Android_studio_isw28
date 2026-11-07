
cmake_minimum_required(VERSION 3.22.1)
project(geodoom LANGUAGES C CXX)

set(CMAKE_C_STANDARD 99)
set(CMAKE_C_STANDARD_REQUIRED ON)
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

# --------------------------------------------------
# SDL2: biblioteca estatica
# --------------------------------------------------

set(SDL2_DISABLE_INSTALL ON CACHE BOOL "" FORCE)
set(SDL2_DISABLE_UNINSTALL ON CACHE BOOL "" FORCE)
set(SDL2_DISABLE_SDL2MAIN ON CACHE BOOL "" FORCE)

set(SDL_SHARED OFF CACHE BOOL "" FORCE)
set(SDL_STATIC ON CACHE BOOL "" FORCE)
set(SDL_STATIC_PIC ON CACHE BOOL "" FORCE)
set(SDL_TEST OFF CACHE BOOL "" FORCE)

add_subdirectory(third_party/SDL2 EXCLUDE_FROM_ALL)

# --------------------------------------------------
# SDL2_mixer: efectos de sonido y musica MIDI
# --------------------------------------------------

set(BUILD_SHARED_LIBS OFF CACHE BOOL "" FORCE)

set(SDL2MIXER_INSTALL OFF CACHE BOOL "" FORCE)
set(SDL2MIXER_SAMPLES OFF CACHE BOOL "" FORCE)
set(SDL2MIXER_CMD OFF CACHE BOOL "" FORCE)
set(SDL2MIXER_VENDORED ON CACHE BOOL "" FORCE)
set(SDL2MIXER_DEPS_SHARED OFF CACHE BOOL "" FORCE)

# Desactivar formatos que no necesitamos por ahora
set(SDL2MIXER_FLAC OFF CACHE BOOL "" FORCE)
set(SDL2MIXER_GME OFF CACHE BOOL "" FORCE)
set(SDL2MIXER_MOD OFF CACHE BOOL "" FORCE)
set(SDL2MIXER_MP3 OFF CACHE BOOL "" FORCE)
set(SDL2MIXER_OPUS OFF CACHE BOOL "" FORCE)
set(SDL2MIXER_VORBIS "" CACHE STRING "" FORCE)
set(SDL2MIXER_WAVPACK OFF CACHE BOOL "" FORCE)

# Mantener MIDI mediante Timidity y soporte WAV
set(SDL2MIXER_MIDI ON CACHE BOOL "" FORCE)
set(SDL2MIXER_MIDI_TIMIDITY ON CACHE BOOL "" FORCE)
set(SDL2MIXER_MIDI_FLUIDSYNTH OFF CACHE BOOL "" FORCE)
set(SDL2MIXER_WAVE ON CACHE BOOL "" FORCE)

add_subdirectory(third_party/SDL2_mixer EXCLUDE_FROM_ALL)

# --------------------------------------------------
# Archivos del motor DOOM
# --------------------------------------------------

set(DOOM_CORE_FILES
        dummy.c am_map.c doomdef.c doomstat.c dstrings.c d_event.c d_items.c
        d_iwad.c d_loop.c d_main.c d_mode.c d_net.c f_finale.c f_wipe.c
        g_game.c hu_lib.c hu_stuff.c info.c i_cdmus.c i_endoom.c i_joystick.c
        i_scale.c i_sound.c i_sdlsound.c i_sdlmusic.c mus2mid.c
        i_system.c i_timer.c memio.c m_argv.c m_bbox.c
        m_cheat.c m_config.c m_controls.c m_fixed.c m_menu.c m_misc.c m_random.c
        p_ceilng.c p_doors.c p_enemy.c p_floor.c p_inter.c p_lights.c p_map.c
        p_maputl.c p_mobj.c p_plats.c p_pspr.c p_saveg.c p_setup.c p_sight.c
        p_spec.c p_switch.c p_telept.c p_tick.c p_user.c r_bsp.c r_data.c
        r_draw.c r_main.c r_plane.c r_segs.c r_sky.c r_things.c sha1.c
        sounds.c statdump.c st_lib.c st_stuff.c s_sound.c tables.c v_video.c
        wi_stuff.c w_checksum.c w_file.c w_main.c w_wad.c z_zone.c w_file_stdc.c
        i_input.c i_video.c doomgeneric.c
)

list(TRANSFORM DOOM_CORE_FILES
        PREPEND "${CMAKE_CURRENT_SOURCE_DIR}/doomgeneric/"
)

# --------------------------------------------------
# Biblioteca nativa de GeoDoom
# --------------------------------------------------

add_library(geodoom SHARED
        native_bridge.cpp
        doomgeneric_android.c
        ${DOOM_CORE_FILES}
)

target_include_directories(geodoom PRIVATE
        "${CMAKE_CURRENT_SOURCE_DIR}/doomgeneric"
)

target_compile_definitions(geodoom PRIVATE
        NORMALUNIX
        LINUX
        SNDSERV
        _DEFAULT_SOURCE
        FEATURE_SOUND
        DOOMGENERIC_RESX=320
        DOOMGENERIC_RESY=200
)

target_link_libraries(geodoom PRIVATE
        SDL2_mixer::SDL2_mixer-static
        SDL2::SDL2-static
        m
        log
        android
)
