// Adaptador Android de Doomgeneric: reloj, teclado y framebuffer.
// El motor y la interfaz se llaman desde un hilo Kotlin; esta cola es thread-safe.
#include "doomgeneric.h"
#include <pthread.h>
#include <errno.h>
#include <stdint.h>
#include <time.h>

#define KEY_QUEUE_SIZE 128

typedef struct {
    int pressed;
    unsigned char key;
} KeyEvent;

static KeyEvent key_queue[KEY_QUEUE_SIZE];
static unsigned int head = 0;
static unsigned int tail = 0;
static pthread_mutex_t key_mutex = PTHREAD_MUTEX_INITIALIZER;

void DG_QueueKey(int pressed, unsigned char key) {
    pthread_mutex_lock(&key_mutex);
    unsigned int next = (head + 1) % KEY_QUEUE_SIZE;
    if (next == tail) tail = (tail + 1) % KEY_QUEUE_SIZE;
    key_queue[head].pressed = pressed ? 1 : 0;
    key_queue[head].key = key;
    head = next;
    pthread_mutex_unlock(&key_mutex);
}

void DG_Init(void) { }
void DG_DrawFrame(void) { }  // JNI lee DG_ScreenBuffer tras cada Tick.

void DG_SleepMs(uint32_t ms) {
    struct timespec req = {(time_t)(ms / 1000), (long)(ms % 1000) * 1000000L};
    struct timespec rem;
    while (nanosleep(&req, &rem) == -1 && errno == EINTR) req = rem;
}

uint32_t DG_GetTicksMs(void) {
    struct timespec now;
    if (clock_gettime(CLOCK_MONOTONIC, &now) != 0) return 0;
    return (uint32_t)((uint64_t)now.tv_sec * 1000u + (uint64_t)now.tv_nsec / 1000000u);
}

int DG_GetKey(int *pressed, unsigned char *key) {
    pthread_mutex_lock(&key_mutex);
    if (head == tail) {
        pthread_mutex_unlock(&key_mutex);
        return 0;
    }
    *pressed = key_queue[tail].pressed;
    *key = key_queue[tail].key;
    tail = (tail + 1) % KEY_QUEUE_SIZE;
    pthread_mutex_unlock(&key_mutex);
    return 1;
}

void DG_SetWindowTitle(const char *title) { (void)title; }
