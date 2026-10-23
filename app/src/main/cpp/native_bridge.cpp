#include <jni.h>

// El paquete Kotlin es com.example.proyecto_segundo_parcial.
// En nombres JNI los guiones bajos del paquete se codifican como _1.
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_proyecto_1segundo_1parcial_DoomNative_estado(
        JNIEnv* env, jobject /* instancia */) {
    return env->NewStringUTF("JNI conectado: Kotlin y C++ se comunican correctamente.");
}
