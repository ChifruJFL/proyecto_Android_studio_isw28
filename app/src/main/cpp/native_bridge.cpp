
#include <jni.h>
#include <stdint.h>
#include <cstdio>
#include <mutex>
#include <string>
#include <vector>
#include "doomgeneric.h"

// Función implementada en doomgeneric_android.c
extern "C" void DG_QueueKey(int pressed, unsigned char key);

static std::mutex engine_mutex;
static bool initialized = false;
static std::string wad_path;
static std::string config_path;
static char* engine_args[5];

// Comprobar conexión Kotlin y C++
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_proyecto_1segundo_1parcial_DoomNative_estado(
        JNIEnv* env, jobject) {

    return env->NewStringUTF(
            "JNI conectado: Kotlin y C++ se comunican correctamente."
    );
}

// Iniciar el motor DOOM
extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_proyecto_1segundo_1parcial_DoomNative_iniciar(
        JNIEnv* env,
        jobject,
        jstring path,
        jstring configDir) {

    std::lock_guard<std::mutex> lock(engine_mutex);

    if (initialized) return JNI_TRUE;

    if (!path || !configDir) return JNI_FALSE;

    const char* chars = env->GetStringUTFChars(path, nullptr);
    if (!chars) return JNI_FALSE;

    wad_path = chars;
    env->ReleaseStringUTFChars(path, chars);

    FILE* test = std::fopen(wad_path.c_str(), "rb");

    if (!test) return JNI_FALSE;

    std::fclose(test);

    chars = env->GetStringUTFChars(configDir, nullptr);
    if (!chars) return JNI_FALSE;

    config_path = std::string(chars) + "/geodoom.cfg";
    env->ReleaseStringUTFChars(configDir, chars);

    static char program[] = "geodoom";
    static char iwadArg[] = "-iwad";
    static char configArg[] = "-config";

    engine_args[0] = program;
    engine_args[1] = iwadArg;
    engine_args[2] = const_cast<char*>(wad_path.c_str());
    engine_args[3] = configArg;
    engine_args[4] = const_cast<char*>(config_path.c_str());

    doomgeneric_Create(5, engine_args);

    initialized = true;

    return JNI_TRUE;
}

// Obtener los fotogramas del juego
extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_proyecto_1segundo_1parcial_DoomNative_cuadro(
        JNIEnv* env,
        jobject,
        jintArray argb) {

    if (!argb) return JNI_FALSE;

    constexpr int width = DOOMGENERIC_RESX;
    constexpr int height = DOOMGENERIC_RESY;
    constexpr int count = width * height;

    if (env->GetArrayLength(argb) < count)
        return JNI_FALSE;

    std::lock_guard<std::mutex> lock(engine_mutex);

    if (!initialized || !DG_ScreenBuffer)
        return JNI_FALSE;

    doomgeneric_Tick();

    std::vector<jint> pixels(count);

    for (int i = 0; i < count; ++i) {
        pixels[i] = static_cast<jint>(
                0xff000000u |
                (DG_ScreenBuffer[i] & 0x00ffffffu)
        );
    }

    env->SetIntArrayRegion(argb, 0, count, pixels.data());

    return JNI_TRUE;
}

// Recibir controles táctiles desde Kotlin
extern "C" JNIEXPORT void JNICALL
Java_com_example_proyecto_1segundo_1parcial_DoomNative_tecla(
        JNIEnv*,
        jobject,
        jint key,
        jboolean pressed) {

    if (key >= 0 && key <= 255) {
        DG_QueueKey(
                pressed ? 1 : 0,
                static_cast<unsigned char>(key)
        );
    }
}
