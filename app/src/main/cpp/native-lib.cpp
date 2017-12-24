#include <jni.h>
#include <string>
#include <android/log.h>
#include <fstream>
#include <iostream>
#include <sys/stat.h>
#include <vector>

using namespace std;

#define LOG_TAG "MainActivity"

//JNIEXPORT void JNICALL
//Java_com_ashutosh_ndkpixels_MainActivity_stringToJNI(JNIEnv *env, jobject, jstring str_) {
//
//    std::string imgUri = ConvertJString( env, str_);
//}

jintArray to_java(JNIEnv *env, int *arr, int size) {
    // allocate the int array first
    jintArray inner = env->NewIntArray(size);

    // then fill that array with data from the input
    env->SetIntArrayRegion(inner, 0, size, &arr[0]);

    return inner;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_ashutosh_ndkpixels_MainActivity_processImage(JNIEnv *env, jobject instance,
                                                      jstring imgUrl_) {
    const char *imgUrl = env->GetStringUTFChars(imgUrl_, 0);

    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "Img url (jni)=  %s ", imgUrl);

//    ifstream imgFile;
//    imgFile.open(imgUrl, ios::in | ios::out | ios::binary);
//
//    if (imgFile.is_open()) {
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "File open success.");

    struct stat results;

    if (stat(imgUrl, &results) == 0) {
        // The size of the file in bytes is in
        __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "Img size = %lld", results.st_size);

        char imgBytes[results.st_size];

        ifstream imgFile(imgUrl, ios::in | ios::binary);

        if (!imgFile.read(imgBytes, results.st_size)) {
            // Error occurred - Reached end of file without reading complete. - imgFile.gcount()
            __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "Remaining bytes = %ld",
                                imgFile.gcount());
        }

        int freqA[256] = {}, freqR[256] = {}, freqG[256] = {}, freqB[256] = {};
        int k = 0, l = 0, m = 0, n = 0;

        for (int i = 0; i < results.st_size; i++) {
            if (i % 4 == 0) {
                freqA[imgBytes[i]]++;
                k++;
            } else if (i % 4 == 1) {
                freqR[imgBytes[i]]++;
                l++;
            } else if (i % 4 == 2) {
                freqG[imgBytes[i]]++;
                m++;
            } else if (i % 4 == 3) {
                freqB[imgBytes[i]]++;
                n++;
            }
        }

        int j = 0;
        for (int i = 0; i < 256; i++) {
            __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "i = %d, A = %d", i, freqA[i]);
            j += freqA[i];
        }

        __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "k = %d, j = %d", k, j);

        imgFile.close();

        jintArray jintArrA = to_java(env, freqA, 256);
        jintArray jintArrR = to_java(env, freqR, 256);
        jintArray jintArrG = to_java(env, freqG, 256);
        jintArray jintArrB = to_java(env, freqB, 256);

        // Getting the class
        jclass clazz = env->FindClass("com/ashutosh/ndkpixels/MainActivity");

        // Getting the method - int [] args, void return type
        jmethodID methodShowGraphId = env->GetMethodID(clazz, "showGraphs", "([I[I[I[I)V");
        env->CallVoidMethod(instance, methodShowGraphId, jintArrA, jintArrR, jintArrG, jintArrB);

    } else {
        // An error occurred
        __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "Error in getting size");
    }
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_ashutosh_ndkpixels_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++.";

    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "Log like this");

    return env->NewStringUTF(hello.c_str());
}